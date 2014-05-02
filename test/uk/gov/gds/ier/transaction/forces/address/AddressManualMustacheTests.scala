package uk.gov.gds.ier.transaction.forces.address

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.forces.InprogressForces

class AddressManualMustacheTests
  extends FlatSpec
  with Matchers
  with AddressForms
  with AddressManualMustache
  with ErrorMessages
  with WithSerialiser
  with FormKeys
  with TestHelpers {


  val serialiser = jsonSerialiser

  it should "empty progress form should produce empty Model (manualData)" in {

    val emptyApplicationForm =  addressForm
    val addressModel = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/forces/address/manual"),
      InprogressForces()
    ).asInstanceOf[ManualModel]

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/manual")

    addressModel.lookupUrl should be ("/register-to-vote/forces/address")
    addressModel.postcode.value should be ("")
    addressModel.maLineOne.value should be ("")
    addressModel.maLineTwo.value should be ("")
    addressModel.maLineThree.value should be ("")
    addressModel.maCity.value should be ("")

  }

  it should "progress form with valid values should produce Mustache Model with values present (manualData)" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressForces(
      address = Some(LastUkAddress(Some(true), Some(PartialAddress(
        addressLine = None,
        uprn = None,
        postcode = "WR26NJ",
        manualAddress = Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester")))))
      )),
      possibleAddresses = None
    ))

    val addressModel = mustache.data(
      partiallyFilledApplicationForm,
      Call("POST", "/register-to-vote/forces/address/manual"),
      InprogressForces()
    ).asInstanceOf[ManualModel]

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/manual")

    addressModel.lookupUrl should be ("/register-to-vote/forces/address")
    addressModel.postcode.value should be ("WR26NJ")
    addressModel.maLineOne.value should be ("Unit 4, Elgar Business Centre")
    addressModel.maLineTwo.value should be ("Moseley Road")
    addressModel.maLineThree.value should be ("Hallow")
    addressModel.maCity.value should be ("Worcester")
  }
}
