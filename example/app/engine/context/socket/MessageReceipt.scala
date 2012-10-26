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
import play.api.libs.concurrent.Akka

import akka._
import akka.actor._
import akka.actor.Actor._
import akka.util.duration._

import models._

import engine._
import engine.socket.MessageWrap._

import scala.collection.mutable.Map

import java.util.Date

import org.webbitserver._
import org.squeryl._
import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.PrimitiveTypeMode._

import com.codahale.jerkson.Json._


/**
 * send receipts for incoming messages
 */
class MessageReceipt extends Actor {
  
  def receive = {
    case Tuple2(s:WebSocketConnection,msgId:String) => {
      s.send(MsgReceipt( msgId, true ))
    }
  }
  
}


object MessageReceipt {
  
  val system = ActorSystem("messaging")
  val Receiptor = system.actorOf(Props(new MessageReceipt))
  
}