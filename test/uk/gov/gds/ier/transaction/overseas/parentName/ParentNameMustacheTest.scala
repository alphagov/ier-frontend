package uk.gov.gds.ier.transaction.overseas.parentName

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{Name, OverseasParentName, PreviousName, InprogressOverseas, 
  ParentName, ParentPreviousName}

/**
 * Unit test to test form to Mustache model transformation.
 *
 * Testing Mustache html text rendering requires running application, it is not easily unit testable,
 * so method {@link NameMustache#nameMustache()} is tested as a part of MustacheControllerTest.
 */
class ParentNameMustacheTest
  extends FlatSpec
  with Matchers
  with ParentNameForms
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val parentNameMustache = new ParentNameMustache {}

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = parentNameForm
    val nameModel = parentNameMustache.transformFormStepToMustacheData(emptyApplicationForm, 
        "/register-to-vote/overseas/parent-name", Some("/register-to-vote/overseas/last-registered-uk-address"))

    nameModel.question.title should be("Register to Vote - Parent or guardian's registration details")
    nameModel.question.postUrl should be("/register-to-vote/overseas/parent-name")
    nameModel.question.backUrl should be("/register-to-vote/overseas/last-registered-uk-address")

    nameModel.firstName.value should be("")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("")
//    nameModel.hasPreviousName.classes should be ("invalid")
    nameModel.hasPreviousNameTrue.attributes should be("")
    nameModel.hasPreviousNameFalse.attributes should be("")
    nameModel.previousFirstName.value should be("")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("")
  }

  it should "progress form with filled applicant parent name with hasPrevious should produce Mustache Model with name values present" in {
    val partiallyFilledApplicationForm = parentNameForm.fill(InprogressOverseas(
      overseasParentName = Some(OverseasParentName(name = Some(ParentName(
        firstName = "John",
        middleNames = None,
        lastName = "Smith")),
      previousName = Some(ParentPreviousName(false, None))))))
    val nameModel = parentNameMustache.transformFormStepToMustacheData(partiallyFilledApplicationForm, 
        "/register-to-vote/overseas/parent-name", Some("/register-to-vote/overseas/last-registered-uk-address"))

    nameModel.question.title should be("Register to Vote - Parent or guardian's registration details")
    nameModel.question.postUrl should be("/register-to-vote/overseas/parent-name")
    nameModel.question.backUrl should be("/register-to-vote/overseas/last-registered-uk-address")

    nameModel.firstName.value should be("John")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("Smith")
    nameModel.hasPreviousName.classes should be("")
    nameModel.hasPreviousNameTrue.attributes should be("")
    nameModel.hasPreviousNameFalse.attributes should be("checked=\"checked\"")
    nameModel.previousFirstName.value should be("")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("")
  }

  it should "progress form with filled applicant name and previous should produce Mustache Model with name and previous name values present" in {
    val partiallyFilledApplicationForm = parentNameForm.fill(InprogressOverseas(
      overseasParentName = Some(OverseasParentName(name = Some(ParentName(
        firstName = "John",
        middleNames = None,
        lastName = "Smith")),
      previousName = Some(ParentPreviousName(
        hasPreviousName = true,
        previousName = Some(ParentName(
          firstName = "Jan",
          middleNames = None,
          lastName = "Kovar"))
      ))))
    ))
    val nameModel = parentNameMustache.transformFormStepToMustacheData(partiallyFilledApplicationForm, 
        "/register-to-vote/overseas/parent-name", Some("/register-to-vote/overseas/last-registered-uk-address"))

    nameModel.question.title should be("Register to Vote - Parent or guardian's registration details")
    nameModel.question.postUrl should be("/register-to-vote/overseas/parent-name")
    nameModel.question.backUrl should be("/register-to-vote/overseas/last-registered-uk-address")

    nameModel.firstName.value should be("John")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("Smith")
    nameModel.hasPreviousName.classes should be("")
    nameModel.hasPreviousNameTrue.attributes should be("checked=\"checked\"")
    nameModel.hasPreviousNameFalse.attributes should be("")
    nameModel.previousFirstName.value should be("Jan")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("Kovar")
  }

  it should "progress form with validation errors should produce Model with error list present" in {
    val partiallyFilledApplicationFormWithErrors = parentNameForm.fillAndValidate(InprogressOverseas(
      overseasParentName = Some(OverseasParentName(name = Some(ParentName(
        firstName = "John",
        middleNames = None,
        lastName = ""))))))
    val nameModel = parentNameMustache.transformFormStepToMustacheData(partiallyFilledApplicationFormWithErrors, 
        "/register-to-vote/overseas/parent-name", Some("/register-to-vote/overseas/last-registered-uk-address"))

    nameModel.question.title should be("Register to Vote - Parent or guardian's registration details")
    nameModel.question.postUrl should be("/register-to-vote/overseas/parent-name")
    nameModel.question.backUrl should be("/register-to-vote/overseas/last-registered-uk-address")

    nameModel.firstName.value should be("John")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("")
    nameModel.hasPreviousName.classes should be("invalid")
    nameModel.hasPreviousNameTrue.attributes should be("")
    nameModel.hasPreviousNameFalse.attributes should be("")
    nameModel.previousFirstName.value should be("")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("")

    nameModel.question.errorMessages should be(Seq("Please enter their last name", "Please answer this question"))
  }
}
