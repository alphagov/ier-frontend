package uk.gov.gds.ier.form

import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, Key, InProgressForm}
import uk.gov.gds.ier.model.PartialManualAddress

trait AddressHelpers extends FormKeys {

  /**
   * Concatenate manual address, using comas, as one line of text or return None.
   * Wrap as Option to allow eventual further chaining.
   */
  def manualAddressToOneLine(
      form: ErrorTransformForm[_],
      manualAddressKey: Key): Option[String] = {
    val maLines = List (
      form(manualAddressKey.lineOne.key).value,
      form(manualAddressKey.lineTwo.key).value,
      form(manualAddressKey.lineThree.key).value,
      form(manualAddressKey.city.key).value
    ).flatten
    if (maLines == Nil) return None else Some(maLines.mkString(", "))
  }

  def manualAddressToOneLine(
      form: InProgressForm[_],
      manualAddressKey: Key): Option[String] = {
    manualAddressToOneLine(form.form, manualAddressKey)
  }

  def manualAddressToOneLine(
      manualAddress: PartialManualAddress): Option[String] = {
    val maLines = List (
      manualAddress.lineOne,
      manualAddress.lineTwo,
      manualAddress.lineThree,
      manualAddress.city
    ).flatten
    if (maLines == Nil) return None else Some(maLines.mkString(", "))
  }
}
