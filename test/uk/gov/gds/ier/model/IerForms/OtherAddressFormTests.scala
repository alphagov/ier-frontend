package uk.gov.gds.ier.model.IerForms

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.IerForms
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import play.api.libs.json.{Json, JsNull}

@RunWith(classOf[JUnitRunner])
class OtherAddressFormTests extends FlatSpec with Matchers with IerForms with WithSerialiser {

  val serialiser = new JsonSerialiser

  def toJson(obj: AnyRef): String = serialiser.toJson(obj)

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = serialiser.fromJson(json)

  it should "error out on empty json" in {
    val js = JsNull

    otherAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("otherAddress") should be(Some(Seq("Please answer this question")))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on empty values" in {
    val js = Json.toJson(
      Map(
        "otherAddress.hasOtherAddress" -> ""
      )
    )
    otherAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("otherAddress") should be(Some(Seq("Please answer this question")))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on invalid values" in {
    val js = Json.toJson(
      Map(
        "otherAddress.hasOtherAddress" -> "bleurch"
      )
    )
    otherAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("otherAddress.hasOtherAddress") should be(Some(Seq("error.boolean")))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "successfully bind (true)" in {
    val js = Json.toJson(
      Map(
        "otherAddress.hasOtherAddress" -> "true"
      )
    )
    otherAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.otherAddress.isDefined should be(true)
        val otherAddress = success.otherAddress.get
        otherAddress.hasOtherAddress should be(true)
      }
    )
  }

  it should "successfully bind(false)" in {
    val js = Json.toJson(
      Map(
        "otherAddress.hasOtherAddress" -> "false"
      )
    )
    otherAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.otherAddress.isDefined should be(true)
        val otherAddress = success.otherAddress.get
        otherAddress.hasOtherAddress should be(false)
      }
    )
  }
}
