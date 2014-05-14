package uk.gov.gds.ier.transaction.overseas.address

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.{WithMockRemoteAssets, TestHelpers}
import uk.gov.gds.ier.model.OverseasAddress
import play.api.mvc.Call
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

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
  with AddressMustache
  with ErrorMessages
  with FormKeys
  with WithMockRemoteAssets
  with TestHelpers {

  val postCall = new Call("POST", "/register-to-vote/overseas/address")

  it should "empty progress form should produce empty Model" in {
    val emptyApplicationForm = addressForm
    val addressModel = mustache.data(
       emptyApplicationForm,
       postCall,
       InprogressOverseas()
     ).asInstanceOf[AddressModel]

    addressModel.question.title should be(title)
    addressModel.question.postUrl should be(postCall.url)

    addressModel.countrySelect.value should be("")
    addressModel.addressLine1.value should be("")
    addressModel.addressLine2.value should be("")
    addressModel.addressLine3.value should be("")
    addressModel.addressLine4.value should be("")
    addressModel.addressLine5.value should be("")
  }

  it should "fully filled form should produce Mustache Model" in {
    val filledApplicationForm = addressForm.fill(InprogressOverseas(
      address = Some(OverseasAddress(
        country = Some("United Kingdom"),
        addressLine1 = Some("some address line 1"),
        addressLine2 = None,
        addressLine3 = None,
        addressLine4 = None,
        addressLine5 = None))))

    val addressModel = mustache.data(
      filledApplicationForm,
      postCall,
      InprogressOverseas()
    ).asInstanceOf[AddressModel]

    addressModel.question.title should be(title)
    addressModel.question.postUrl should be(postCall.url)

    addressModel.countrySelect.value should be("United Kingdom")
    addressModel.addressLine1.value should be("some address line 1")
  }

  it should "progress form with validation errors in the model if address is missing" in {
    val uncompletedFormWithErrors = addressForm.fillAndValidate(InprogressOverseas(
      address = Some(OverseasAddress(
        country = Some("United Kingdom"),
        addressLine1 = None,
        addressLine2 = None,
        addressLine3 = None,
        addressLine4 = None,
        addressLine5 = None))))

    val addressModel = mustache.data(
      uncompletedFormWithErrors,
      postCall,
      InprogressOverseas()
    ).asInstanceOf[AddressModel]

    addressModel.question.title should be(title)
    addressModel.question.postUrl should be(postCall.url)

    addressModel.countrySelect.value should be("United Kingdom")
    addressModel.addressLine1.value should be("")

    addressModel.question.errorMessages.mkString(", ") should be("" +
      "Please enter your address")
  }

  it should "progress form with validation errors in the model if country is missing" in {
    val uncompletedFormWithErrors = addressForm.fillAndValidate(InprogressOverseas(
      address = Some(OverseasAddress(
        country = None,
        addressLine1 = Some("Francisco de Quevedo 34"),
        addressLine2 = Some("08191 Rubí"),
        addressLine3 = Some("Barcelona"),
        addressLine4 = None,
        addressLine5 = None))))

    val addressModel = mustache.data(
      uncompletedFormWithErrors,
      postCall,
      InprogressOverseas()
    ).asInstanceOf[AddressModel]


    addressModel.question.title should be(title)
    addressModel.question.postUrl should be(postCall.url)

    addressModel.countrySelect.value should be("")
    addressModel.addressLine1.value should be("Francisco de Quevedo 34")
    addressModel.addressLine2.value should be("08191 Rubí")
    addressModel.addressLine3.value should be("Barcelona")

    addressModel.question.errorMessages.mkString(", ") should be("" +
      "Please enter your country")
  }
}
