package uk.gov.gds.ier.service

import com.google.inject.Inject
import uk.gov.gds.ier.client.{IerApiClient, ApiClient}
import uk.gov.gds.ier.model._

import uk.gov.gds.ier.model.Fail
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.digest.ShaHashProvider
import org.joda.time.DateTime

abstract class IerApiService {
  def submitApplication(ipAddress: Option[String], applicant: InprogressApplication, referenceNumber: Option[String]): ApiApplicationResponse
  def generateReferenceNumber(application:InprogressApplication): String
}

class ConcreteIerApiService @Inject() (apiClient: IerApiClient,
                                       serialiser: JsonSerialiser,
                                       config: Config,
                                       placesService:PlacesService,
                                       shaHashProvider:ShaHashProvider,
                                       isoCountryService: IsoCountryService) extends IerApiService with Logging {

  def submitApplication(ipAddress: Option[String], applicant: InprogressApplication, referenceNumber: Option[String]) = {
    val authority = applicant.address.map(address => placesService.lookupAuthority(address.postcode)).getOrElse(None)

    val completeApplication = applicant.toApiMap ++
      referenceNumber.map(refNum => Map("refNum" -> refNum)).getOrElse(Map.empty) ++
      authority.map(auth => Map("gssCode" -> auth.gssId)).getOrElse(Map.empty)  ++
      ipAddress.map(ipAddress => Map("webIpAddress" -> ipAddress)).getOrElse(Map.empty)

    val apiApplicant = ApiApplication(completeApplication)

    apiClient.post(config.ierApiUrl, serialiser.toJson(apiApplicant), ("Authorization", "BEARER " + config.ierApiToken)) match {
      case Success(body) => serialiser.fromJson[ApiApplicationResponse](body)
      case Fail(error) => {
        logger.error("Submitting application to api failed: " + error)
        throw new ApiException(error)
      }
    }
  }

  def generateReferenceNumber(application:InprogressApplication) = {
    val json = serialiser.toJson(application.toApiMap)
    shaHashProvider.getHash(json, Some(DateTime.now.toString)).map("%02X" format _).take(3).mkString
  }
}


