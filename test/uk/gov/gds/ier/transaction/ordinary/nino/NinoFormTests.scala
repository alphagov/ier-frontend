package uk.gov.gds.ier.transaction.ordinary.nino

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.libs.json.{Json, JsNull}
import uk.gov.gds.ier.test.TestHelpers

class NinoFormTests
  extends FlatSpec
  with Matchers
  with NinoForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers{

  val serialiser = jsonSerialiser

  it should "successfully bind to a valid nino" in {
    val js = Json.toJson(
      Map(
        "NINO.NINO" -> "AB 12 34 56 D"
      )
    )
    ninoForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.nino.isDefined should be(true)
        val nino = success.nino.get
        nino.nino should be(Some("AB 12 34 56 D"))
        nino.noNinoReason should be(None)
      }
    )
  }

  it should "successfully bind to a valid no nino reason" in {
    val js = Json.toJson(
      Map(
        "NINO.NoNinoReason" -> "Uh, whuh, dunno!"
      )
    )
    ninoForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.nino.isDefined should be(true)
        val nino = success.nino.get
        nino.nino should be(None)
        nino.noNinoReason should be(Some("Uh, whuh, dunno!"))
      }
    )
  }

  it should "error out if no nino reason is over the max length" in {
    val js = Json.toJson(
      Map(
        "NINO.NoNinoReason" -> "a" * 501
      )
    )
    ninoForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(
           Map("nino.noNinoReason" -> "ordinary_nino_error_maxLength")
        )
      },
      success => {
        success.nino.isDefined should be(true)
        val nino = success.nino.get
        nino.nino should be(None)
        nino.noNinoReason should be(Some("Uh, whuh, dunno!"))
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    ninoForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("ordinary_nino_error_none_entered"))
        hasErrors.errorMessages("NINO.NINO") should be(Seq("ordinary_nino_error_none_entered"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "NINO.NINO" -> "",
        "NINO.NoNinoReason" -> ""
      )
    )
    ninoForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("ordinary_nino_error_none_entered"))
        hasErrors.errorMessages("NINO.NINO") should be(Seq("ordinary_nino_error_none_entered"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out invalid nino" in {
    val js = Json.toJson(
      Map(
        "NINO.NINO" -> "bleurch"
      )
    )
    ninoForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("ordinary_nino_error_incorrect_format"))
        hasErrors.errorMessages("NINO.NINO") should be(Seq("ordinary_nino_error_incorrect_format"))
      },
      success => fail("Should have errored out")
    )
  }
}
