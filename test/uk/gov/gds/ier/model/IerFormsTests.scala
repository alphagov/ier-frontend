package uk.gov.gds.ier.model

import play.api.libs.json._
import uk.gov.gds.ier.validation.IerForms
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import uk.gov.gds.ier.serialiser.JsonSerialiser
import org.joda.time.DateTime

@RunWith(classOf[JUnitRunner])
class IerFormsTests extends FlatSpec with Matchers with IerForms {

  val serialiser = new JsonSerialiser

  "PostcodeForm" should "bind a postcode" in {
    val jsVal = Json.toJson(
      Map(
        "postcode" -> "BT12 5EG"
      )
    )
    postcodeForm.bind(jsVal).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success should be("BT12 5EG")
      }
    )
  }
  it should "throw an error on a bad postcode" in {
    val jsVal = Json.toJson(
      Map(
        "postcode" -> "ZX123 BAD"
      )
    )
    postcodeForm.bind(jsVal).fold(
      hasErrors => {
        hasErrors.errorsAsMap.contains("postcode") should be(true)
      },
      success => {
        fail("Should not have succeeded " + success)
      }
    )
  }

  behavior of "nationalityForm"
  it should "succesfully bind json" in {
    val js = JsObject(
      Seq("nationality.nationalities" -> Json.toJson(List("British", "Irish")),
        "nationality.otherCountries" -> Json.toJson(List("Italy", "France")),
        "nationality.hasOtherCountry" -> JsBoolean(true)
      )
    )
    nationalityForm.bind(js).fold(
      hasErrors => {
        fail(hasErrors.toString)
      },
      success => {
        success.nationality.isDefined should be(true)
        val nationality = success.nationality.get

        nationality.nationalities should contain("British")
        nationality.nationalities should contain("Irish")

        nationality.otherCountries should contain("Italy")
        nationality.otherCountries should contain("France")

        nationality.hasOtherCountry should be(Some(true))
      }
    )
  }

  it should "only bind to nationality in InProgressApplication" in {
    val js = JsObject(
      Seq("nationality.nationalities" -> Json.toJson(List("British", "Irish")),
        "nationality.otherCountries" -> Json.toJson(List("Italy", "France")),
        "nationality.hasOtherCountry" -> JsBoolean(true)
      )
    )
    nationalityForm.bind(js).fold(
      hasErrors => {
        fail(hasErrors.toString)
      },
      success => {
        success.nationality.isDefined should be(true)

        success.address should be(None)
        success.contact should be(None)
        success.dob should be(None)
        success.name should be(None)
        success.nino should be(None)
        success.openRegisterOptin should be(None)
        success.otherAddress should be(None)
        success.postalVoteOptin should be(None)
        success.previousAddress should be(None)
        success.previousName should be(None)
      }
    )
  }

  it should "handle no nationality or other country correctly" in {
    val js = Json.toJson(
      Map("nationality.noNationalityReason" -> "I don't have a nationality. I am stateless.")
    )
    nationalityForm.bind(js).fold(
      hasErrors => fail(hasErrors.toString),
      success => {
        val nationality = success.nationality.get
        nationality.hasOtherCountry should be(None)
        nationality.nationalities should be(List.empty)
        nationality.otherCountries should be(List.empty)
        nationality.noNationalityReason should be(Some("I don't have a nationality. I am stateless."))
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull
    nationalityForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorsAsMap.get("nationality") should be(Some(Seq("Please select your Nationality")))
        hasErrors.errors.size should be(1)
      },
      success => fail("Should have errored out.")
    )
  }

  behavior of "dateOfBirthForm"

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

  behavior of "nameForm"
  it should "error out on empty json" in {
    val js = JsNull
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorsAsMap.get("name.firstName") should be(Some(Seq("error.required")))
        hasErrors.errorsAsMap.get("name.lastName") should be(Some(Seq("error.required")))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing fields" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "",
        "name.middleNames" -> "joe",
        "name.lastName" -> ""
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("name") should be(Some(Seq("Please enter your full name")))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "John",
        "name.middleNames" -> "joe",
        "name.lastName" -> "Smith"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        fail(serialiser.toJson(hasErrors.errorsAsMap))
      },
      success => {
        success.name.isDefined should be(true)
        val name = success.name.get
        name.firstName should be("John")
        name.lastName should be("Smith")
        name.middleNames should be(Some("joe"))
      }
    )
  }

  behavior of "previousNameForm"
  it should "successfully bind" in {
    val js = Json.toJson(
      Map(
        "previousName.hasPreviousName" -> "true",
        "previousName.previousName.firstName" -> "John",
        "previousName.previousName.middleNames" -> "Joe",
        "previousName.previousName.lastName" -> "Smith"
      )
    )
    previousNameForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.previousName.isDefined should be(true)
        val previousNameWrapper = success.previousName.get
        previousNameWrapper.hasPreviousName should be(true)
        previousNameWrapper.previousName.isDefined should be(true)

        val previousName = previousNameWrapper.previousName.get
        previousName.firstName should be("John")
        previousName.lastName should be("Smith")
        previousName.middleNames should be(Some("Joe"))
      }
    )
  }

  it should "successfully bind with no previous name" in {
    val js = Json.toJson(
      Map(
        "previousName.hasPreviousName" -> "false"
      )
    )
    previousNameForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.errorsAsMap)),
      success => {
        success.previousName.isDefined should be(true)
        val previousName = success.previousName.get
        previousName.hasPreviousName should be (false)
        previousName.previousName should be(None)
      }
    )
  }

  it should "error out if previousName is not provided" in {
    val js = Json.toJson(
      Map(
        "previousName.hasPreviousName" -> "true"
      )
    )
    previousNameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("previousName") should be(Some(Seq("Please enter your previous name")))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out with no json" in {
    val js = JsNull
    previousNameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("previousName") should be(Some(Seq("Please answer this question")))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing fields" in {
    val js = Json.toJson(
      Map(
        "previousName.hasPreviousName" -> "true",
        "previousName.previousName.firstName" -> "",
        "previousName.previousName.middleNames" -> "Joe",
        "previousName.previousName.lastName" -> ""
      )
    )
    previousNameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorsAsMap.get("previousName.previousName") should be(Some(Seq("Please enter your full name")))
      },
      success => fail("Should have errorred out")
    )
  }
}
