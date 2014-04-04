package uk.gov.gds.ier.transaction.forces.address

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.WithSerialiser
import controllers.step.forces.routes._
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.validation.ErrorTransformForm
import scala.Some
import uk.gov.gds.ier.transaction.forces.InprogressForces

class AddressMustacheTest
  extends FlatSpec
  with Matchers
  with AddressForms
  with AddressMustache
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  self: FormKeys
    with ErrorMessages
    with WithSerialiser =>

  val serialiser = jsonSerialiser

  it should "empty progress form should produce empty Model (lookupData)" in {

    val emptyApplicationForm =  addressForm
    val addressModel = AddressMustache.lookupData(
      emptyApplicationForm,
      "/register-to-vote/forces/statement",
      "/register-to-vote/forces/address/lookup")

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/lookup")
    addressModel.question.backUrl should be("/register-to-vote/forces/statement")

    addressModel.postcode.value should be ("")
  }

  it should "empty progress form should produce empty Model (selectData)" in {

    val emptyApplicationForm =  addressForm
    val addressModel = AddressMustache.selectData(
      emptyApplicationForm,
      "/register-to-vote/forces/statement",
      "/register-to-vote/forces/address/select",
      "/register-to-vote/forces/address",
      "/register-to-vote/forces/address/manual",
      None)

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.backUrl should be("/register-to-vote/forces/statement")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/select")

    addressModel.lookupUrl should be ("/register-to-vote/forces/address")
    addressModel.manualUrl should be ("/register-to-vote/forces/address/manual")
    addressModel.postcode.value should be ("")
    addressModel.possibleJsonList.value should be ("")
    addressModel.possiblePostcode.value should be ("")
    addressModel.hasAddresses should be (false)
  }

  it should "empty progress form should produce empty Model (manualData)" in {

    val emptyApplicationForm =  addressForm
    val addressModel = AddressMustache.manualData(
      emptyApplicationForm,
      "/register-to-vote/forces/statement",
      "/register-to-vote/forces/address/select",
      "/register-to-vote/forces/address")

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.backUrl should be("/register-to-vote/forces/statement")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/select")

    addressModel.lookupUrl should be ("/register-to-vote/forces/address")
    addressModel.postcode.value should be ("")
    addressModel.maLineOne.value should be ("")
    addressModel.maLineTwo.value should be ("")
    addressModel.maLineThree.value should be ("")
    addressModel.maCity.value should be ("")

  }


  it should "progress form with valid values should produce Mustache Model with values present (lookupData)" in {
    val partiallyFilledApplicationForm = addressForm.fill(InprogressForces(
      address = Some(PartialAddress(
        addressLine = Some("Fake street 123"),
        uprn = Some("1234567"),
        postcode = "WR26NJ",
        manualAddress = None
      ))
    ))

    val addressModel = AddressMustache.lookupData(
      partiallyFilledApplicationForm,
      "/register-to-vote/forces/statement",
      "/register-to-vote/forces/address/lookup")

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/lookup")
    addressModel.question.backUrl should be("/register-to-vote/forces/statement")

    addressModel.postcode.value should be ("WR26NJ")

  }

  it should "progress form with valid values should produce Mustache Model with values present (selectData)" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressForces(
      address = Some(PartialAddress(
        addressLine = Some("Fake street 123"),
        uprn = Some("1234567"),
        postcode = "WR26NJ",
        manualAddress = None
      )),
      possibleAddresses = None
    ))

    val addressModel = AddressMustache.selectData(
      partiallyFilledApplicationForm,
      "/register-to-vote/forces/statement",
      "/register-to-vote/forces/address/select",
      "/register-to-vote/forces/address",
      "/register-to-vote/forces/address/manual",
      None)

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.backUrl should be("/register-to-vote/forces/statement")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/select")

    addressModel.lookupUrl should be ("/register-to-vote/forces/address")
    addressModel.manualUrl should be ("/register-to-vote/forces/address/manual")
    addressModel.postcode.value should be ("WR26NJ")
    addressModel.possibleJsonList.value should be ("")
    addressModel.possiblePostcode.value should be ("WR26NJ")
    addressModel.hasAddresses should be (false)

  }

  it should "progress form with valid values should produce Mustache Model with values present (manualData)" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressForces(
      address = Some(PartialAddress(
        addressLine = None,
        uprn = None,
        postcode = "WR26NJ",
        manualAddress = Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester")))
      )),
      possibleAddresses = None
    ))

    val addressModel = AddressMustache.manualData(
      partiallyFilledApplicationForm,
      "/register-to-vote/forces/statement",
      "/register-to-vote/forces/address/select",
      "/register-to-vote/forces/address")

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.backUrl should be("/register-to-vote/forces/statement")
    addressModel.question.postUrl should be("/register-to-vote/forces/address/select")

    addressModel.lookupUrl should be ("/register-to-vote/forces/address")
    addressModel.postcode.value should be ("WR26NJ")
    addressModel.maLineOne.value should be ("Unit 4, Elgar Business Centre")
    addressModel.maLineTwo.value should be ("Moseley Road")
    addressModel.maLineThree.value should be ("Hallow")
    addressModel.maCity.value should be ("Worcester")
  }
}
