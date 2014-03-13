package uk.gov.gds.ier.transaction.crown.address

import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.WithSerialiser
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import play.api.libs.json.{Json, JsNull}
import uk.gov.gds.ier.model.{Addresses, PartialAddress}

class AddressFormTests
  extends FlatSpec
  with Matchers
  with AddressForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  behavior of "AddressForms.addressForm"

  it should "successfully bind a valid address" in {
    val js = Json.toJson(
      Map(
        "address.uprn" -> "12345678",
        "address.postcode" -> "SW1A1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.address.isDefined should be(true)
        val address = success.address.get
        address.uprn should be(Some("12345678"))
        address.postcode should be("SW1A1AA")
      }
    )
  }

  it should "successfully bind a valid manual input address" in {
    val js = Json.toJson(
      Map(
        "address.manualAddress" -> "123 Fake Street entered manually",
        "address.postcode" -> "SW1A1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.address.isDefined should be(true)
        val address = success.address.get
        address.manualAddress should be(Some("123 Fake Street entered manually"))
        address.postcode should be("SW1A1AA")
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
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
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("address") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty values in manual address" in {
    val js =  Json.toJson(
      Map(
        "address.manualAddress" -> "",
        "address.postcode" -> ""
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("address") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind possible Address list" in {
    val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"), 
                                         uprn = Some("12345678"),
                                         postcode = "AB12 3CD",
                                         manualAddress = None)
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "address.uprn" -> "12345678",
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

        address.uprn should be(Some("12345678"))
        address.postcode should be("SW1A 1AA")

        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "error out if it looks like you haven't selected your address" in {
    val possibleAddress = PartialAddress(
      addressLine = Some("123 Fake Street"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None
    )
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "address.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("address.uprn") should be(
          Seq("Please select your address")
        )
        hasErrors.errorMessages("address.manualAddress") should be(
          Seq("Please select your address")
        )
        hasErrors.globalErrorMessages should be(Seq("Please select your address"))
      },
      success => {
        fail("Should have errored out")
      }
    )
  }

  it should "not error if you haven't selected your address but there is a manual address" in {
    val possibleAddress = PartialAddress(
      addressLine = Some("123 Fake Street"),
      uprn = Some("12345678"),
      postcode = "AB12 3CD",
      manualAddress = None
    )
    val possibleAddressJS = serialiser.toJson(Addresses(List(possibleAddress)))
    val js = Json.toJson(
      Map(
        "address.manualAddress" -> "1428 Elm Street",
        "address.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail("Should not fail"),
      success => {
        success.address.isDefined should be(true)
        val Some(address) = success.address

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        address.manualAddress should be(Some("1428 Elm Street"))
        address.postcode should be("SW1A 1AA")

        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "not error out with empty text" in {
    val js = Json.toJson(
      Map(
        "address.uprn" -> "87654321",
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

        address.uprn should be(Some("87654321"))
        address.postcode should be("SW1A 1AA")
      }
    )
  }

  behavior of "AddressForms.lookupForm"

  it should "succeed on valid postcode" in {
    val js = Json.toJson(
      Map(
        "address.postcode" -> "SW1A 1AA"
      )
    )

    lookupAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.address.isDefined should be(true)
        val Some(address) = success.address

        address.postcode should be("SW1A 1AA")
        address.uprn should be(None)
        address.manualAddress should be(None)
        address.addressLine should be (None)
      }
    )
  }

  it should "fail out on no postcode" in {
    val js = Json.toJson(Map("address.postcode" -> ""))

    lookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("address.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }

  it should "fail out on empty json" in {
    val js = JsNull

    lookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("address.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }

  it should "fail out on missing values" in {
    val js = Json.toJson(Map("" -> ""))

    lookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("address.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }

  behavior of "AddressForms.manualAddressForm"

  it should "succeed on valid input" in {
    val js = Json.toJson(
      Map(
        "address.manualAddress" -> "123 Fake Street entered manually",
        "address.postcode" -> "SW1A1AA"
      )
    )
    manualAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.address.isDefined should be(true)
        val address = success.address.get
        address.manualAddress should be(Some("123 Fake Street entered manually"))
        address.postcode should be("SW1A1AA")
      }
    )
  }

  it should "error out on empty values for manual address" in {
    val js =  Json.toJson(
      Map(
        "address.manualAddress" -> "",
        "address.postcode" -> "SW1A 1AA"
      )
    )
    manualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("address.manualAddress") should be(
          Seq("Please answer this question")
        )
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty json for manual address" in {
    val js =  JsNull

    manualAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("address.manualAddress") should be(
          Seq("Please answer this question")
        )
      },
      success => fail("Should have errored out")
    )
  }
}
