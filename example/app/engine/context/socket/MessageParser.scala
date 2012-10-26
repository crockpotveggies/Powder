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

import models._

import engine.logic._
import engine.socket._
import engine.socket.msg._

import scala.collection.mutable.Map
import java.util.Date
import java.io._

import util.time.TimeFormats
import util.time.TimeFormats._
import util.time.TimeParsers
import util.time.TimeParsers._

import org.webbitserver._
import org.squeryl._
import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.PrimitiveTypeMode._

import com.codahale.jerkson.Json._

/**
 * it's all about parsing messages
 */
object MessageParser {
  	
  def readStringMsg(s:WebSocketConnection, rawMessage:String) = {
  	try {
	    parseRawMessage(s, rawMessage) match {

	      case NewContext(context, email, teamId) => 
	        MessageBus.handleContext(s, context, email, teamId)

	      case HandshakeResponse(user,password) => 
	        MessageBus.handleHandshake(s, user, password)

	      case UserConnected(userId) => 
	        //val teams = models.User.teamIds(userId)
	        //teams.foreach{ case t => EngineSync.handleUserConnected(s, userId, t) }

	      case UserDevice(userId,token,vendor,action) => 
	        Meta.handleUserDevice ! (userId,token,vendor,action)

	      case UserLocation(userId,lat,lng)   =>
	        Sync.sendUserLocation ! (userId,lat,lng)

	      case JobMoved(jobId, sourceUser, sourceIndex, targetUser, targetIndex, teamId, actorId) =>
	        State.handleJobMoved ! (jobId, sourceUser, sourceIndex, targetUser, targetIndex, teamId, actorId)

	      case JobScheduled(jobId, targetUser, targetStartTime, targetEndTime, teamId, actorId) =>
	        State.handleJobScheduled ! (jobId, targetUser, targetStartTime, targetEndTime, teamId, actorId)

	      case JobState(jobId, state, userId, teamId) =>
	        State.handleJobState ! (jobId, state, userId, teamId)

	      case MemberState(state, userId, teamId) =>
	        State.handleMemberState ! (state, userId, teamId)

	      case ClientState(state, clientId, userId, teamId) =>
	        State.handleClientState ! (state, clientId, userId, teamId)

	      case ProjectState(state, projectId, userId, teamId) =>
	        State.handleProjectState ! (state, projectId, userId, teamId)

	      case ReportState(state, instanceId, userId, teamId) =>
	        State.handleFormState ! (state, instanceId, userId, teamId)

	      case TemplateState(state, templateType, templateId, userId, teamId) =>
	        State.handleTemplateState ! (state, templateType, templateId, userId, teamId)

	      case ChatMsg(userId, teamId, content, lat, lng, announce) =>
	        Communication.handleChatMsg ! (userId, teamId, content, lat, lng, announce)
	
	    }
	  } catch {
	    case e => 
	      val errors = new StringWriter()
	      e.printStackTrace(new PrintWriter(errors));
	      Logger.error("Engine MessageParser failed. \r\n The MESSAGE: %s \r\n The error %s" format (rawMessage,errors))
	  }
  }
  
  /**
   * match to a case class
   */
  def matchMsg(s:WebSocketConnection, msgCase:String, msgData:String) = {
    msgCase match {
       // context events
       case "NewContext"            => parse[NewContext](msgData)
       
       // browser events
       case "TeamData"							=> parse[TeamModel](msgData)
      
       // mobile events
       case "HandshakeResponse"     => parse[HandshakeResponse](msgData)
       case "UserConnected"         => parse[UserConnected](msgData)
       case "UserLocation"          => parse[UserLocation](msgData)
       case "UserDevice"            => parse[UserDevice](msgData)

       // engine events
       case "JobMoved"           		=> parse[JobMoved](msgData)
       case "JobScheduled"       		=> parse[JobScheduled](msgData)
       case "JobState"          		=> parse[JobState](msgData)
       case "ChatMsg"          			=> parse[ChatMsg](msgData)
       
       // team events
       case "MemberState"          	=> parse[MemberState](msgData)
       case "ClientState"          	=> parse[ClientState](msgData)
       case "ReportState"          	=> parse[ReportState](msgData)
       case "ProjectState"          => parse[ProjectState](msgData)
       case "TemplateState"         => parse[TemplateState](msgData)
       
       // huh?
       case _												=> s.send(MsgError("Requested relay action not available on this server"))
    }
  }
  
  /**
   * parse the raw JSON string
   */
  def parseRawMessage(s:WebSocketConnection, messageRaw:String):AnyRef = {
    val msgData = {
	    	val MsgEncoded = "\\{(.*)\"data\":(.*)\\}".r
	      val MsgEncoded(crap, data) = messageRaw
	      (data)
    }
    val (msgType, msgId) = {
      val output = parse[MsgRaw](messageRaw)
      (output.relay, output.relayId)
    }
    
    var Receipt = Tuple2[WebSocketConnection, String](s, "000000")
	  if(msgId!=None) Receipt = Tuple2(s, msgId.get)
    MessageReceipt.Receiptor ! Receipt
    
    matchMsg(s, msgType, msgData)
  }
  
}
