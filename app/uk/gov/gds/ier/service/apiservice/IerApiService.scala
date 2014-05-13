package uk.gov.gds.ier.service.apiservice

import com.google.inject.Inject
import uk.gov.gds.ier.client.{StatsdClient, IerApiClient}
import uk.gov.gds.ier.model.{
  LastRegisteredToVote,
  LastRegisteredType,
  MovedHouseOption,
  Fail,
  Success}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.digest.ShaHashProvider
import org.joda.time.DateTime
import uk.gov.gds.ier.service._
import uk.gov.gds.ier.step.InprogressApplication
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

abstract class IerApiService {
  def submitOrdinaryApplication(
      ipAddress: Option[String],
      applicant: InprogressOrdinary,
      referenceNumber: Option[String]
  ): IerApiApplicationResponse

  def submitOverseasApplication(
      ip: Option[String],
      applicant: InprogressOverseas,
      refNum: Option[String]
  ): IerApiApplicationResponse

  def submitForcesApplication (
      ip: Option[String],
      applicant: InprogressForces,
      refNum: Option[String]
  ): IerApiApplicationResponse

  def submitCrownApplication (
      ip: Option[String],
      applicant: InprogressCrown,
      refNum: Option[String]
  ): IerApiApplicationResponse

  def generateOrdinaryReferenceNumber(application: InprogressOrdinary): String
  def generateOverseasReferenceNumber(application: InprogressOverseas): String
  def generateForcesReferenceNumber(application: InprogressForces): String
  def generateCrownReferenceNumber(application: InprogressCrown): String
}

class ConcreteIerApiService @Inject() (
    apiClient: IerApiClient,
    serialiser: JsonSerialiser,
    config: Config,
    addressService: AddressService,
    shaHashProvider:ShaHashProvider,
    isoCountryService: IsoCountryService
  ) extends IerApiService with Logging {

  def submitOrdinaryApplication(
      ipAddress: Option[String],
      applicant: InprogressOrdinary,
      referenceNumber: Option[String]) = {

    val isoCodes = applicant.nationality map { nationality =>
      isoCountryService.transformToIsoCode(nationality)
    }
    val currAddrGssCode = applicant.address flatMap { address =>
      addressService.gssCodeFor(address)
    }
    val prevAddrGssCode = applicant.previousAddress flatMap { prevAddress =>
      prevAddress.previousAddress flatMap { prevAddress =>
        addressService.gssCodeFor(prevAddress)
      }
    }
    val fullCurrentAddress = addressService.formFullAddress(applicant.address)
    val fullPreviousAddress = applicant.previousAddress flatMap { prevAddress =>
      addressService.formFullAddress(prevAddress.previousAddress)
    }

    val lastRegistered = applicant.previousAddress.flatMap(_.movedRecently) match {
      case Some(MovedHouseOption.MovedFromAbroadRegistered) => {
        Some(LastRegisteredToVote(LastRegisteredType.Overseas))
      }
      case _ => None
    }

    val completeApplication = OrdinaryApplication(
      name = applicant.name,
      lastRegisteredToVote = lastRegistered,
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
      authorityGssCode = currAddrGssCode,
      previousAuthorityGssCode = prevAddrGssCode,
      ip = ipAddress
    )

    val apiApplicant = ApiApplication(completeApplication.toApiMap)

    sendApplication(apiApplicant)
  }

  def submitOverseasApplication(ip:Option[String],
                                applicant: InprogressOverseas,
                                refNum:Option[String]) = {

    val fullLastUkRegAddress = addressService.formFullAddress(applicant.lastUkAddress)
    val currentAuthorityGssCode = applicant.lastUkAddress flatMap { address =>
      addressService.gssCodeFor(address)
    }

    val fullParentRegAddress = addressService.formFullAddress(applicant.parentsAddress)
    val currentAuthorityParentsGssCode = applicant.parentsAddress flatMap { address =>
      addressService.gssCodeFor(address)
    }

    val completeApplication = OverseasApplication(
      overseasName = applicant.overseasName,
      dateLeftSpecial = applicant.dateLeftSpecial,
      dateLeftUk = applicant.dateLeftUk,
      overseasParentName = applicant.overseasParentName,
      lastRegisteredToVote = applicant.lastRegisteredToVote,
      dob = applicant.dob,
      nino = applicant.nino,
      lastUkAddress = fullLastUkRegAddress.orElse(fullParentRegAddress),
      address = applicant.address,
      openRegisterOptin = applicant.openRegisterOptin,
      postalOrProxyVote = applicant.postalOrProxyVote,
      passport = applicant.passport,
      contact = applicant.contact,
      referenceNumber = refNum,
      authorityGssCode = currentAuthorityGssCode.orElse(currentAuthorityParentsGssCode),
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
    val currentAuthorityGssCode = applicant.address flatMap { pAddress =>
      pAddress.address.flatMap { address =>
        addressService.gssCodeFor(address)
      }
    }

    val fullCurrentAddress =
      (for (lastUkAddress <- applicant.address)
      yield addressService.formFullAddress(lastUkAddress.address)) flatten

    val previousAuthorityGssCode = applicant.previousAddress flatMap { prevAddress =>
      prevAddress.previousAddress flatMap { address =>
        addressService.gssCodeFor(address)
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
      authorityGssCode = currentAuthorityGssCode,
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

    val currentAuthorityGssCode = applicant.address flatMap { address =>
      address.address.flatMap { address =>
        addressService.gssCodeFor(address)
      }
    }

    val lastUkAddress = applicant.address
    val fullCurrentAddress = if (lastUkAddress.isDefined) {
      addressService.formFullAddress(lastUkAddress.get.address)
    }
    else None


    val fullPreviousAddress = applicant.previousAddress flatMap { prevAddress =>
      addressService.formFullAddress(prevAddress.previousAddress)
    }

    val completeApplication = CrownApplication(
      statement = applicant.statement,
      address = fullCurrentAddress,
      previousAddress = fullPreviousAddress,
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
      authorityGssCode = currentAuthorityGssCode,
      ip = ipAddress
    )

    val apiApplicant = ApiApplication(completeApplication.toApiMap)

    sendApplication(apiApplicant)
  }

  private def sendApplication(application: ApiApplication) = {
    val applicationType = application.application.get("applicationType").getOrElse("")
    apiClient.post(config.ierApiUrl,
                   serialiser.toJson(application),
                   ("Authorization", "BEARER " + config.ierApiToken)) match {
      case Success(body,timeTakenMs) => {
        StatsdClient.timing("submission." + applicationType + ".OK", timeTakenMs)
        serialiser.fromJson[IerApiApplicationResponse](body)
      }
      case Fail(error,timeTakenMs) => {
        logger.error("Submitting application to api failed: " + error)
        StatsdClient.timing("submission." + applicationType + ".Error", timeTakenMs)
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


