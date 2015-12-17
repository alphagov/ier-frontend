package uk.gov.gds.ier.transaction.overseas.previousName

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class PreviousNameMustacheTest
  extends MustacheTestSuite
  with PreviousNameMustache
  with PreviousNameForms {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = previousNameForm
    val previousNameModel = mustache.data(
      emptyApplicationForm,
      Call("GET", "/register-to-vote/overseas/previous-name"),
      InprogressOverseas()
    ).asInstanceOf[PreviousNameModel]

    previousNameModel.question.title should be("What was your name when you left the UK?")
    previousNameModel.question.postUrl should be("/register-to-vote/overseas/previous-name")

    previousNameModel.previousFirstName.value should be("")
    previousNameModel.previousMiddleNames.value should be("")
    previousNameModel.previousLastName.value should be("")
  }

  it should "progress form with filled applicant name and previous should produce Mustache Model with name and previous name values present" in {
    val partiallyFilledApplicationForm = previousNameForm.fill(InprogressOverseas(
      previousName = Some(PreviousName(
        hasPreviousName = true,
        hasPreviousNameOption = "true",
        previousName = Some(Name(
          firstName = "Jan",
          middleNames = None,
          lastName = "Kovar"
        )),
        changedNameBeforeLeavingUKOption = Some("true")
      ))
    ))

    val previousNameModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("GET", "/register-to-vote/overseas/previous-name"),
      InprogressOverseas()
    ).asInstanceOf[PreviousNameModel]

    previousNameModel.question.title should be("What was your name when you left the UK?")
    previousNameModel.question.postUrl should be("/register-to-vote/overseas/previous-name")

    previousNameModel.previousFirstName.value should be("Jan")
    previousNameModel.previousMiddleNames.value should be("")
    previousNameModel.previousLastName.value should be("Kovar")
  }
}
