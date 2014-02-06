package uk.gov.gds.ier.transaction.overseas.dateOfBirth

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.DOB
import uk.gov.gds.ier.model.InprogressOverseas
import scala.Some
import play.api.mvc.Call

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
  with TestHelpers {

  // tested unit
  val dateOfBirthMustache = new DateOfBirthMustache {}

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = dateOfBirthForm
    
    val dateOfBirthModel = dateOfBirthMustache.transformFormStepToMustacheData(emptyApplicationForm, 
        new Call("POST", "/register-to-vote/date-of-birth"), None)

    dateOfBirthModel.question.title should be("What is your date of birth?")
    dateOfBirthModel.question.postUrl should be("/register-to-vote/date-of-birth")
    dateOfBirthModel.question.backUrl should be("")

    dateOfBirthModel.day.value should be("")
    dateOfBirthModel.month.value should be("")
    dateOfBirthModel.year.value should be("")
  }

  it should "fully filled applicant dob should produce Mustache Model with dob values present" in {
    val filledForm = dateOfBirthForm.fillAndValidate(InprogressOverseas(
      dob = Some(DOB(day=12, month= 12, year = 1980))))
      
    val dateOfBirthModel = dateOfBirthMustache.transformFormStepToMustacheData(filledForm,
        new Call("POST", "/register-to-vote/date-of-birth"), None)

    dateOfBirthModel.question.title should be("What is your date of birth?")
    dateOfBirthModel.question.postUrl should be("/register-to-vote/date-of-birth")
    dateOfBirthModel.question.backUrl should be("")

    dateOfBirthModel.day.value should be("12")
    dateOfBirthModel.month.value should be("12")
    dateOfBirthModel.year.value should be("1980")
  }
}
