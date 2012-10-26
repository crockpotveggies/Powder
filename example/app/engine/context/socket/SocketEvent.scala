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
package engine.socket

import play.api._
import play.api.Logger

import org.webbitserver._
import org.squeryl._
import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.PrimitiveTypeMode._

/**
 * SocketEvent
 * Sealed trait based on the WebSocketEvent wrapper for Webbit
 * 
 * License: http://www.apache.org/licenses/LICENSE-2.0.html
 */
sealed trait SocketEvent

class EngineConnection(val connection:WebSocketConnection) extends WebSocketConnection {

  def send(msg:String) = {
    Logger.debug(msg)
    connection.send(msg)
    this
  }

  def httpRequest() = connection.httpRequest()

  def send(bytes:Array[Byte]) = connection.send(bytes)

  def ping(bytes:Array[Byte]) = connection.ping(bytes)

  def pong(bytes:Array[Byte]) = connection.pong(bytes)

  def send(bytes:Array[Byte], offset:Int, length:Int) = connection.send(bytes, offset, length)
  
  def close() = connection.close()

  def data(string:String, `object`: AnyRef) = connection.data(string, `object`)

  def handlerExecutor() = connection.handlerExecutor()

  def execute(command:java.lang.Runnable) = connection.execute(command)

  def data() = connection.data()

  def data(string:String) = connection.data(string)

  def dataKeys() = connection.dataKeys()

  def version() = connection.version()

  override def equals(o:Any) = {
    if (o.isInstanceOf[EngineConnection])
      connection.equals(o.asInstanceOf[EngineConnection].connection)
    else
      connection.equals(o)
  }

  override def hashCode = {
    connection.hashCode
  }

}

object EngineConnection {
  implicit def webSocketConn2LoggingWebSocketConn(c:WebSocketConnection) = new EngineConnection(c)
}

class MsgEventAdapter(listener:PartialFunction[SocketEvent,Unit]) extends WebSocketHandler {
  def onOpen(connection:WebSocketConnection) {
    Logger.debug(connection.toString + " OPEN")
    listener(Open(connection))
  }

  def onClose(connection:WebSocketConnection) {
    Logger.debug(connection.toString + " CLOSED")
    listener(Close(connection))
  }

  def onMessage(connection:WebSocketConnection, message:String) {
    Logger.debug(connection.toString + " MSG: " + message)
    transaction{ listener(StringMsg(connection, message)) }
  }
		  
  def onMessage(connection:WebSocketConnection, message:Array[Byte]) {
    Logger.debug(connection.toString + " MSG: " + message)
    transaction{ listener(BinaryMsg(connection, message)) }
  }

  def onPing(connection:WebSocketConnection, message:String) {
    Logger.debug(connection.toString + " PING: " + message)
    listener(Ping(connection, message))
  }

  def onPong(connection:WebSocketConnection, message:String) {
    Logger.debug(connection.toString + " PONG: " + message)
    listener(Pong(connection, message))
  }

  def onPing(connection:WebSocketConnection, message:Array[Byte]) {
    Logger.debug(connection.toString + " PING: " + message)
    listener(Ping(connection, new String(message)))
  }

  def onPong(connection:WebSocketConnection, message:Array[Byte]) {
    Logger.debug(connection.toString + " PONG: " + message)
    listener(Pong(connection, new String(message)))
  }
}

case class Open(connection:EngineConnection) extends SocketEvent
case class Close(connection:EngineConnection) extends SocketEvent
case class StringMsg(connection:EngineConnection, relay:String) extends SocketEvent
case class BinaryMsg(connection:EngineConnection, relay:Array[Byte]) extends SocketEvent
case class Pong(connection:EngineConnection, relay:String) extends SocketEvent
case class Ping(connection:EngineConnection, relay:String) extends SocketEvent
