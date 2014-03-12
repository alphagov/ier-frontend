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
                               deliveryMethod: Option[PostalVoteDeliveryMethod],
                               forceRedirectToPostal: Boolean = false) {

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
    postcode: Option[String],
    addressLine1: Option[String],
    addressLine2: Option[String],
    addressLine3: Option[String],
    addressLine4: Option[String],
    addressLine5: Option[String] ) {

  def toApiMap =
    country.map(country => Map("corrcountry" -> country.toString)).getOrElse(Map.empty) ++
      postcode.map(postcode => Map("corrpostcode" -> postcode.toString)).getOrElse(Map.empty) ++
      addressLine1.map(addressLine => Map("corraddressline1" -> addressLine.toString)).getOrElse(Map.empty) ++
      addressLine2.map(addressLine => Map("corraddressline2" -> addressLine.toString)).getOrElse(Map.empty) ++
      addressLine3.map(addressLine => Map("corraddressline3" -> addressLine.toString)).getOrElse(Map.empty) ++
      addressLine4.map(addressLine => Map("corraddressline4" -> addressLine.toString)).getOrElse(Map.empty) ++
      addressLine5.map(addressLine => Map("corraddressline5" -> addressLine.toString)).getOrElse(Map.empty)

}

case class PossibleContactAddresses(
  contactAddressType: Option[String],
  ukAddressLine: Option[String],
  bfpoContactAddress: Option[ContactAddress],
  otherContactAddress: Option[ContactAddress] ) {

  def toApiMap = contactAddressType match {
    case Some("uk") => Map.empty
    case Some("bfpo") => bfpoContactAddress.get.toApiMap
    case Some("other") => otherContactAddress.get.toApiMap
    case _ => throw new IllegalArgumentException
  }

  def toApiMapFromUkAddress (address: Option[Address]) = {
    if (address.isDefined) {
      val ukAddress = address.get
      Map("corrcountry" -> "uk") ++
      Map("corrpostcode" -> ukAddress.postcode) ++
      ukAddress.lineOne.map(lineOne => Map("corraddressline1" -> lineOne.toString)).getOrElse(Map.empty) ++
      ukAddress.lineTwo.map(lineTwo => Map("corraddressline2" -> lineTwo.toString)).getOrElse(Map.empty) ++
      ukAddress.lineThree.map(lineThree => Map("corraddressline3" -> lineThree.toString)).getOrElse(Map.empty) ++
      ukAddress.city.map(city => Map("corraddressline4" -> city.toString)).getOrElse(Map.empty) ++
      ukAddress.county.map(county => Map("corraddressline5" -> county.toString)).getOrElse(Map.empty)
    }
    else Map.empty
  }
}

