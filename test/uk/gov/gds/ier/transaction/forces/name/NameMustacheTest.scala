package uk.gov.gds.ier.transaction.forces.name

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.Name
import uk.gov.gds.ier.model.InprogressForces
import scala.Some

class NameMustacheTest
  extends FlatSpec
  with Matchers
  with NameForms
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  // tested unit
  val nameMustache = new NameMustache {}

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = nameForm
    val nameModel = nameMustache.transformFormStepToMustacheData(emptyApplicationForm, "/register-to-vote/name", Some("/register-to-vote/date-of-birth"))

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/name")
    nameModel.question.backUrl should be("/register-to-vote/date-of-birth")

    nameModel.firstName.value should be("")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("")

  }

  it should "progress form with filled applicant name should produce Mustache Model with name values present" in {
    val partiallyFilledApplicationForm = nameForm.fill(InprogressForces(
      name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith"))))
    val nameModel = nameMustache.transformFormStepToMustacheData(partiallyFilledApplicationForm, "/register-to-vote/name", Some("/register-to-vote/date-of-birth"))

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/name")
    nameModel.question.backUrl should be("/register-to-vote/date-of-birth")

    nameModel.firstName.value should be("John")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("Smith")

  }

  it should "progress form with filled applicant name and previous should produce Mustache Model with name and previous name values present" in {
    val partiallyFilledApplicationForm = nameForm.fill(InprogressForces(
      name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith"))
    ))
    val nameModel = nameMustache.transformFormStepToMustacheData(partiallyFilledApplicationForm, "/register-to-vote/name", Some("/register-to-vote/date-of-birth"))

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/name")
    nameModel.question.backUrl should be("/register-to-vote/date-of-birth")

    nameModel.firstName.value should be("John")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("Smith")

  }

  it should "progress form with validation errors should produce Model with error list present" in {
    val partiallyFilledApplicationFormWithErrors = nameForm.fillAndValidate(InprogressForces(
      name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = ""))))
    val nameModel = nameMustache.transformFormStepToMustacheData(partiallyFilledApplicationFormWithErrors, "/register-to-vote/name", Some("/register-to-vote/date-of-birth"))

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/name")
    nameModel.question.backUrl should be("/register-to-vote/date-of-birth")

    nameModel.firstName.value should be("John")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("")

    nameModel.question.errorMessages.mkString(", ") should be("Please enter your last name")
  }
}
