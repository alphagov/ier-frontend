package uk.gov.gds.ier.step.previousAddress

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.libs.json.Json
import uk.gov.gds.ier.model.{Addresses, Address}
import uk.gov.gds.ier.step.address.AddressForms

class PreviousAddressFormTests
  extends FlatSpec
  with Matchers
  with AddressForms
  with PreviousAddressForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "successfully bind to address and movedRecently=true" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.address" -> "123 Fake Street",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(true)
        
        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.addressLine should be(Some("123 Fake Street"))
        previousAddress.postcode should be("SW1A 1AA")
      }
    )
  }

  it should "successfully bind to address and movedRecently=true with possible addresses" in {
    val possibleAddressJS = serialiser.toJson(Addresses(List(Address(Some("123 Fake Street"), "AB12 3CD"))))
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.address" -> "123 Fake Street",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(true)

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.addressLine should be(Some("123 Fake Street"))
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses
        possibleAddresses.addresses should be(List(Address(Some("123 Fake Street"), "AB12 3CD")))
      }
    )
  }

  it should "not error out with empty text" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.address" -> "123 Fake Street",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> "",
        "possibleAddresses.postcode" -> ""
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(true)

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.addressLine should be(Some("123 Fake Street"))
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses should be(None)
      }
    )
  }

  it should "successfully bind to no address and movedRecently=false" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "false"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(false)

        previousAddressWrapper.previousAddress should be(None)
      }
    )
  }

  it should "error out with no address and movedRecently=true" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.previousAddress.postcode") should be(Seq("Please enter your postcode"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your postcode"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out with address and movedRecently=false" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "false",
        "previousAddress.previousAddress.address" -> "123 Fake Street",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("previousAddress.movedRecently") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have thrown an error")
    )
  }
}
