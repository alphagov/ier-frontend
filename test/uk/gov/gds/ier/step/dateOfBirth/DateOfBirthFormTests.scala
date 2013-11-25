package uk.gov.gds.ier.step.dateOfBirth

import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import org.scalatest.{Matchers, FlatSpec}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.libs.json.{Json, JsNull}
import org.joda.time.DateTime
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}

class DateOfBirthFormTests 
  extends FlatSpec
  with Matchers
  with DateOfBirthForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "error out on empty json" in {
    val js = JsNull
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("dob") should be(Seq("Please enter your date of birth"))
        hasErrors.errors.size should be(1)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "",
        "dob.dob.month" -> "",
        "dob.dob.year" -> ""
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("dob") should be(Seq("Please enter your date of birth"))
        hasErrors.errors.size should be(1)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "describe missing values (month, year)" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "1",
        "dob.dob.month" -> "",
        "dob.dob.year" -> ""
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("dob.dob.year") should be(Seq("Please enter your year of birth"))
        hasErrors.errorMessages("dob.dob.month") should be(Seq("Please enter your month of birth"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "describe missing values (day, month)" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "",
        "dob.dob.month" -> "",
        "dob.dob.year" -> "1988"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("dob.dob.day") should be(Seq("Please enter your day of birth"))
        hasErrors.errorMessages("dob.dob.month") should be(Seq("Please enter your month of birth"))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "successfully bind a valid date" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "1",
        "dob.dob.month" -> "12",
        "dob.dob.year" -> "1980"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString(",")),
      success => {
        success.dob.isDefined should be(true)
        val Some(dob) = success.dob.get.dob
        dob.day should be(1)
        dob.month should be(12)
        dob.year should be(1980)
      }
    )
  }

  it should "error out on a date under 16 years from today" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "1",
        "dob.dob.month" -> "12",
        "dob.dob.year" -> (DateTime.now().getYear - 10).toString
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorMessages("dob.dob") should be(Seq("Minimum age to register to vote is 16"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on a date over 100 years old" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "1",
        "dob.dob.month" -> "12",
        "dob.dob.year" -> (DateTime.now().getYear - 120).toString
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorMessages("dob.dob") should be(Seq("The date you specified is invalid"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on a invalid date values" in {
    val js = Json.toJson(
      Map(
        "dob.dob.day" -> "a",
        "dob.dob.month" -> "b",
        "dob.dob.year" -> "c"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.errorMessages("dob.dob.day") should be(Seq("The day you provided is invalid"))
        hasErrors.errorMessages("dob.dob.month") should be(Seq("The month you provided is invalid"))
        hasErrors.errorMessages("dob.dob.year") should be(Seq("The year you provided is invalid"))
      },
      success => fail("Should have errored out")
    )
  }
}
