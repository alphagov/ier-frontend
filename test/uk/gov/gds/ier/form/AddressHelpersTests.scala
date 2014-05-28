package uk.gov.gds.ier.form

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{HasAddressOption, PartialManualAddress, PartialAddress, LastAddress}
import uk.gov.gds.ier.transaction.forces.confirmation.ConfirmationForms
import play.api.data.Form
import uk.gov.gds.ier.transaction.ordinary.address.AddressForms
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

/**
 * Test AddressHelpers utility class against multiple forms
 */
class AddressHelpersTests extends FlatSpec
with TestHelpers
with FormKeys
with AddressHelpers
with Matchers {

  val serialiser = null
  val addressService = null

  // Use composition rather then inheritance to get Play Form instances
  val manualAddressForm = new AddressForms with ErrorMessages with FormKeys with WithSerialiser {
    val serialiser = null
  }.manualAddressForm

  val confirmationForm = new ConfirmationForms {
    val serialiser = null
    val addressService = null
  }.confirmationForm

  behavior of "AddressHelpers.manualAddressToOneLine with Forces ConfirmationForm"

  it should "return all elements of address separated by comma for form with full manual address" in {
    val partiallyFilledForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
        address = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "AB12 3CD",
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("Unit 4, Elgar Business Centre"),
            lineTwo = Some("Moseley Road"),
            lineThree = Some("Hallow"),
            city = Some("Worcester")))
        ))
      ))
    ))
    // local variable names matter!
    // if you try to rename 'result' to 'manualAddress' Scala compiler implodes with internal error
    val result = manualAddressToOneLine(
      partiallyFilledForm,
      keys.address.address.manualAddress)
    result should be(Some("Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester"))
  }

  it should "return address without comma for form with manual address of just one line" in {
    val partiallyFilledForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
        address = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "AB12 3CD",
          manualAddress = Some(PartialManualAddress(
            lineOne = Some("123 Fake Street"),
            lineTwo = None,
            lineThree = None,
            city = None))
        ))
      ))
    ))
    val result = manualAddressToOneLine(
      partiallyFilledForm,
      keys.address.address.manualAddress)
    result should be(Some("123 Fake Street"))
  }

  it should "return None for form without manual address" in {
    val partiallyFilledForm = confirmationForm.fillAndValidate(InprogressForces(
      address = Some(LastAddress(
        hasAddress = Some(HasAddressOption.YesAndLivingThere),
        address = Some(PartialAddress(
          addressLine = None,
          uprn = None,
          postcode = "AB12 3CD",
          manualAddress = Some(PartialManualAddress(
            lineOne = None,
            lineTwo = None,
            lineThree = None,
            city = None))
        ))
      ))
    ))
    val result = manualAddressToOneLine(
      partiallyFilledForm,
      keys.address.manualAddress)
    result should be(None)
  }


  behavior of "AddressHelpers.manualAdressToOneLine with Ordinary AddressForm"

  it should "return all elements of address separated by comma for form with full manual address" in {
    val partiallyFilledForm = manualAddressForm.fillAndValidate(InprogressOrdinary(
      address = Some(PartialAddress(
        addressLine = None,
        uprn = None,
        postcode = "AB12 3CD",
        manualAddress = Some(PartialManualAddress(
          lineOne = Some("Unit 4, Elgar Business Centre"),
          lineTwo = Some("Moseley Road"),
          lineThree = Some("Hallow"),
          city = Some("Worcester")))
      ))
    ))
    val result = manualAddressToOneLine(
      partiallyFilledForm,
      keys.address.manualAddress)
    result should be(Some("Unit 4, Elgar Business Centre, Moseley Road, Hallow, Worcester"))
  }

  it should "return address without comma for form with manual address of just one line" in {
    val partiallyFilledForm = manualAddressForm.fillAndValidate(InprogressOrdinary(
      address = Some(PartialAddress(
        addressLine = None,
        uprn = None,
        postcode = "AB12 3CD",
        manualAddress = Some(PartialManualAddress(
          lineOne = Some("123 Fake Street"),
          lineTwo = None,
          lineThree = None,
          city = None))
      ))
    ))
    val result = manualAddressToOneLine(
      partiallyFilledForm,
      keys.address.manualAddress)
    result should be(Some("123 Fake Street"))
  }

  it should "return None for form without manual address" in {
    val partiallyFilledForm = manualAddressForm.fillAndValidate(InprogressOrdinary(
      address = Some(PartialAddress(
        addressLine = None,
        uprn = None,
        postcode = "AB12 3CD",
        manualAddress = Some(PartialManualAddress(
          lineOne = None,
          lineTwo = None,
          lineThree = None,
          city = None))
      ))
    ))
    val result = manualAddressToOneLine(
      partiallyFilledForm,
      keys.address.manualAddress)
    result should be(None)
  }

  "concatListOfOptionalStrings()" should
    "concatenate only Some values if there are any or return None" in {
    concatListOfOptionalStrings(List(None, None, None)) should be(None)
    concatListOfOptionalStrings(List(None, Some("aaa"), None)) should be(Some("aaa"))
    concatListOfOptionalStrings(List(Some("aaa"), Some("bbb"), Some("ccc"))) should be(Some("aaa, bbb, ccc"))
  }
}
