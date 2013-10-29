package uk.gov.gds.ier.model.IerForms

import play.api.libs.json.Json
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.IerForms
import uk.gov.gds.ier.serialiser.JsonSerialiser

@RunWith(classOf[JUnitRunner])
class PostcodeFormTests extends FlatSpec with Matchers with IerForms {

  val serialiser = new JsonSerialiser

  it should "bind a postcode" in {
    val jsVal = Json.toJson(
      Map(
        "postcode" -> "BT12 5EG"
      )
    )
    postcodeForm.bind(jsVal).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success should be("BT12 5EG")
      }
    )
  }
  it should "throw an error on a bad postcode" in {
    val jsVal = Json.toJson(
      Map(
        "postcode" -> "ZX123 BAD"
      )
    )
    postcodeForm.bind(jsVal).fold(
      hasErrors => {
        hasErrors.errorsAsMap.contains("postcode") should be(true)
      },
      success => {
        fail("Should not have succeeded " + success)
      }
    )
  }
}
