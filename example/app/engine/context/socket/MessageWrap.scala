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

import play.api.Logger
import play.api.libs.json

import com.codahale.jerkson.Json._

import engine.socket._
import engine.socket.msg._

trait MessageWrap

object MessageWrap {
  
  val dataBoundary = "--PAYLOADDATABOUNDARY--"
  val varBoundary = "--PAYLOADVAR--"

  /**
   * cast the model used for browser contexts
   */
  implicit def castUserModel(users: List[(Option[models.User], List[(models.Job, Option[models.Client], Option[models.Project])])], teamId: Long) = users.map{
    (user) => 
      try {
	      List(TeamUser(
	        user._1.map(_.id).getOrElse(0),
	        user._1.map(_.firstName).getOrElse("Pending"),
	        user._1.map(_.firstAndInitial).getOrElse("Pending"),
	        user._1.map(_.email).getOrElse(""),
	        user._1.map(_.emailhash).getOrElse(""),
	        models.User.isTeamAdmin(user._1.map(_.id).getOrElse(0), Some(teamId)), 
	        castJobModel( user._2 ), 
	        user._2.length))
      } catch {
        case e =>
          List()
      }
  }.flatten
  
  /**
   * cast the job model used per user in browser contexts
   */
  implicit def castJobModel(jobs:List[Tuple3[ models.Job, Option[models.Client], Option[models.Project] ]]) = jobs.map{
    (job) => 
      List(TeamJob(
        job._1.id, 
        job._1.startTimeMillis,
        job._1.name, 
        job._1.startTimePretty, 
        job._1.endTimePretty,
        job._1.startAddress, 
        job._1.startCity, 
        job._1.startProvince, 
        job._1.started, 
        job._1.completed,
        job._1.canceled, 
        job._1.hold, 
        job._1.startLat.getOrElse(""), 
        job._1.startLng.getOrElse(""),
        job._1.userId, 
        job._1.clientId, 
        job._1.comments,
        job._1.dominantStatus,
        job._2.map(_.name).getOrElse(""),
        job._2.map(_.phone).getOrElse(Some(""))))
  }.flatten.sortBy(_.sortIndex).take(50)
  
  /**
   * cast the model used for user drafts
   */
  implicit def castDraftModel(drafts:List[Tuple2[models.ReportInstance,Option[models.Report]]]) = drafts.map{
    (draft) => 
      List(Draft(
        draft._1.id, 
        draft._1.teamId, 
        draft._2.map(_.name).getOrElse(""), 
        draft._1.lastModifiedRelative, 
        draft._2.map(_.id).getOrElse(0L), 
        draft._1.referenceType.getOrElse(""), 
        draft._1.referenceId, 
        draft._1.referenceName( draft._1.referenceType ).getOrElse("General") ))
  }.flatten.sortBy(_.id)
  
  /**
   * cast the model used for user chats
   */
  implicit def castChatModel(chats:List[models.Chat]) = chats.map{
    (chat) => 
      List(Chat(
        chat.id, 
        chat.teamId, 
        chat.userId, 
        chat.userHash, 
        chat.userName, 
        chat.content, 
        chat.lat, 
        chat.lng, 
        chat.announce))
  }.flatten.sortBy(_.id)

  /**
   * cast job info used on per-user mobile contexts
   */
  implicit def castJobInfo(jobs:List[models.Job]) = jobs.map{
    (job) =>  
      List(JobInfo( 
        job.name, 
        job.startTimeMillis,
        job.clientName, 
        job.startNoYear, 
        job.endNoYear, 
        job.id, 
        job.started, 
        job.completed, 
        job.hold,
        job.startAddress, 
        job.startCity, "", 
        job.comments, 
        job.startLat.getOrElse(""), 
        job.startLng.getOrElse(""), 
        job.teamId ))
  }.flatten.sortBy(_.sortIndex)
  
  /**
   * cast the model used for chats in browser contexts
   */
  implicit def castTeamChatModel(teamChats:Set[Long]) = teamChats.toList.map{
    (teamId) => 
      List(TeamChatModel(
        teamId, 
        models.Team.getName(teamId), 
        castChatModel( models.Chat.listTeamChats(teamId,0,10) )))
  }.flatten.sortBy(_.teamName.toLowerCase)

  /**
   * generate the string data used in message transports
   */
  implicit def generateMsgString(data:MessageWrap):String = {
    generate(Map(
    	"relay" -> data.getClass.getSimpleName,
      "data" -> data
    ))
  }

  /**
   * convert a list of data to JSON
   */
  implicit def castListString(data:List[String]) = {
    generate(data)
  }

}
  

// Relay Utilities
case class MsgRaw(relay: String, relayId: Option[String], data: AnyRef) extends MessageWrap
case class MsgReceipt(relayId: String, status: Boolean) extends MessageWrap
case class MsgError(message: String) extends MessageWrap



