package uk.gov.gds.ier.service

import com.google.inject.Inject
import uk.gov.gds.ier.client.ApiClient
import uk.gov.gds.ier.model._

import uk.gov.gds.ier.model.Fail
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config

class IerApiService @Inject() (apiClient: ApiClient, serialiser: JsonSerialiser, config: Config, eroService: EroService) extends Logging {

  def submitApplication(applicant: InprogressApplication) {
    val completeApplication = CompleteApplication(applicant)
    val gss = completeApplication.cpost.map(postcode => eroService.lookupGSS(postcode))

    val apiApplicant = ApiApplication(completeApplication.copy(gssCode = gss))

    apiClient.post(config.ierApiUrl, serialiser.toJson(apiApplicant), ("Authorization", "BEARER " + config.ierApiToken)) match {
      case Success(body) => serialiser.fromJson[ApiApplicationResponse](body)
      case Fail(error) => {
        logger.error("Submitting application to api failed: " + error)
        throw new ApiException(error)
      }
    }
  }
}