package uk.gov.gds.ier.transaction.forces.confirmation

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.test.TestHelpers
import play.api.test.Helpers._
import play.api.test.FakeRequest
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.controller.MockConfig
import uk.gov.gds.ier.security.{Base64EncodingService, EncryptionService}
import uk.gov.gds.ier.transaction.complete.ConfirmationCookie

/**
 * Test ConfirmationStep and ConfirmationController for Ordinary route,
 * notably application submission
 */
class ConfirmationStepTest extends FlatSpec
with Matchers
with MockitoSugar
with TestHelpers {

  val config = new MockConfig
  implicit val serialiser = jsonSerialiser
  implicit val encryptionService = new EncryptionService (new Base64EncodingService, config)

  behavior of "ConfirmationStep.post submit application and set Refnum and LocalAuthority for the next step"

    running(FakeApplication()) {
      val Some(resultFuture) = route(
        FakeRequest(POST, "/register-to-vote/forces/confirmation")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(address =
            Some(LastAddress(
              hasAddress = Some(HasAddressOption.YesAndLivingThere),
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

      it should "redirect to Complete page" in {
        status(resultFuture) should be(SEE_OTHER)
        redirectLocation(resultFuture) should be(Some("/register-to-vote/complete"))
      }

      val allCookies = cookies(resultFuture)

      it should "delete application inprogress data, delete main cookie but not session" in {
        allCookies.get(sessionPayloadKey) should not be (None)
        allCookies.get(sessionPayloadKey).get.value should be("")
      }

      it should "add new cookie with confirmation data for Complete page" in {
        allCookies.get(confirmationCookieKey) should not be (None)
        allCookies.get(confirmationCookieKey).get.value.trim should not be empty
      }

      "content of Confirmation cookie" should "contain refnum and local ERO authority details" in {
        val result = getConfirmationCookie(allCookies)
        result should not be (None)
        result.get.refNum.trim should not be ("")
        result.get.hasOtherAddress should be(false)
        result.get.backToStartUrl should be("/register-to-vote")
        result.get.authority should be(Some(EroAuthorityDetails(
          name = "Malvern Hills (test)",
          urls = List(
            "http://www.malvernhills.gov.uk/",
            "http://www.malvernhills.gov.uk/cms/council-and-democracy/elections.aspx"
          ),
          email = Some("worcestershirehub@malvernhills.gov.uk.test"),
          addressLine1 = Some("Council House"),
          addressLine2 = Some("Avenue Road"),
          addressLine3 = Some("Malvern"),
          addressLine4 = Some(""),
          postcode = Some("WR14 3AF"),
          phone = Some("01684 862151")
        )))
      }
    }

//  behavior of "ConfirmationStep.post"
//  it should "submit application and set Refnum and LocalAuthority for the next step" in {
//    running(FakeApplication()) {
//      val Some(result) = route(
//        FakeRequest(POST, "/register-to-vote/forces/confirmation")
//          .withIerSession()
//          .withApplication(completeForcesApplication.copy(address =
//            Some(LastAddress(
//              hasAddress = Some(HasAddressOption.YesAndLivingThere),
//              address = Some(PartialAddress(
//                addressLine = Some("1 The Cottages, Moseley Road, Hallow, Worcestershire"),
//                uprn = Some("100120595384"),
//                postcode = "WR2 6NJ",
//                gssCode = Some("E07000235"),
//                manualAddress = None
//              ))
//            ))
//          ))
//      )
//
//      status(result) should be(SEE_OTHER)
//      redirectLocation(result) should be(Some("/register-to-vote/complete"))
//      val flashData = flash(result).data
//      flashData("refNum") should not be(None)
//      flashData("localAuthority") should not be(None)
//      val localAuthority = jsonSerialiser.fromJson[EroAuthorityDetails](flashData("localAuthority"))
//
//      localAuthority should be(EroAuthorityDetails(
//        name = "Malvern Hills (test)",
//        urls  = List(
//          "http://www.malvernhills.gov.uk/",
//          "http://www.malvernhills.gov.uk/cms/council-and-democracy/elections.aspx"
//        ),
//        email  = Some("worcestershirehub@malvernhills.gov.uk.test"),
//        addressLine1 = Some("Council House"),
//        addressLine2 = Some("Avenue Road"),
//        addressLine3 = Some("Malvern"),
//        addressLine4 = Some(""),
//        postcode = Some("WR14 3AF"),
//        phone = Some("01684 862151")
//      ))
//    }
//  }

  behavior of "showEmailConfirmation flag"

  it should "submit application and set show email message flag to false for no email addresses present" in runningApp {
    val Some(result) = route(
      FakeRequest(POST, "/register-to-vote/forces/confirmation")
        .withIerSession()
        .withApplication(completeForcesApplication.copy(
        waysToVote = Some(WaysToVote(WaysToVoteType.InPerson)),
        postalOrProxyVote = None,
        contact = Some(Contact(
          post = true,
          email = None,
          phone = None
        ))
      ))
    )

    status(result) should be(SEE_OTHER)
    redirectLocation(result) should be(Some("/register-to-vote/complete"))
    val allCookies = cookies(result)
    val completeStepData = getConfirmationCookie(allCookies)
    completeStepData should not be(None)
    completeStepData.get.showEmailConfirmation should be(false)
  }



  it should "submit application and set show email message flag to true if contact email is present" in runningApp {
    val Some(result) = route(
      FakeRequest(POST, "/register-to-vote/forces/confirmation")
        .withIerSession()
        .withApplication(completeForcesApplication.copy(
        waysToVote = Some(WaysToVote(WaysToVoteType.InPerson)),
        postalOrProxyVote = None,
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
    val allCookies = cookies(result)
    val completeStepData = getConfirmationCookie(allCookies)
    completeStepData should not be(None)
    completeStepData.get.showEmailConfirmation should be(true)
  }

  it should "submit application and set show email message flag to true if postal email is present" in runningApp {
    val Some(result) = route(
      FakeRequest(POST, "/register-to-vote/forces/confirmation")
        .withIerSession()
        .withApplication(completeForcesApplication.copy(
        waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
        postalOrProxyVote = Some(PostalOrProxyVote(
          typeVote = WaysToVoteType.ByPost,
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
    val allCookies = cookies(result)
    val completeStepData = getConfirmationCookie(allCookies)
    completeStepData should not be(None)
    completeStepData.get.showEmailConfirmation should be(true)
  }

  it should "submit application and set show email message flag to true if proxy postal email is present" in runningApp {
    val Some(result) = route(
      FakeRequest(POST, "/register-to-vote/forces/confirmation")
        .withIerSession()
        .withApplication(completeForcesApplication.copy(
        waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
        postalOrProxyVote = Some(PostalOrProxyVote(
          typeVote = WaysToVoteType.ByPost,
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
    val allCookies = cookies(result)
    val completeStepData = getConfirmationCookie(allCookies)
    completeStepData should not be(None)
    completeStepData.get.showEmailConfirmation should be(true)
  }

  it should "submit application and set show email message flag to true if postal email and contact email are present" in runningApp {
    val Some(result) = route(
      FakeRequest(POST, "/register-to-vote/forces/confirmation")
        .withIerSession()
        .withApplication(completeForcesApplication.copy(
        waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
        postalOrProxyVote = Some(PostalOrProxyVote(
          typeVote = WaysToVoteType.ByPost,
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
    val allCookies = cookies(result)
    val completeStepData = getConfirmationCookie(allCookies)
    completeStepData should not be(None)
    completeStepData.get.showEmailConfirmation should be(true)
  }
}
