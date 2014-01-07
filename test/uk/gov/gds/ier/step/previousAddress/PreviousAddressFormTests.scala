package uk.gov.gds.ier.step.previousAddress

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.libs.json.Json
import uk.gov.gds.ier.model.{Addresses, Address, PartialAddress}
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
        "previousAddress.previousAddress.uprn" -> "12345678",
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
        previousAddress.uprn should be(Some("12345678"))
        previousAddress.postcode should be("SW1A 1AA")
      }
    )
  }

  it should "successfully bind to address and movedRecently=true (manual address)" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.manualAddress" -> "123 Fake Street",
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
        previousAddress.manualAddress should be(Some("123 Fake Street"))
        previousAddress.postcode should be("SW1A 1AA")
      }
    )
  }

  it should "successfully bind to address and movedRecently=true with possible addresses" in {
    val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"), 
                                         uprn = Some("12345678"),
                                         postcode = "AB12 3CD", 
                                         manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.uprn" -> "12345678",
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
        previousAddress.uprn should be(Some("12345678"))
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses
        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "successfully bind to address and movedRecently=true with possible addresses (manual address)" in {
    val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"), 
                                         uprn = Some("12345678"),
                                         postcode = "AB12 3CD", 
                                         manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.manualAddress" -> "123 Fake Street",
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
        previousAddress.manualAddress should be(Some("123 Fake Street"))
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses
        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "not error out with empty text" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.uprn" -> "12345678",
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
        previousAddress.uprn should be(Some("12345678"))
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses should be(None)
      }
    )
  }

  it should "not error out with empty text (manual address)" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.manualAddress" -> "123 Fake Street",
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
        previousAddress.manualAddress should be(Some("123 Fake Street"))
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
        hasErrors.errorMessages("previousAddress.previousAddress.uprn") should be(Seq("Please select your address"))
        hasErrors.globalErrorMessages should be(Seq("Please select your address"))
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
