package uk.gov.gds.ier.transaction.ordinary.confirmation

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.test.TestHelpers
import play.api.test.Helpers._
import play.api.test.FakeRequest
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails
import scala.Some

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
        FakeRequest(POST, "/register-to-vote/confirmation")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(address = Some(PartialAddress(
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

  it should "submit application and set show email message flag to false for no email addresses" in runningApp {
    val Some(result) = route(
      FakeRequest(POST, "/register-to-vote/confirmation")
        .withIerSession()
        .withApplication(completeOrdinaryApplication.copy(
          postalVote = Some(PostalVote(
            postalVoteOption = Some(false),
            deliveryMethod = None
          )),
          contact = Some(Contact(
            post = true,
            email = None,
            phone = None
          ))
        ))
    )

    status(result) should be(SEE_OTHER)
    redirectLocation(result) should be(Some("/register-to-vote/complete"))
    val flashData = flash(result).data
    flashData("showEmailConfirmation") should be("false")
  }

  it should "submit application and set show email message flag to true if the contact email address is present" in runningApp {
    val Some(result) = route(
      FakeRequest(POST, "/register-to-vote/confirmation")
        .withIerSession()
        .withApplication(completeOrdinaryApplication.copy(
        postalVote = Some(PostalVote(
          postalVoteOption = Some(false),
          deliveryMethod = None
        )),
        contact = Some(Contact(
          post = false,
          email = Some(ContactDetail(
            contactMe = true,
            detail = Some("test@email.com")
          )),
          phone = None
        ))
      ))
    )

    status(result) should be(SEE_OTHER)
    redirectLocation(result) should be(Some("/register-to-vote/complete"))
    val flashData = flash(result).data
    flashData("showEmailConfirmation") should be("true")
  }

  it should "submit application and set show email message flag to true if the postal vote email is present" in runningApp {
    val Some(result) = route(
      FakeRequest(POST, "/register-to-vote/confirmation")
        .withIerSession()
        .withApplication(completeOrdinaryApplication.copy(
        postalVote = Some(PostalVote(
          postalVoteOption = Some(true),
          deliveryMethod = Some(PostalVoteDeliveryMethod(
            deliveryMethod = Some("email"),
            emailAddress = Some("test@email.com")
          ))
        )),
        contact = Some(Contact(
          post = true,
          email = None,
          phone = None
        ))
      ))
    )

    status(result) should be(SEE_OTHER)
    redirectLocation(result) should be(Some("/register-to-vote/complete"))
    val flashData = flash(result).data
    flashData("showEmailConfirmation") should be("true")
  }

  it should "submit application and set show email message flag to true if the postal vote and contact email are present" in runningApp {
    val Some(result) = route(
      FakeRequest(POST, "/register-to-vote/confirmation")
        .withIerSession()
        .withApplication(completeOrdinaryApplication.copy(
        postalVote = Some(PostalVote(
          postalVoteOption = Some(true),
          deliveryMethod = Some(PostalVoteDeliveryMethod(
            deliveryMethod = Some("email"),
            emailAddress = Some("test@email.com")
          ))
        )),
        contact = Some(Contact(
          post = false,
          email = Some(ContactDetail(
            contactMe = true,
            detail = Some("test@email.com")
          )),
          phone = None
        ))
      ))
    )

    status(result) should be(SEE_OTHER)
    redirectLocation(result) should be(Some("/register-to-vote/complete"))
    val flashData = flash(result).data
    flashData("showEmailConfirmation") should be("true")
  }

}
