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

  it should "should error out when in session is no postcode" in {
    val emptyUserInput = JsNull
    declarationPdfForm.bind(emptyUserInput).fold(
      failedForm => {
        failedForm.errorsAsTextAll should be("" +
          " -> error.required\n" +
          "address.address.postcode -> error.required") // no fancy message here, it will not be displayed anyway
      },
      success => fail("Should have errored out")
    )
  }

  it should "proceed with just address postcode on input" in {
    declarationPdfForm.bind(Map(
      "address.address.postcode" -> "WR26NJ"
    )).fold(
      hasErrors => fail("Should not have errored out"),
      success => success // do nothing
    )
  }
}

