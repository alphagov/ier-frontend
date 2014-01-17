package uk.gov.gds.ier.test

import play.api.data.Form
import play.api.test.FakeRequest
import play.api.mvc.Cookie
import org.joda.time.DateTime
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.security._
import play.api.mvc.Cookie
import uk.gov.gds.ier.session.{SessionKeys, ResultStoring, ResultHandling, RequestHandling}
import uk.gov.gds.ier.guice.{WithConfig, WithEncryption}
import uk.gov.gds.ier.config.Config

trait TestHelpers {

  val jsonSerialiser = new JsonSerialiser

  implicit class EasyGetErrorMessageError(form: ErrorTransformForm[_]) {
    def errorMessages(key:String) = form.errors(key).map(_.message)
    def globalErrorMessages = form.globalErrors.map(_.message)
    def prettyPrint = form.errors.map(error => s"${error.key} -> ${error.message}")
  }

  implicit class FakeRequestWithOurSessionCookies[A](request: FakeRequest[A]) extends ResultHandling with WithConfig with SessionKeys {
    val config = new Config
    val serialiser = jsonSerialiser
    val encryptionService = new EncryptionService (new AesEncryptionService(new Base64EncodingService), new RsaEncryptionService(new Base64EncodingService))
    val encryptionKeys = new EncryptionKeys(new Base64EncodingService)

    def withIerSession(timeSinceInteraction:Int = 1) = {
      val (encryptedSessionTokenValue, sessionTokenCookieKey) = encryptionService.encrypt(DateTime.now.minusMinutes(timeSinceInteraction).toString(), encryptionKeys.cookies.getPublic)
      request.withCookies(
        createSecureCookie(sessionTokenKey, encryptedSessionTokenValue.filter(_ >= ' ')),
        createSecureCookie(sessionTokenCookieKeyParam, sessionTokenCookieKey.filter(_ >= ' ')))
    }

    def withInvalidSession() = withIerSession(6)

    def withApplication[T <: InprogressApplication[T]](application: T) = {
      val (encryptedSessionPayloadValue, payloadCookieKey) = encryptionService.encrypt(serialiser.toJson(application), encryptionKeys.cookies.getPublic)
      request.withCookies(
        createSecureCookie(sessionPayloadKey, encryptedSessionPayloadValue.filter(_ >= ' ')),
        createSecureCookie(payloadCookieKeyParam, payloadCookieKey.filter(_ >= ' ')))
    }
  }

  lazy val completeOrdinaryApplication = InprogressOrdinary(
    name = Some(Name("John", None, "Smith")), 
    previousName = Some(PreviousName(false, None)), 
    dob = Some(DateOfBirth(Some(DOB(1988, 1, 1)), None)),
    nationality = Some(PartialNationality(Some(true), None, None, List.empty, None)),
    nino = Some(Nino(Some("AB 12 34 56 D"), None)),
    address = Some(PartialAddress(Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None)),
    previousAddress = Some(PartialPreviousAddress(false, None)),
    otherAddress = Some(OtherAddress(false)),
    openRegisterOptin = Some(false),
    postalVote = Some(PostalVote(false,None)),
    contact = Some(Contact(true, None, None)),
    possibleAddresses = None,
    country = Some(Country("England"))
  )
}
