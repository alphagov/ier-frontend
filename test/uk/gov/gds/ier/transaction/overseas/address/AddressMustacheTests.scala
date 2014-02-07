package uk.gov.gds.ier.transaction.overseas.address

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.OverseasAddress
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call

/**
 * Unit test to test form to Mustache model transformation.
 *
 * Testing Mustache html text rendering requires running application, it is not easily unit testable,
 * so method {@link AddressMustache#transformFormStepToMustacheData()} is tested as a part of 
 * MustacheControllerTest.
 */
class AddressMustacheTest
  extends FlatSpec
  with Matchers
  with AddressForms
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  // tested unit
  val addressMustache = new AddressMustache {}
  
  val title = "Where do you live?"
  val postCall = new Call("POST", "/register-to-vote/overseas/address")
  val backCall = new Call("POST", "/register-to-vote/overseas/nino")

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = addressForm
    val addressModel = addressMustache.transformFormStepToMustacheData(emptyApplicationForm, 
        postCall, Some(backCall))

    addressModel.question.title should be(title)
    addressModel.question.postUrl should be(postCall.url)
    addressModel.question.backUrl should be(backCall.url)

    addressModel.countrySelect.value should be("")
    addressModel.address.value should be("")
  }

  it should "fully filled form should produce Mustache Model" in {
    val filledApplicationForm = addressForm.fill(InprogressOverseas(
      address = Some(OverseasAddress(country = Some("United Kingdom"), 
      addressDetails = Some("some address")))))
      
    val addressModel = addressMustache.transformFormStepToMustacheData(filledApplicationForm, 
        postCall, Some(backCall))

    addressModel.question.title should be(title)
    addressModel.question.postUrl should be(postCall.url)
    addressModel.question.backUrl should be(backCall.url)

    addressModel.countrySelect.value should be("United Kingdom")
    addressModel.address.value should be("some address")
  }

  it should "progress form with validation errors in the model if address is missing" in {
    val uncompletedFormWithErrors = addressForm.fillAndValidate(InprogressOverseas(
      address = Some(OverseasAddress(country = Some("United Kingdom"), addressDetails = None))))
      
    val addressModel = addressMustache.transformFormStepToMustacheData(uncompletedFormWithErrors, 
        postCall, Some(backCall))

    addressModel.question.title should be(title)
    addressModel.question.postUrl should be(postCall.url)
    addressModel.question.backUrl should be(backCall.url)

    addressModel.countrySelect.value should be("United Kingdom")
    addressModel.address.value should be("")

    addressModel.question.errorMessages.mkString(", ") should be
      ("Correspondence address is required")
  }
  
  it should "progress form with validation errors in the model if country is missing" in {
    val uncompletedFormWithErrors = addressForm.fillAndValidate(InprogressOverseas(
      address = Some(OverseasAddress(country = None, addressDetails = Some("address")))))
      
    val addressModel = addressMustache.transformFormStepToMustacheData(uncompletedFormWithErrors, 
        postCall, Some(backCall))

    addressModel.question.title should be(title)
    addressModel.question.postUrl should be(postCall.url)
    addressModel.question.backUrl should be(backCall.url)

    addressModel.countrySelect.value should be("")
    addressModel.address.value should be("address")

    addressModel.question.errorMessages.mkString(", ") should be
      ("Correspondence country is required")
  }
}