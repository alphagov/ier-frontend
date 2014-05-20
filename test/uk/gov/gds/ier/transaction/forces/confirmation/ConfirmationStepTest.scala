package uk.gov.gds.ier.transaction.forces.confirmation

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.test.TestHelpers
import play.api.test.Helpers._
import play.api.test.FakeRequest
import uk.gov.gds.ier.model.{LastUkAddress, PartialAddress}
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails

/**
 * Test ConfirmationStep and ConfirmationController for Ordinary route,
 * notably application submission
 */
class ConfirmationStepTest extends FlatSpec
with Matchers
with MockitoSugar
with TestHelpers {

  behavior of "ConfirmationStep.post"
  it should "submit application and set Refnum and LocalAuthority for the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/confirmation")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(address =
            Some(LastUkAddress(
              hasUkAddress = Some(true),
              address = Some(PartialAddress(
                addressLine = Some("1 The Cottages, Moseley Road, Hallow, Worcestershire"),
                uprn = Some("100120595384"),
                postcode = "WR2 6NJ",
                gssCode = Some("E07000235"),
                manualAddress = None
              ))
            ))
          ))
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/complete"))
      val flashData = flash(result).data
      flashData("refNum") should not be(None)
      flashData("localAuthority") should not be(None)
      val localAuthority = jsonSerialiser.fromJson[EroAuthorityDetails](flashData("localAuthority"))

      localAuthority should be(EroAuthorityDetails(
        name = "Malvern Hills (test)",
        urls  = List(
          "http://www.malvernhills.gov.uk/",
          "http://www.malvernhills.gov.uk/cms/council-and-democracy/elections.aspx"
        ),
        email  = Some("worcestershirehub@malvernhills.gov.uk.test"),
        addressLine1 = Some("Council House"),
        addressLine2 = Some("Avenue Road"),
        addressLine3 = Some("Malvern"),
        addressLine4 = Some(""),
        postcode = Some("WR14 3AF"),
        phone = Some("01684 862151")
      ))
    }
  }
}
