package uk.gov.gds.ier.transaction.forces.nino

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.libs.json.Json
import uk.gov.gds.ier.test.TestHelpers
import controllers.step.forces.routes._
import scala.Some
import play.api.templates.Html
import play.api.test.WithApplication
import play.api.test.FakeApplication

class NinoMustacheTests
  extends FlatSpec
  with Matchers
  with NinoForms
  with NinoMustache
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "successfully render to a valid nino" in new WithApplication(FakeApplication()) {
    val js = Json.toJson(
      Map(
        "NINO.NINO" -> "AB 12 34 56 D"
      )
    )
    val newNinoForm = ninoForm.bind(js)
    val mustachedResult:Html = ninoMustache(newNinoForm, NinoController.post, Some(NameController.get))
    mustachedResult.body should include("value=\"AB 12 34 56 D\"")
  }

  it should "successfully render to a valid 'no nino reason'" in new WithApplication(FakeApplication()) {
    val js = Json.toJson(
      Map(
        "NINO.NoNinoReason" -> "Don't have any NINO"
      )
    )
    val newNinoForm = ninoForm.bind(js)
    val mustachedResult:Html = ninoMustache(newNinoForm, NinoController.post, Some(NameController.get))
    mustachedResult.body should include("Don&#39;t have any NINO")
  }
}
