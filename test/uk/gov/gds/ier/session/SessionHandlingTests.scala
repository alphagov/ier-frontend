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
import uk.gov.gds.ier.model.{Name, Address, Addresses, InprogressApplication}

@RunWith(classOf[JUnitRunner])
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
      val tokenDate = DateTime.parse(cookie.value)
      tokenDate.getDayOfMonth should be(DateTime.now.getDayOfMonth)
      tokenDate.getMonthOfYear should be(DateTime.now.getMonthOfYear)
      tokenDate.getYear should be(DateTime.now.getYear)
      tokenDate.getHourOfDay should be(DateTime.now.getHourOfDay)
      tokenDate.getMinuteOfHour should be(DateTime.now.getMinuteOfHour)
      tokenDate.getSecondOfMinute should be(DateTime.now.getSecondOfMinute +- 1)
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
      val initialTokenDate = DateTime.parse(initialToken.value)

      val nextResult = controller.nextStep()(FakeRequest().withCookies(initialToken))
      status(nextResult) should be(OK)
      val Some(newToken) = cookies(nextResult).get("sessionKey")
      val nextTokenDate = DateTime.parse(newToken.value)

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
      val result = controller.index()(FakeRequest().withCookies(Cookie("sessionKey", DateTime.now.minusMinutes(5).toString())))

      status(result) should be(SEE_OTHER)

      cookies(result).get("sessionKey") should not be None
      val Some(token) = cookies(result).get("sessionKey")
      val tokenDate = DateTime.parse(token.value)
      tokenDate.getDayOfMonth should be(DateTime.now.getDayOfMonth)
      tokenDate.getMonthOfYear should be(DateTime.now.getMonthOfYear)
      tokenDate.getYear should be(DateTime.now.getYear)
      tokenDate.getHourOfDay should be(DateTime.now.getHourOfDay)
      tokenDate.getMinuteOfHour should be(DateTime.now.getMinuteOfHour)
      tokenDate.getSecondOfMinute should be(DateTime.now.getSecondOfMinute +- 1)

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
      val result = controller.index()(FakeRequest().withCookies(Cookie("sessionKey", DateTime.now.minusMinutes(4).toString())))

      status(result) should be(OK)

      cookies(result).get("sessionKey") should not be None
      val Some(token) = cookies(result).get("sessionKey")
      val tokenDate = DateTime.parse(token.value)
      tokenDate.getDayOfMonth should be(DateTime.now.getDayOfMonth)
      tokenDate.getMonthOfYear should be(DateTime.now.getMonthOfYear)
      tokenDate.getYear should be(DateTime.now.getYear)
      tokenDate.getHourOfDay should be(DateTime.now.getHourOfDay)
      tokenDate.getMinuteOfHour should be(DateTime.now.getMinuteOfHour)
      tokenDate.getSecondOfMinute should be(DateTime.now.getSecondOfMinute +- 1)
    }
  }

  behavior of "SimpleResultToSession.merge"

  it should "not allow possibleAddresses in to the session, we don't want to store those, ever!" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      class TestController extends Controller with WithSerialiser with SessionHandling with ApiResults {
        val serialiser = jsonSerialiser

        def index() = ValidSession requiredFor {
          implicit request => application =>
            okResult(Map("status" -> "Ok")).mergeWithSession(
              application.copy(possibleAddresses = Some(Addresses(addresses = List(Address(Some("123 Fake Street"), "SW1A 1AA")))), name = Some(Name("John", None, "Smith"))))
        }
      }
      val result = new TestController().index()(FakeRequest().withCookies(Cookie("sessionKey", DateTime.now.minusMinutes(4).toString())))

      cookies(result).get("application") match {
        case Some(cookie) => {
          val application = jsonSerialiser.fromJson[InprogressApplication](cookie.value)
          application.possibleAddresses should be(None)
        }
        case _ => fail("Should have been able to deserialise")
      }
    }
  }
}
