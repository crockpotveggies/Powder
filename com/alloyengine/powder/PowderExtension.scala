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

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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
  
}

