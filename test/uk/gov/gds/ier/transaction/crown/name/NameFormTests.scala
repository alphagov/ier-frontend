package uk.gov.gds.ier.transaction.crown.name

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import play.api.libs.json.{Json, JsNull}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers

class NameFormTests
  extends FlatSpec
  with Matchers
  with NameForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "error out on empty json" in {
    val js = JsNull
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(3)
        hasErrors.errorMessages("name.lastName") should be(Seq("Please enter your full name"))
        hasErrors.errorMessages("name.firstName") should be(Seq("Please enter your full name"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your full name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "describe all missing fields" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "",
        "name.middleNames" -> "joe",
        "name.lastName" -> ""
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.globalErrorMessages should be(Seq(
          "Please enter your first name",
          "Please enter your last name"))
        hasErrors.errorMessages("name.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("name.lastName") should be(Seq("Please enter your last name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing fields" in {
    val js = Json.toJson(
      Map(
        "name.middleNames" -> "joe"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("name.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("name.lastName") should be(Seq("Please enter your last name"))

        hasErrors.globalErrorMessages should be(Seq(
          "Please enter your first name",
          "Please enter your last name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on a missing field" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "john",
        "name.middleNames" -> "joe"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("name.lastName") should be(Seq("Please enter your last name"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your last name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind with no previous name" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "John",
        "name.middleNames" -> "joe",
        "name.lastName" -> "Smith"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        fail(serialiser.toJson(hasErrors.prettyPrint))
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
        fail(serialiser.toJson(hasErrors.prettyPrint))
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
}

