package uk.gov.gds.ier.transaction.overseas.confirmation

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.test.{WithLongTimeout, TestHelpers}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, FakeApplication}
import uk.gov.gds.ier.model.{LastUkAddress, PartialAddress}
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails

/**
 * Test ConfirmationStep and ConfirmationController for Ordinary route,
 * notably application submission
 *
 * This is an integration test, expected to run against real IER-API service
 */
class ConfirmationStepTest extends FlatSpec
with Matchers
with MockitoSugar
with TestHelpers {

  behavior of "ConfirmationStep.post"
  it should "submit application and set Refnum and LocalAuthority for the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/confirmation")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(
            lastUkAddress = Some(PartialAddress(
              addressLine = Some("1 The Cottages, Moseley Road, Hallow, Worcestershire"),
              uprn = Some("100120595384"),
              postcode = "WR2 6NJ",
              gssCode = Some("E07000235"),
              manualAddress = None
            ))
          ))
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/complete"))
      val flashData = flash(result).data
      flashData("refNum") should not be(None)
      flashData("localAuthority") should not be(None)
      val localAuthority = jsonSerialiser.fromJson[EroAuthorityDetails](flashData("localAuthority"))
      localAuthority.name should be("Malvern Hills")
      localAuthority.urls should be(List[String]())
      // local authority database is still patchy so let's do not test more for now
    }
  }
}
