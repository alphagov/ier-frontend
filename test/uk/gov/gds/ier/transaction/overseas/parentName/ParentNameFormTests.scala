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
        "parentName.firstName" -> "firstname",
        "parentName.middleNames" -> "middlenames",
        "parentName.lastName" -> "lastname",
        "parentPreviousName.hasPreviousName" -> "true",
        "parentPreviousName.previousName.firstName" -> "firstname",
        "parentPreviousName.previousName.middleNames" -> "middlenames",
        "parentPreviousName.previousName.lastName" -> "lastname"
    )
    
    parentNameForm.bind(data).fold(
        formWithErrors => {
          fail(formWithErrors.prettyPrint.mkString(", "))
        },
        success => {
          success.parentName.isDefined should be(true)
          val Some(parentName) = success.parentName
          parentName.firstName should be("firstname")
          parentName.middleNames should be(Some("middlenames"))
          parentName.lastName should be("lastname")
          
          success.parentPreviousName.isDefined should be(true)
          val Some(parentPreviousName) = success.parentPreviousName
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
        "parentName.firstName" -> "",
        "parentName.middleNames" -> "",
        "parentName.lastName" -> "",
        "parentPreviousName.hasPreviousName" -> "",
        "parentPreviousName.previousName.firstName" -> "",
        "parentPreviousName.previousName.middleNames" -> "",
        "parentPreviousName.previousName.lastName" -> ""
    )
    parentNameForm.bind(data).fold(
        formWithErrors => {
          formWithErrors.errors.size == 6
          formWithErrors.errorMessages("parentName").head should be("Please enter their full name")
          formWithErrors.errorMessages("parentName.lastName").head should be("Please enter their full name")
          formWithErrors.errorMessages("parentName.firstName").head should be("Please enter their full name")
          formWithErrors.errorMessages("parentPreviousName").head should be("Please answer this question")
          formWithErrors.globalErrorMessages should be (Seq("Please enter their full name", 
              "Please answer this question"))
        },
        success => fail("should have errored out")
    )
  }


  
  it should "describe all missing fields" in {
    val data = 
      Map(
        "parentName.firstName" -> "",
        "parentName.middleNames" -> "joe",
        "parentName.lastName" -> "",
        "parentPreviousName.hasPreviousName" -> "true",
        "parentPreviousName.previousName.firstName" -> "",
        "parentPreviousName.previousName.middleNames" -> "Joe",
        "parentPreviousName.previousName.lastName" -> ""
      )
      
    parentNameForm.bind(data).fold(
      formWithErrors => {
        formWithErrors.errors.size should be(8)
        formWithErrors.globalErrorMessages should be(Seq(
          "Please enter their first name",
          "Please enter their last name",
          "Please enter their previous first name",
          "Please enter their previous last name"))
        
        formWithErrors.errorMessages("parentName.firstName").head should be("Please enter their first name")
        formWithErrors.errorMessages("parentName.lastName").head should be("Please enter their last name")
        formWithErrors.errorMessages("parentPreviousName.previousName.firstName").head should be("Please enter their previous first name")
        formWithErrors.errorMessages("parentPreviousName.previousName.lastName").head should be("Please enter their previous last name")
      },
      success => fail("Should have errored out")
    )
  }

  it should "check for too long names" in {
    val data = 
      Map(
        "parentName.firstName" -> textTooLong,
        "parentName.middleNames" -> textTooLong,
        "parentName.lastName" -> textTooLong,
        "parentPreviousName.hasPreviousName" -> "true",
        "parentPreviousName.previousName.firstName" -> textTooLong,
        "parentPreviousName.previousName.middleNames" -> textTooLong,
        "parentPreviousName.previousName.lastName" -> textTooLong
      )
    
    parentNameForm.bind(data).fold(
      formWithErrors => {
        formWithErrors.errorMessages("parentName.firstName").head should be ("First name can be no longer than 256 characters")
        formWithErrors.errorMessages("parentName.middleNames").head should be ("Middle names can be no longer than 256 characters")
        formWithErrors.errorMessages("parentName.lastName").head should be ("Last name can be no longer than 256 characters")
        formWithErrors.errorMessages("parentPreviousName.previousName.firstName").head should be ("First name can be no longer than 256 characters")
        formWithErrors.errorMessages("parentPreviousName.previousName.middleNames").head should be ("Middle names can be no longer than 256 characters")
        formWithErrors.errorMessages("parentPreviousName.previousName.lastName").head should be ("Last name can be no longer than 256 characters")
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
        "parentName.firstName" -> "john",
        "parentName.middleNames" -> "joe",
        "parentName.lastName" -> "",
        "parentPreviousName.hasPreviousName" -> "true",
        "parentPreviousName.previousName.firstName" -> "john",
        "parentPreviousName.previousName.middleNames" -> "Joe",
        "parentPreviousName.previousName.lastName" -> ""
      )
    parentNameForm.bind(data).fold(
      formWithErrors => {
        formWithErrors.errors.size should be(4)
        formWithErrors.errorMessages("parentName.lastName").head should be("Please enter their last name")
        formWithErrors.errorMessages("parentPreviousName.previousName.lastName").head should be("Please enter their previous last name")
        formWithErrors.globalErrorMessages should be(Seq("Please enter their last name","Please enter their previous last name"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "successfully bind with no previous name" in {
    val data = 
      Map(
        "parentName.firstName" -> "John",
        "parentName.middleNames" -> "joe",
        "parentName.lastName" -> "Smith",
        "parentPreviousName.previousName.hasPreviousName" -> "false",
        "parentPreviousName.previousName.firstName" -> "",
        "parentPreviousName.previousName.middleNames" -> "",
        "parentPreviousName.previousName.lastName" -> ""
      )
    parentNameForm.bind(data).fold(
      formWithErrors => {
        fail(serialiser.toJson(formWithErrors.prettyPrint))
      },
      success => {
        success.parentName.isDefined should be(true)
        val parentName = success.parentName.get
        parentName.firstName should be("John")
        parentName.lastName should be("Smith")
        parentName.middleNames should be(Some("joe"))

        success.parentPreviousName.isDefined should be(true)
        success.parentPreviousName.get.previousName.isDefined should be(true)
        success.parentPreviousName.get.hasPreviousName should be(false)
      }
    )
  }

}

