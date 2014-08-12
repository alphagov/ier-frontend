package uk.gov.gds.ier.transaction.country

import uk.gov.gds.ier.test.ControllerTestSuite

class CountryControllerTests extends ControllerTestSuite {

  behavior of "CountryController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/country-of-residence").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 1")
      contentAsString(result) should not include("<a class=\"back-to-previous\"")
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("/register-to-vote/country-of-residence")
    }
  }

  behavior of "CountryController.post"
  it should "bind successfully and redirect to the Nationality step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "England"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/nationality"))
    }
  }

  it should "bind successfully on Northern Ireland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "Northern Ireland"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/northern-ireland"))
    }
  }

  it should "bind successfully on British Islands and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "British Islands"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/british-islands"))
    }
  }

  it should "bind successfully on Scotland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "Scotland"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/scotland"))
    }
  }

  it should "bind successfully on Abroad + Wales and redirect to the overseas" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "Wales"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/start"))
    }
  }

  it should "bind successfully on Abroad + England and redirect to the overseas" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "England"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/start"))
    }
  }

  it should "bind successfully on Abroad + NIreland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "Northern Ireland"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/northern-ireland"))
    }
  }

  it should "bind successfully on Abroad + British Islands and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "British Islands"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/british-islands"))
    }
  }

  it should "bind successfully on Abroad + Scotland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "Scotland"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/scotland"))
    }
  }

  it should "require the origin question answered when abroad" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad"
          )
      )

      status(result) should be(OK)
      contentAsString(result) should include("Please answer this question")
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/country-of-residence")
    }
  }

  behavior of "CountryController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/country-of-residence").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 1")
      contentAsString(result) should not include("<a class=\"back-to-previous\"")
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("/register-to-vote/edit/country-of-residence")
    }
  }

  behavior of "CountryController.editPost"
  it should "bind successfully and redirect to the Nationality step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "England"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/nationality"))
    }
  }

  it should "bind successfully on Northern Ireland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "Northern Ireland"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/northern-ireland"))
    }
  }

  it should "bind successfully on British Islands and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "British Islands"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/british-islands"))
    }
  }

  it should "bind successfully on Abroad + Wales and redirect to the overseas" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "Wales"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/start"))
    }
  }

  it should "bind successfully on Abroad + England and redirect to the overseas" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "England"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/start"))
    }
  }

  it should "bind successfully on Abroad + NIreland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "Northern Ireland"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/northern-ireland"))
    }
  }

  it should "bind successfully on Abroad + British Islands and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "British Islands"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/british-islands"))
    }
  }

  it should "bind successfully on Abroad + Scotland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad",
            "country.origin" -> "Scotland"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/scotland"))
    }
  }

  it should "require the origin question answered when abroad" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
            "country.residence" -> "Abroad"
          )
      )

      status(result) should be(OK)
      contentAsString(result) should include("Please answer this question")
    }
  }


  it should "bind successfully on Scotland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "Scotland"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/scotland"))
    }
  }
}
