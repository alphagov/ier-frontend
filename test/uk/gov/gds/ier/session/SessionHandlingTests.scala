package uk.gov.gds.ier.session

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import play.api.mvc.{Cookie, Controller}
import uk.gov.gds.ier.client.ApiResults
import play.api.test._
import play.api.test.Helpers._
import org.joda.time.DateTime
import uk.gov.gds.ier.security._
import uk.gov.gds.ier.controller.MockConfig
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import scala.Some
import play.api.test.FakeApplication
import play.api.mvc.Cookie

class SessionHandlingTests extends FlatSpec with Matchers {

  val jsonSerialiser = new JsonSerialiser

  it should "successfully create a new session" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      class TestController extends Controller with WithSerialiser with WithConfig with SessionHandling with ApiResults with WithEncryption {
        val serialiser = jsonSerialiser
        val config = new MockConfig
        val encryptionService = new EncryptionService (new AesEncryptionService(new Base64EncodingService), new RsaEncryptionService(new Base64EncodingService))
        val encryptionKeys = new EncryptionKeys(new Base64EncodingService)

        def index() = NewSession requiredFor {
          request =>
            okResult(Map("status" -> "Ok"))
        }
      }

      val controller = new TestController()
      val result = controller.index()(FakeRequest())

      status(result) should be(OK)
      jsonSerialiser.fromJson[Map[String,String]](contentAsString(result)) should be(Map("status" -> "Ok"))

