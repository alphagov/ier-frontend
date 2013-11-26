package uk.gov.gds.ier.controller

import play.api.test._
import play.api.test.Helpers._
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.test.TestHelpers

class ConfirmationController
  extends FlatSpec
  with Matchers
  with TestHelpers {

  it should "display no errors with a full inprogress application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/confirmation").withIerSession(3,
          Some(InprogressApplication(
            name = Some(Name("john", Some("johhny"), "Smith")),
            previousName = Some(PreviousName(false, None)),
            dob = Some(DateOfBirth(1988, 1, 1)),
            nationality = Some(Nationality(Some(true), Some(true), Some(false),
              List.empty, None, Some(List("GB", "IE")))),
            nino = Some(Nino(Some("AB 12 34 56 D"), None)),
            address = Some(Address(Some("123 Fake Street"), "BT12 34D")),
            previousAddress = Some(PreviousAddress(false, None)),
            otherAddress = Some(OtherAddress(false)),
            openRegisterOptin = Some(false),
            postalVoteOptin = Some(false),
            contact = Some(Contact(true, None, None, None)),
            possibleAddresses = None,
            country = None
          ))
        )
      )
      status(result) should be(OK)
      contentAsString(result) shouldNot include("Please answer this question")
    }
  }
  it should "display all errors relevant for an empty inprogress application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/confirmation").withIerSession(3,
          Some(InprogressApplication(
            name = None, previousName = None, dob = None, nationality = None,
            nino = None, address = None, previousAddress = None, otherAddress = None,
            openRegisterOptin = None, postalVoteOptin = None, contact = None,
            possibleAddresses = None, country = None
          ))
        )
      )
      status(result) should be(OK)
      contentAsString(result) should include("data-for=\"name\">Please complete this step")
      contentAsString(result) should include("data-for=\"previousName\">Please complete this step")
      contentAsString(result) should include("data-for=\"dob\">Please complete this step")
      contentAsString(result) should include("data-for=\"nationality\">Please complete this step")
      contentAsString(result) should include("data-for=\"NINO\">Please complete this step")
      contentAsString(result) should include("data-for=\"address\">Please complete this step")
      contentAsString(result) should include("data-for=\"previousAddress\">Please complete this step")
      contentAsString(result) should include("data-for=\"otherAddress\">Please complete this step")
      contentAsString(result) should include("data-for=\"openRegister\">Please complete this step")
      contentAsString(result) should include("data-for=\"postalVote\">Please complete this step")
      contentAsString(result) should include("data-for=\"contact\">Please complete this step")
      contentAsString(result) shouldNot include("data-for=\"possibleAddress\">Please complete this step")
      contentAsString(result) shouldNot include("data-for=\"country\">Please complete this step")
    }
  }
}
