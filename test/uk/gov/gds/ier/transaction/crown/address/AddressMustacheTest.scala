package uk.gov.gds.ier.transaction.crown.address

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.model.InprogressCrown
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some

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

    val emptyApplicationForm =  InProgressForm(addressForm)
    val addressModel = AddressMustache.lookupData(
      emptyApplicationForm,
      "/register-to-vote/crown/statement",
      "/register-to-vote/crown/address/lookup")

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/lookup")
    addressModel.question.backUrl should be("/register-to-vote/crown/statement")

    addressModel.postcode.value should be ("")
  }

  it should "empty progress form should produce empty Model (selectData)" in {

    val emptyApplicationForm =  InProgressForm(addressForm)
    val addressModel = AddressMustache.selectData(
      emptyApplicationForm,
      "/register-to-vote/crown/statement",
      "/register-to-vote/crown/address/select",
      "/register-to-vote/crown/address",
      "/register-to-vote/crown/address/manual",
      None)

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.backUrl should be("/register-to-vote/crown/statement")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/select")

    addressModel.lookupUrl should be ("/register-to-vote/crown/address")
    addressModel.manualUrl should be ("/register-to-vote/crown/address/manual")
    addressModel.postcode.value should be ("")
    addressModel.possibleJsonList.value should be ("")
    addressModel.possiblePostcode.value should be ("")
    addressModel.hasAddresses should be (false)
  }

  it should "empty progress form should produce empty Model (manualData)" in {

    val emptyApplicationForm =  InProgressForm(addressForm)
    val addressModel = AddressMustache.manualData(
      emptyApplicationForm,
      "/register-to-vote/crown/statement",
      "/register-to-vote/crown/address/manual",
      "/register-to-vote/crown/address")

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.backUrl should be("/register-to-vote/crown/statement")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/manual")

    addressModel.lookupUrl should be ("/register-to-vote/crown/address")
    addressModel.postcode.value should be ("")
    addressModel.maLineOne.value should be ("")
    addressModel.maLineTwo.value should be ("")
    addressModel.maLineThree.value should be ("")
    addressModel.maCity.value should be ("")

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

    val addressModel = AddressMustache.lookupData(
      InProgressForm(partiallyFilledApplicationForm),
      "/register-to-vote/crown/statement",
      "/register-to-vote/crown/address/lookup")

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/lookup")
    addressModel.question.backUrl should be("/register-to-vote/crown/statement")

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

    val addressModel = AddressMustache.lookupData(
      InProgressForm(partiallyFilledApplicationForm),
      "/register-to-vote/crown/statement",
      "/register-to-vote/crown/address/lookup")

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/lookup")
    addressModel.question.backUrl should be("/register-to-vote/crown/statement")

    addressModel.postcode.value should be ("WR26NJ")

  }

  it should "progress form with valid values should produce Mustache Model with values present "+
    "(selectData) - lastUkAddress = true" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressCrown(
      address = Some(LastUkAddress(
        hasUkAddress = Some(true),
        address = Some(PartialAddress(
          addressLine = Some("Fake street 123"),
          uprn = Some("1234567"),
          postcode = "WR26NJ",
          manualAddress = None
        ))
      )),
      possibleAddresses = None
    ))

    val addressModel = AddressMustache.selectData(
      InProgressForm(partiallyFilledApplicationForm),
      "/register-to-vote/crown/statement",
      "/register-to-vote/crown/address/select",
      "/register-to-vote/crown/address",
      "/register-to-vote/crown/address/manual",
      None)

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.backUrl should be("/register-to-vote/crown/statement")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/select")

    addressModel.lookupUrl should be ("/register-to-vote/crown/address")
    addressModel.manualUrl should be ("/register-to-vote/crown/address/manual")
    addressModel.postcode.value should be ("WR26NJ")
    addressModel.possibleJsonList.value should be ("")
    addressModel.possiblePostcode.value should be ("WR26NJ")
    addressModel.hasAddresses should be (false)

  }

  it should "progress form with valid values should produce Mustache Model with values present "+
    "(selectData) - lastUkAddress = false" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressCrown(
      address = Some(LastUkAddress(
        hasUkAddress = Some(false),
        address = Some(PartialAddress(
          addressLine = Some("Fake street 123"),
          uprn = Some("1234567"),
          postcode = "WR26NJ",
          manualAddress = None
        ))
      )),
      possibleAddresses = None
    ))

    val addressModel = AddressMustache.selectData(
      InProgressForm(partiallyFilledApplicationForm),
      "/register-to-vote/crown/statement",
      "/register-to-vote/crown/address/select",
      "/register-to-vote/crown/address",
      "/register-to-vote/crown/address/manual",
      None)

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.backUrl should be("/register-to-vote/crown/statement")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/select")

    addressModel.lookupUrl should be ("/register-to-vote/crown/address")
    addressModel.manualUrl should be ("/register-to-vote/crown/address/manual")
    addressModel.postcode.value should be ("WR26NJ")
    addressModel.possibleJsonList.value should be ("")
    addressModel.possiblePostcode.value should be ("WR26NJ")
    addressModel.hasAddresses should be (false)

  }

  it should "progress form with valid values should produce Mustache Model with values present "+
    "(manualData) - lastUkAddress = true" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressCrown(
      address = Some(LastUkAddress(
        hasUkAddress = Some(true),
        address = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "WR26NJ",
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")))
        ))
      )),
      possibleAddresses = None
    ))

    val addressModel = AddressMustache.manualData(
      InProgressForm(partiallyFilledApplicationForm),
      "/register-to-vote/crown/statement",
      "/register-to-vote/crown/address/manual",
      "/register-to-vote/crown/address")

    addressModel.question.title should be("What is your UK address?")
    addressModel.question.backUrl should be("/register-to-vote/crown/statement")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/manual")

    addressModel.lookupUrl should be ("/register-to-vote/crown/address")
    addressModel.postcode.value should be ("WR26NJ")
    addressModel.maLineOne.value should be ("Unit 4, Elgar Business Centre")
    addressModel.maLineTwo.value should be ("Moseley Road")
    addressModel.maLineThree.value should be ("Hallow")
    addressModel.maCity.value should be ("Worcester")
  }

  it should "progress form with valid values should produce Mustache Model with values present "+
    "(manualData) - lastUkAddress = false" in {

    val partiallyFilledApplicationForm = addressForm.fill(InprogressCrown(
      address = Some(LastUkAddress(
        hasUkAddress = Some(false),
        address = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "WR26NJ",
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")))
        ))
      )),
      possibleAddresses = None
    ))

    val addressModel = AddressMustache.manualData(
      InProgressForm(partiallyFilledApplicationForm),
      "/register-to-vote/crown/statement",
      "/register-to-vote/crown/address/manual",
      "/register-to-vote/crown/address")

    addressModel.question.title should be("What was your last UK address?")
    addressModel.question.backUrl should be("/register-to-vote/crown/statement")
    addressModel.question.postUrl should be("/register-to-vote/crown/address/manual")

    addressModel.lookupUrl should be ("/register-to-vote/crown/address")
    addressModel.postcode.value should be ("WR26NJ")
    addressModel.maLineOne.value should be ("Unit 4, Elgar Business Centre")
    addressModel.maLineTwo.value should be ("Moseley Road")
    addressModel.maLineThree.value should be ("Hallow")
    addressModel.maCity.value should be ("Worcester")
  }
}
