package uk.gov.gds.ier.session

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import play.api.mvc.Controller
import uk.gov.gds.ier.client.ApiResults
import play.api.test._
import play.api.test.Helpers._
import org.joda.time.DateTime

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

      session(result).get("sessionKey") should not be None
      val Some(token) = session(result).get("sessionKey")
      val tokenDate = DateTime.parse(token)
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

      session(result).get("sessionKey") should not be None
      val Some(initialToken) = session(result).get("sessionKey")
      val initialTokenDate = DateTime.parse(initialToken)

      val nextResult = controller.nextStep()(FakeRequest().withSession("sessionKey" -> initialToken))
      status(nextResult) should be(OK)
      val Some(newToken) = session(nextResult).get("sessionKey")
      val nextTokenDate = DateTime.parse(newToken)

      nextTokenDate should not be initialTokenDate
      nextTokenDate.getDayOfMonth should be(DateTime.now.getDayOfMonth)
      nextTokenDate.getMonthOfYear should be(DateTime.now.getMonthOfYear)
      nextTokenDate.getYear should be(DateTime.now.getYear)
      nextTokenDate.getHourOfDay should be(DateTime.now.getHourOfDay)
      nextTokenDate.getMinuteOfHour should be(DateTime.now.getMinuteOfHour)
      nextTokenDate.getSecondOfMinute should be(DateTime.now.getSecondOfMinute +- 1)
    }
  }

  it should "invalidate a session after 15 mins" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      class TestController extends Controller with WithSerialiser with SessionHandling with ApiResults {
        val serialiser = jsonSerialiser

        def index() = ValidSession requiredFor {
          request => application =>
            okResult(Map("status" -> "Ok"))
        }
      }

      val controller = new TestController()
      val result = controller.index()(FakeRequest().withSession("sessionKey" -> DateTime.now.minusMinutes(15).toString()))

      status(result) should be(SEE_OTHER)

      session(result).get("sessionKey") should not be None
      val Some(token) = session(result).get("sessionKey")
      val tokenDate = DateTime.parse(token)
      tokenDate.getDayOfMonth should be(DateTime.now.getDayOfMonth)
      tokenDate.getMonthOfYear should be(DateTime.now.getMonthOfYear)
      tokenDate.getYear should be(DateTime.now.getYear)
      tokenDate.getHourOfDay should be(DateTime.now.getHourOfDay)
      tokenDate.getMinuteOfHour should be(DateTime.now.getMinuteOfHour)
      tokenDate.getSecondOfMinute should be(DateTime.now.getSecondOfMinute +- 1)

      tokenDate.getMinuteOfHour should not be(DateTime.now.minusMinutes(16).getMinuteOfHour)
    }
  }

  it should "refresh a session before 15 mins" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      class TestController extends Controller with WithSerialiser with SessionHandling with ApiResults {
        val serialiser = jsonSerialiser

        def index() = ValidSession requiredFor {
          request => application =>
            okResult(Map("status" -> "Ok"))
        }
      }

      val controller = new TestController()
      val result = controller.index()(FakeRequest().withSession("sessionKey" -> DateTime.now.minusMinutes(14).toString()))

      status(result) should be(OK)

      session(result).get("sessionKey") should not be None
      val Some(token) = session(result).get("sessionKey")
      val tokenDate = DateTime.parse(token)
      tokenDate.getDayOfMonth should be(DateTime.now.getDayOfMonth)
      tokenDate.getMonthOfYear should be(DateTime.now.getMonthOfYear)
      tokenDate.getYear should be(DateTime.now.getYear)
      tokenDate.getHourOfDay should be(DateTime.now.getHourOfDay)
      tokenDate.getMinuteOfHour should be(DateTime.now.getMinuteOfHour)
      tokenDate.getSecondOfMinute should be(DateTime.now.getSecondOfMinute +- 1)
    }
  }
}
