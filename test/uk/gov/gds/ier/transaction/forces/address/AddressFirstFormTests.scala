package uk.gov.gds.ier.transaction.forces.address

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.libs.json.{JsNull, Json}
import uk.gov.gds.ier.model.HasAddressOption

class AddressFirstFormTests
  extends FlatSpec
  with Matchers
  with AddressForms
  with AddressFirstForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "error out on empty input" in {
    val js = JsNull
    addressFirstForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("address.hasAddress") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing values in input" in {
    val js = Json.toJson(
      Map(
        "address.hasAddress" -> ""
      )
    )
    addressFirstForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("address.hasAddress") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "successfully bind when user has uk address" in {
    val js = Json.toJson(
      Map(
        "address.hasAddress" -> "yes-living-there"
      )
    )
    addressFirstForm.bind(js).fold(
      hasErrors => {
        fail("Binding failed with " + hasErrors.errorsAsTextAll)
      },
      success => {
        val Some(address) = success.address
        address should have(
          'hasAddress (Some(HasAddressOption.YesAndLivingThere))
        )
      }
    )
  }

  it should "successfully bind when user doesn't live at their uk address" in {
    val js = Json.toJson(
      Map(
        "address.hasAddress" -> "yes-not-living-there"
      )
    )
    addressFirstForm.bind(js).fold(
      hasErrors => {
        fail("Binding failed with " + hasErrors.errorsAsTextAll)
      },
      success => {
        val Some(address) = success.address
        address should have(
          'hasAddress (Some(HasAddressOption.YesAndNotLivingThere))
        )
      }
    )
  }

  it should "successfully bind when user does not has previous address" in {
    val js = Json.toJson(
      Map(
        "address.hasAddress" -> "no"
      )
    )
    addressFirstForm.bind(js).fold(
      hasErrors => {
        fail("Binding failed with " + hasErrors.errorsAsTextAll)
      },
      success => {
        val Some(address) = success.address
        address should have(
          'hasAddress (Some(HasAddressOption.No))
        )
      }
    )
  }
}
