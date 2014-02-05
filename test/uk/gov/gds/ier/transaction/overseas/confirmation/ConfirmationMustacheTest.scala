package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{PreviousName, Name, InprogressOverseas}
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
    val partiallyFilledApplicationForm = confirmationForm.fill(InprogressOverseas(
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
    val partiallyFilledApplicationForm = confirmationForm.fill(InprogressOverseas(
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
}
