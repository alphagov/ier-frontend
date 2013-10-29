package uk.gov.gds.ier.model.IerForms

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.IerForms
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import play.api.libs.json.{Json, JsNull}
import uk.gov.gds.ier.model.{Addresses, Address}

@RunWith(classOf[JUnitRunner])
class PreviousAddressTests extends FlatSpec with Matchers with IerForms with WithSerialiser {

  val serialiser = new JsonSerialiser

  def toJson(obj: AnyRef): String = serialiser.toJson(obj)

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = serialiser.fromJson(json)

  it should "successfully bind to address and movedRecently=true" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.address" -> "123 Fake Street",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(true)
        
        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.addressLine should be("123 Fake Street")
        previousAddress.postcode should be("SW1A 1AA")
      }
    )
  }

  it should "successfully bind to address and movedRecently=true with possible addresses" in {
    val possibleAddressJS = serialiser.toJson(Addresses(List(Address("123 Fake Street", "AB12 3CD"))))
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.address" -> "123 Fake Street",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(true)

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.addressLine should be("123 Fake Street")
        previousAddress.postcode should be("SW1A 1AA")

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses
        possibleAddresses.addresses should be(List(Address("123 Fake Street", "AB12 3CD")))
      }
    )
  }

  it should "not error out with empty text" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "true",
        "previousAddress.previousAddress.address" -> "123 Fake Street",
        "previousAddress.previousAddress.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> ""
      )
    )
    previousAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.previousAddress.isDefined should be(true)
        val previousAddressWrapper = success.previousAddress.get
        previousAddressWrapper.movedRecently should be(true)

        previousAddressWrapper.previousAddress.isDefined should be(true)
        val previousAddress = previousAddressWrapper.previousAddress.get
        previousAddress.addressLine should be("123 Fake Street")
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
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
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
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("previousAddress") should be(Some(Seq("Please enter your postcode")))
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
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("previousAddress") should be(Some(Seq("Please answer this question")))
      },
      success => fail("Should have thrown an error")
    )
  }
}
