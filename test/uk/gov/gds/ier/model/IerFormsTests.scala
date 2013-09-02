package uk.gov.gds.ier.model

import org.specs2.mutable.Specification
import org.joda.time.{DateTime, LocalDate}
import play.api.libs.json._
import play.api.test.FakeRequest

class IerFormsTests extends Specification with IerForms {

  "CitizenForm" should {
    "bind a WebApplication from a map" in {
      val map = Map("firstName" -> "John", "middleName" -> "James", "lastName" -> "Smith",
        "previousLastName" -> "Jones", "nino" -> "AB 12 34 56 C", "dob" -> "1988-01-01")
      webApplicationForm.bind(map).fold(
        hasErrors => failure(hasErrors.toString),
        success => {
          success.firstName must be("John")
          success.lastName must be("Smith")
          success.middleName must be("James")
          success.previousLastName must be("Jones")
          success.nino must be("AB 12 34 56 C")
          success.dob mustEqual LocalDate.now().withYear(1988).withMonthOfYear(1).withDayOfMonth(1)
        }
      )
    }

    "fill a WebApplication from an instance" in {
      val citizen = WebApplication(firstName = "John", middleName = "James",
        lastName = "Smith", previousLastName = "Smit", nino = "AB 12 34 56 C", dob = LocalDate.now)
      webApplicationForm.fill(citizen).fold(
        errors => failure(errors.toString),
        success => {
          success mustEqual(citizen)
        }
      )
    }

    "error out on an empty map" in {
      val map = Map.empty[String,String]
      webApplicationForm.bind(map).fold(
        errors => {
          errors.hasErrors mustEqual(true)
          errors.error("firstName").get.message mustEqual "error.required"
          errors.error("lastName").get.message mustEqual "error.required"
          errors.error("middleName").get.message mustEqual "error.required"
          errors.error("dob").get.message mustEqual "error.required"
          errors.error("nino").get.message mustEqual "error.required"
        },
        success => failure("Should not have been able to bind")
      )
    }
  }

  "ApiApplicationForm" should {
    "bind an ApiApplication from a map" in {
      val map = Map("fn" -> "John", "mn" -> "James", "ln" -> "Smith", "pln" -> "Jones",
        "nino" -> "AB 12 34 56 C", "dob" -> "1988-01-01", "gssCode" -> "test-gss-code")
      apiApplicationForm.bind(map).fold(
        hasErrors => failure(hasErrors.toString),
        success => {
          success.fn must be("John")
          success.ln must be("Smith")
          success.mn must be("James")
          success.nino must be("AB 12 34 56 C")
          success.gssCode must be("test-gss-code")
          success.pln must be("Jones")
          success.dob mustEqual LocalDate.now().withYear(1988).withMonthOfYear(1).withDayOfMonth(1)
        }
      )
    }

    "fill an ApiApplication from an instance" in {
      val application = ApiApplication(fn = "John", ln = "Smith",
        mn = "James", pln = "Jones", nino = "AB 12 34 56 D", dob = LocalDate.now)
      apiApplicationForm.fill(application).fold(
        errors => failure(errors.toString),
        success => {
          success mustEqual application
        }
      )
    }
  }

  "ApiApplicationResponseForm" should {
    "bind an ApiApplication from a map" in {

      val jsVal = Json.toJson(
        Map(
          "detail" -> Json.toJson(
            Map(
              "fn" -> JsString("John"),
              "mn" -> JsString("James"),
              "ln" -> JsString("Smith"),
              "pln" -> JsString("Jones"),
              "nino" -> JsString("AB 12 34 56 C"),
              "dob" -> JsString("1988-01-01"),
              "gssCode" -> JsString("test-gss-code"))),
          "ierId" -> JsString("1234"),
          "createdAt" -> JsString("1988-01-01 12:00:00"),
          "status" -> JsString("Unprocessed"),
          "source" -> JsString("web")
        )
      )
      apiApplicationResponseForm.bind(jsVal).fold(
        hasErrors => failure(hasErrors.toString),
        success => {
          success.ierId mustEqual "1234"
          success.detail.fn must be("John")
          success.detail.ln must be("Smith")
          success.detail.mn must be("James")
          success.detail.pln must be("Jones")
          success.detail.nino must be("AB 12 34 56 C")
          success.detail.gssCode must be("test-gss-code")
          success.detail.dob mustEqual LocalDate.now().withYear(1988).withMonthOfYear(1).withDayOfMonth(1)
          success.createdAt mustEqual "1988-01-01 12:00:00"
          success.status mustEqual "Unprocessed"
          success.source mustEqual "web"
        }
      )
    }
  }

  "PostcodeForm" should {
    "bind a postcode" in {
      val jsVal = Json.toJson(
        Map(
          "postcode" -> "BT12 5EG"
        )
      )
      postcodeForm.bind(jsVal).fold(
        hasErrors => failure(hasErrors.toString),
        success => {
          success mustEqual "BT12 5EG"
        }
      )
    }
    "throw an error on a bad postcode" in {
      val jsVal = Json.toJson(
        Map(
          "postcode" -> "ZX123 BAD"
        )
      )
      postcodeForm.bind(jsVal).fold(
        hasErrors => {
          hasErrors.errorsAsMap.contains("postcode") must beTrue
        },
        success => {
          failure("Should not have succeeded " + success)
        }
      )
    }
  }
}
