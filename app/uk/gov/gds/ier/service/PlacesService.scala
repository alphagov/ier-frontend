package uk.gov.gds.ier.service

import uk.gov.gds.ier.client.ApiClient
import uk.gov.gds.ier.serialiser.JsonSerialiser
import com.google.inject.Inject
import uk.gov.gds.ier.model.Fail
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.model.Address
import uk.gov.gds.common.model.{GovUkAddress, LocalAuthority}
import uk.gov.gds.ier.exception.PostcodeLookupFailedException
import uk.gov.gds.ier.config.Config

class PlacesService @Inject() (apiClient: ApiClient, serialiser: JsonSerialiser, config:Config) {

  def lookupAddress(postcode: String) : List[Address] = {
    val result = apiClient.get((config.placesUrl + "/address?postcode=%s").format(postcode))
    result match {
      case Success(body) => {
        serialiser.fromJson[List[GovUkAddress]](body).map(pa => {
          Address(List(pa.lineOne, pa.lineTwo, pa.lineThree, pa.lineFour, pa.lineFive, pa.city, pa.county).filterNot(_.isEmpty).mkString(", "), pa.postcode)
        })
      }
      case Fail(error) => throw new PostcodeLookupFailedException(error)
    }
  }

  def lookupAuthority(postcode:String) : Option[LocalAuthority] = {
    val result = apiClient.get((config.placesUrl + "/authority?postcode=%s").format(postcode.replaceAllLiterally(" ","").toLowerCase))
    result match {
      case Success(body) => Some(serialiser.fromJson[LocalAuthority](body))
      case Fail(error) => None
    }
  }
}
