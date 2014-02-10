package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{InProgressForm, ErrorMessages, FormKeys}
import uk.gov.gds.guice.GuiceContainer

class ConfirmationMustacheTest
  extends FlatSpec
  with Matchers
  with ConfirmationForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with ConfirmationMustache {

  val serialiser = jsonSerialiser

  "In-progress application form with filled name and previous name" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith")),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        previousName = Some(Name(
          firstName = "Jan",
          middleNames = None,
          lastName = "Kovar"))
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))
    
    val nameModel = confirmation.name
    nameModel.content should be("<p>John Smith</p>")
    nameModel.editLink should be("/register-to-vote/overseas/edit/name")

    val prevNameModel = confirmation.previousName
    prevNameModel.content should be("<p>Jan Kovar</p>")
    prevNameModel.editLink should be("/register-to-vote/overseas/edit/name")
  }


  "In-progress application form with filled name and previous name with middle names" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      name = Some(Name(
        firstName = "John",
        middleNames = Some("Walker Junior"),
        lastName = "Smith")),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        previousName = Some(Name(
          firstName = "Jan",
          middleNames = Some("Janko Janik"),
          lastName = "Kovar"))
      ))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partiallyFilledApplicationForm))
    
    val nameModel = confirmation.name
    nameModel.content should be("<p>John Walker Junior Smith</p>")
    nameModel.editLink should be("/register-to-vote/overseas/edit/name")

    val prevNameModel = confirmation.previousName
    prevNameModel.content should be("<p>Jan Janko Janik Kovar</p>")
    prevNameModel.editLink should be("/register-to-vote/overseas/edit/name")
  }

  behavior of "ConfirmationBlocks.passport"

  it should "return 'complete this' message if not a renewer" in {
    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      previouslyRegistered = Some(PreviouslyRegistered(false))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partialApplication))
    val passportModel = confirmation.passport

    passportModel.isDefined should be(true)

    val Some(model) = passportModel
    model.content should include("Please complete this step")
    model.editLink should be("/register-to-vote/overseas/edit/passport")
  }

  it should "return None if a renewer" in {
    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      previouslyRegistered = Some(PreviouslyRegistered(true))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partialApplication))
    val passportModel = confirmation.passport

    passportModel.isDefined should be(false)
  }

  it should "return 'complete this' message if passport details only partially complete" in {
    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      previouslyRegistered = Some(PreviouslyRegistered(false)),
      passport = Some(Passport(true, None, None, None))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partialApplication))
    val passportModel = confirmation.passport

    passportModel.isDefined should be(true)

    val Some(model) = passportModel
    model.content should include("Please complete this step")
    model.editLink should be("/register-to-vote/overseas/edit/passport-details")
  }

  it should "return 'complete this' message if citizen details only partially complete" in {
    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      previouslyRegistered = Some(PreviouslyRegistered(false)),
      passport = Some(Passport(false, Some(false), None, None))
    ))

    val confirmation = new ConfirmationBlocks(InProgressForm(partialApplication))
    val passportModel = confirmation.passport

    passportModel.isDefined should be(true)

    val Some(model) = passportModel
    model.content should include("Please complete this step")
    model.editLink should be("/register-to-vote/overseas/edit/citizen-details")
  }
}
