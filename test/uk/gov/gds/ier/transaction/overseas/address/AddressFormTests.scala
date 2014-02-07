package uk.gov.gds.ier.transaction.overseas.address

import uk.gov.gds.ier.serialiser.WithSerialiser
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.{Json, JsNull}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}

class AddressFormTests
  extends FlatSpec
  with Matchers
  with AddressForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "error out on empty json" in {
    val js = JsNull
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("overseasAddress.country") should be(Seq("Correspondence country is required"))
        hasErrors.errorMessages("overseasAddress.overseasAddressDetails") should be(Seq("Correspondence address is required"))
        hasErrors.globalErrorMessages should be(Seq("Correspondence country is required", "Correspondence address is required" ))
        hasErrors.errors.size should be(4)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "overseasAddress.country" -> "",
        "overseasAddress.addressDetails" -> ""
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("overseasAddress.country") should be(Seq("Correspondence country is required"))
        hasErrors.errorMessages("overseasAddress.overseasAddressDetails") should be(Seq("Correspondence address is required"))
        hasErrors.globalErrorMessages should be(Seq("Correspondence country is required", "Correspondence address is required" ))
        hasErrors.errors.size should be(4)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing country" in {
    val js = Json.toJson(
      Map(
        "overseasAddress.country" -> "",
        "overseasAddress.overseasAddressDetails" -> "some address"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("overseasAddress.country") should be(Seq("Correspondence country is required"))
        hasErrors.globalErrorMessages should be(Seq("Correspondence country is required"))
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }
  
  it should "error out on missing address" in {
    val js = Json.toJson(
      Map(
        "overseasAddress.country" -> "United Kingdom",
        "overseasAddress.addressDetails" -> ""
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("overseasAddress.overseasAddressDetails") should be(Seq("Correspondence address is required"))
        hasErrors.globalErrorMessages should be(Seq("Correspondence address is required" ))
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }
  
  it should "successfully parse" in {
    val js = Json.toJson(
      Map(
        "overseasAddress.country" -> "United Kingdom",
        "overseasAddress.overseasAddressDetails" -> "some address"
      )
    )
    addressForm.bind(js).fold(
      hasErrors => {
        fail("Should have errored out.")
      },
      success => {
        val Some(overseasAddress) = success.address
        overseasAddress.country.get should be ("United Kingdom")
      }
    )
  }
}
