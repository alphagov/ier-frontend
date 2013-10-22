package uk.gov.gds.ier.model.IerForms

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.IerForms
import uk.gov.gds.ier.serialiser.JsonSerialiser
import play.api.libs.json.{JsNull, JsBoolean, Json, JsObject}

@RunWith(classOf[JUnitRunner])
class NationalityFormTests extends FlatSpec with Matchers with IerForms {

  val serialiser = new JsonSerialiser

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
}
