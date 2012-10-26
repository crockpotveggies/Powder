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
package engine.socket.context

import play.api._
import play.api.mvc._
import play.api.Logger
import play.api.Play.current
import play.api.db.DB

import akka._
import akka.actor._
import akka.actor.Actor._
import akka.routing._
import akka.event._

import models._

import engine._
import engine.hive._
import engine.socket.MessageEvent
import engine.socket.msg._

import scala.collection.mutable.Map
import java.util.Date
import com.codahale.logula.Logging
import org.webbitserver._

import org.squeryl._
import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.PrimitiveTypeMode._

import com.codahale.jerkson.Json._

/**
 * special actor transporting messages to the hive
 */
class HiveTransport extends Actor {
  
  def receive = {
    case MessageEvent(instance, channel, message, None) => 
      if(instance == Engine.instanceID) Hive.rePub(channel, message)
    	
    case MessageEvent(instance, channel, message, Some(ping)) => 
    	// do nothing
    	
  }
  
}
