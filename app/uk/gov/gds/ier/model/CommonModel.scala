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

case class ContactAddress(
  country: Option[String],
  addressLine1: Option[String],
  addressLine2: Option[String],
  addressLine3: Option[String],
  addressLine4: Option[String],
  addressLine5: Option[String]) {
  def toApiMap =
    Map("corrcountry" -> country.getOrElse("")) ++
      addressLine1.map(addressLine => Map("corraddressline1" -> addressLine.toString)).getOrElse(Map.empty) ++
      addressLine2.map(addressLine => Map("corraddressline2" -> addressLine.toString)).getOrElse(Map.empty) ++
      addressLine3.map(addressLine => Map("corraddressline3" -> addressLine.toString)).getOrElse(Map.empty) ++
      addressLine4.map(addressLine => Map("corraddressline4" -> addressLine.toString)).getOrElse(Map.empty) ++
      addressLine5.map(addressLine => Map("corraddressline5" -> addressLine.toString)).getOrElse(Map.empty)
}

