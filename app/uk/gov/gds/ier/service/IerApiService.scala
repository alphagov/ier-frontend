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
import uk.gov.gds.common.model.LocalAuthority

abstract class IerApiService {
  def submitOrdinaryApplication(ipAddress: Option[String],
                                applicant: InprogressOrdinary,
                                referenceNumber: Option[String]): ApiApplicationResponse

  def submitOverseasApplication(ip:Option[String],
                                applicant: InprogressOverseas,
                                refNum:Option[String]): ApiApplicationResponse


  def submitForcesApplication (ip:Option[String],
                               applicant: InprogressForces,
                               refNum:Option[String]): ApiApplicationResponse

  def submitCrownApplication (ip:Option[String],
                               applicant: InprogressCrown,
                               refNum:Option[String]): ApiApplicationResponse

  def generateOrdinaryReferenceNumber(application: InprogressOrdinary): String
  def generateOverseasReferenceNumber(application: InprogressOverseas): String
  def generateForcesReferenceNumber(application: InprogressForces): String
  def generateCrownReferenceNumber(application: InprogressCrown): String
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

    val fullParentRegAddress = addressService.formFullAddress(applicant.parentsAddress)
    val currentAuthorityParents = applicant.parentsAddress flatMap { address =>
      placesService.lookupAuthority(address.postcode)
    }

    val completeApplication = OverseasApplication(
      overseasName = applicant.overseasName,
      previouslyRegistered = applicant.previouslyRegistered,
      dateLeftSpecial = applicant.dateLeftSpecial,
      dateLeftUk = applicant.dateLeftUk,
      overseasParentName = applicant.overseasParentName,
      lastRegisteredToVote = applicant.lastRegisteredToVote,
      dob = applicant.dob,
      nino = applicant.nino,
      lastUkAddress = Some(fullLastUkRegAddress.getOrElse(fullParentRegAddress.get)),
      address = applicant.address,
      openRegisterOptin = applicant.openRegisterOptin,
      postalOrProxyVote = applicant.postalOrProxyVote,
      passport = applicant.passport,
      contact = applicant.contact,
      referenceNumber = refNum,
      authority = Some(currentAuthority.getOrElse(currentAuthorityParents.get)),
      ip = ip
    )

    val apiApplicant = ApiApplication(completeApplication.toApiMap)
    sendApplication(apiApplicant)
  }

  def submitForcesApplication(
      ipAddress: Option[String],
      applicant: InprogressForces,
      referenceNumber: Option[String]) = {

    val isoCodes = applicant.nationality map { nationality =>
      isoCountryService.transformToIsoCode(nationality)
    }
    val currentAuthority = applicant.address flatMap { address =>
      placesService.lookupAuthority(address.postcode)
    }
    val fullCurrentAddress = addressService.formFullAddress(applicant.address)

    val previousAuthority = applicant.previousAddress flatMap { prevAddress =>
      prevAddress.previousAddress flatMap { prevAddress =>
        placesService.lookupAuthority(prevAddress.postcode)
      }
    }
    val fullPreviousAddress = applicant.previousAddress flatMap { prevAddress =>
      addressService.formFullAddress(prevAddress.previousAddress)
    }

    val completeApplication = ForcesApplication(
      statement = applicant.statement,
      address = fullCurrentAddress,
      previousAddress = fullPreviousAddress,
      nationality = isoCodes,
      dob = applicant.dob,
      name = applicant.name,
      previousName = applicant.previousName,
      nino = applicant.nino,
      service = applicant.service,
      rank = applicant.rank,
      contactAddress = applicant.contactAddress,
      openRegisterOptin = applicant.openRegisterOptin,
      postalOrProxyVote = applicant.postalOrProxyVote,
      contact = applicant.contact,
      referenceNumber = referenceNumber,
      authority = currentAuthority,
      ip = ipAddress
    )

    val apiApplicant = ApiApplication(completeApplication.toApiMap)

    sendApplication(apiApplicant)
  }

  def submitCrownApplication(
      ipAddress: Option[String],
      applicant: InprogressCrown,
      referenceNumber: Option[String]) = {

    val isoCodes = applicant.nationality map { nationality =>
      isoCountryService.transformToIsoCode(nationality)
    }
    val currentAuthority = applicant.address flatMap { address =>
      if (address.address.isDefined) {
        val realAddress = address.address.get
        placesService.lookupAuthority(realAddress.postcode)
      }
      else None
    }

    val lastUkAddress = applicant.address
    val fullCurrentAddress = if (lastUkAddress.isDefined) {
      addressService.formFullAddress(lastUkAddress.get.address)
    }
    else None


    val completeApplication = CrownApplication(
      statement = applicant.statement,
      address = fullCurrentAddress,
      nationality = isoCodes,
      dob = applicant.dob,
      name = applicant.name,
      previousName = applicant.previousName,
      job = applicant.job,
      nino = applicant.nino,
      contactAddress = applicant.contactAddress,
      openRegisterOptin = applicant.openRegisterOptin,
      postalOrProxyVote = applicant.postalOrProxyVote,
      contact = applicant.contact,
      referenceNumber = referenceNumber,
      authority = currentAuthority,
      ip = ipAddress
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

  def generateOrdinaryReferenceNumber(application: InprogressOrdinary): String = {
    generateReferenceNumber(application)
  }

  def generateOverseasReferenceNumber(application: InprogressOverseas): String = {
    generateReferenceNumber(application)
  }

  def generateForcesReferenceNumber(application: InprogressForces): String = {
    generateReferenceNumber(application)
  }

  def generateCrownReferenceNumber(application: InprogressCrown): String = {
    generateReferenceNumber(application)
  }

  private def generateReferenceNumber[T <: InprogressApplication[T]](application:T) = {
    val json = serialiser.toJson(application)
    val encryptedBytes = shaHashProvider.getHash(json, Some(DateTime.now.toString))
    val encryptedHex = encryptedBytes.map{ byte => "%02X" format byte }
    encryptedHex.take(3).mkString
  }
}


