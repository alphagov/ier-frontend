package uk.gov.gds.ier.test

import play.api.test.FakeRequest
import org.joda.time.DateTime
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.security._
import uk.gov.gds.ier.session.{SessionKeys, ResultHandling}
import uk.gov.gds.ier.guice.WithConfig
import uk.gov.gds.ier.config.Config
import play.api.data.FormError
import uk.gov.gds.ier.model.LastRegisteredType

trait TestHelpers extends CustomMatchers with OverseasApplications {

  val jsonSerialiser = new JsonSerialiser

  lazy val textTooLong = "x" * 1000

  implicit class EasyGetErrorMessageError(form: ErrorTransformForm[_]) {
    def keyedErrorsAsMap = {
      form.errors.filterNot( error =>
        error.key == ""
      ).map( error =>
        error.key -> this.errorMessages(error.key)
      ).toMap
    }
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
    previousAddress = Some(PartialPreviousAddress(Some(false), None)),
    otherAddress = Some(OtherAddress(OtherAddress.NoOtherAddress)),
    openRegisterOptin = Some(false),
    postalVote = Some(PostalVote(Some(false),None)),
    contact = Some(Contact(true, None, None)),
    possibleAddresses = None,
    country = Some(Country("England", false))
  )

  

  lazy val completeForcesApplication = InprogressForces(
    statement = Some(Statement(memberForcesFlag = Some(true), None)),
    address = Some(PartialAddress(Some("123 Fake Street, Fakerton"), Some("123456789"), "WR26NJ", None)),
    previousAddress = Some(PartialPreviousAddress(Some(false), None)),
    nationality = Some(PartialNationality(Some(true), None, None, List.empty, None)),
    dob = Some(DateOfBirth(Some(DOB(1988, 1, 1)), None)),
    name = Some(Name("John", None, "Smith")),
    previousName = Some(PreviousName(true, Some(Name("George", None, "Smith")))),
    nino = Some(Nino(Some("AB 12 34 56 D"), None)),
    service = Some(Service(Some(ServiceType.RoyalAirForce), None)),
    rank = Some(Rank(Some("1234567"), Some("rank 1"))),
    contactAddress = Some (PossibleContactAddresses(
      contactAddressType = Some("uk"),
      ukAddressLine = Some("my uk address, london"),
      bfpoContactAddress = None,
      otherContactAddress = None
    )),
    openRegisterOptin = Some(true),
    waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
    postalOrProxyVote = Some(PostalOrProxyVote(
      WaysToVoteType.ByPost,
      Some(true),
      Some(PostalVoteDeliveryMethod(Some("post"),None))
    )),
    contact = Some(Contact(true, None, None)),
    possibleAddresses = None
  )

  class ErrorsOps(errors: Seq[FormError], globalErrors: Seq[FormError]) {
    /**
     * Transform errors to a multi line text suitable for testing.
     * Errors order is important, test it too.
     * Filter out all items with no key, this reduces duplicities, otherwise every(?) item is there twice
     */
    def errorsAsText() = {
      errors.filter(_.key != "").map(e => e.key + " -> " + e.message).mkString("", "\n", "")
    }

    def errorsAsTextAll() = {
      errors.map(e => e.key + " -> " + e.message).mkString("", "\n", "")
    }

    /**
     * Transform errors to a multi line text suitable for testing.
     * Errors order is important, test it too.
     * Note: global errors has always empty keys, so ignore them.
     */
    def globalErrorsAsText() = {
      globalErrors.map(x => x.message).mkString("", "\n", "")
    }
  }

  implicit def formToErrorOps(form: ErrorTransformForm[InprogressOrdinary]) = {
    new ErrorsOps(form.errors, form.globalErrors)
  }

  implicit def overseasFormToErrorOps(form: ErrorTransformForm[InprogressOverseas]) = {
    new ErrorsOps(form.errors, form.globalErrors)
  }

  implicit def forcesFormToErrorOps(form: ErrorTransformForm[InprogressForces]) = {
    new ErrorsOps(form.errors, form.globalErrors)
  }

  // Comment out for longer timeouts, essential for debugging the test
  // implicit def defaultAwaitTimeout = Timeout(10, TimeUnit.MINUTES)
}
