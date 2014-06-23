package uk.gov.gds.ier.localAuthority

import uk.gov.gds.ier.test.TestHelpers
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._

class LocalAuthorityControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "LocalAuthorityController.showLookup"
  it should "display the lookup page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/local-authority/lookup").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Contact your local Electoral Registration Office")
      contentAsString(result) should include("/register-to-vote/local-authority/lookup")
    }
  }
}