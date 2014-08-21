package uk.gov.gds.ier.controller

import uk.gov.gds.ier.test.ControllerTestSuite
import uk.gov.gds.ier.DynamicGlobal
import uk.gov.gds.ier.config.Config

class AssetsControllerTests extends ControllerTestSuite {

  val stubGlobal = new DynamicGlobal {
    override lazy val config = new Config {
      override def revision = "abcdef1234567890abcdef1234567890abcdef12"
    }
  }

  behavior of "Retrieving assets"
  it should "return asset without adding pragma: no-cache to the header for known sha" in {
    running(FakeApplication(withGlobal = Some(stubGlobal))) {
      val Some(result) = route(FakeRequest(GET,
        "/assets/abcdef1234567890abcdef1234567890abcdef12/template/stylesheets/fonts.css"))

      status(result) should be(OK)
      headers(result) should not contain("Pragma" -> "no-cache")
    }
  }

  it should "return asset without adding pragma: no-cache to the header for no sha" in {
    running(FakeApplication(withGlobal = Some(stubGlobal))) {
      val Some(result) = route(FakeRequest(GET,
        "/assets/template/stylesheets/fonts.css"))

      status(result) should be(OK)
      headers(result) should not contain("Pragma" -> "no-cache")
    }
  }

  it should "return asset with pragma: no-cache for unrecognised sha" in {
    running(FakeApplication(withGlobal = Some(stubGlobal))) {
      val Some(result) = route(FakeRequest(GET,
        "/assets/atestf1234567890atestf1234567890atestf00/template/stylesheets/fonts.css"))

      status(result) should be(OK)
      headers(result) should contain("Pragma" -> "no-cache")
    }
  }

}
