package uk.gov.gds.ier.step.country

import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{ErrorMessages, ErrorTransformer, FormKeys}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.libs.json.{Json, JsNull}
import org.joda.time.DateTime
import uk.gov.gds.ier.model.Country

@RunWith(classOf[JUnitRunner])
class CountryFormTests
  extends FlatSpec
  with Matchers
  with CountryForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "successfully bind a valid country choice (Abroad)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "Abroad"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.country should be("Abroad")
      }
    )
  }

  it should "successfully bind a valid country choice (British Islands)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "British Islands"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.country should be("British Islands")
      }
    )
  }

  it should "successfully bind a valid country choice (Northern Ireland)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "Northern Ireland"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.country should be("Northern Ireland")
      }
    )
  }

  it should "successfully bind a valid country choice (Wales)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "Wales"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.country should be("Wales")
      }
    )
  }

  it should "successfully bind a valid country choice (Scotland)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "Scotland"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.country should be("Scotland")
      }
    )
  }

  it should "successfully bind a valid country choice (England)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "England"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.country should be("England")
      }
    )
  }

  it should "error out on invalid country choice" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "Joe Bloggs"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        hasErrors.errorMessages("country") should be(Seq("This is not a valid country"))
        new ErrorTransformer().transform(hasErrors).errorMessages("country.residence") should be(Seq("This is not a valid country"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    countryForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        new ErrorTransformer().transform(hasErrors).errorMessages("country.residence") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }


  it should "error out on empty values" in {
    val js =  Json.toJson(
      Map(
        "country.residence" -> ""
      )
    )
    countryForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(1)
        new ErrorTransformer().transform(hasErrors).errorMessages("country.residence") should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }
}

