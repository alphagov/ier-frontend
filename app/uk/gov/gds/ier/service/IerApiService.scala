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
  def submitApplication(ipAddress: Option[String], applicant: InprogressOrdinary, referenceNumber: Option[String]): ApiApplicationResponse
  def generateReferenceNumber(application:InprogressOrdinary): String
}

class ConcreteIerApiService @Inject() (apiClient: IerApiClient,
                                       serialiser: JsonSerialiser,
                                       config: Config,
                                       placesService:PlacesService,
                                       addressService: AddressService,
                                       shaHashProvider:ShaHashProvider,
                                       isoCountryService: IsoCountryService) extends IerApiService with Logging {

  def submitApplication(ipAddress: Option[String], applicant: InprogressOrdinary, referenceNumber: Option[String]) = {
    val isoCodes = applicant.nationality.map(nationality => isoCountryService.transformToIsoCode(nationality))
    val currentAuthority = applicant.address.flatMap(address => placesService.lookupAuthority(address.postcode))
    val previousAuthority = applicant.previousAddress.flatMap(_.previousAddress).flatMap(prevAddress => placesService.lookupAuthority(prevAddress.postcode))
    val fullCurrentAddress = addressService.formFullAddress(applicant.address)
    val fullPreviousAddress = addressService.formFullAddress(applicant.previousAddress.flatMap(_.previousAddress))

    val completeApplication = OrdinaryApplication(
      name = applicant.name,
      previousName = applicant.previousName,
      dob = applicant.dob,
      nationality = isoCodes,
      nino = applicant.nino,
      address = fullCurrentAddress,
      previousAddress = fullPreviousAddress,
      otherAddress = applicant.otherAddress,
      openRegisterOptin = applicant.openRegisterOptin,
      postalVoteOptin = applicant.postalVoteOptin,
      contact = applicant.contact,
      referenceNumber = referenceNumber,
      authority = currentAuthority,
      previousAuthority = previousAuthority,
      ip = ipAddress
    )

    val apiApplicant = ApiApplication(completeApplication.toApiMap)

    apiClient.post(config.ierApiUrl, serialiser.toJson(apiApplicant), ("Authorization", "BEARER " + config.ierApiToken)) match {
      case Success(body) => serialiser.fromJson[ApiApplicationResponse](body)
      case Fail(error) => {
        logger.error("Submitting application to api failed: " + error)
        throw new ApiException(error)
      }
    }
  }

  def generateReferenceNumber(application:InprogressOrdinary) = {
    val json = serialiser.toJson(application)
    shaHashProvider.getHash(json, Some(DateTime.now.toString)).map("%02X" format _).take(3).mkString
  }
}


