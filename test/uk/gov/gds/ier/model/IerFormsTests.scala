package uk.gov.gds.ier.model

import org.specs2.mutable.Specification
import org.joda.time.{DateTime, LocalDate}
import play.api.libs.json._
import play.api.test.FakeRequest
import uk.gov.gds.ier.validation.IerForms

class IerFormsTests extends Specification with IerForms {

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
