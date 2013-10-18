package uk.gov.gds.ier.model

import play.api.libs.json._
import uk.gov.gds.ier.validation.IerForms
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import uk.gov.gds.ier.serialiser.JsonSerialiser

@RunWith(classOf[JUnitRunner])
class IerFormsTests extends FlatSpec with Matchers with IerForms {

  "PostcodeForm" should "bind a postcode" in {
    val jsVal = Json.toJson(
      Map(
        "postcode" -> "BT12 5EG"
      )
    )
    postcodeForm.bind(jsVal).fold(
      hasErrors => fail(hasErrors.toString),
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

  it should ""
}
