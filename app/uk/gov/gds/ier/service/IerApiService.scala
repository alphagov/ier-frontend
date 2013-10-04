package uk.gov.gds.ier.service

import com.google.inject.Inject
import uk.gov.gds.ier.client.ApiClient
import uk.gov.gds.ier.model._

import uk.gov.gds.ier.model.Fail
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.JsonSerialiser

class IerApiService @Inject() (apiClient: ApiClient, serialiser: JsonSerialiser) extends ApiUrls with Logging {

  def submitApplication(applicant: InprogressApplication): ApiApplicationResponse = {
    val apiApplicant = CompleteApplication(applicant)
    apiClient.post(submitApplicationUrl, serialiser.toJson(apiApplicant)) match {
      case Success(body) => serialiser.fromJson[ApiApplicationResponse](body)
      case Fail(error) => {
        logger.error("Submitting application to api failed: " + error)
        throw new ApiException(error)
      }
    }
  }
}