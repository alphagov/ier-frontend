package uk.gov.gds.ier.transaction.crown.declaration

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PreviousName, Name}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.NameConstraints
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait DeclarationPdfForms extends NameConstraints {
  self:  FormKeys
    with ErrorMessages =>

  // FIXME: how to create empty form?
  val declarationPdfForm = ErrorTransformForm(
    mapping(
      "nothing" -> optional(text)
    ) (
      (x) => InprogressCrown()
    ) (
      inprogress => None
    )
  )
}
