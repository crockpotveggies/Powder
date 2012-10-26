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
 * powder event
 */
class Event(
  
  val name: String,
  val channels: Seq[String],
  val processors: Seq[T <: Processor],
  val data: T

) {
  
}

