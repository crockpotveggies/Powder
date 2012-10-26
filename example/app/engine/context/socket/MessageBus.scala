/*
 *	        __   __
 *	_____ _|  | |  |   ____ ___ __
 *	\__    |  | |  |  /  _ \   |  |
 *	 / __  |  |_|  |_(  |_| )___  |
 *	(____  /____/____/\____// ____|
 *	     \/                 \/
 *     
 *	     Copyright Alloy Technologies
 */
package engine.socket

import play.api._
import play.api.mvc._
import play.api.Logger
import play.api.Play.current
import play.api.db.DB

import akka._
import akka.actor._
import akka.actor.Actor._
import akka.event.ActorEventBus
import akka.event.SubchannelClassification
import akka.util.Subclassification

import models._

import engine.logic._
import engine.socket.msg._
import engine.socket.context._
import engine.socket.MessageWrap._

import scala.collection.mutable.Map
import java.util.Date
import com.codahale.logula.Logging
import org.webbitserver._

import org.squeryl._
import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.PrimitiveTypeMode._

import com.codahale.jerkson.Json._



case class MessageEvent(val instance:String, val channel:String, val message:String, val ping:Option[Array[Byte]])

/**
 * message bus to route messages to their appropriate contexts
 */
class MessageBus extends ActorEventBus with SubchannelClassification {

	type Event = MessageEvent
  type Classifier = String
  
  protected implicit def subclassification: Subclassification[Classifier] = 
	  new Subclassification[Classifier] {
	    def isEqual(a: Classifier, b: Classifier): Boolean = {
	      a.equals(b)
	    }
	
	    def isSubclass(a: Classifier, b: Classifier): Boolean = {
	      a.startsWith(b)
	    }
	}
  
  protected def mapSize(): Int = {
    10
  }

  protected def classify(event: Event): Classifier = {
    event.channel
  }
  
  protected def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event
  }
	
}


object MessageBus {
  
  val actorSystem = ActorSystem("contexts")
  val Bus = new MessageBus
  
  /**
   * special actor that transports messages to the hive
   */
  val hiveTalk = {
    val subscriber = actorSystem.actorOf(Props(new HiveTransport))
    
    Bus.subscribe( subscriber, "/alloy/app" )
    Bus.subscribe( subscriber, "/alloy/browser/" )
    Bus.subscribe( subscriber, "/alloy/mobile/" )
  }
  
  /**
   * create an actor that stores a browser socket
   */
  def browserSocketContext(s: WebSocketConnection, userId: Long, teamId: Long) = {
    val socketId = formatSocketId(s.toString)
    val subscriber = actorSystem.actorOf(Props(new BrowserSocket(s,userId,teamId)))
    
    Bus.subscribe( subscriber, "/alloy/socket/%s" format socketId )
    Bus.subscribe( subscriber, "/alloy/browser/u/%s" format userId )
    Bus.subscribe( subscriber, "/alloy/browser/t/%s" format teamId )
    Bus.subscribe( subscriber, "/alloy/browser/ut/%s/%s" format (teamId,userId) )
    Bus.subscribe( subscriber, "/alloy/app" )
    
    Meta.handleUserConnected ! (userId,teamId)
    Sync.sendTeamModel ! (teamId)
    Sync.sendTeamChat ! (teamId)
    Sync.sendWhosOnline ! (s,teamId)
    Sync.sendBrowserDrafts ! (userId,teamId)
  }
  
  /**
   * create an actor that stores a mobile socket
   */
  def mobileSocketContext(s: WebSocketConnection, userId: Long) = {
    val socketId = formatSocketId(s.toString)
    val subscriber = actorSystem.actorOf(Props(new MobileSocket(s,userId)))

    Bus.subscribe( subscriber, "/alloy/socket/%s" format socketId)
    Bus.subscribe( subscriber, "/alloy/mobile/u/%s" format userId )
    Bus.subscribe( subscriber, "/alloy/app" )
    
    Meta.handleUserConnected ! (userId)
    Sync.sendUserModel ! (userId)
    Sync.sendUserChats ! (userId)
    Sync.sendMobileDrafts ! (userId)
  }
  
  /**
   * handle a new context
   */
  def handleContext(s:WebSocketConnection, context:String, email:String, teamId:Long) = {
    Logger.info("New "+context+" context requested for "+email)
    models.User.getByEmail(email) match {
      case Some(user) =>
        context match {
          case "browser" =>
            browserSocketContext(s,user.id,teamId)

          case "mobile" =>
				    s.send(HandshakeRequest(email))
        }                

      case None => 
        s.close()
      
    }
  }
  
  /**
   * handle the incoming handshake from mobile
   */
  def handleHandshake(s:WebSocketConnection, email:String, password:String) = {
    models.User.authenticate(email, password) match {
      case Some(user:models.User) =>
        mobileSocketContext(s,user.id)
        Logger.info("Successful mobile context for "+user.email)

      case _ =>
        s.send(HandshakeSeal("fail",0))
    }
  }
  
  /**
   * format the socket ID
   */
  def formatSocketId(rawSocket: String):String = {
    val Array(str1:String,str2:String) = rawSocket.split("@")
    str2
  }
  
}
