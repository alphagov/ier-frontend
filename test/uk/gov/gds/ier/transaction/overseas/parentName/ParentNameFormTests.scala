package uk.gov.gds.ier.transaction.overseas.parentName

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import play.api.libs.json.{Json, JsNull}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers
import play.api.i18n.Lang

class ParentNameFormTests
  extends FlatSpec
  with Matchers
  with ParentNameForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser
  
  it should "bind successfully" in {
    val data = Map(
        "overseasParentName.parentName.firstName" -> "firstname",
        "overseasParentName.parentName.middleNames" -> "middlenames",
        "overseasParentName.parentName.lastName" -> "lastname",
        "overseasParentName.parentPreviousName.hasPreviousName" -> "true",
        "overseasParentName.parentPreviousName.previousName.firstName" -> "firstname",
        "overseasParentName.parentPreviousName.previousName.middleNames" -> "middlenames",
        "overseasParentName.parentPreviousName.previousName.lastName" -> "lastname"
    )
    
    parentNameForm.bind(data).fold(
        formWithErrors => {
          fail(formWithErrors.prettyPrint.mkString(", "))
        },
        success => {
          success.overseasParentName.get.name.isDefined should be(true)
          val Some(parentName) = success.overseasParentName.get.name
          parentName.firstName should be("firstname")
          parentName.middleNames should be(Some("middlenames"))
          parentName.lastName should be("lastname")
          
          success.overseasParentName.get.previousName.isDefined should be(true)
          val Some(parentPreviousName) = success.overseasParentName.get.previousName
          parentPreviousName.hasPreviousName should be(true)
          parentPreviousName.previousName.isDefined should be(true)
          
          val Some(previousName) = parentPreviousName.previousName
          previousName.firstName should be("firstname")
          previousName.middleNames should be(Some("middlenames"))
          previousName.lastName should be("lastname")
          
        }
    )
  }
  
  it should "error out if empty form is submitted" in {
    val data = Map(
        "overseasParentName.parentName.firstName" -> "",
        "overseasParentName.parentName.middleNames" -> "",
        "overseasParentName.parentName.lastName" -> "",
        "overseasParentName.parentPreviousName.hasPreviousName" -> "",
        "overseasParentName.parentPreviousName.previousName.firstName" -> "",
        "overseasParentName.parentPreviousName.previousName.middleNames" -> "",
        "overseasParentName.parentPreviousName.previousName.lastName" -> ""
    )
    parentNameForm.bind(data).fold(
        formWithErrors => {
          formWithErrors.errors.size == 6
          formWithErrors.errorMessages("overseasParentName.parentName").head should be("Please enter their full name")
          formWithErrors.errorMessages("overseasParentName.parentName.lastName").head should be("Please enter their full name")
          formWithErrors.errorMessages("overseasParentName.parentName.firstName").head should be("Please enter their full name")
          formWithErrors.errorMessages("overseasParentName.parentPreviousName").head should be("Please answer this question")
          formWithErrors.globalErrorMessages should be (Seq("Please enter their full name", 
              "Please answer this question"))
        },
        success => fail("should have errored out")
    )
  }


  
  it should "describe all missing fields" in {
    val data = 
      Map(
        "overseasParentName.parentName.firstName" -> "",
        "overseasParentName.parentName.middleNames" -> "joe",
        "overseasParentName.parentName.lastName" -> "",
        "overseasParentName.parentPreviousName.hasPreviousName" -> "true",
        "overseasParentName.parentPreviousName.previousName.firstName" -> "",
        "overseasParentName.parentPreviousName.previousName.middleNames" -> "Joe",
        "overseasParentName.parentPreviousName.previousName.lastName" -> ""
      )
      
    parentNameForm.bind(data).fold(
      formWithErrors => {
        formWithErrors.errors.size should be(8)
        formWithErrors.globalErrorMessages should be(Seq(
          "Please enter their first name",
          "Please enter their last name",
          "Please enter their previous first name",
          "Please enter their previous last name"))
        
        formWithErrors.errorMessages("overseasParentName.parentName.firstName").head should be("Please enter their first name")
        formWithErrors.errorMessages("overseasParentName.parentName.lastName").head should be("Please enter their last name")
        formWithErrors.errorMessages("overseasParentName.parentPreviousName.previousName.firstName").head should be("Please enter their previous first name")
        formWithErrors.errorMessages("overseasParentName.parentPreviousName.previousName.lastName").head should be("Please enter their previous last name")
      },
      success => fail("Should have errored out")
    )
  }

  it should "check for too long names" in {
    val data = 
      Map(
        "overseasParentName.parentName.firstName" -> textTooLong,
        "overseasParentName.parentName.middleNames" -> textTooLong,
        "overseasParentName.parentName.lastName" -> textTooLong,
        "overseasParentName.parentPreviousName.hasPreviousName" -> "true",
        "overseasParentName.parentPreviousName.previousName.firstName" -> textTooLong,
        "overseasParentName.parentPreviousName.previousName.middleNames" -> textTooLong,
        "overseasParentName.parentPreviousName.previousName.lastName" -> textTooLong
      )
    
    parentNameForm.bind(data).fold(
      formWithErrors => {
        formWithErrors.errorMessages("overseasParentName.parentName.firstName").head should be ("First name can be no longer than 256 characters")
        formWithErrors.errorMessages("overseasParentName.parentName.middleNames").head should be ("Middle names can be no longer than 256 characters")
        formWithErrors.errorMessages("overseasParentName.parentName.lastName").head should be ("Last name can be no longer than 256 characters")
        formWithErrors.errorMessages("overseasParentName.parentPreviousName.previousName.firstName").head should be ("First name can be no longer than 256 characters")
        formWithErrors.errorMessages("overseasParentName.parentPreviousName.previousName.middleNames").head should be ("Middle names can be no longer than 256 characters")
        formWithErrors.errorMessages("overseasParentName.parentPreviousName.previousName.lastName").head should be ("Last name can be no longer than 256 characters")
        formWithErrors.globalErrorMessages should be (Seq(
          "First name can be no longer than 256 characters",
          "Middle names can be no longer than 256 characters",
          "Last name can be no longer than 256 characters",
          "First name can be no longer than 256 characters",
          "Middle names can be no longer than 256 characters",
          "Last name can be no longer than 256 characters"))
      },
      success => fail("Should have errored out")
    )
  }
  
  it should "error out on a missing field" in {
    val data = 
      Map(
        "overseasParentName.parentName.firstName" -> "john",
        "overseasParentName.parentName.middleNames" -> "joe",
        "overseasParentName.parentName.lastName" -> "",
        "overseasParentName.parentPreviousName.hasPreviousName" -> "true",
        "overseasParentName.parentPreviousName.previousName.firstName" -> "john",
        "overseasParentName.parentPreviousName.previousName.middleNames" -> "Joe",
        "overseasParentName.parentPreviousName.previousName.lastName" -> ""
      )
    parentNameForm.bind(data).fold(
      formWithErrors => {
        formWithErrors.errors.size should be(4)
        formWithErrors.errorMessages("overseasParentName.parentName.lastName").head should be("Please enter their last name")
        formWithErrors.errorMessages("overseasParentName.parentPreviousName.previousName.lastName").head should be("Please enter their previous last name")
        formWithErrors.globalErrorMessages should be(Seq("Please enter their last name","Please enter their previous last name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind with no previous name" in {
    val data = 
      Map(
        "overseasParentName.parentName.firstName" -> "John",
        "overseasParentName.parentName.middleNames" -> "joe",
        "overseasParentName.parentName.lastName" -> "Smith",
        "overseasParentName.parentPreviousName.previousName.hasPreviousName" -> "false",
        "overseasParentName.parentPreviousName.previousName.firstName" -> "",
        "overseasParentName.parentPreviousName.previousName.middleNames" -> "",
        "overseasParentName.parentPreviousName.previousName.lastName" -> ""
      )
    parentNameForm.bind(data).fold(
      formWithErrors => {
        fail(serialiser.toJson(formWithErrors.prettyPrint))
      },
      success => {
        success.overseasParentName.get.name.isDefined should be(true)
        val parentName = success.overseasParentName.get.name.get
        parentName.firstName should be("John")
        parentName.lastName should be("Smith")
        parentName.middleNames should be(Some("joe"))

        success.overseasParentName.get.previousName.isDefined should be(true)
        success.overseasParentName.get.previousName.get.previousName.isDefined should be(true)
        success.overseasParentName.get.previousName.get.hasPreviousName should be(false)
      }
    )
  }

}
