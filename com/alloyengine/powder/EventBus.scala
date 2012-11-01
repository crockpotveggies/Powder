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

import akka._
import akka.actor._
import akka.actor.Actor._
import akka.event.ActorEventBus
import akka.event.SubchannelClassification
import akka.util.Subclassification

/**
 * powder event setup
 */
trait EventData

case class PowderEvent( val channel: String, val data: EventData )

/**
 * event bus handles routing of all the different events
 */
class MessageBus extends ActorEventBus with SubchannelClassification {

	type Event = PowderEvent
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
