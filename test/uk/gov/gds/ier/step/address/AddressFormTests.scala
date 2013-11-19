package uk.gov.gds.ier.step.address

import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.libs.json.{Json, JsNull}
import org.joda.time.DateTime
import uk.gov.gds.ier.model.{Addresses, Address}

class AddressFormTests
  extends FlatSpec
  with Matchers
  with AddressForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "successfully bind a valid address" in {
    val js = Json.toJson(
      Map(
        "address.address" -> "123 Fake Street",
        "address.postcode" -> "SW1A1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.address.isDefined should be(true)
        val address = success.address.get
        address.addressLine should be(Some("123 Fake Street"))
        address.postcode should be("SW1A1AA")
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorMessages("address") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }


  it should "error out on empty values" in {
    val js =  Json.toJson(
      Map(
        "address.address" -> "",
        "address.postcode" -> ""
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorMessages("address") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind possible Address list" in {
    val possibleAddressJS = serialiser.toJson(Addresses(List(Address(Some("123 Fake Street"), "AB12 3CD"))))
    val js = Json.toJson(
      Map(
        "address.address" -> "321 My Street",
        "address.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.address.isDefined should be(true)
        val Some(address) = success.address

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        address.addressLine should be(Some("321 My Street"))
        address.postcode should be("SW1A 1AA")

        possibleAddresses.addresses should be(List(Address(Some("123 Fake Street"), "AB12 3CD")))
      }
    )
  }

  it should "not error out with empty text" in {
    val js = Json.toJson(
      Map(
        "address.address" -> "321 My Street",
        "address.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> "",
        "possibleAddresses.postcode" -> ""
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.address.isDefined should be(true)
        val Some(address) = success.address

        success.possibleAddresses.isDefined should be(false)

        address.addressLine should be(Some("321 My Street"))
        address.postcode should be("SW1A 1AA")
      }
    )
  }

}
