package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.WithSerialiser
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import play.api.libs.json.{Json, JsNull}
import uk.gov.gds.ier.model.{Addresses, Address, PartialAddress}

class LastUkAddressFormTests
  extends FlatSpec
  with Matchers
  with LastUkAddressForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  behavior of "LastUkAddressForms.lastUkAddressForm"

  it should "successfully bind a valid address" in {
    val js = Json.toJson(
      Map(
        "lastUkAddress.uprn" -> "12345678",
        "lastUkAddress.postcode" -> "SW1A1AA"
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.lastUkAddress.isDefined should be(true)
        val lastUkAddress = success.lastUkAddress.get
        lastUkAddress.uprn should be(Some("12345678"))
        lastUkAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "successfully bind a valid manual input address" in {
    val js = Json.toJson(
      Map(
        "lastUkAddress.manualAddress" -> "123 Fake Street entered manually",
        "lastUkAddress.postcode" -> "SW1A1AA"
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.lastUkAddress.isDefined should be(true)
        val lastUkAddress = success.lastUkAddress.get
        lastUkAddress.manualAddress should be(Some("123 Fake Street entered manually"))
        lastUkAddress.postcode should be("SW1A1AA")
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    lastUkAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("lastUkAddress") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }


  it should "error out on empty values" in {
    val js =  Json.toJson(
      Map(
        "lastUkAddress.address" -> "",
        "lastUkAddress.postcode" -> ""
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("lastUkAddress") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty values in manual address" in {
    val js =  Json.toJson(
      Map(
        "lastUkAddress.manualAddress" -> "",
        "lastUkAddress.postcode" -> ""
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("lastUkAddress") should be(Seq("Please answer this question"))
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
        "lastUkAddress.uprn" -> "12345678",
        "lastUkAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.lastUkAddress.isDefined should be(true)
        val Some(lastUkAddress) = success.lastUkAddress

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        lastUkAddress.uprn should be(Some("12345678"))
        lastUkAddress.postcode should be("SW1A 1AA")

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
        "lastUkAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("lastUkAddress.uprn") should be(Seq("Please select your address"))
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
        "lastUkAddress.manualAddress" -> "1428 Elm Street",
        "lastUkAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS,
        "possibleAddresses.postcode" -> "SW1A 1AA"
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => fail("Should not fail"),
      success => {
        success.lastUkAddress.isDefined should be(true)
        val Some(lastUkAddress) = success.lastUkAddress

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        lastUkAddress.manualAddress should be(Some("1428 Elm Street"))
        lastUkAddress.postcode should be("SW1A 1AA")

        possibleAddresses.jsonList.addresses should be(List(possibleAddress))
      }
    )
  }

  it should "not error out with empty text" in {
    val js = Json.toJson(
      Map(
        "lastUkAddress.uprn" -> "87654321",
        "lastUkAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> "",
        "possibleAddresses.postcode" -> ""
      )
    )
    lastUkAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.lastUkAddress.isDefined should be(true)
        val Some(lastUkAddress) = success.lastUkAddress

        success.possibleAddresses.isDefined should be(false)

        lastUkAddress.uprn should be(Some("87654321"))
        lastUkAddress.postcode should be("SW1A 1AA")
      }
    )
  }

  behavior of "LastUkAddressForms.lookupForm"

  it should "succeed on valid postcode" in {
    val js = Json.toJson(
      Map(
        "lastUkAddress.postcode" -> "SW1A 1AA"
      )
    )

    lookupAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.lastUkAddress.isDefined should be(true)
        val Some(lastUkAddress) = success.lastUkAddress

        lastUkAddress.postcode should be("SW1A 1AA")
        lastUkAddress.uprn should be(None)
        lastUkAddress.manualAddress should be(None)
        lastUkAddress.addressLine should be (None)
      }
    )
  }

  it should "fail out on no postcode" in {
    val js = Json.toJson(Map("lastUkAddress.postcode" -> ""))

    lookupAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("lastUkAddress.postcode") should be(
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
        hasErrors.errorMessages("lastUkAddress.postcode") should be(
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
        hasErrors.errorMessages("lastUkAddress.postcode") should be(
          Seq("Please enter your postcode")
        )
      },
      success => fail("Should have failed out")
    )
  }
}
