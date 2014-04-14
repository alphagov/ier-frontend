package uk.gov.gds.ier.transaction.crown.declaration

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import play.api.libs.json.{Json, JsNull}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.test.TestHelpers

/**
 * Declaration is a simple PDF file download page, the validation form
 */
class DeclarationFormTests
  extends FlatSpec
  with Matchers
  with DeclarationPdfForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "report no errors on empty input" in {
    val emptyUserInput = JsNull
    declarationPdfForm.bind(emptyUserInput).fold(
      hasErrors => fail("Should not have errored out"),
      success => success // do nothing
    )
  }
}

