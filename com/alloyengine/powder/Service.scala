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


/**
 * powder service
 */
class Service(
  
  val signal: Signal = new Signal,
  val delay: akka.util.Duration,
  val interval: akka.util.Duration,
  val processors: Seq[Class[_ <: Processor]]
   
) {
  
}