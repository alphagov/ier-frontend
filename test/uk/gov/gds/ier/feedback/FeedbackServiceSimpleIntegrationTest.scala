package uk.gov.gds.ier.feedback

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.stubs.FeedbackStubClient
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.config.Config
import play.api.test.Helpers._
import org.mockito.Mockito._
import scala.Some

class FeedbackServiceSimpleIntegrationTest
  extends FlatSpec
  with FeedbackService
  with Matchers
  with MockitoSugar
  with TestHelpers {

  val feedbackClient = new FeedbackStubClient()

  val serialiser = jsonSerialiser
//  val config = new Config() {
//    override def zendeskUsername = "feedback@registertovote.service.gov.uk"
//    override def zendeskPassword = "F33back!"
//  }
  val config = mock[Config]
  when(config.zendeskUrl).thenReturn("https://cabinetoffice1402570809.zendesk.com/api/v2/tickets.json")
  when(config.zendeskUsername).thenReturn("feedback@registertovote.service.gov.uk")
  when(config.zendeskPassword).thenReturn("F33back!")

  val anonymous = FeedbackRequester(
    anonymousContactName,
    anonymousContactEmail)

  behavior of "FeedbackService#submit"
  it should "submit request when everything is set up correctly" in {
    running(FakeApplication()) {
      val request = FeedbackRequest(
        sourcePath = Some("/register-to-vote/previous-name"),
        comment = "Test test test",
        contactName = Some("Foo Bar"),
        contactEmail = Some("foo@foofoo.foo"))
      val feedbackService = new FeedbackService() {
        val feedbackClient = new FeedbackClientImpl(serialiser, config)
      }
      feedbackService.submit(request, Some("cool web browser 2.3.5"))
      Thread.sleep(100000)
    }
  }
}
