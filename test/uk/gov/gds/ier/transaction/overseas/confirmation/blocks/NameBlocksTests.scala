package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import org.joda.time.DateTime
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{
  Name,
  PreviousName,
  WaysToVote}
import uk.gov.gds.ier.transaction.overseas.confirmation.ConfirmationForms
import org.joda.time.DateTime
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.transaction.shared.BlockContent

class NameBlocksTests
  extends FlatSpec
  with Matchers
  with ConfirmationForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  "In-progress application form with filled name and previous name" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      overseasName = Some(OverseasName(
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
    ))))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val nameModel = confirmation.name
    nameModel.content should be(BlockContent("John Smith"))
    nameModel.editLink should be("/register-to-vote/overseas/edit/name")

    val prevNameModel = confirmation.previousName
    prevNameModel.content should be(BlockContent("Jan Kovar"))
    prevNameModel.editLink should be("/register-to-vote/overseas/edit/name")
  }

  "In-progress application form with filled name and previous name with middle names" should
    "generate confirmation mustache model with correctly rendered names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      overseasName = Some(OverseasName(
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
    ))))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val nameModel = confirmation.name
    nameModel.content should be(BlockContent("John Walker Junior Smith"))
    nameModel.editLink should be("/register-to-vote/overseas/edit/name")

    val prevNameModel = confirmation.previousName
    prevNameModel.content should be(BlockContent("Jan Janko Janik Kovar"))
    prevNameModel.editLink should be("/register-to-vote/overseas/edit/name")
  }
}
