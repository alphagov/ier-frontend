package uk.gov.gds.ier.transaction.overseas.parentsAddress

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

class OverseasParentsAddressStepTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "ParentsAddressStep.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/parents-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your parent or guardian&#39;s last UK address?"
      )
      contentAsString(result) should include("Question 7")
      contentAsString(result) should include("/register-to-vote/overseas/parents-address")
    }
  }

  behavior of "ParentsAddressStep.post"
  it should "bind successfully and redirect to the Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parents-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "parentsAddress.uprn" -> "123456789",
            "parentsAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect to the Name step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parents-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "parentsAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "parentsAddress.manualAddress.lineTwo" -> "Moseley Road",
            "parentsAddress.manualAddress.lineThree" -> "Hallow",
            "parentsAddress.manualAddress.city" -> "Worcester",
            "parentsAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parents-address")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "parentsAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "parentsAddress.manualAddress.lineTwo" -> "Moseley Road",
            "parentsAddress.manualAddress.lineThree" -> "Hallow",
            "parentsAddress.manualAddress.city" -> "Worcester",
            "parentsAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parents-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your parent or guardian&#39;s last UK address?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/parents-address")
    }
  }

behavior of "ParentsAddressStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/parents-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your parent or guardian&#39;s last UK address?"
      )
      contentAsString(result) should include("Question 7")
      contentAsString(result) should include("/register-to-vote/overseas/parents-address/lookup")
    }
  }

  behavior of "ParentsAddressStep.editPost"
  it should "bind successfully and redirect to the Previous Address step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parents-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "parentsAddress.uprn" -> "123456789",
            "parentsAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect to the Name step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parents-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "parentsAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "parentsAddress.manualAddress.lineTwo" -> "Moseley Road",
            "parentsAddress.manualAddress.lineThree" -> "Hallow",
            "parentsAddress.manualAddress.city" -> "Worcester",
            "parentsAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parents-address")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "parentsAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "parentsAddress.manualAddress.lineTwo" -> "Moseley Road",
            "parentsAddress.manualAddress.lineThree" -> "Hallow",
            "parentsAddress.manualAddress.city" -> "Worcester",
            "parentsAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parents-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your parent or guardian&#39;s last UK address?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/parents-address/lookup")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/previously-registered")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(parentsAddress = None))
          .withFormUrlEncodedBody(
            "previouslyRegistered.hasPreviouslyRegistered" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/parents-address"))
    }
  }
}
