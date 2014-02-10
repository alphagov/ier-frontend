package uk.gov.gds.ier.transaction.country

import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.WithSerialiser
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import play.api.libs.json.{Json, JsNull}

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

  it should "successfully bind a valid country choice (Channel Islands)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "Channel Islands"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.country should be("Channel Islands")
      }
    )
  }

  it should "successfully bind a valid country choice (Isle Of Man)" in {
    val js = Json.toJson(
      Map(
        "country.residence" -> "Isle Of Man"
      )
    )
    countryForm.bind(js).fold(
      hasErrors => fail(hasErrors.prettyPrint.mkString),
      success => {
        success.country.isDefined should be(true)
        val country = success.country.get
        country.country should be("Isle Of Man")
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
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("country.residence") should be(Seq("This is not a valid country"))
        hasErrors.globalErrorMessages should be(Seq("This is not a valid country"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    countryForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("country.residence") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
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
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("country.residence") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }
}

