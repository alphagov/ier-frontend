package uk.gov.gds.ier.model.IerForms

import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.IerForms
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.libs.json.{Json, JsNull}
import org.joda.time.DateTime
import uk.gov.gds.ier.model.Address

@RunWith(classOf[JUnitRunner])
class AddressFormTests extends FlatSpec with Matchers with IerForms with WithSerialiser {

  val serialiser = new JsonSerialiser

  def toJson(obj: AnyRef): String = serialiser.toJson(obj)

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = serialiser.fromJson(json)

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
        address.addressLine should be("123 Fake Street")
        address.postcode should be("SW1A1AA")
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    addressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("address") should be(Some(
          Seq("Please answer this question")))
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
        hasErrors.errorsAsMap.get("address") should be(Some(
          Seq("Please answer this question")))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind possible Address list" in {
    val possibleAddressJS = serialiser.toJson(List(Address("123 Fake Street", "AB12 3CD")))
    val js = Json.toJson(
      Map(
        "address.address" -> "321 My Street",
        "address.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> possibleAddressJS
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.address.isDefined should be(true)
        val Some(address) = success.address

        success.possibleAddresses.isDefined should be(true)
        val Some(possibleAddresses) = success.possibleAddresses

        address.addressLine should be("321 My Street")
        address.postcode should be("SW1A 1AA")

        possibleAddresses should be(List(Address("123 Fake Street", "AB12 3CD")))
      }
    )
  }

  it should "not error out with empty text" in {
    val js = Json.toJson(
      Map(
        "address.address" -> "321 My Street",
        "address.postcode" -> "SW1A 1AA",
        "possibleAddresses.jsonList" -> ""
      )
    )
    addressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.address.isDefined should be(true)
        val Some(address) = success.address

        success.possibleAddresses.isDefined should be(false)

        address.addressLine should be("321 My Street")
        address.postcode should be("SW1A 1AA")
      }
    )
  }

}
