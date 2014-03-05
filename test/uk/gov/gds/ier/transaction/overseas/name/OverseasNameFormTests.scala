package uk.gov.gds.ier.transaction.overseas.name

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import play.api.libs.json.{Json, JsNull}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers

class OverseasNameFormTests
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
        hasErrors.errorMessages("overseasName.name.lastName") should be(Seq("Please enter your full name"))
        hasErrors.errorMessages("overseasName.name.firstName") should be(Seq("Please enter your full name"))
        hasErrors.errorMessages("overseasName.previousName") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your full name", "Please answer this question"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "describe all missing fields" in {
    val js = Json.toJson(
      Map(
        "overseasName.name.firstName" -> "",
        "overseasName.name.middleNames" -> "joe",
        "overseasName.name.lastName" -> "",
        "overseasName.previousName.hasPreviousName" -> "true",
        "overseasName.previousName.previousName.firstName" -> "",
        "overseasName.previousName.previousName.middleNames" -> "Joe",
        "overseasName.previousName.previousName.lastName" -> ""
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
        hasErrors.errorMessages("overseasName.name.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("overseasName.name.lastName") should be(Seq("Please enter your last name"))
        hasErrors.errorMessages("overseasName.previousName.previousName.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("overseasName.previousName.previousName.lastName") should be(Seq("Please enter your last name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "check for too long names" in {
    val inputDataJson = Json.toJson(
      Map(
        "overseasName.name.firstName" -> textTooLong,
        "overseasName.name.middleNames" -> textTooLong,
        "overseasName.name.lastName" -> textTooLong,
        "overseasName.previousName.hasPreviousName" -> "true",
        "overseasName.previousName.previousName.firstName" -> textTooLong,
        "overseasName.previousName.previousName.middleNames" -> textTooLong,
        "overseasName.previousName.previousName.lastName" -> textTooLong
      )
    )
    nameForm.bind(inputDataJson).fold(
      hasErrors => {
        hasErrors.errorsAsText should be("" +
          "overseasName.name.firstName -> First name can be no longer than 256 characters\n" +
          "overseasName.name.middleNames -> Middle names can be no longer than 256 characters\n" +
          "overseasName.name.lastName -> Last name can be no longer than 256 characters\n" +
          "overseasName.name.firstName -> First name can be no longer than 256 characters\n" +
          "overseasName.name.middleNames -> Middle names can be no longer than 256 characters\n" +
          "overseasName.name.lastName -> Last name can be no longer than 256 characters"
        )
        hasErrors.globalErrorsAsText should be("" +
          "First name can be no longer than 256 characters\n" +
          "Middle names can be no longer than 256 characters\n" +
          "Last name can be no longer than 256 characters\n" +
          "First name can be no longer than 256 characters\n" +
          "Middle names can be no longer than 256 characters\n" +
          "Last name can be no longer than 256 characters")
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on missing fields" in {
    val js = Json.toJson(
      Map(
        "overseasName.name.middleNames" -> "joe",
        "overseasName.previousName.hasPreviousName" -> "true",
        "overseasName.previousName.previousName.middleNames" -> "joe"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(8)
        hasErrors.errorMessages("overseasName.name.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("overseasName.name.lastName") should be(Seq("Please enter your last name"))
        hasErrors.errorMessages("overseasName.previousName.previousName.firstName") should be(Seq("Please enter your first name"))
        hasErrors.errorMessages("overseasName.previousName.previousName.lastName") should be(Seq("Please enter your last name"))


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
        "overseasName.name.firstName" -> "john",
        "overseasName.name.middleNames" -> "joe",
        "overseasName.previousName.hasPreviousName" -> "true",
        "overseasName.previousName.previousName.middleNames" -> "joe",
        "overseasName.previousName.previousName.firstName" -> "john"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(4)
        hasErrors.errorMessages("overseasName.name.lastName") should be(Seq("Please enter your last name"))
        hasErrors.errorMessages("overseasName.previousName.previousName.lastName") should be(Seq("Please enter your last name"))
        hasErrors.globalErrorMessages should be(Seq("Please enter your last name","Please enter your last name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind with no previous name" in {
    val js = Json.toJson(
      Map(
        "overseasName.name.firstName" -> "John",
        "overseasName.name.middleNames" -> "joe",
        "overseasName.name.lastName" -> "Smith",
        "overseasName.previousName.hasPreviousName" -> "false"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        fail(serialiser.toJson(hasErrors.prettyPrint))
      },
      success => {
        success.overseasName.get.name.isDefined should be(true)
        val name = success.overseasName.get.name.get
        name.firstName should be("John")
        name.lastName should be("Smith")
        name.middleNames should be(Some("joe"))

        success.overseasName.get.previousName.isDefined should be(true)
        val previousName = success.overseasName.get.previousName.get
        previousName.previousName.isDefined should be(false)
        previousName.hasPreviousName should be(false)
      }
    )
  }
  it should "successfully bind" in {
    val js = Json.toJson(
      Map(
        "overseasName.name.firstName" -> "John",
        "overseasName.name.middleNames" -> "joe",
        "overseasName.name.lastName" -> "Smith",
        "overseasName.previousName.hasPreviousName" -> "true",
        "overseasName.previousName.previousName.firstName" -> "Jonny",
        "overseasName.previousName.previousName.middleNames" -> "Joe",
        "overseasName.previousName.previousName.lastName" -> "Bloggs"
      )
    )
    nameForm.bind(js).fold(
      hasErrors => {
        fail(serialiser.toJson(hasErrors.prettyPrint))
      },
      success => {
        success.overseasName.get.name.isDefined should be(true)
        val name = success.overseasName.get.name.get
        
        name.firstName should be("John")
        name.lastName should be("Smith")
        name.middleNames should be(Some("joe"))

        success.overseasName.get.previousName.isDefined should be(true)
        success.overseasName.get.previousName.get.hasPreviousName should be(true)
        val previousName = success.overseasName.get.previousName.get
        previousName.previousName.get.firstName should be("Jonny")
        previousName.previousName.get.middleNames should be(Some("Joe"))
        previousName.previousName.get.lastName should be("Bloggs")
      }
    )
  }
}

