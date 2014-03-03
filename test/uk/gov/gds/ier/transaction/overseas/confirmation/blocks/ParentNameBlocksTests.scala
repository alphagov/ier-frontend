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

class ParentNameBlocksTests
  extends FlatSpec
  with Matchers
  with ConfirmationForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  behavior of "confirmationBlocks.parentName"
  
  it should "return 'complete this' (age > 18, leftuk < 15 years, and step incomplete)" in {
    val twentyYearsAgo = new DateTime().minusYears(20).getYear
    val fourteenYearsAgo = new DateTime().minusYears(14).getYear
    
    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      dob = Some(DOB(twentyYearsAgo, 10, 10)),
      dateLeftUk = Some(DateLeft(fourteenYearsAgo, 10))
    ))

    partialApplication(keys.overseasParentName.parentName.key).hasErrors should be (true)

    val confirmation = new ConfirmationBlocks(partialApplication)
    val parentNameModel = confirmation.parentName
    val parentPreviousNameModel = confirmation.parentPreviousName

    val nameModel = parentNameModel
    nameModel.content should include ("Please complete this step")
    nameModel.editLink should be ("/register-to-vote/overseas/edit/parent-name")

    val previousNameModel = parentPreviousNameModel
    previousNameModel.content should include ("Please complete this step")
    previousNameModel.editLink should be ("/register-to-vote/overseas/edit/parent-name")
  }

  it should "correctly render parent names and previous names and correct URLs" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      overseasParentName = Some(OverseasName(
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
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val nameModel = confirmation.parentName
    nameModel.content should be("<p>John Smith</p>")
    nameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")

    val prevNameModel = confirmation.parentPreviousName
    prevNameModel.content should be("<p>Jan Kovar</p>")
    prevNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")
  }


  it should "correctly render parent names and previous names with middle names" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      overseasParentName = Some(OverseasName(
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
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val parentNameModel = confirmation.parentName
    parentNameModel.content should be("<p>John Walker Junior Smith</p>")
    parentNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")

    val parentPrevNameModel = confirmation.parentPreviousName
    parentPrevNameModel.content should be("<p>Jan Janko Janik Kovar</p>")
    parentPrevNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")
  }

  it should "correctly render parent names" in {
    val twentyYearsAgo = new DateTime().minusYears(20).getYear
    val fourteenYearsAgo = new DateTime().minusYears(14).getYear
    
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      dob = Some(DOB(twentyYearsAgo, 10, 10)),
      dateLeftUk = Some(DateLeft(fourteenYearsAgo, 10)),
      overseasParentName = Some(OverseasName(
        name = Some(Name(
          firstName = "John",
          middleNames = Some("Walker Junior"),
          lastName = "Smith")),
        previousName = Some(PreviousName(
          hasPreviousName = false,
          previousName = None
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val parentNameModel = confirmation.parentName
    parentNameModel.content should be("<p>John Walker Junior Smith</p>")
    parentNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")

    val parentPrevNameModel = confirmation.parentPreviousName
    parentPrevNameModel.content should be("<p>They haven't changed their name since they left the UK</p>")
    parentPrevNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")
  }
}
