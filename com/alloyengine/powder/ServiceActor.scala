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
 * powder service actor
 */
class ServiceActor(service: Service) extends Actor {
  
  def receive = {
    case "tick" => 
      service.processors.foreach { processorClass =>
        val processor = Powder.getInstance.registerProcessor(processorClass)
        processor ! service.signal
      }
  }
  
}

