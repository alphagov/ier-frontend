package uk.gov.gds.ier.form

import uk.gov.gds.ier.serialiser.WithSerialiser
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.transaction.ordinary.confirmation.ConfirmationForms
import uk.gov.gds.ier.service.AddressService

class OrdinaryFormImplicitsTests
  extends FlatSpec
  with Matchers
  with FormKeys
  with TestHelpers
  with OrdinaryFormImplicits
  with ConfirmationForms
  with WithSerialiser {

  val serialiser = jsonSerialiser

  behavior of "OrdinaryFormImplicits"
  it should "return the max number of the country list when calling obtainOtherCountriesList" in {
    println (confirmationForm.obtainOtherCountriesList)
  }
}