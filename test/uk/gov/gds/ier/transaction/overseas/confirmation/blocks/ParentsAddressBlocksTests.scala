package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import org.joda.time.DateTime
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{
  InprogressOverseas,
  Name,
  PreviousName,
  WaysToVote}
import uk.gov.gds.ier.transaction.overseas.confirmation.ConfirmationForms
import org.joda.time.DateTime

class ParentsAddressBlocksTests
  extends FlatSpec
  with Matchers
  with ConfirmationForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  behavior of "ConfirmationBlocks.parentsAddress"
  it should "return parents address" in {
    val twentyYearsAgo = new DateTime().minusYears(20).getYear()
    val fiveYearsAgo = new DateTime().minusYears(5).getYear()

    val partialApplication = confirmationForm.fillAndValidate(
      InprogressOverseas(
        dob = Some(DOB(year = twentyYearsAgo, month = 1, day = 1)),
        dateLeftUk = Some(DateLeft(year = fiveYearsAgo, month = 1)),
        parentsAddress = Some(PartialAddress(
          addressLine = Some("123 Fake Street"),
          postcode = "AB12 34DC",
          uprn = Some("12345678"),
          manualAddress = None
        ))
      )
    )
    val confirmation = new ConfirmationBlocks(partialApplication)
    val model = confirmation.parentsAddress

    model.title should be("Parent's or guardian's last UK address")
    model.editLink should be("/register-to-vote/overseas/edit/parents-address/select")
    model.changeName should be("your parent's or guardian's last UK address")
    model.content should be("<p>123 Fake Street</p><p>AB12 34DC</p>")
  }

  it should "return parents manual address" in {
    val twentyYearsAgo = new DateTime().minusYears(20).getYear()
    val fiveYearsAgo = new DateTime().minusYears(5).getYear()

    val partialApplication = confirmationForm.fillAndValidate(
      InprogressOverseas(
        dob = Some(DOB(year = twentyYearsAgo, month = 1, day = 1)),
        dateLeftUk = Some(DateLeft(year = fiveYearsAgo, month = 1)),
        parentsAddress = Some(PartialAddress(
          addressLine = None,
          postcode = "AB12 34DC",
          uprn = None,
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")))
        ))
      )
    )
    val confirmation = new ConfirmationBlocks(partialApplication)
    val model = confirmation.parentsAddress

    model.title should be("Parent's or guardian's last UK address")
    model.editLink should be("/register-to-vote/overseas/edit/parents-address/manual")
    model.changeName should be("your parent's or guardian's last UK address")
    model.content should be("" +
      "<p>Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester</p>" +
      "<p>AB12 34DC</p>")
  }

  it should "return 'complete this step'" in {

    val partialApplication = confirmationForm.fillAndValidate(
      incompleteYoungApplication.copy(
        parentsAddress = Some(PartialAddress(
          addressLine = None,
          postcode = "AB12 34DC",
          uprn = None,
          manualAddress = None
        ))
      )
    )
    val confirmation = new ConfirmationBlocks(partialApplication)
    val model = confirmation.parentsAddress

    model.title should be("Parent's or guardian's last UK address")
    model.editLink should be("/register-to-vote/overseas/edit/parents-address")
    model.changeName should be("your parent's or guardian's last UK address")
    model.content should be(
      "<div class=\"validation-message visible\">Please complete this step</div>"
    )
  }
}

