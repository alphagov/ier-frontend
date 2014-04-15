package uk.gov.gds.ier.transaction.crown.address

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class AddressLookupMustacheTest
  extends FlatSpec
  with Matchers
  with AddressForms
  with AddressLookupMustache
  with ErrorMessages
  with WithSerialiser
  with FormKeys
  with TestHelpers {


  val serialiser = jsonSerialiser

  it should "empty progress form should produce empty Model (lookupData)" in {

    val emptyApplicationForm =  addressForm
    val addressModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/crown/address/lookup"),
      InprogressCrown()
    ).data.asInstanceOf[LookupModel]

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/lookup")

    addressModel.postcode.value should be ("")
  }

  it should "progress form with valid values should produce Mustache Model with values present"+
    " (lookupData) - lastUkAddress = true" in {
    val partiallyFilledApplicationForm = addressForm.fill(InprogressCrown(
      address = Some(LastUkAddress(
        hasUkAddress = Some(true),
        address = Some(PartialAddress(
          addressLine = Some("Fake street 123"),
          uprn = Some("1234567"),
          postcode = "WR26NJ",
          manualAddress = None
        ))
      ))
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/address/lookup"),
      InprogressCrown()
    ).data.asInstanceOf[LookupModel]

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/lookup")

    addressModel.postcode.value should be ("WR26NJ")

  }

  it should "progress form with valid values should produce Mustache Model with values present "+
    "(lookupData) - lastUkAddress = false" in {
    val partiallyFilledApplicationForm = addressForm.fill(InprogressCrown(
      address = Some(LastUkAddress(
        hasUkAddress = Some(false),
        address = Some(PartialAddress(
          addressLine = Some("Fake street 123"),
          uprn = Some("1234567"),
          postcode = "WR26NJ",
          manualAddress = None
        ))
      ))
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/crown/address/lookup"),
      InprogressCrown()
    ).data.asInstanceOf[LookupModel]

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/lookup")

    addressModel.postcode.value should be ("WR26NJ")

  }
}
