package uk.gov.gds.ier.transaction.overseas.parentName

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.model.OverseasParentName
import uk.gov.gds.ier.model.Name
import uk.gov.gds.ier.model.PreviousName

class ParentNameMustacheTest
  extends MustacheTestSuite
  with ParentNameForms
  with ParentNameMustache {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = parentNameForm
    val nameModel = mustache.data(
      emptyApplicationForm,
      new Call("POST","/register-to-vote/overseas/parent-name"),
      InprogressOverseas()
    ).asInstanceOf[ParentNameModel]

    nameModel.question.title should be("Parent or guardian's registration details")
    nameModel.question.postUrl should be("/register-to-vote/overseas/parent-name")

    nameModel.firstName.value should be("")
    nameModel.middleNames.value should be("")
    nameModel.lastName.value should be("")
    nameModel.hasPreviousNameTrue.attributes should be("")
    nameModel.hasPreviousNameFalse.attributes should be("")
    nameModel.previousFirstName.value should be("")
    nameModel.previousMiddleNames.value should be("")
    nameModel.previousLastName.value should be("")
  }

  it should "progress form with filled applicant parent name with hasPrevious should produce Mustache Model with name values present" in {
    val partiallyFilledApplicationForm = parentNameForm.fill(InprogressOverseas(
      overseasParentName = Some(OverseasParentName(name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = "Smith")),
      previousName = Some(PreviousName(false, None))))))

    val nameModel = mustache.data(
      partiallyFilledApplicationForm,
      new Call("POST","/register-to-vote/overseas/parent-name"),
      InprogressOverseas()
    ).asInstanceOf[ParentNameModel]

    nameModel.question.title should be("Parent or guardian's registration details")
    nameModel.question.postUrl should be("/register-to-vote/overseas/parent-name")

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
      overseasParentName = Some(OverseasParentName(name = Some(Name(
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
      new Call("POST","/register-to-vote/overseas/parent-name"),
      InprogressOverseas()
    ).asInstanceOf[ParentNameModel]

    nameModel.question.title should be("Parent or guardian's registration details")
    nameModel.question.postUrl should be("/register-to-vote/overseas/parent-name")

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
      overseasParentName = Some(OverseasParentName(name = Some(Name(
        firstName = "John",
        middleNames = None,
        lastName = ""))))))

    val nameModel = mustache.data(
      partiallyFilledApplicationFormWithErrors,
      new Call("POST","/register-to-vote/overseas/parent-name"),
      InprogressOverseas()
    ).asInstanceOf[ParentNameModel]

    nameModel.question.title should be("Parent or guardian's registration details")
    nameModel.question.postUrl should be("/register-to-vote/overseas/parent-name")

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
