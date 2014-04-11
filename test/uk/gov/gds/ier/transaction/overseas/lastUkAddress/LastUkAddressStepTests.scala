package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

class LastUkAddressStepTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "LastUkAddressStep.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/last-uk-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was the UK address where you were last registered to vote?"
      )
      contentAsString(result) should include("/register-to-vote/overseas/last-uk-address")
    }
  }

  behavior of "LastUkAddressStep.post"
  it should "bind successfully and redirect a renewer to the Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteRenewerApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.uprn" -> "123456789",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/name"))
    }
  }

  it should "bind successfully and redirect a renewer to the Name step with manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteRenewerApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "lastUkAddress.manualAddress.lineTwo" -> "Moseley Road",
            "lastUkAddress.manualAddress.lineThree" -> "Hallow",
            "lastUkAddress.manualAddress.city" -> "Worcester",
            "lastUkAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/name"))
    }
  }
  
  it should "redirect a renewer to the Northern Ireland Exit page if the postcode starts with BT" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/lookup")
          .withIerSession()
          .withApplication(incompleteRenewerApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.postcode" -> "BT1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/northern-ireland"))
    }
  }

  it should "bind successfully and redirect a young voter to the Passport step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteYoungApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.uprn" -> "123456789",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect a young voter to the Name step (manual address)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteYoungApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "lastUkAddress.manualAddress.lineTwo" -> "Moseley Road",
            "lastUkAddress.manualAddress.lineThree" -> "Hallow",
            "lastUkAddress.manualAddress.city" -> "Worcester",
            "lastUkAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }
  it should "bind successfully and redirect a new voter to the Passport step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteNewApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.uprn" -> "123456789",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect a new voter to passport step (manual address)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteNewApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "lastUkAddress.manualAddress.lineTwo" -> "Moseley Road",
            "lastUkAddress.manualAddress.lineThree" -> "Hallow",
            "lastUkAddress.manualAddress.city" -> "Worcester",
            "lastUkAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect a special voter to the Passport step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteForcesApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.uprn" -> "123456789",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully, redirect a special voter to Passport step(manual address)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteForcesApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "lastUkAddress.manualAddress.lineTwo" -> "Moseley Road",
            "lastUkAddress.manualAddress.lineThree" -> "Hallow",
            "lastUkAddress.manualAddress.city" -> "Worcester",
            "lastUkAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "lastUkAddress.manualAddress.lineTwo" -> "Moseley Road",
            "lastUkAddress.manualAddress.lineThree" -> "Hallow",
            "lastUkAddress.manualAddress.city" -> "Worcester",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was the UK address where you were last registered to vote?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/last-uk-address")
    }
  }

behavior of "LastUkAddressStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/last-uk-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was the UK address where you were last registered to vote?"
      )
      contentAsString(result) should include("/register-to-vote/overseas/last-uk-address/lookup")
    }
  }

  behavior of "LastUkAddressStep.editPost"
  it should "bind successfully and redirect a renewer to the Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteRenewerApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.uprn" -> "123456789",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/name"))
    }
  }

  it should "bind successfully and redirect a renewer to the Name step with manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteRenewerApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "lastUkAddress.manualAddress.lineTwo" -> "Moseley Road",
            "lastUkAddress.manualAddress.lineThree" -> "Hallow",
            "lastUkAddress.manualAddress.city" -> "Worcester",
            "lastUkAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/name"))
    }
  }

  it should "bind successfully and redirect a young voter to the Passport step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteYoungApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.uprn" -> "123456789",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect a young voter to the Name step (manual address)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteYoungApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "lastUkAddress.manualAddress.lineTwo" -> "Moseley Road",
            "lastUkAddress.manualAddress.lineThree" -> "Hallow",
            "lastUkAddress.manualAddress.city" -> "Worcester",
            "lastUkAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect a new voter to the Passport step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteNewApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.uprn" -> "123456789",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect a new voter to passport step (manual address)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteNewApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "lastUkAddress.manualAddress.lineTwo" -> "Moseley Road",
            "lastUkAddress.manualAddress.lineThree" -> "Hallow",
            "lastUkAddress.manualAddress.city" -> "Worcester",
            "lastUkAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect a special voter to the Passport step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteForcesApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.uprn" -> "123456789",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully, redirect a special voter to Passport step(manual address)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(incompleteForcesApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "lastUkAddress.manualAddress.lineTwo" -> "Moseley Road",
            "lastUkAddress.manualAddress.lineThree" -> "Hallow",
            "lastUkAddress.manualAddress.city" -> "Worcester",
            "lastUkAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-uk-address")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "lastUkAddress.manualAddress.lineTwo" -> "Moseley Road",
            "lastUkAddress.manualAddress.lineThree" -> "Hallow",
            "lastUkAddress.manualAddress.city" -> "Worcester",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-uk-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was the UK address where you were last registered to vote?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/last-uk-address/lookup")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/previously-registered")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(lastUkAddress = None))
          .withFormUrlEncodedBody(
            "previouslyRegistered.hasPreviouslyRegistered" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-uk-address"))
    }
  }
}