      cookies(result).get("sessionKey") should not be None
      val Some(cookie) = cookies(result).get("sessionKey")
      val decryptedInfo = controller.encryptionService.decrypt(cookie.value,  cookies(result).get("sessionTokenCookieKey").get.value, controller.encryptionKeys.cookies.getPrivate)
      val tokenDate = DateTime.parse(decryptedInfo)
      tokenDate.getDayOfMonth should be(DateTime.now.getDayOfMonth)
      tokenDate.getMonthOfYear should be(DateTime.now.getMonthOfYear)
      tokenDate.getYear should be(DateTime.now.getYear)
      tokenDate.getHourOfDay should be(DateTime.now.getHourOfDay)
      tokenDate.getMinuteOfHour should be(DateTime.now.getMinuteOfHour)
      tokenDate.getSecondOfMinute should be(DateTime.now.getSecondOfMinute +- 2)
    }
  }

  it should "force a redirect with no valid session" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      class TestController extends Controller with WithSerialiser with WithConfig with SessionHandling with ApiResults with WithEncryption {
        val serialiser = jsonSerialiser
        val config = new MockConfig
        val encryptionService = new EncryptionService (new AesEncryptionService(new Base64EncodingService), new RsaEncryptionService(new Base64EncodingService))
        val encryptionKeys = new EncryptionKeys(new Base64EncodingService)

        def index() = ValidSession requiredFor {
          request => application =>
            okResult(Map("status" -> "Ok"))
        }
      }

      val controller = new TestController()
      val result = controller.index()(FakeRequest())

      status(result) should be(SEE_OTHER)
    }
  }

  it should "refresh a session with a new timestamp" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      class TestController extends Controller with WithSerialiser with WithConfig with SessionHandling with ApiResults with WithEncryption {
        val serialiser = jsonSerialiser
        val config = new MockConfig
        val encryptionService = new EncryptionService (new AesEncryptionService(new Base64EncodingService), new RsaEncryptionService(new Base64EncodingService))
        val encryptionKeys = new EncryptionKeys(new Base64EncodingService)

        def index() = NewSession requiredFor {
          request =>
            okResult(Map("status" -> "Ok"))
        }
        def nextStep() = ValidSession requiredFor {
          request => application =>
            okResult(Map("status" -> "Ok"))
        }
      }

      val controller = new TestController()
      val result = controller.index()(FakeRequest())

      status(result) should be(OK)
      jsonSerialiser.fromJson[Map[String,String]](contentAsString(result)) should be(Map("status" -> "Ok"))

      cookies(result).get("sessionKey") should not be None
      val Some(initialToken) = cookies(result).get("sessionKey")
      val tokenKey = cookies(result).get("sessionTokenCookieKey").get.value
      val initialTokendecryptedInfo = controller.encryptionService.decrypt(initialToken.value, tokenKey, controller.encryptionKeys.cookies.getPrivate)
      val initialTokenDate = DateTime.parse(initialTokendecryptedInfo)

      val nextResult = controller.nextStep()(FakeRequest().withCookies(initialToken, Cookie("sessionTokenCookieKey", tokenKey)))
      status(nextResult) should be(OK)
      val Some(newToken) = cookies(nextResult).get("sessionKey")
      val tokenKey2 = cookies(nextResult).get("sessionTokenCookieKey").get.value
      val newTokendecryptedInfo = controller.encryptionService.decrypt(newToken.value, tokenKey2, controller.encryptionKeys.cookies.getPrivate)
      val nextTokenDate = DateTime.parse(newTokendecryptedInfo)

      nextTokenDate should not be initialTokenDate
      nextTokenDate.getDayOfMonth should be(DateTime.now.getDayOfMonth)
      nextTokenDate.getMonthOfYear should be(DateTime.now.getMonthOfYear)
      nextTokenDate.getYear should be(DateTime.now.getYear)
      nextTokenDate.getHourOfDay should be(DateTime.now.getHourOfDay)
      nextTokenDate.getMinuteOfHour should be(DateTime.now.getMinuteOfHour)
      nextTokenDate.getSecondOfMinute should be(DateTime.now.getSecondOfMinute +- 1)
    }
  }

  it should "invalidate a session after 20 mins" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      class TestController extends Controller with WithSerialiser with WithConfig with SessionHandling with ApiResults with WithEncryption {
        val serialiser = jsonSerialiser
        val config = new MockConfig
        val encryptionService = new EncryptionService (new AesEncryptionService(new Base64EncodingService), new RsaEncryptionService(new Base64EncodingService))
        val encryptionKeys = new EncryptionKeys(new Base64EncodingService)

        def index() = ValidSession requiredFor {
          request => application =>
            okResult(Map("status" -> "Ok"))
        }
      }

      val controller = new TestController()

      val (encryptedSessionTokenValue, sessionTokenCookieKey) = controller.encryptionService.encrypt(DateTime.now.minusMinutes(20).toString(), controller.encryptionKeys.cookies.getPublic)

      val result = controller.index()(FakeRequest().withCookies(Cookie("sessionKey", encryptedSessionTokenValue), Cookie("sessionTokenCookieKey",sessionTokenCookieKey)))

      status(result) should be(SEE_OTHER)

      cookies(result).get("sessionKey") should not be None
      val Some(token) = cookies(result).get("sessionKey")
      val tokenKey = cookies(result).get("sessionTokenCookieKey").get.value

      val decryptedInfo = controller.encryptionService.decrypt(token.value, tokenKey, controller.encryptionKeys.cookies.getPrivate)

      val tokenDate = DateTime.parse(decryptedInfo)
      tokenDate.getDayOfMonth should be(DateTime.now.getDayOfMonth)
      tokenDate.getMonthOfYear should be(DateTime.now.getMonthOfYear)
      tokenDate.getYear should be(DateTime.now.getYear)
      tokenDate.getHourOfDay should be(DateTime.now.getHourOfDay)
      tokenDate.getMinuteOfHour should be(DateTime.now.getMinuteOfHour)
      tokenDate.getSecondOfMinute should be(DateTime.now.getSecondOfMinute +- 2)
      tokenDate.getMinuteOfHour should not be(DateTime.now.minusMinutes(6).getMinuteOfHour)
    }
  }

  it should "refresh a session before 20 mins" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      class TestController extends Controller with WithSerialiser with WithConfig with SessionHandling with ApiResults with WithEncryption {
        val serialiser = jsonSerialiser
        val config = new MockConfig
        val encryptionService = new EncryptionService (new AesEncryptionService(new Base64EncodingService), new RsaEncryptionService(new Base64EncodingService))
        val encryptionKeys = new EncryptionKeys(new Base64EncodingService)


        def index() = ValidSession requiredFor {
          request => application =>
            okResult(Map("status" -> "Ok"))
        }
      }

      val controller = new TestController()
      val (encryptedSessionTokenValue, sessionTokenCookieKey) = controller.encryptionService.encrypt(DateTime.now.minusMinutes(19).toString(), controller.encryptionKeys.cookies.getPublic)
      val result = controller.index()(FakeRequest().withCookies(Cookie("sessionKey",encryptedSessionTokenValue), Cookie("sessionTokenCookieKey",sessionTokenCookieKey)))

      status(result) should be(OK)

      cookies(result).get("sessionKey") should not be None
      val Some(token) = cookies(result).get("sessionKey")
      val tokenKey = cookies(result).get("sessionTokenCookieKey").get.value

      val decryptedInfo = controller.encryptionService.decrypt(token.value, tokenKey, controller.encryptionKeys.cookies.getPrivate)

      val tokenDate = DateTime.parse(decryptedInfo)
      tokenDate.getDayOfMonth should be(DateTime.now.getDayOfMonth)
      tokenDate.getMonthOfYear should be(DateTime.now.getMonthOfYear)
      tokenDate.getYear should be(DateTime.now.getYear)
      tokenDate.getHourOfDay should be(DateTime.now.getHourOfDay)
      tokenDate.getMinuteOfHour should be(DateTime.now.getMinuteOfHour)
      tokenDate.getSecondOfMinute should be(DateTime.now.getSecondOfMinute +- 1)
    }
  }

  it should "clear session cookies by setting a maxage below 0" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      class TestController extends Controller with WithSerialiser with WithConfig with SessionHandling with ApiResults with WithEncryption {
        val serialiser = jsonSerialiser
        val config = new MockConfig
        val encryptionService = new EncryptionService (new AesEncryptionService(new Base64EncodingService), new RsaEncryptionService(new Base64EncodingService))
        val encryptionKeys = new EncryptionKeys(new Base64EncodingService)

        def noSessionTest() = ClearSession requiredFor {
          request =>
            okResult(Map("status" -> "no session"))
        }
      }

      val controller = new TestController()
      val result = controller.noSessionTest()(FakeRequest().withCookies(Cookie("application", "barfoo")))

      status(result) should be(OK)
      jsonSerialiser.fromJson[Map[String,String]](contentAsString(result)) should be(Map("status" -> "no session"))

      cookies(result).get("sessionKey").isDefined should be(true)
      cookies(result).get("sessionKey").get.maxAge.get should be < 0
      cookies(result).get("application").isDefined should be(true)
      cookies(result).get("application").get.maxAge.get should be < 0
      cookies(result).get("application").get.value should be("")
    }
  }
}
