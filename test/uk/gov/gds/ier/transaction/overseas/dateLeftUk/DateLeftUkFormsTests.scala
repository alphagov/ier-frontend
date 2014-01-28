package uk.gov.gds.ier.transaction.overseas.dateLeftUk

import uk.gov.gds.ier.serialiser.WithSerialiser
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.{Json, JsNull}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}

class DateLeftUkFormsTests
  extends FlatSpec
  with Matchers
  with DateLeftUkForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "error out on empty json" in {
    val js = JsNull
    dateLeftUkForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.errorMessages("dateLeftUk.month") should be(Seq("Please answer this question"))
        hasErrors.errorMessages("dateLeftUk.year") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "dateLeftUk.month" -> "",
        "dateLeftUk.year" -> ""
      )
    )
    dateLeftUkForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.errorMessages("dateLeftUk.month") should be(Seq("Please answer this question"))
        hasErrors.errorMessages("dateLeftUk.year") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing year" in {
    val js = Json.toJson(
      Map(
        "dateLeftUk.month" -> "10",
        "dateLeftUk.year" -> ""
      )
    )
    dateLeftUkForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("dateLeftUk.year") should be(Seq("Please enter the year when you left the UK"))
        hasErrors.globalErrorMessages should be(Seq("Please enter the year when you left the UK"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing month" in {
    val js = Json.toJson(
      Map(
        "dateLeftUk.month" -> "",
        "dateLeftUk.year" -> "2000"
      )
    )
    dateLeftUkForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("dateLeftUk.month") should be(Seq("Please enter the month when you left the UK"))
        hasErrors.globalErrorMessages should be(Seq("Please enter the month when you left the UK"))
      },
      success => fail("Should have errored out.")
    )
  }
}
