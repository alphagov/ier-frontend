package uk.gov.gds.ier.model.IerForms

import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.IerForms
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.libs.json.{Json, JsNull}
import org.joda.time.DateTime

@RunWith(classOf[JUnitRunner])
class DateOfBirthFormTests extends FlatSpec with Matchers with IerForms with WithSerialiser {

  val serialiser = new JsonSerialiser

  def toJson(obj: AnyRef): String = serialiser.toJson(obj)

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = serialiser.fromJson(json)

  it should "error out on empty json" in {
    val js = JsNull
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorsAsMap.get("dob") should be(Some(Seq("Please enter your date of birth")))
        hasErrors.errors.size should be(1)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "",
        "dob.month" -> "",
        "dob.year" -> ""
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorsAsMap.get("dob") should be(Some(Seq("Please enter your date of birth")))
        hasErrors.errors.size should be(1)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "describe missing values (month, year)" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "1",
        "dob.month" -> "",
        "dob.year" -> ""
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorsAsMap.get("dob.year") should be(Some(Seq("Please enter your year of birth")))
        hasErrors.errorsAsMap.get("dob.month") should be(Some(Seq("Please enter your month of birth")))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "describe missing values (day, month)" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "",
        "dob.month" -> "",
        "dob.year" -> "1988"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorsAsMap.get("dob.day") should be(Some(Seq("Please enter your day of birth")))
        hasErrors.errorsAsMap.get("dob.month") should be(Some(Seq("Please enter your month of birth")))
      },
      success => fail("Should have errored out.")
    )
  }

  it should "successfully bind a valid date" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "1",
        "dob.month" -> "12",
        "dob.year" -> "1980"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.dob.isDefined should be(true)
        val dob = success.dob.get
        dob.day should be(1)
        dob.month should be(12)
        dob.year should be(1980)
      }
    )
  }

  it should "error out on a date under 16 years from today" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "1",
        "dob.month" -> "12",
        "dob.year" -> (DateTime.now().getYear - 10).toString
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("dob") should be(Some(Seq("Minimum age to register to vote is 16")))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on a date over 100 years old" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "1",
        "dob.month" -> "12",
        "dob.year" -> (DateTime.now().getYear - 120).toString
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("dob") should be(Some(Seq("The date you specified is invalid")))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on a invalid date values" in {
    val js = Json.toJson(
      Map(
        "dob.day" -> "a",
        "dob.month" -> "b",
        "dob.year" -> "c"
      )
    )
    dateOfBirthForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.errorsAsMap.get("dob.day") should be(Some(Seq("The day you provided is invalid")))
        hasErrors.errorsAsMap.get("dob.month") should be(Some(Seq("The month you provided is invalid")))
        hasErrors.errorsAsMap.get("dob.year") should be(Some(Seq("The year you provided is invalid")))
      },
      success => fail("Should have errored out")
    )
  }
}
