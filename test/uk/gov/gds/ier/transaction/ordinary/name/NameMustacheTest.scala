package uk.gov.gds.ier.transaction.ordinary.name

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.Name
import uk.gov.gds.ier.model.PreviousName
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

/**
 * Unit test to test form to Mustache model transformation.
 *
 * Testing Mustache html text rendering requires running application, it is not easily unit testable,
 * so method {@link NameMustache#nameMustache()} is tested as a part of MustacheControllerTest.
 */
class NameMustacheTest
  extends FlatSpec
  with Matchers
  with NameForms
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with NameMustache {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = nameForm
    val nameModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/name"),
      InprogressOrdinary()
    ).data.asInstanceOf[NameModel]

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/name")

    nameModel.firstName.value should be("")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("")
    nameModel.hasPreviousNameTrue.value should be("true")
    nameModel.hasPreviousNameFalse.value should be("false")
    nameModel.previousFirstName.value should be("")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("")
  }

  it should "progress form with filled applicant name should produce Mustache Model with name values present" in {
    val partiallyFilledApplicationForm = nameForm.fill(InprogressOrdinary(
      name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith"))))
    val nameModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/name"),
      InprogressOrdinary()
    ).data.asInstanceOf[NameModel]

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/name")

    nameModel.firstName.value should be("John")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("Smith")
    nameModel.hasPreviousNameTrue.value should be("true")
    nameModel.hasPreviousNameFalse.value should be("false")
    nameModel.previousFirstName.value should be("")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("")
  }

  it should "progress form with filled applicant name and previous should produce Mustache Model with name and previous name values present" in {
    val partiallyFilledApplicationForm = nameForm.fill(InprogressOrdinary(
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
    val nameModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/name"),
      InprogressOrdinary()
    ).data.asInstanceOf[NameModel]

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/name")

    nameModel.firstName.value should be("John")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("Smith")
    nameModel.hasPreviousNameTrue.attributes should be("checked=\"checked\"")
    nameModel.hasPreviousNameFalse.attributes should be("")
    nameModel.previousFirstName.value should be("Jan")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("Kovar")
  }

  it should "progress form with validation errors should produce Model with error list present" in {
    val partiallyFilledApplicationFormWithErrors = nameForm.fillAndValidate(InprogressOrdinary(
      name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = ""))))
    val nameModel = mustache.data(
      partiallyFilledApplicationFormWithErrors,
      Call("POST", "/register-to-vote/name"),
      InprogressOrdinary()
    ).data.asInstanceOf[NameModel]

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/name")

    nameModel.firstName.value should be("John")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("")
    nameModel.hasPreviousNameTrue.value should be("true")
    nameModel.hasPreviousNameFalse.value should be("false")
    nameModel.previousFirstName.value should be("")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("")

    nameModel.question.errorMessages.mkString(", ") should be("Please enter your last name, Please answer this question")
  }
}
