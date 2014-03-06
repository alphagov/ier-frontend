package uk.gov.gds.ier.transaction.forces.openRegister

import play.api.libs.json.{JsNull, Json}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.test.TestHelpers

class OpenRegisterFormTests  
  extends FlatSpec
  with Matchers
  with OpenRegisterForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser
    
  it should "successfully bind (true)" in {
    val js = Json.toJson(
      Map(
        "openRegister.optIn" -> "true"
      )
    )
    openRegisterForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
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
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.openRegisterOptin should be(Some(false))
      }
    )
  }

  it should "not error with empty json (this is a dark pattern)" in {
    val js = JsNull

    openRegisterForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
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
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.openRegisterOptin should be(Some(true))
      }
    )
  }
}
