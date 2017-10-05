package uk.gov.gds.ier.transaction.crown.dateOfBirth

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class DateOfBirthMustacheTest
  extends MustacheTestSuite
  with DateOfBirthForms
  with DateOfBirthMustache {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = dateOfBirthForm

    val dateOfBirthModel = mustache.data(
        emptyApplicationForm,
        Call("POST", "/register-to-vote/date-of-birth"),
        InprogressCrown()
    ).asInstanceOf[DateOfBirthModel]

    dateOfBirthModel.question.title should be("www.gov.uk/register-to-vote - What is your date of birth?")
    dateOfBirthModel.question.postUrl should be("/register-to-vote/date-of-birth")

    dateOfBirthModel.day.value should be("")
    dateOfBirthModel.month.value should be("")
    dateOfBirthModel.year.value should be("")
  }

  it should "fully filled applicant dob should produce Mustache Model with dob values present" in {
    val filledForm = dateOfBirthForm.fillAndValidate(InprogressCrown(
      dob = Some(DateOfBirth(Some(DOB(day=12, month= 12, year = 1980)), None))))

    val dateOfBirthModel = mustache.data(
        filledForm,
        Call("POST", "/register-to-vote/date-of-birth"),
        InprogressCrown()
    ).asInstanceOf[DateOfBirthModel]

    dateOfBirthModel.question.title should be("www.gov.uk/register-to-vote - What is your date of birth?")
    dateOfBirthModel.question.postUrl should be("/register-to-vote/date-of-birth")

    dateOfBirthModel.day.value should be("12")
    dateOfBirthModel.month.value should be("12")
    dateOfBirthModel.year.value should be("1980")
  }

  it should "fully filled applicant no dob reason should produce Mustache Model with values present" in {
    val filledForm = dateOfBirthForm.fillAndValidate(InprogressCrown(
      dob = Some(DateOfBirth(None, Some(noDOB(Some("dunno my birthday... ???"), Some("18to75")))))))

    val dateOfBirthModel = mustache.data(
        filledForm,
        Call("POST", "/register-to-vote/date-of-birth"),
        InprogressCrown()
    ).asInstanceOf[DateOfBirthModel]

    dateOfBirthModel.question.title should be("www.gov.uk/register-to-vote - What is your date of birth?")
    dateOfBirthModel.question.postUrl should be("/register-to-vote/date-of-birth")

    dateOfBirthModel.day.value should be("")
    dateOfBirthModel.month.value should be("")
    dateOfBirthModel.year.value should be("")

    dateOfBirthModel.noDobReason.value should be("dunno my birthday... ???")
    dateOfBirthModel.range18to75.attributes should be("checked=\"checked\"")
    dateOfBirthModel.rangeDontKnow.attributes should be("")
    dateOfBirthModel.rangeOver75.attributes should be("")
    dateOfBirthModel.rangeUnder18.attributes should be("")

  }
}
