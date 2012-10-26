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



/**
 * event bus handles routing of all the different events
 */
class EventBus extends ActorEventBus with SubchannelClassification {

	type Event = Event
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
