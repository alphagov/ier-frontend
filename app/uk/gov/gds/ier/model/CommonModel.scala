package uk.gov.gds.ier.model

import scala.util.Try

case class Stub() {
  def toApiMap = Map.empty
}

case class WaysToVote (waysToVoteType: WaysToVoteType)

sealed case class WaysToVoteType(name:String)
object WaysToVoteType {
  val InPerson = WaysToVoteType("in-person")
  val ByPost = WaysToVoteType("by-post")
  val ByProxy = WaysToVoteType("by-proxy")

  def parse(str: String) = {
    str match {
      case "in-person" => InPerson
      case "by-proxy" => ByProxy
      case "by-post" => ByPost
    }
  }
  def isValid(str: String) = {
    Try{ parse(str) }.isSuccess
  }
}

case class CountryWithCode(
                            country: String,
                            code: String
                            )

case class PostalOrProxyVote (
                               typeVote: WaysToVoteType,
                               postalVoteOption: Option[Boolean],
                               deliveryMethod: Option[PostalVoteDeliveryMethod]) {

  def toApiMap = {
    val voteMap = postalVoteOption match {
      case Some(pvote) => typeVote match {
        case WaysToVoteType.ByPost => Map("pvote" -> pvote.toString)
        case WaysToVoteType.ByProxy => Map("proxyvote" -> pvote.toString)
        case _ => Map.empty
      }
      case _ => Map.empty
    }
    val emailMap = deliveryMethod.flatMap(_.emailAddress) match {
      case Some(email) => typeVote match {
        case WaysToVoteType.ByPost => Map("pvoteemail" -> email)
        case WaysToVoteType.ByProxy => Map("proxyvoteemail" -> email)
        case _ => Map.empty
      }
      case _ => Map.empty
    }
    voteMap ++ emailMap
  }
}

