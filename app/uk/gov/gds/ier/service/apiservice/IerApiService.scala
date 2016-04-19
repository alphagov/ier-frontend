package uk.gov.gds.ier.service.apiservice

import java.lang.System._

import com.google.inject.Inject
import uk.gov.gds.ier.client.{StatsdClient, IerApiClient}
import uk.gov.gds.ier.model.HasAddressOption._
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
import play.api.libs.json.Json
import uk.gov.gds.ier.model.LocalAuthority
import uk.gov.gds.ier.langs.Language

abstract class IerApiService {
  def submitOrdinaryApplication(
      ipAddress: Option[String],
      applicant: InprogressOrdinary,
      referenceNumber: Option[String],
      timeTaken: Option[String],
      language: String
  ): IerApiApplicationResponse

  def submitOverseasApplication(
      ip: Option[String],
      applicant: InprogressOverseas,
      refNum: Option[String],
      timeTaken: Option[String]
  ): IerApiApplicationResponse

  def submitForcesApplication (
      ip: Option[String],
      applicant: InprogressForces,
      refNum: Option[String],
      timeTaken: Option[String]
  ): IerApiApplicationResponse

  def submitCrownApplication (
      ip: Option[String],
      applicant: InprogressCrown,
      refNum: Option[String],
      timeTaken: Option[String]
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
      referenceNumber: Option[String],
      timeTaken: Option[String],
      language: String
  ) = {

    val isoCodes = applicant.nationality map { nationality =>
      isoCountryService.transformToIsoCode(nationality)
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
      ip = ipAddress,
      timeTaken = timeTaken.getOrElse("-1"),
      language = Language.emailLang,
      sessionId = applicant.sessionId.getOrElse("")
    )

    val apiApplicant = ApiApplication(completeApplication.toApiMap)

    sendApplication(apiApplicant)
  }

  def submitOverseasApplication(
      ip:Option[String],
      applicant: InprogressOverseas,
      refNum:Option[String],
      timeTaken: Option[String]
  ) = {

    val fullLastUkRegAddress = addressService.formFullAddress(applicant.lastUkAddress)

    val fullParentRegAddress = addressService.formFullAddress(applicant.parentsAddress)

    val completeApplication = OverseasApplication(
      name = applicant.name,
      previousName = applicant.previousName,
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
      ip = ip,
      timeTaken = timeTaken.getOrElse("-1"),
      sessionId = applicant.sessionId.getOrElse("")
    )

    val apiApplicant = ApiApplication(completeApplication.toApiMap)

    sendApplication(apiApplicant)
  }

  def submitForcesApplication(
      ipAddress: Option[String],
      applicant: InprogressForces,
      referenceNumber: Option[String],
      timeTaken: Option[String]
  ) = {

    val isoCodes = applicant.nationality map { nationality =>
      isoCountryService.transformToIsoCode(nationality)
    }

    val fullCurrentAddress = applicant.address flatMap { lastUkAddress =>
      addressService.formFullAddress(lastUkAddress.address)
    }

    val residentType = applicant.address flatMap { lastUkAddress =>
      lastUkAddress.hasAddress.flatMap {
        case `YesAndLivingThere` => Some("resident")
        case `YesAndNotLivingThere` => Some("not-resident")
        case `No` => Some("no-connection")
        case _ => None
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
      ip = ipAddress,
      timeTaken = timeTaken.getOrElse("-1"),
      sessionId = applicant.sessionId.getOrElse(""),
      ukAddr = residentType
    )

    val apiApplicant = ApiApplication(completeApplication.toApiMap)

    sendApplication(apiApplicant)
  }

  def submitCrownApplication(
      ipAddress: Option[String],
      applicant: InprogressCrown,
      referenceNumber: Option[String],
      timeTaken: Option[String]
  ) = {

    val isoCodes = applicant.nationality map { nationality =>
      isoCountryService.transformToIsoCode(nationality)
    }

    val fullCurrentAddress = applicant.address flatMap { lastUkAddress =>
      addressService.formFullAddress(lastUkAddress.address)
    }

    val residentType = applicant.address flatMap { lastUkAddress =>
      lastUkAddress.hasAddress.flatMap {
        case `YesAndLivingThere` => Some("resident")
        case `YesAndNotLivingThere` => Some("not-resident")
        case `No` => Some("no-connection")
        case _ => None
      }
    }

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
      ip = ipAddress,
      timeTaken = timeTaken.getOrElse("-1"),
      sessionId = applicant.sessionId.getOrElse(""),
      ukAddr = residentType
    )

    val apiApplicant = ApiApplication(completeApplication.toApiMap)

    sendApplication(apiApplicant)
  }

  def getLocalAuthorityByGssCode(gssCode: String): LocalAuthority = {
    apiClient.get(config.ierLocalAuthorityLookupUrl + gssCode,
                   ("Authorization", "BEARER " + config.ierApiToken)) match {
      case Success(body,timeTakenMs) => {
        serialiser.fromJson[LocalAuthority](body)
      }
      case Fail(error,timeTakenMs) => {
        logger.error("Local Authority lookup failed: " + error)
        throw new ApiException(error)
      }
    }
  }

