package uk.gov.gds.ier.service

import uk.gov.gds.ier.client.PlacesApiClient
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

class PlacesService @Inject() (apiClient: PlacesApiClient, serialiser: JsonSerialiser, config:Config) extends Logging {
 
  def lookupAuthority(postcode:String) : Option[LocalAuthority] = {
    val result = apiClient.get((config.placesUrl + "/authority?postcode=%s").format(postcode.replaceAllLiterally(" ","").toLowerCase))
    result match {
      case Success(body,_) => Some(serialiser.fromJson[LocalAuthority](body))
      case Fail(error,_) => None
    }
  }

  def beaconFire:Boolean = {
    apiClient.get(config.placesUrl + "/status") match {
      case Success(body,_) => {
        serialiser.fromJson[Map[String,String]](body).get("status") match {
          case Some("up") => true
          case _ => false
        }
      }
      case Fail(error,_) => {
        false
      }
    }
  }
}
