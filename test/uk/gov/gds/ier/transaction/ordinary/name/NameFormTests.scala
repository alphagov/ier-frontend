package uk.gov.gds.ier.transaction.ordinary.name

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
        hasErrors.errors.size should be(5)
        hasErrors.errorMessages("name.lastName") should be(Seq("Please enter your full name"))
        hasErrors.errorMessages("name.firstName") should be(Seq("Please enter your full name"))
        hasErrors.errorMessages("previousName") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your full name", "Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "describe all missing fields" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "",
        "name.middleNames" -> "joe",
        "name.lastName" -> "",
        "previousName.hasPreviousName" -> "true",
        "previousName.previousName.firstName" -> "",
        "previousName.previousName.middleNames" -> "Joe",
        "previousName.previousName.lastName" -> ""
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(8)
        hasErrors.globalErrorMessages should be(Seq(
          "Please enter your first name",
          "Please enter your last name",
          "Please enter your first name",
          "Please enter your last name"))
        hasErrors.errorMessages("name.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("name.lastName") should be(Seq("Please enter your last name"))
        hasErrors.errorMessages("previousName.previousName.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("previousName.previousName.lastName") should be(Seq("Please enter your last name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing fields" in {
    val js = Json.toJson(
      Map(
        "name.middleNames" -> "joe",
        "previousName.hasPreviousName" -> "true",
        "previousName.previousName.middleNames" -> "joe"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(8)
        hasErrors.errorMessages("name.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("name.lastName") should be(Seq("Please enter your last name"))
        hasErrors.errorMessages("previousName.previousName.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("previousName.previousName.lastName") should be(Seq("Please enter your last name"))


        hasErrors.globalErrorMessages should be(Seq(
          "Please enter your first name",
          "Please enter your last name",
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
        "name.middleNames" -> "joe",
        "previousName.hasPreviousName" -> "true",
        "previousName.previousName.middleNames" -> "joe",
        "previousName.previousName.firstName" -> "john"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("name.lastName") should be(Seq("Please enter your last name"))
        hasErrors.errorMessages("previousName.previousName.lastName") should be(Seq("Please enter your last name"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your last name","Please enter your last name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind with no previous name" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "John",
        "name.middleNames" -> "joe",
        "name.lastName" -> "Smith",
        "previousName.hasPreviousName" -> "false"
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

        success.previousName.isDefined should be(true)
        success.previousName.get.previousName.isDefined should be(false)
        success.previousName.get.hasPreviousName should be(false)
      }
    )
  }
  it should "successfully bind" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "John",
        "name.middleNames" -> "joe",
        "name.lastName" -> "Smith",
        "previousName.hasPreviousName" -> "true",
        "previousName.previousName.firstName" -> "Jonny",
        "previousName.previousName.middleNames" -> "Joe",
        "previousName.previousName.lastName" -> "Bloggs"
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

        success.previousName.isDefined should be(true)
        success.previousName.get.previousName.isDefined should be(true)
        success.previousName.get.hasPreviousName should be(true)
        val previousName = success.previousName.get.previousName.get
        previousName.firstName should be("Jonny")
        previousName.middleNames should be(Some("Joe"))
        previousName.lastName should be("Bloggs")
      }
    )
  }
}

