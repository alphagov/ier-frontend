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
        hasErrors.errorMessages("name.lastName") should be(Seq("ordinary_name_error_enterFullName"))
        hasErrors.errorMessages("name.firstName") should be(Seq("ordinary_name_error_enterFullName"))
        hasErrors.errorMessages("previousName") should be(Seq("ordinary_previousName_error_answerThis"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_name_error_enterFullName", "ordinary_previousName_error_answerThis"))
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
          "ordinary_name_error_enterFirstName",
          "ordinary_name_error_enterLastName",
          "ordinary_previousName_error_enterFirstName",
          "ordinary_previousName_error_enterLastName"))
        hasErrors.errorMessages("name.firstName") should be(Seq("ordinary_name_error_enterFirstName"))
        hasErrors.errorMessages("name.lastName") should be(Seq("ordinary_name_error_enterLastName"))
        hasErrors.errorMessages("previousName.previousName.firstName") should be(Seq("ordinary_previousName_error_enterFirstName"))
        hasErrors.errorMessages("previousName.previousName.lastName") should be(Seq("ordinary_previousName_error_enterLastName"))
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
        hasErrors.errorMessages("name.firstName") should be(Seq("ordinary_name_error_enterFirstName"))
        hasErrors.errorMessages("name.lastName") should be(Seq("ordinary_name_error_enterLastName"))
        hasErrors.errorMessages("previousName.previousName.firstName") should be(Seq("ordinary_previousName_error_enterFirstName"))
        hasErrors.errorMessages("previousName.previousName.lastName") should be(Seq("ordinary_previousName_error_enterLastName"))


        hasErrors.globalErrorMessages should be(Seq(
          "ordinary_name_error_enterFirstName",
          "ordinary_name_error_enterLastName",
          "ordinary_previousName_error_enterFirstName",
          "ordinary_previousName_error_enterLastName"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing previous name" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "john",
        "name.middleNames" -> "joe",
        "name.lastName" -> "smith",
        "previousName.hasPreviousName" -> "true"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousName.previousName.firstName" -> Seq(
            "ordinary_previousName_error_enterFullName"
          ),
          "previousName.previousName.lastName" -> Seq(
            "ordinary_previousName_error_enterFullName"
          ),
          "previousName.previousName" -> Seq(
            "ordinary_previousName_error_enterFullName"
          )
        ))
        hasErrors.globalErrorMessages should be(Seq(
          "ordinary_previousName_error_enterFullName"
        ))
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
        hasErrors.errorMessages("name.lastName") should be(Seq("ordinary_name_error_enterLastName"))
        hasErrors.errorMessages("previousName.previousName.lastName") should be(Seq("ordinary_previousName_error_enterLastName"))
        hasErrors.globalErrorMessages should be(Seq(
          "ordinary_name_error_enterLastName",
          "ordinary_previousName_error_enterLastName"
        ))
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

  it should "ignore previous name values if hasPreviousName is false" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> "John",
        "name.middleNames" -> "joe",
        "name.lastName" -> "Smith",
        "previousName.hasPreviousName" -> "false",
        "previousName.previousName.firstName" -> "John",
        "previousName.previousName.middleNames" -> "joe",
        "previousName.previousName.lastName" -> "Smith"
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
        success.previousName.get.hasPreviousName should be(false)
      }
    )
  }

  it should "error on too long values" in {
    val js = Json.toJson(
      Map(
        "name.firstName" -> textTooLong,
        "name.middleNames" -> textTooLong,
        "name.lastName" -> textTooLong,
        "previousName.hasPreviousName" -> "true",
        "previousName.previousName.firstName" -> textTooLong,
        "previousName.previousName.middleNames" -> textTooLong,
        "previousName.previousName.lastName" -> textTooLong
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "name.firstName" -> Seq("ordinary_name_error_firstNameTooLong"),
          "name.middleNames" -> Seq("ordinary_name_error_middleNamesTooLong"),
          "name.lastName" -> Seq("ordinary_name_error_lastNameTooLong"),
          "previousName.previousName.firstName" -> Seq("ordinary_previousName_error_firstNameTooLong"),
          "previousName.previousName.middleNames" -> Seq("ordinary_previousName_error_middleNamesTooLong"),
          "previousName.previousName.lastName" -> Seq("ordinary_previousName_error_lastNameTooLong")
        ))
      },
      success => fail("Should have errored out")
    )
  }
}

