package uk.gov.gds.ier.service

import uk.gov.gds.ier.client.{LocateApiClient, ApiClient}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import com.google.inject.Inject
import uk.gov.gds.ier.model.{Fail, Success, Address, PartialAddress,
  LocateAuthority
}
import uk.gov.gds.common.model.{GovUkAddress, LocalAuthority}
import uk.gov.gds.ier.exception.{GssCodeLookupFailedException, PostcodeLookupFailedException}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.logging.Logging
import java.net.ConnectException
import uk.gov.gds.ier.model.LocateAddress
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.Logger

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
    val result = apiClient.get((partialAddressLookupUrl + "?postcode=%s").format(postcode.replaceAllLiterally(" ","").toLowerCase),
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
    val cleanPostcode = postcode.replaceAllLiterally(" ", "").toLowerCase
    val result = apiClient.get(
      s"$partialAuthorityUrl?postcode=$cleanPostcode",
      ("Authorization", authorizationToken)
    )

    result match {
      case Success(body, _) => Some(serialiser.fromJson[LocateAuthority](body))
      case Fail(error, _) => None
    }
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
