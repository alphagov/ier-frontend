package uk.gov.gds.ier.service

import uk.gov.gds.ier.client.LocateApiClient
import uk.gov.gds.ier.serialiser.JsonSerialiser
import com.google.inject.Inject
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.exception.PostcodeLookupFailedException
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.logging.Logging
import play.api.Logger
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.model.Fail
import scala.Some
import uk.gov.gds.ier.model.LocateAuthority
import uk.gov.gds.ier.model.LocateAddress

class LocateService @Inject() (
    apiClient: LocateApiClient,
    serialiser: JsonSerialiser,
    config:Config
) extends Logging {

  lazy val partialLocateUrl = config.locateUrl
  lazy val partialAuthorityUrl = config.locateAuthorityUrl
  lazy val partialAddressLookupUrl = config.locateUrl
  lazy val authorizationToken = config.locateApiAuthorizationToken


  def lookupAddress(partialAddress: PartialAddress):Option[Address] = {
    val listOfAddresses = lookupAddress(partialAddress.postcode)
    listOfAddresses.find(address => address.uprn == partialAddress.uprn)
  }

  def lookupAddress(postcode: String) : List[Address] = {
    val result = apiClient.get((partialAddressLookupUrl + "?postcode=%s").format(Postcode.toCleanFormat(postcode)),
        ("Authorization", authorizationToken))
    result match {
      case Success(body, _) => {
        serialiser.fromJson[List[LocateAddress]](body).map(pa => {
          Address(
            lineOne = pa.property,
            lineTwo = pa.street,
            lineThree = pa.locality,
            city = pa.town,
            county = pa.area,
            uprn = pa.uprn,
            postcode = pa.postcode,
            gssCode = pa.gssCode)
        })
      }
      case Fail(error, _) => throw new PostcodeLookupFailedException(error)
    }
  }

  def lookupAuthority(postcode: String) : Option[LocateAuthority] = {
    val cleanPostcode = Postcode.toCleanFormat(postcode)
    val result = apiClient.get(
      s"$partialAuthorityUrl?postcode=$cleanPostcode",
      ("Authorization", authorizationToken)
    )
    result match {
      case Success(body, _) => Some(serialiser.fromJson[LocateAuthority](body))
      case Fail(error, _) => None
    }
  }

  def lookupGssCode(postcode: String): Option[String] = {
    def gssFromAuthority = {
      lookupAuthority(postcode).map(_.gssCode )
    }
    def gssFromAddress = {
      lookupAddress(postcode).find(_.gssCode.isDefined).flatMap(_.gssCode)
    }

    gssFromAuthority orElse gssFromAddress
  }

  def beaconFire:Boolean = {
    apiClient.get(config.locateUrl + "/status") match {
      case Success(body, _) => {
        serialiser.fromJson[Map[String,String]](body).get("status") match {
          case Some("up") => true
          case _ => {
            Logger.error("The locate api is not available")
            false
          }
        }
      }
      case Fail(error, _) => {
        Logger.error(error)
        false
      }
    }
  }
}
