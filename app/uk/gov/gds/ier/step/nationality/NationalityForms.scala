package uk.gov.gds.ier.step.nationality

import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{InprogressApplication, Nationality}
import play.api.data.Form
import play.api.data.Forms._

trait NationalityForms {
  self:  FormKeys
    with ErrorMessages =>

  val nationalityMapping = mapping(
    keys.nationalities.key -> list(nonEmptyText
      .verifying(nationalityMaxLengthError, _.size <= maxTextFieldLength)),
    keys.hasOtherCountry.key -> optional(boolean),
    keys.otherCountries.key -> list(text
      .verifying(nationalityMaxLengthError, _.size <= maxTextFieldLength)),
    keys.noNationalityReason.key -> optional(nonEmptyText
      .verifying(noNationalityReasonMaxLengthError, _.size <= maxExplanationFieldLength))
  ) (
    Nationality.apply
  ) (
    Nationality.unapply
  ) verifying("Please select your Nationality", nationality => 
      (nationality.nationalities.size > 0 
        || (nationality.otherCountries.filter(_.nonEmpty).size > 0 
          && nationality.hasOtherCountry.exists(b => b))) 
      || nationality.noNationalityReason.isDefined
  ) verifying("You can specify no more than five countries", 
      mapping => mapping.nationalities.size + mapping.otherCountries.size <=5)

  val nationalityForm = Form(
    mapping(
      keys.nationality.key -> nationalityMapping
    ) (
      nationality => InprogressApplication(nationality = Some(nationality))
    ) (
      inprogressApplication => inprogressApplication.nationality
    )
  )
}

