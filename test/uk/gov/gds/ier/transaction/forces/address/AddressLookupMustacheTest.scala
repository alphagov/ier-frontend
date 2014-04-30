package uk.gov.gds.ier.transaction.forces.address

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.forces.InprogressForces

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
      Call("POST", "/register-to-vote/forces/address/lookup"),
      InprogressForces()
    ).data.asInstanceOf[LookupModel]

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/lookup")

    addressModel.postcode.value should be ("")
  }

  it should "progress form with valid values should produce Mustache Model with values present"+
    " (lookupData) - lastUkAddress = true" in {
    val partiallyFilledApplicationForm = addressForm.fill(InprogressForces(
      address = Some(LastUkAddress(Some(true), Some(PartialAddress(
        addressLine = Some("Fake street 123"),
        uprn = Some("1234567"),
        postcode = "WR26NJ",
        manualAddress = None
      ))))
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/forces/address/lookup"),
      InprogressForces()
    ).data.asInstanceOf[LookupModel]

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/lookup")

    addressModel.postcode.value should be ("WR26NJ")
  }

}
