package uk.gov.gds.ier.service

import uk.gov.gds.ier.client.{PlacesApiClient, ApiClient}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import com.google.inject.Inject
import uk.gov.gds.ier.model.Fail
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.model.Address
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.common.model.{GovUkAddress, LocalAuthority}
import uk.gov.gds.ier.exception.PostcodeLookupFailedException
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.logging.Logging
import java.net.ConnectException

class PlacesService @Inject() (apiClient: PlacesApiClient, serialiser: JsonSerialiser, config:Config) extends Logging {
 
  def lookupAddress(partialAddress: PartialAddress):Option[Address] = {
    val listOfAddresses = lookupAddress(partialAddress.postcode)
    listOfAddresses.find(address => address.uprn == partialAddress.uprn)
  }

  def lookupAddress(postcode: String) : List[Address] = {
    val result = apiClient.get((config.placesUrl + "/address?postcode=%s").format(postcode.replaceAllLiterally(" ","").toLowerCase))
    result match {
      case Success(body) => {
        serialiser.fromJson[List[GovUkAddress]](body).map(pa => {
          Address(
            Option(pa.lineOne),
            Option(pa.lineTwo),
            Option(List(pa.lineThree, pa.lineFour, pa.lineFive).filter(!_.isEmpty).mkString(", ")),
            Option(pa.city),
            Option(pa.county),
            pa.uprn,
            pa.postcode)
        })
      }
      case Fail(error) => throw new PostcodeLookupFailedException(error)
    }
  }

  def lookupPartialAddress(postcode:String):List[PartialAddress] = {
    lookupAddress(postcode) map { address => 
      PartialAddress(
        addressLine = Some(formAddressLine(address)),
        uprn = address.uprn,
        postcode = address.postcode,
        None
      )
    }
  }

  def fillAddressLine(partial:PartialAddress):PartialAddress = {
    val line = lookupAddress(partial) map formAddressLine
    partial.copy(addressLine = line)
  }

  protected[service] def formAddressLine(address:Address):String = {
    List(address.lineOne, address.lineTwo, address.lineThree, address.city, address.county)
      .filterNot(line => line.map(_.replaceAllLiterally(" ","")) == Some(""))
      .flatten
      .mkString(", ")
  }

  def lookupAuthority(postcode:String) : Option[LocalAuthority] = {
    val result = apiClient.get((config.placesUrl + "/authority?postcode=%s").format(postcode.replaceAllLiterally(" ","").toLowerCase))
    result match {
      case Success(body) => Some(serialiser.fromJson[LocalAuthority](body))
      case Fail(error) => None
    }
  }

  def beaconFire:Boolean = {
    apiClient.get(config.placesUrl + "/status") match {
      case Success(body) => {
        serialiser.fromJson[Map[String,String]](body).get("status") match {
          case Some("up") => true
          case _ => false
        }
      }
      case Fail(error) => {
        false
      }
    }
  }
}
