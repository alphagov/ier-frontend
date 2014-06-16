package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test._
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import scala.Some
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.transaction.shared.BlockContent
import uk.gov.gds.ier.transaction.overseas.confirmation.ConfirmationForms

class NinoBlocksTest
  extends FlatSpec
  with Matchers
  with ConfirmationForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  behavior of "confirmationBlocks.nino"

  "In-progress application confirmation form" should
    "display NINO uppercased" in {
    val partiallyFilledApplicationForm = confirmationForm.fillAndValidate(InprogressOverseas(
      nino = Some(Nino(
        nino = Some("ab123456"),
        noNinoReason = None
      ))
    ))

    val confirmation = new ConfirmationBlocks(partiallyFilledApplicationForm)
    confirmation.nino.content should be(BlockContent(List("AB123456")))
  }
}
