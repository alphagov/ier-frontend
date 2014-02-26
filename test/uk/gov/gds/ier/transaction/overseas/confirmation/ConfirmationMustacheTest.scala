package uk.gov.gds.ier.transaction.overseas.confirmation

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
import org.joda.time.DateTime

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

    val Some(nameModel) = confirmation.name
    nameModel.content should be("<p>John Smith</p>")
    nameModel.editLink should be("/register-to-vote/overseas/edit/name")

    val Some(prevNameModel) = confirmation.previousName
    prevNameModel.content should be("<p>Jan Kovar</p>")
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

    val Some(nameModel) = confirmation.name
    nameModel.content should be("<p>John Walker Junior Smith</p>")
    nameModel.editLink should be("/register-to-vote/overseas/edit/name")

    val Some(prevNameModel) = confirmation.previousName
    prevNameModel.content should be("<p>Jan Janko Janik Kovar</p>")
    prevNameModel.editLink should be("/register-to-vote/overseas/edit/name")
  }

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

    parentNameModel.isDefined should be (true)
    val Some(nameModel) = parentNameModel
    nameModel.content should include ("Please complete this step")
    nameModel.editLink should be ("/register-to-vote/overseas/edit/parent-name")

    parentPreviousNameModel.isDefined should be (true)
    val Some(previousNameModel) = parentPreviousNameModel
    previousNameModel.content should include ("Please complete this step")
    previousNameModel.editLink should be ("/register-to-vote/overseas/edit/parent-name")
  }
  
  it should "correctly render parent names and previous names and correct URLs" in {
    val twentyYearsAgo = new DateTime().minusYears(20).getYear
    val fourteenYearsAgo = new DateTime().minusYears(14).getYear

    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      dob = Some(DOB(twentyYearsAgo, 10, 10)),
      dateLeftUk = Some(DateLeft(fourteenYearsAgo, 10)),
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

    val Some(nameModel) = confirmation.parentName
    nameModel.content should be("<p>John Smith</p>")
    nameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")

    val Some(prevNameModel) = confirmation.parentPreviousName
    prevNameModel.content should be("<p>Jan Kovar</p>")
    prevNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")
  }


  it should "correctly render parent names and previous names with middle names" in {
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
          hasPreviousName = true,
          previousName = Some(Name(
            firstName = "Jan",
            middleNames = Some("Janko Janik"),
            lastName = "Kovar"))
        ))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)

    val Some(parentNameModel) = confirmation.parentName
    parentNameModel.content should be("<p>John Walker Junior Smith</p>")
    parentNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")

    val Some(parentPrevNameModel) = confirmation.parentPreviousName
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

    val Some(parentNameModel) = confirmation.parentName
    parentNameModel.content should be("<p>John Walker Junior Smith</p>")
    parentNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")

    val Some(parentPrevNameModel) = confirmation.parentPreviousName
    parentPrevNameModel.content should be("<p>They haven't changed their name since they left the UK</p>")
    parentPrevNameModel.editLink should be("/register-to-vote/overseas/edit/parent-name")
  }
  
  behavior of "ConfirmationBlocks.passport"

  it should "return 'complete this' message if not a renewer" in {
    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      previouslyRegistered = Some(PreviouslyRegistered(false))
    ))

    val confirmation = new ConfirmationBlocks(partialApplication)
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

    val confirmation = new ConfirmationBlocks(partialApplication)
    val passportModel = confirmation.passport

    passportModel.isDefined should be(false)
  }

  it should "return 'complete this' message if passport details only partially complete" in {
    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      previouslyRegistered = Some(PreviouslyRegistered(false)),
      passport = Some(Passport(true, None, None, None))
    ))

    val confirmation = new ConfirmationBlocks(partialApplication)
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

    val confirmation = new ConfirmationBlocks(partialApplication)
    val passportModel = confirmation.passport

    passportModel.isDefined should be(true)

    val Some(model) = passportModel
    model.content should include("Please complete this step")
    model.editLink should be("/register-to-vote/overseas/edit/citizen-details")
  }

  "application form with filled way to vote as by-post" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost))))
    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)
    val Some(nameModel) = confirmation.waysToVote
    nameModel.content should be("<p>By post</p>")
  }

  "application form with filled way to vote as by-proxy" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy))))
    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)
    val Some(nameModel) = confirmation.waysToVote
    nameModel.content should be("<p>By proxy (someone else voting for you)</p>")
  }

  "application form with filled way to vote as in-person" should
    "generate confirmation mustache model with correctly rendered way to vote type" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      waysToVote = Some(WaysToVote(WaysToVoteType.InPerson))))
    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)
    val Some(nameModel) = confirmation.waysToVote
    nameModel.content should be("<p>In the UK, at a polling station</p>")
  }

  behavior of "ConfirmationBlocks.postalVote"

  it should "return none (waysToVote not answered)" in {
    val partialApplication = confirmationForm

    val confirmation = new ConfirmationBlocks(partialApplication)
    val model = confirmation.postalOrProxyVote

    model.isDefined should be(false)
  }

  behavior of "ConfirmationBlocks.parentsAddress"
  it should "return none (over 18)" in {
    val twentyFiveYearsAgo = new DateTime().minusYears(25).getYear()
    val fiveYearsAgo = new DateTime().minusYears(5).getYear()

    val partialApplication = confirmationForm.fillAndValidate(
      InprogressOverseas(
        dob = Some(DOB(year = twentyFiveYearsAgo, month = 1, day = 1)),
        dateLeftUk = Some(DateLeft(year = fiveYearsAgo, month = 1))
      )
    )
    val confirmation = new ConfirmationBlocks(partialApplication)
    val model = confirmation.parentsAddress

    model.isDefined should be(false)
  }

  it should "return none (age not answered)" in {
    val fiveYearsAgo = new DateTime().minusYears(5).getYear()

    val partialApplication = confirmationForm.fillAndValidate(
      InprogressOverseas(
        dob = None,
        dateLeftUk = Some(DateLeft(year = fiveYearsAgo, month = 1))
      )
    )
    val confirmation = new ConfirmationBlocks(partialApplication)
    val model = confirmation.parentsAddress

    model.isDefined should be(false)
  }

  it should "return none (date left not answered)" in {
    val fifteenYearsAgo = new DateTime().minusYears(15).getYear()

    val partialApplication = confirmationForm.fillAndValidate(
      InprogressOverseas(
        dob = Some(DOB(year = fifteenYearsAgo, month = 1, day = 1)),
        dateLeftUk = None
      )
    )
    val confirmation = new ConfirmationBlocks(partialApplication)
    val model = confirmation.parentsAddress

    model.isDefined should be(false)
  }

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
    val Some(model) = confirmation.parentsAddress

    model.title should be("Parents Last UK Address")
    model.editLink should be("/register-to-vote/overseas/edit/parents-address/select")
    model.changeName should be("your parents' last UK address")
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
          manualAddress = Some("123 Manual Street")
        ))
      )
    )
    val confirmation = new ConfirmationBlocks(partialApplication)
    val Some(model) = confirmation.parentsAddress

    model.title should be("Parents Last UK Address")
    model.editLink should be("/register-to-vote/overseas/edit/parents-address/manual")
    model.changeName should be("your parents' last UK address")
    model.content should be("<p>123 Manual Street</p><p>AB12 34DC</p>")
  }

  it should "return 'complete this step'" in {
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
          manualAddress = None
        ))
      )
    )
    val confirmation = new ConfirmationBlocks(partialApplication)
    val Some(model) = confirmation.parentsAddress

    model.title should be("Parents Last UK Address")
    model.editLink should be("/register-to-vote/overseas/edit/parents-address")
    model.changeName should be("your parents' last UK address")
    model.content should be(
      "<div class=\"validation-message visible\">Please complete this step</div>"
    )
  }
}
