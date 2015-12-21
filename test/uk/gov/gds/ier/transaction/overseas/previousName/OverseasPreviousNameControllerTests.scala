package uk.gov.gds.ier.transaction.overseas.previousName

import uk.gov.gds.ier.test.ControllerTestSuite

class OverseasPreviousNameControllerTests extends ControllerTestSuite {

  behavior of "PreviousNameController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/previous-name").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What was your name when you left the UK?")
      contentAsString(result) should include("<form action=\"/register-to-vote/overseas/previous-name\"")
    }
  }

  behavior of "PreviousNameController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/previous-name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "true",
            "previousName.hasPreviousNameOption" -> "true",
            "previousName.previousName.firstName" -> "John",
            "previousName.previousName.lastName" -> "Smith",
            "previousName.reason" -> "got bored")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/nino"))
    }
  }

  it should "bind successfully with no previous name and redirect to Nino step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/previous-name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "false",
            "previousName.hasPreviousNameOption" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/nino"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/previous-name")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "true",
            "previousName.hasPreviousNameOption" -> "true",
            "previousName.previousName.firstName" -> "John",
            "previousName.previousName.lastName" -> "Smith",
            "previousName.reason" -> "because I could")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  behavior of "PreviousNameController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/previous-name").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What was your name when you left the UK?")
      contentAsString(result) should include("<form action=\"/register-to-vote/overseas/edit/previous-name\"")
    }
  }
}
