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
package com.alloyengine.powder

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
 * powder event
 */
private[powder] class ListenerActor(val listener: Listener) extends Actor {
  
  def receive = {
    case data =>
      listener.processors.foreach { processor =>
        val processor = registerProcessor(processor)
        processor ! data 
      }
  }
  
}

