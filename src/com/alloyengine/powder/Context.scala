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
import engine.socket.msg._
import engine.socket.MessageEvent
import engine.socket.MessageWrap._

import scala.collection.mutable.Map
import java.util.Date
import com.codahale.logula.Logging
import org.webbitserver._

import org.squeryl._
import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.PrimitiveTypeMode._

import com.codahale.jerkson.Json._

/**
 * powder context
 */
abstract class Context(
  
) extends Actor {
  
  def preStart() {
    
  }
  
}