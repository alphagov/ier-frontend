package uk.gov.gds.ier.transaction.ordinary.soleOccupancy

import uk.gov.gds.ier.test.ControllerTestSuite

class SoleOccupancyControllerTests extends ControllerTestSuite {

  behavior of "SoleOccupancyController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/sole-occupancy").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Are you the only person aged 16 or over living at this address?")
      contentAsString(result) should include("/register-to-vote/sole-occupancy")
    }
  }

  behavior of "SoleOccupancyController.post"
    it should behave like appWithSoleOccupancy("yes")

  def appWithSoleOccupancy(soleOccupancyOption: String) {
    it should s"bind successfully and redirect to the contact step for option: $soleOccupancyOption" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/sole-occupancy")
            .withIerSession()
            .withFormUrlEncodedBody(
              "soleOccupancy.optIn" -> soleOccupancyOption
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some("/register-to-vote/contact"))
      }
    }
  }

  it should "bind successfully and redirect to the confirmation step when complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/sole-occupancy")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "soleOccupancy.OptIn" -> "yes"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/sole-occupancy").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("Are you the only person aged 16 or over living at this address?")
      contentAsString(result) should include("/register-to-vote/sole-occupancy")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(soleOccupancy = None))
          .withFormUrlEncodedBody(
            "country.residence" -> "England"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/sole-occupancy"))
    }
  }

  behavior of "SoleOccupancyController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/sole-occupancy").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Are you the only person aged 16 or over living at this address?")
      contentAsString(result) should include("/register-to-vote/edit/sole-occupancy")
    }
  }

  behavior of "SoleOccupancyController.editPost"
  it should behave like editedAppWithSoleOccupancy("yes")
  it should behave like editedAppWithSoleOccupancy("no")
  it should behave like editedAppWithSoleOccupancy("not-sure")

  def editedAppWithSoleOccupancy(soleOccupancyOption: String) {
    it should s"bind successfully and redirect to the incomplete contact step for option: $soleOccupancyOption" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/edit/sole-occupancy")
            .withIerSession()
            .withFormUrlEncodedBody(
              "soleOccupancy.optIn" -> soleOccupancyOption
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some("/register-to-vote/contact"))
      }
    }

    it should s"bind successfully and redirect to the Confirmation step when complete application for option: $soleOccupancyOption" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/edit/sole-occupancy")
            .withIerSession()
            .withApplication(completeOrdinaryApplication)
            .withFormUrlEncodedBody(
              "soleOccupancy.optIn" -> soleOccupancyOption
            )
        )

        status(result) should be(SEE_OTHER)
        redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
      }
    }
  }
}
