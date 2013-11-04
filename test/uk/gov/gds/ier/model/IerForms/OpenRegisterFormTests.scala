package uk.gov.gds.ier.model.IerForms

import play.api.libs.json.{JsNull, Json}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.IerForms
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class OpenRegisterFormTests extends FlatSpec with Matchers with IerForms with WithSerialiser {

  val serialiser = new JsonSerialiser

  def toJson(obj: AnyRef): String = serialiser.toJson(obj)

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = serialiser.fromJson(json)

  it should "successfully bind (true)" in {
    val js = Json.toJson(
      Map(
        "openRegister.optIn" -> "true"
      )
    )
    openRegisterForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.openRegisterOptin should be(Some(true))
      }
    )
  }

  it should "successfully bind (false)" in {
    val js = Json.toJson(
      Map(
        "openRegister.optIn" -> "false"
      )
    )
    openRegisterForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.openRegisterOptin should be(Some(false))
      }
    )
  }

  it should "not error with empty json (this is a dark pattern)" in {
    val js = JsNull

    openRegisterForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.openRegisterOptin should be(Some(true))
      }
    )
  }

  it should "not error with empty values (this is a dark pattern)" in {
    val js = Json.toJson(
      Map(
        "openRegister.optIn" -> ""
      )
    )

    openRegisterForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.openRegisterOptin should be(Some(true))
      }
    )
  }
}
