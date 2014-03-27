package uk.gov.gds.ier.form

import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, Key, InProgressForm}
import uk.gov.gds.ier.model.PartialManualAddress

trait AddressHelpers extends FormKeys {

  /**
   * Concatenate manual address, using comas, as one line of text or return None.
   * Wrap as Option to allow eventual further chaining.
   * @param form source of data
   * @param manualAddressKey example: keys.lastUkAddress.manualAddress
   */
  def manualAddressToOneLine(
      form: ErrorTransformForm[_],
      manualAddressKey: Key): Option[String] = {
    val maLines = List (
      form(manualAddressKey.lineOne).value,
      form(manualAddressKey.lineTwo).value,
      form(manualAddressKey.lineThree).value,
      form(manualAddressKey.city).value
    ).flatten
    if (maLines == Nil) return None else Some(maLines.mkString(", "))
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

  /**
   * Check if manual address is defined in form data
   * @param form source of data
   * @param manualAddressKey example: keys.lastUkAddress.manualAddress
   */
  def isManualAddressDefined(form: ErrorTransformForm[_], manualAddressKey: Key): Boolean = {
    // is checking by just line one enough?
    form(manualAddressKey.lineOne).value.isDefined
  }
}
