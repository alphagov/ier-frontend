package uk.gov.gds.ier.transaction.crown.declaration

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PreviousName, Name}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.{CommonConstraints, NameConstraints}
import uk.gov.gds.ier.transaction.crown.InprogressCrown

/**
 * Validation form for Download PDF, there is no used input, nothing to validate, just a formality
 */
trait DeclarationPdfForms extends CommonConstraints {
  self:  FormKeys
    with ErrorMessages =>

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
