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
 * powder event
 */
class ListenerActor(val listener: Listener) extends Actor {
  
  def receive = {
    case data =>
      listener.processors.map { processorClass =>
        val processor = Powder.getInstance.registerProcessor(processorClass)
        processor ! data 
      }
  }
  
}

