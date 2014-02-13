package uk.gov.gds.ier.service

import com.google.inject.Inject
import uk.gov.gds.ier.client.IerApiClient
import uk.gov.gds.ier.model._

import uk.gov.gds.ier.model.Fail
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.digest.ShaHashProvider
import org.joda.time.DateTime

abstract class IerApiService {
  def submitOrdinaryApplication(ipAddress: Option[String],
                                applicant: InprogressOrdinary,
                                referenceNumber: Option[String]): ApiApplicationResponse

  def submitOverseasApplication(ip:Option[String],
                                applicant: InprogressOverseas,
                                refNum:Option[String]): ApiApplicationResponse

  def generateReferenceNumber[T <: InprogressApplication[T]](application:T): String
}

class ConcreteIerApiService @Inject() (apiClient: IerApiClient,
                                       serialiser: JsonSerialiser,
                                       config: Config,
                                       placesService:PlacesService,
                                       addressService: AddressService,
                                       shaHashProvider:ShaHashProvider,
                                       isoCountryService: IsoCountryService)
  extends IerApiService
     with Logging {

  def submitOrdinaryApplication(ipAddress: Option[String],
                                applicant: InprogressOrdinary,
                                referenceNumber: Option[String]) = {
    val isoCodes = applicant.nationality map { nationality =>
      isoCountryService.transformToIsoCode(nationality)
    }
    val currentAuthority = applicant.address flatMap { address =>
      placesService.lookupAuthority(address.postcode)
    }
    val previousAuthority = applicant.previousAddress flatMap { prevAddress =>
      prevAddress.previousAddress flatMap { prevAddress =>
        placesService.lookupAuthority(prevAddress.postcode)
      }
    }
    val fullCurrentAddress = addressService.formFullAddress(applicant.address)
    val fullPreviousAddress = applicant.previousAddress flatMap { prevAddress =>
      addressService.formFullAddress(prevAddress.previousAddress)
    }

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
      postalVote = applicant.postalVote,
      contact = applicant.contact,
      referenceNumber = referenceNumber,
      authority = currentAuthority,
      previousAuthority = previousAuthority,
      ip = ipAddress
    )

    val apiApplicant = ApiApplication(completeApplication.toApiMap)

    sendApplication(apiApplicant)
  }

  def submitOverseasApplication(ip:Option[String],
                                applicant: InprogressOverseas,
                                refNum:Option[String]) = {

    val fullLastUkRegAddress = addressService.formFullAddress(applicant.lastUkAddress)
    val currentAuthority = applicant.lastUkAddress flatMap { address =>
      placesService.lookupAuthority(address.postcode)
    }

    val completeApplication = OverseasApplication(
      name = applicant.name,
      previousName = applicant.previousName,
      previouslyRegistered = applicant.previouslyRegistered,
      dateLeftSpecial = applicant.dateLeftSpecial,
      dateLeftUk = applicant.dateLeftUk,
      lastRegisteredToVote = applicant.lastRegisteredToVote,
      dob = applicant.dob,
      nino = applicant.nino,
      lastUkAddress = fullLastUkRegAddress,
      address = applicant.address,
      openRegisterOptin = applicant.openRegisterOptin,
      waysToVote = applicant.waysToVote,
      postalOrProxyVote = applicant.postalOrProxyVote,
      contact = applicant.contact,
      referenceNumber = refNum,
      authority = currentAuthority
    )

    val apiApplicant = ApiApplication(completeApplication.toApiMap)
    sendApplication(apiApplicant)
  }



  private def sendApplication(application: ApiApplication) = {
    apiClient.post(config.ierApiUrl,
                   serialiser.toJson(application),
                   ("Authorization", "BEARER " + config.ierApiToken)) match {
      case Success(body) => serialiser.fromJson[ApiApplicationResponse](body)
      case Fail(error) => {
        logger.error("Submitting application to api failed: " + error)
        throw new ApiException(error)
      }
    }
  }

  def generateReferenceNumber[T <: InprogressApplication[T]](application:T) = {
    val json = serialiser.toJson(application)
    val encryptedBytes = shaHashProvider.getHash(json, Some(DateTime.now.toString))
    val encryptedHex = encryptedBytes.map{ byte => "%02X" format byte }
    encryptedHex.take(3).mkString
  }
}


