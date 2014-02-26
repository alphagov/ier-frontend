package uk.gov.gds.ier.transaction.forces.confirmation

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.InprogressForces
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.JsNull
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}

class ConfirmationFormTests
  extends FlatSpec
  with Matchers
  with ConfirmationForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "error out on empty json" in {
    val js = JsNull
    confirmationForm.bind(js).fold(
      hasErrors => fail("Should have NOT errored out."),
      success => success should not be None
    )
  }

  it should "error out on empty application" in {
    val application = InprogressForces()
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => fail("Should have NOT errored out."),
      success => success should not be None
    )
  }
}
