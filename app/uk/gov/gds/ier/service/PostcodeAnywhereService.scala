package uk.gov.gds.ier.service

import uk.gov.gds.ier.client.ApiClient
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.JsonSerialiser
import com.google.inject.Inject
import uk.gov.gds.ier.model.Fail
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.model.Address
import uk.gov.gds.common.model.{PAAddress, PAList}
import uk.gov.gds.common.http.ApiResponseException
import uk.gov.gds.ier.exception.PostcodeLookupFailedException

class PostcodeAnywhereService @Inject() (apiClient: ApiClient, serialiser: JsonSerialiser) {

  def lookup(postcode: String) : List[Address] = {
    val result = apiClient.get("http://services.postcodeanywhere.co.uk/PostcodeAnywhere/Interactive/RetrieveByPostcodeAndBuilding/v1.30/json3.ws?key=FD18-ZK26-TZ22-GK98&postcode=" + postcode)
    result match {
      case Success(body) => {
        serialiser.fromJson[PAList](body).Items.map(pa => {
          Address(List(pa.Line1, pa.Line2, pa.Line3, pa.Line4, pa.Line5, pa.PostTown, pa.County).filterNot(_ == "").mkString(", "), pa.Postcode)
        })
      }
      case Fail(error) => throw new PostcodeLookupFailedException(error)
    }
  }
}
