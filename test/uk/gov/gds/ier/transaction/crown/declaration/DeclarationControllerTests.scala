package uk.gov.gds.ier.transaction.crown.declaration

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.model.{PartialAddress, LastUkAddress}

class DeclarationControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "DeclarationPdfController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      route(
        FakeRequest(GET, "/register-to-vote/crown/declaration-pdf").withIerSession()
      ) match {
        case Some(result) => {
          status(result) should be(OK)
          contentType(result) should be(Some("text/html"))
          contentAsString(result) should include("Download your service declaration form")
          contentAsString(result) should include(
            "<form action=\"/register-to-vote/crown/declaration-pdf\"")
        }
        case None => fail("Request failed, wrong URL?")
      }
    }
  }

  behavior of "DeclarationPdfController.post"
  it should "redirect to the next step even when request is empty as this step has no inputs" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/declaration-pdf")
          .withIerSession()
          .withApplication(inprogressApplicationWithPostcode("WR26NJ"))
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nino"))
    }
  }

  private def inprogressApplicationWithPostcode(postcode: String) = {
    InprogressCrown().copy(
      address = Some(LastUkAddress(
        hasUkAddress = Some(true),
        address = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          manualAddress = None,
          postcode = postcode)))))
  }
}
