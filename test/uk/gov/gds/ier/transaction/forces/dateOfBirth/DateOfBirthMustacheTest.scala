package uk.gov.gds.ier.transaction.forces.dateOfBirth

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model._
import scala.Some
import play.api.mvc.Call
import uk.gov.gds.ier.model.DateOfBirth
import play.api.mvc.Call
import uk.gov.gds.ier.model.DOB
import scala.Some
import uk.gov.gds.ier.transaction.forces.InprogressForces

/**
 * Unit test to test form to Mustache model transformation.
 *
 * Testing Mustache html text rendering requires running application, it is not easily unit testable,
 * so method {@link NameMustache#nameMustache()} is tested as a part of MustacheControllerTest.
 */
class DateOfBirthMustacheTest
  extends FlatSpec
  with Matchers
  with DateOfBirthForms
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with WithMockConfig
  with WithMockRemoteAssets
  with DateOfBirthMustache {

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = dateOfBirthForm

    val dateOfBirthModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/forces/date-of-birth"),
      InprogressForces()
    ).asInstanceOf[DateOfBirthModel]

    dateOfBirthModel.question.title should be("What is your date of birth?")
    dateOfBirthModel.question.postUrl should be("/register-to-vote/forces/date-of-birth")

    dateOfBirthModel.day.value should be("")
    dateOfBirthModel.month.value should be("")
    dateOfBirthModel.year.value should be("")
  }

  it should "fully filled applicant dob should produce Mustache Model with dob values present" in {
    val filledForm = dateOfBirthForm.fillAndValidate(InprogressForces(
      dob = Some(DateOfBirth(Some(DOB(day=12, month= 12, year = 1980)), None))))

    val dateOfBirthModel = mustache.data(
      filledForm,
      Call("POST", "/register-to-vote/forces/date-of-birth"),
      InprogressForces()
    ).asInstanceOf[DateOfBirthModel]

    dateOfBirthModel.question.title should be("What is your date of birth?")
    dateOfBirthModel.question.postUrl should be("/register-to-vote/forces/date-of-birth")

    dateOfBirthModel.day.value should be("12")
    dateOfBirthModel.month.value should be("12")
    dateOfBirthModel.year.value should be("1980")
  }

  it should "fully filled applicant no dob reason should produce Mustache Model with values present" in {
    val filledForm = dateOfBirthForm.fillAndValidate(InprogressForces(
      dob = Some(DateOfBirth(None, Some(noDOB(Some("dunno my birthday... ???"), Some("18to70")))))))

    val dateOfBirthModel = mustache.data(
      filledForm,
      Call("POST", "/register-to-vote/forces/date-of-birth"),
      InprogressForces()
    ).asInstanceOf[DateOfBirthModel]

    dateOfBirthModel.question.title should be("What is your date of birth?")
    dateOfBirthModel.question.postUrl should be("/register-to-vote/forces/date-of-birth")

    dateOfBirthModel.day.value should be("")
    dateOfBirthModel.month.value should be("")
    dateOfBirthModel.year.value should be("")

    dateOfBirthModel.noDobReason.value should be("dunno my birthday... ???")
    dateOfBirthModel.range18to70.attributes should be("checked=\"checked\"")
    dateOfBirthModel.rangeDontKnow.attributes should be("")
    dateOfBirthModel.rangeOver70.attributes should be("")
    dateOfBirthModel.rangeUnder18.attributes should be("")

  }
}
