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
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import java.util.concurrent.TimeUnit
import akka.util.Timeout

import scala.concurrent.duration.FiniteDuration

/**
 * The [[akka.actor.ExtensionId]] and [[akka.actor.ExtensionIdProvider]] for Powder
 */
object PowderExtension extends ExtensionId[PowderExtension] with ExtensionIdProvider {
  
  override def get(system: ActorSystem): PowderExtension = super.get(system)
  def lookup(): this.type = this
  override def createExtension(system: ExtendedActorSystem): PowderExtension = new PowderExtension(system)

}

/**
 * hello Powder
 *
 * @param system The ActorSystem this extension belongs to.
 */
class PowderExtension(system: ActorSystem) extends Extension {
  
  val version = "0.1.1"
  val instanceID = new java.util.Random().nextInt(77777).toString

	implicit val ec = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())
  
  val eventBus = new EventBus
  private val eventSystem = ActorSystem("events")
  
  private val processorSystem = ActorSystem("processors")
  
  private val tick = "tick"
  val serviceSystem = ActorSystem("services")
  
  /**
   * Factory method to create the [[akka.actor.Props]] to build a Powder event and corresponding listener.
   * @param eventParameters a type of [[com.alloyengine.powder.Event]] to configure the socket
   * @return the [[akka.actor.Props]]
   */
  def newEventProps(eventParameters: Event): Props = {
    Props(new EventActor(eventParameters)).withDispatcher("com.alloyengine.powder.dispatcher")
  }
  
  /**
   * Factory method to create the [[akka.actor.Props]] to build a Powder processor actor.
   * @param processorParameters a type of [[com.alloyengine.powder.Processor]] to configure the socket
   * @return the [[akka.actor.Props]]
   */
  def newProcessorProps(processorClass: T): Props = {
    Props(new processorClass).withDispatcher("com.alloyengine.powder.dispatcher")
  }
  
  /**
   * Factory method to create the [[akka.actor.Props]] to build a Powder service actor.
   * @param serviceParameters a type of [[com.alloyengine.powder.Service]] to configure the socket
   * @return the [[akka.actor.Props]]
   */
  def newServiceProps(serviceParameters: Service): Props = {
    Props(new ServiceActor(serviceParameters)).withDispatcher("com.alloyengine.powder.dispatcher")
  }
  
  /**
   * Factory method to create and register a Powder event.
   * @param eventParameters a type of [[com.alloyengine.powder.Event]] to configure the socket
   * @return the [[akka.actor.ActorRef]]
   */
  def registerEvent(eventParameters: Event): ActorRef = {
    val subscriber = eventSystem.actorOf(newEventProps(eventParameters))
    eventParameters.channels.map { channel => 
      eventBus.subscribe( subscriber, channel )
    }
    subscriber.mapTo[ActorRef]
  }
  
  /**
   * Factory method to create a Powder processor.
   * @param eventParameters a type of [[com.alloyengine.powder.Event]] to configure the socket
   * @return the [[akka.actor.ActorRef]]
   */
  def registerProcessor(processor: T): ActorRef = {
    val processor = processorSystem.actorOf(newProcessorProps(processor))
    processor.mapTo[ActorRef]
  }
  
  /**
   * Helper method to publish an event on the EventBus.
   * @param eventParameters a type of [[com.alloyengine.powder.Event]] to configure the socket
   * @return the [[akka.actor.ActorRef]]
   */
  def publish(event: Event): ActorRef = {
    eventBus.publish( event )
  }
  
}

