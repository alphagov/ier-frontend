package uk.gov.gds.ier.service

import uk.gov.gds.ier.client.ApiClient
import uk.gov.gds.ier.serialiser.JsonSerialiser
import com.google.inject.Inject
import uk.gov.gds.ier.model.Fail
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.model.Address
import uk.gov.gds.common.model.PAList
import uk.gov.gds.ier.exception.PostcodeLookupFailedException
import uk.gov.gds.ier.config.Config

class PostcodeAnywhereService @Inject() (apiClient: ApiClient, serialiser: JsonSerialiser, config:Config) {

  def lookup(postcode: String) : List[Address] = {
    val result = apiClient.get((config.paUrl + "?key=%s&postcode=%s").format(config.paKey, postcode))
    result match {
      case Success(body) => {
        serialiser.fromJson[PAList](body).Items.map(pa => {
          Address(List(pa.Line1, pa.Line2, pa.Line3, pa.Line4, pa.Line5, pa.PostTown, pa.County).filterNot(_.isEmpty).mkString(", "), pa.Postcode)
        })
      }
      case Fail(error) => throw new PostcodeLookupFailedException(error)
    }
  }
}
