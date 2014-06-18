package uk.gov.gds.ier.feedback

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.config.Config
import org.mockito.Mockito._
import play.api.test.Helpers._
import play.api.mvc.Results._
import play.api.test.WithServer
import play.api.libs.ws.WS


class FeedbackServiceTimeoutIntegrationTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  val serialiser = jsonSerialiser

  val config = mock[Config]
  when(config.zendeskUrl).thenReturn("http://localhost:5743/api/v2/tickets.json")
  when(config.zendeskUsername).thenReturn("feedback@registertovote.service.gov.uk")
  when(config.zendeskPassword).thenReturn("aaaaaaaaaaaa")

  val dummyTicketAction = play.api.mvc.Action { request =>
    println("dummyTicketAction start")
    Thread.sleep(10000)
    println("dummyTicketAction end")
    Ok("ok")
  }

  val catchAllDummyAction = play.api.mvc.Action { request =>
    Ok("ok")
  }

  val fakeZendeskApp = play.api.test.FakeApplication(withRoutes = {
    case ("GET", "/api/v2/tickets.json") => dummyTicketAction
    case ("POST", "/api/v2/tickets.json") => dummyTicketAction
    case (x, y) => {
      println(s"unknown URL: ${x} ${y}")
      catchAllDummyAction
    }
  })

  behavior of "FeedbackService#submit"
  it should "timeout" in new WithServer(app = fakeZendeskApp, port = 5743) {
    val request = FeedbackRequest(
      sourcePath = Some("/register-to-vote/previous-name"),
      comment = "Test test test",
      contactName = Some("Foo Bar"),
      contactEmail = Some("foo@foofoo.foo"))
    val feedbackService = new FeedbackService() {
      val feedbackClient = new FeedbackClientImpl(serialiser, config)
    }
    feedbackService.submit(request, Some("cool web browser 2.3.5"))
    Thread.sleep(10000)
  }
}
