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

import akka.actor._


/**
 * the powder
 */
object Powder {
  
  val getInstance = new Powder
  
}

/**
 * the powder
 */
class Powder {

  val instanceID = new java.util.Random().nextInt(77777).toString
  
  val eventBus = new MessageBus
  private val eventSystem = ActorSystem("events")
  
  private val processorSystem = ActorSystem("processors")
  
  private val tick = "tick"
  val serviceSystem = ActorSystem("services")
  
  /**
   * Helper method to publish an event on the EventBus.
   * @param eventParameters a type of [[com.alloyengine.powder.Event]] to configure the socket
   * @return the [[akka.actor.ActorRef]]
   */
  def publish(event: PowderEvent) = {
    eventBus.publish( event )
  }
  
  /**
   * Helper method to publish an event on the EventBus.
   * @param eventParameters a type of [[com.alloyengine.powder.Event]] to configure the socket
   * @return the [[akka.actor.ActorRef]]
   */
  def subscribe(actorRef: ActorRef, channel: String) = {
    eventBus.subscribe( actorRef, channel )
  }
  
  /**
   * Factory method to create and register a Powder event.
   * @param eventParameters a type of [[com.alloyengine.powder.Event]] to configure the socket
   * @return the [[akka.actor.ActorRef]]
   */
  def registerListener(listener: Listener): ActorRef = {
    val subscriber = eventSystem.actorOf(newListenerProps(listener))
    listener.channels.map { channel => 
      eventBus.subscribe( subscriber, channel )
    }
    subscriber
  }
  
  /**
   * Factory method to create and register a Powder event.
   * @param eventParameters a type of [[com.alloyengine.powder.Event]] to configure the socket
   * @return the [[akka.actor.ActorRef]]
   */
  def registerCustomListener(listener: Actor): ActorRef = {
    eventSystem.actorOf(Props(listener).withDispatcher("com.alloyengine.powder.dispatcher"))
  }
  
  /**
   * Factory method to create a Powder processor.
   * @param eventParameters a type of [[com.alloyengine.powder.Event]] to configure the socket
   * @return the [[akka.actor.ActorRef]]
   */
  def registerProcessor(processorClass: Class[_ <: Processor]): ActorRef = {
    val processor = processorSystem.actorOf(newProcessorProps(processorClass))
    processor
  }
  
  /**
   * Factory method to create the [[akka.actor.Props]] to build a Powder processor actor.
   * @param processorParameters a type of [[com.alloyengine.powder.Processor]] to configure the socket
   * @return the [[akka.actor.Props]]
   */
  def newProcessorProps(processorClass: Class[_ <: Processor]): Props = {
    Props(processorClass.newInstance()).withDispatcher("com.alloyengine.powder.dispatcher")
  }
  
  /**
   * Factory method to create the [[akka.actor.Props]] to build a Powder processor actor.
   * @param processorParameters a type of [[com.alloyengine.powder.Processor]] to configure the socket
   * @return the [[akka.actor.Props]]
   */
  def newListenerProps(listener: Listener): Props = {
    Props(new ListenerActor(listener)).withDispatcher("com.alloyengine.powder.dispatcher")
  }
  
  /**
   * Factory method to create the [[akka.actor.Props]] to build a Powder service actor.
   * @param serviceParameters a type of [[com.alloyengine.powder.Service]] to configure the socket
   * @return the [[akka.actor.Props]]
   */
  def newServiceProps(serviceParameters: Service): Props = {
    Props(new ServiceActor(serviceParameters)).withDispatcher("com.alloyengine.powder.dispatcher")
  }

}

