package uk.gov.gds.ier.session

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import play.api.mvc.{Cookie, Session, Controller}
import uk.gov.gds.ier.client.ApiResults
import play.api.test._
import play.api.test.Helpers._
import org.joda.time.DateTime
import uk.gov.gds.ier.model.{Name, Address, PossibleAddress, InprogressApplication}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}

class SessionHandlingTests extends FlatSpec with Matchers {

  val jsonSerialiser = new JsonSerialiser

  it should "successfully create a new session" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      class TestController extends Controller with WithSerialiser with SessionHandling with ApiResults {
        val serialiser = jsonSerialiser

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
      val decryptedInfo = EncryptionService.decrypt(cookie.value,  session(result).get("sessionTokenCookieKey").get ,EncryptionKeys.cookies.getPrivate)
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
      class TestController extends Controller with WithSerialiser with SessionHandling with ApiResults {
        val serialiser = jsonSerialiser

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
      class TestController extends Controller with WithSerialiser with SessionHandling with ApiResults {
        val serialiser = jsonSerialiser

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
      val tokenKey = session(result).get("sessionTokenCookieKey").get
      val initialTokendecryptedInfo = EncryptionService.decrypt(initialToken.value, tokenKey, EncryptionKeys.cookies.getPrivate)
      val initialTokenDate = DateTime.parse(initialTokendecryptedInfo)

      val nextResult = controller.nextStep()(FakeRequest().withCookies(initialToken).withSession(("sessionTokenCookieKey", tokenKey)))
      status(nextResult) should be(OK)
      val Some(newToken) = cookies(nextResult).get("sessionKey")
      val tokenKey2 = session(nextResult).get("sessionTokenCookieKey").get
      val newTokendecryptedInfo = EncryptionService.decrypt(newToken.value, tokenKey2, EncryptionKeys.cookies.getPrivate)
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

  it should "invalidate a session after 5 mins" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      class TestController extends Controller with WithSerialiser with SessionHandling with ApiResults {
        val serialiser = jsonSerialiser

        def index() = ValidSession requiredFor {
          request => application =>
            okResult(Map("status" -> "Ok"))
        }
      }

      val controller = new TestController()

      val (encryptedSessionTokenValue, sessionTokenCookieKey) = EncryptionService.encrypt(DateTime.now.minusMinutes(5).toString(), EncryptionKeys.cookies.getPublic)

      val result = controller.index()(FakeRequest().withCookies(Cookie("sessionKey", encryptedSessionTokenValue)).withSession(("sessionTokenCookieKey",sessionTokenCookieKey)))

      status(result) should be(SEE_OTHER)

      cookies(result).get("sessionKey") should not be None
      val Some(token) = cookies(result).get("sessionKey")
      val tokenKey = session(result).get("sessionTokenCookieKey").get

      val decryptedInfo = EncryptionService.decrypt(token.value, tokenKey, EncryptionKeys.cookies.getPrivate)

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

  it should "refresh a session before 5 mins" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      class TestController extends Controller with WithSerialiser with SessionHandling with ApiResults {
        val serialiser = jsonSerialiser

        def index() = ValidSession requiredFor {
          request => application =>
            okResult(Map("status" -> "Ok"))
        }
      }

      val controller = new TestController()
      val (encryptedSessionTokenValue, sessionTokenCookieKey) = EncryptionService.encrypt(DateTime.now.minusMinutes(4).toString(), EncryptionKeys.cookies.getPublic)

      val result = controller.index()(FakeRequest().withCookies(Cookie("sessionKey",encryptedSessionTokenValue)).withSession(("sessionTokenCookieKey",sessionTokenCookieKey)))

      status(result) should be(OK)

      cookies(result).get("sessionKey") should not be None
      val Some(token) = cookies(result).get("sessionKey")
      val tokenKey = session(result).get("sessionTokenCookieKey").get

      val decryptedInfo = EncryptionService.decrypt(token.value, tokenKey, EncryptionKeys.cookies.getPrivate)

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
      class TestController extends Controller with WithSerialiser with SessionHandling with ApiResults {
        val serialiser = jsonSerialiser

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
