package uk.gov.gds.ier.step.nationality

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{InprogressOrdinary, PartialNationality}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.NationalityConstraints

trait NationalityForms extends NationalityConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val nationalityMapping = mapping(
    keys.british.key -> optional(boolean),
    keys.irish.key -> optional(boolean),
    keys.hasOtherCountry.key -> optional(boolean),
    keys.otherCountries.key -> list(text
      .verifying(nationalityMaxLengthError, _.size <= maxTextFieldLength)),
    keys.noNationalityReason.key -> optional(nonEmptyText
      .verifying(noNationalityReasonMaxLengthError, _.size <= maxExplanationFieldLength))
  ) (
    PartialNationality.apply
  ) (
    PartialNationality.unapply
  ) verifying (
    nationalityIsChosen, notTooManyNationalities, otherCountry0IsValid, otherCountry1IsValid, otherCountry2IsValid
  )

  val nationalityForm = ErrorTransformForm(
    mapping(
      keys.nationality.key -> nationalityMapping
    ) (
      nationality => InprogressOrdinary(nationality = Some(nationality))
    ) (
      inprogressApplication => inprogressApplication.nationality
    )
  )
}

