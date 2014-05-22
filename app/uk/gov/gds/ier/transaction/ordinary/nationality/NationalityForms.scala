package uk.gov.gds.ier.transaction.ordinary.nationality

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PartialNationality}
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import play.api.data.validation.Constraint
import uk.gov.gds.ier.validation.constants.NationalityConstants
import play.api.data.validation._

trait NationalityForms extends NationalityConstraints {
  self:  FormKeys
    with ErrorMessages =>

  val nationalityForm = ErrorTransformForm(
    mapping(
      keys.nationality.key -> PartialNationality.mapping
    ) (
      nationality => InprogressOrdinary(nationality = Some(nationality))
    ) (
      inprogressApplication => inprogressApplication.nationality
    ) verifying (
    nationalityIsChosen, notTooManyNationalities, otherCountry0IsValid, otherCountry1IsValid, otherCountry2IsValid
  )
  )
}

trait NationalityConstraints extends FormKeys with ErrorMessages {

  lazy val notTooManyNationalities = Constraint[InprogressOrdinary](keys.nationality.key) {
    application =>
      if (application.nationality.exists(_.otherCountries.size <= NationalityConstants.numberMaxOfOtherCountries)) Valid
      else Invalid("ordinary_nationality_no_more_five_countries", keys.nationality)
  }

  lazy val nationalityIsChosen = Constraint[InprogressOrdinary](keys.nationality.key) {
    application =>
      val isNationalityValid = application.nationality.exists { nationality =>
        (nationality.british == Some(true) || nationality.irish == Some(true)) ||
        (nationality.otherCountries.exists(_.nonEmpty) && nationality.hasOtherCountry.exists(b => b)) ||
        (nationality.noNationalityReason.isDefined)
      }

      application.nationality match {
        case Some(nationality) if (isNationalityValid) => Valid
        case _ => Invalid("ordinary_nationality_please_answer", keys.nationality)
      }
  }

  lazy val otherCountry0IsValid = otherCountryIsValid(0)
  lazy val otherCountry1IsValid = otherCountryIsValid(1)
  lazy val otherCountry2IsValid = otherCountryIsValid(2)

  private def otherCountryIsValid(i:Int) = Constraint[InprogressOrdinary](keys.nationality.otherCountries.key) {
    application =>
      val isNationalityValid = application.nationality exists { nationality =>
        (nationality.otherCountries.isEmpty || !nationality.hasOtherCountry.exists(b => b)) ||
        (nationality.otherCountries.size != i+1) ||
        (nationality.otherCountries.size > i
          && NationalityConstants.countryNameToCodes.contains(nationality.otherCountries(i).toLowerCase))
      }

      if (isNationalityValid) Valid
      else Invalid("ordinary_nationality_not_valid", keys.nationality.otherCountries.item(i))
  }

}