  private def sendApplication(application: ApiApplication) = {
    val applicationType = application.application.get("applicationType").getOrElse("")
    apiClient.post(config.ierApiUrl,
                   serialiser.toJson(application),
                   ("Authorization", "BEARER " + config.ierApiToken)) match {
      case Success(body,timeTakenMs) => {
        serialiser.fromJson[IerApiApplicationResponse](body)
      }
      case Fail(error,timeTakenMs) => {
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
    val currentTime = currentTimeMillis()
    val badWords = List(
      "4R5E",
      "5H1T",
      "5HIT",
      "A55",
      "ANAL",
      "ANUS",
      "AR5E",
      "ARRSE",
      "ARSE",
      "ASS",
      "B00BS",
      "B17CH",
      "B1TCH",
      "BALLBAG",
      "BALLS",
      "BASTARD",
      "BEASTIAL",
      "BELLEND",
      "BESTIAL",
      "BIATCH",
      "BITCH",
      "BLOODY",
      "BLOWJOB",
      "BOIOLAS",
      "BOLLOCK",
      "BOLLOK",
      "BONER",
      "BOOB",
      "BOOBS",
      "BOOOBS",
      "BOOOOBS",
      "BOOOOOBS",
      "BREASTS",
      "BUCETA",
      "BUGGER",
      "BUM",
      "BUTT",
      "C0CK",
      "CAWK",
      "CHINK",
      "CIPA",
      "CL1T",
      "CLIT",
      "CNUT",
      "COCK",
      "COON",
      "CRAP",
      "CUM",
      "CUNT",
      "CYALIS",
      "CYBERFUC",
      "D1CK",
      "DAMN",
      "DICK",
      "DILDO",
      "DINK",
      "DIRSA",
      "DLCK",
      "DOGGIN",
      "DOOSH",
      "DUCHE",
      "DYKE",
      "F4NNY",
      "FAG",
      "FANNY",
      "FANYY",
      "FATASS",
      "FCUK",
      "FECK",
      "FELLATE",
      "FELLATIO",
      "FISTFUCK",
      "FLANGE",
      "FOOK",
      "FUCK",
      "FUK",
      "FUX",
      "GANGBANG",
      "GAYLORD",
      "GAYSEX",
      "GOD",
      "HELL",
      "HESHE",
      "HOAR",
      "HOER",
      "HOMO",
      "HORE",
      "HORNIEST",
      "HORNY",
      "HOTSEX",
      "JACKOFF",
      "JAP",
      "JISM",
      "JIZZ",
      "KAWK",
      "KNOB",
      "KOCK",
      "KONDUM",
      "KUM",
      "L3ITCH",
      "LABIA",
      "LMFAO",
      "LUST",
      "LUSTING",
      "M0F0",
      "M0FO",
      "MA5TERB8",
      "MASTERB8",
      "MOF0",
      "MOFO",
      "MUFF",
      "MUTHA",
      "MUTHER",
      "N1GGA",
      "N1GGER",
      "NAZI",
      "NIGG3R",
      "NIGG4H",
      "NIGGA",
      "NIGGAH",
      "NIGGAS",
      "NIGGAZ",
      "NIGGER",
      "NOBHEAD",
      "NOBJOCKY",
      "NOBJOKEY",
      "NUMBNUTS",
      "NUTSACK",
      "ORGASIM",
      "ORGASIMS",
      "ORGASM",
      "ORGASMS",
      "P0RN",
      "PAWN",
      "PECKER",
      "PENIS",
      "PHONESEX",
      "PHUCK",
      "PHUK",
      "PHUQ",
      "PIMPIS",
      "PISS",
      "POOP",
      "PORN",
      "PRICK",
      "PRON",
      "PUBE",
      "PUSSE",
      "PUSSI",
      "PUSSY",
      "RECTUM",
      "RETARD",
      "RIMJAW",
      "RIMMING",
      "SHIT",
      "SADIST",
      "SCHLONG",
      "SCREWING",
      "SCROAT",
      "SCROTE",
      "SCROTUM",
      "SEMEN",
      "SEX",
      "SH!T",
      "SH1T",
      "SHAG",
      "SHAGGER",
      "SHAGGIN",
      "SHEMALE",
      "SHIT",
      "SKANK",
      "SLUT",
      "SMEGMA",
      "SMUT",
      "SNATCH",
      "SPAC",
      "SPUNK",
      "T1TT1E5",
      "T1TTIES",
      "TEETS",
      "TEEZ",
      "TESTICAL",
      "TESTICLE",
      "TIT",
      "TOSSER",
      "TURD",
      "TW4T",
      "TWAT",
      "TWUNT",
      "V14GRA",
      "V1GRA",
      "VAGINA",
      "VIAGRA",
      "VULVA",
      "W00SE",
      "WANG",
      "WANK",
      "WHOAR",
      "WHORE",
      "WILLIES",
      "WILLY",
      "XRATED",
      "XXX"
    )
    var refNumber: String = java.lang.Long.toString(currentTime, 36).toUpperCase
    while (badWords.exists(refNumber.contains)){
      refNumber = java.lang.Long.toString(currentTime, 36).toUpperCase
    }
    refNumber.mkString
  }
}


