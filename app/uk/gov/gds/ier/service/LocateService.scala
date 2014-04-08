package uk.gov.gds.ier.service

import uk.gov.gds.ier.client.{LocateApiClient, ApiClient}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import com.google.inject.Inject
import uk.gov.gds.ier.model.Fail
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.model.Address
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.common.model.{GovUkAddress, LocalAuthority}
import uk.gov.gds.ier.exception.{GssCodeLookupFailedException, PostcodeLookupFailedException}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.logging.Logging
import java.net.ConnectException
import uk.gov.gds.ier.model.LocateAddress
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.Logger

class LocateService @Inject() (apiClient: LocateApiClient, serialiser: JsonSerialiser, config:Config) extends Logging {

  lazy val partialLocateUrl = config.locateUrl
  lazy val partialAddressLookupUrl = config.locateUrl + "?residentialOnly=false"
  lazy val authorizationToken = config.locateApiAuthorizationToken


  def lookupAddress(partialAddress: PartialAddress):Option[Address] = {
    val listOfAddresses = lookupAddress(partialAddress.postcode)
    listOfAddresses.find(address => address.uprn == partialAddress.uprn)
  }

  def lookupAddress(postcode: String) : List[Address] = {
    val result = apiClient.get((partialAddressLookupUrl + "&postcode=%s").format(postcode.replaceAllLiterally(" ","").toLowerCase),
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
            postcode = pa.postcode)
        })
      }
      case Fail(error, _) => throw new PostcodeLookupFailedException(error)
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