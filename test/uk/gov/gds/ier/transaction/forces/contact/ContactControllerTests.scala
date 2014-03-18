package uk.gov.gds.ier.transaction.forces.contact

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.model._


class ContactControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "ContactController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/contact").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 14")
      contentAsString(result) should include(
        "If we have questions about your application, how should we contact you?")
      contentAsString(result) should include("/register-to-vote/forces/contact")
    }
  }

  behavior of "ContactController.post"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/contact")
          .withIerSession()
          .withFormUrlEncodedBody(
            "contact.contactType" -> "phone", 
            "contact.phone" -> "01234 123 456")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/contact").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "If we have questions about your application, how should we contact you?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/forces/contact")
    }
  }

  behavior of "ContactController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/edit/contact").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 14")
      contentAsString(result) should include(
        "If we have questions about your application, how should we contact you?")
      contentAsString(result) should include("/register-to-vote/forces/edit/contact")
    }
  }

  behavior of "ContactController.editPost"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/contact")
          .withIerSession()
          .withFormUrlEncodedBody(
            "contact.contactType" -> "phone",
            "contact.phone" -> "01234 123 456")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/contact").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "If we have questions about your application, how should we contact you?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/forces/edit/contact")
    }
  }

    it should "prepopulate the email address for the contact step if it is filled in the postal step" +
    "when submitting the form successfully" in {
    val mockedJsonSerialiser = mock[JsonSerialiser]
    val mockedConfig = mock[Config]
    val mockedEncryptionService = mock[EncryptionService]

    val postalVoteStep = new ContactStep(mockedJsonSerialiser, mockedConfig,
        mockedEncryptionService)

    val currentState = completeForcesApplication.copy(
        postalOrProxyVote = Some(
            PostalOrProxyVote(
                typeVote = WaysToVoteType.ByPost, 
                postalVoteOption = Some(true),
                deliveryMethod = Some(
                    PostalVoteDeliveryMethod(
                    		deliveryMethod = Some("email"),
                    		emailAddress = Some("test@test.com")
                    )
                )
            )
        ),
        contact = None)

    val transferedState = postalVoteStep.prepopulateEmailAddress(currentState)
    transferedState.contact should not be (None)
    transferedState.contact.get.email should not be (None)
    transferedState.contact.get.email.get.detail should not be (None)
    transferedState.contact.get.email.get.detail.get should be ("test@test.com")
  }

  
}
