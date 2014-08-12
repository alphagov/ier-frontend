package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import uk.gov.gds.ier.test.ControllerTestSuite

class LastUkAddressStepTests extends ControllerTestSuite {

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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
      contentAsString(result) should include("Please enter your postcode")
      contentAsString(result) should include("/register-to-vote/overseas/last-uk-address")
    }
  }

behavior of "LastUkAddressStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/last-uk-address/select").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was the UK address where you were last registered to vote?"
      )
      contentAsString(result) should include("/register-to-vote/overseas/edit/last-uk-address")
    }
  }

  behavior of "LastUkAddressStep.editPost"
  it should "bind successfully and redirect a renewer to the Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address/select")
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
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-uk-address/select")
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

  it should "display errors when posting empty form on a edit manual page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-uk-address/manual").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was the UK address where you were last registered to vote?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/edit/last-uk-address/manual")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-of-birth")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(
            lastUkAddress = None
          ))
          .withFormUrlEncodedBody(
            "dob.day" -> "1",
            "dob.month" -> "1",
            "dob.year" -> "1970"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-uk-address"))
    }
  }
}
