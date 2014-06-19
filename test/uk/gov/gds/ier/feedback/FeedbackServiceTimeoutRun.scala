package uk.gov.gds.ier.feedback

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.config.Config
import org.mockito.Mockito._
import play.api.mvc.Results._
import play.api.test.WithServer
import scala.concurrent.duration._
import uk.gov.gds.ier.logging.Logging
import play.api.mvc.Request


class FeedbackServiceTimeoutRun
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with Logging
  with TestHelpers {

  val serialiser = jsonSerialiser

  val config = mock[Config]
  when(config.zendeskUrl).thenReturn("http://localhost:5743/api/v2/tickets.json")
  when(config.zendeskUsername).thenReturn("feedback@registertovote.service.gov.uk")
  when(config.zendeskPassword).thenReturn("fake-password")

  val dummyTicketAction = play.api.mvc.Action { request =>
    logger.debug("dummyTicketAction start")
    Thread.sleep(20.seconds.toMillis.toInt)
    logger.debug("dummyTicketAction end")
    Ok("ok")
  }

  val catchAllDummyAction = play.api.mvc.Action { request =>
    Ok("ok")
  }

  val fakeZendeskApp = play.api.test.FakeApplication(withRoutes = {
    case ("GET", "/api/v2/tickets.json") => dummyTicketAction
    case ("POST", "/api/v2/tickets.json") => dummyTicketAction
    case (x, y) => {
      logger.error(s"unknown URL: ${x} ${y}")
      catchAllDummyAction
    }
  })

  val mockedRequest = mock[Request[Any]]
  val dummyHeader = DummyRequestHeader(Map(("user-agent" -> Seq("cool web browser version 1.2.3"))))
  when(mockedRequest.headers).thenReturn(dummyHeader.headers)

  behavior of "FeedbackService#submit"
  it should "timeout" in new WithServer(app = fakeZendeskApp, port = 5743) {
    val request = FeedbackRequest(
      comment = "Test test test",
      contactName = Some("Foo Bar"),
      contactEmail = Some("foo@foofoo.foo"))
    val feedbackService = new FeedbackService(feedbackClient = new FeedbackClientImpl(serialiser, config))
    feedbackService.submit(request, Some("cool web browser 2.3.5"))(mockedRequest)
    Thread.sleep(20.seconds.toMillis.toInt)
  }
}
