package uk.gov.gds.ier.transaction.overseas.name

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{Name, PreviousName, OverseasName}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class OverseasNameMustacheTest
  extends FlatSpec
  with Matchers
  with NameForms
  with NameMustache
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = nameForm
    val nameModel = mustache.data(
      emptyApplicationForm,
      new Call("POST", "/register-to-vote/overseas/name"),
      Some(new Call("GET", "/register-to-vote/overseas/last-registered-uk-address")),
      InprogressOverseas()
    ).data.asInstanceOf[NameModel]

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/overseas/name")
    nameModel.question.backUrl should be("/register-to-vote/overseas/last-registered-uk-address")

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
    val partiallyFilledApplicationForm = nameForm.fill(InprogressOverseas(
      overseasName = Some(OverseasName(name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith"))))))

    val nameModel = mustache.data(
      partiallyFilledApplicationForm,
      new Call("POST", "/register-to-vote/overseas/name"),
      Some(new Call("GET", "/register-to-vote/overseas/last-registered-uk-address")),
        InprogressOverseas()
      ).data.asInstanceOf[NameModel]

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/overseas/name")
    nameModel.question.backUrl should be("/register-to-vote/overseas/last-registered-uk-address")

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
    val partiallyFilledApplicationForm = nameForm.fill(InprogressOverseas(
      overseasName = Some(OverseasName(name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith")),
      previousName = Some(PreviousName(
        hasPreviousName = true,
        previousName = Some(Name(
          firstName = "Jan",
          middleNames = None,
          lastName = "Kovar"))
      ))))
    ))

    val nameModel = mustache.data(
      partiallyFilledApplicationForm,
      new Call("POST", "/register-to-vote/overseas/name"),
      Some(new Call("GET", "/register-to-vote/overseas/last-registered-uk-address")),
      InprogressOverseas()
    ).data.asInstanceOf[NameModel]

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/overseas/name")
    nameModel.question.backUrl should be("/register-to-vote/overseas/last-registered-uk-address")

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
    val partiallyFilledApplicationFormWithErrors = nameForm.fillAndValidate(InprogressOverseas(
      overseasName = Some(OverseasName(name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = ""))))))

    val nameModel = mustache.data(
      partiallyFilledApplicationFormWithErrors,
      new Call("POST", "/register-to-vote/overseas/name"),
      Some(new Call("GET", "/register-to-vote/overseas/last-registered-uk-address")),
      InprogressOverseas()
    ).data.asInstanceOf[NameModel]

    nameModel.question.title should be("What is your full name?")
    nameModel.question.postUrl should be("/register-to-vote/overseas/name")
    nameModel.question.backUrl should be("/register-to-vote/overseas/last-registered-uk-address")

    nameModel.firstName.value should be("John")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("")
    nameModel.hasPreviousNameTrue.value should be("true")
    nameModel.hasPreviousNameFalse.value should be("false")
    nameModel.previousFirstName.value should be("")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("")

    nameModel.question.errorMessages.mkString(", ") should be(
      "Please enter your last name, Please answer this question")
  }
}
