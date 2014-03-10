package uk.gov.gds.ier.transaction.country

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressApplication, Country}
import uk.gov.gds.ier.validation.constraints.CountryConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import play.api.data.Forms._

trait CountryForms extends CountryConstraints {
  self:  FormKeys
    with ErrorMessages =>
  
  lazy val countryMapping = mapping(
    keys.residence.key -> optional(text).verifying("Please answer this question", _.isDefined),
    keys.origin.key -> optional(text)
  ) (
    (residence, origin) => if (residence == Some("Abroad")) {
      Country(origin.getOrElse(""), true)
    } else {
      Country(residence.getOrElse(""), false)
    }
  ) (
    country => if (country.abroad) {
      Some(Some("Abroad"), Some(country.country))
    } else {
      Some(Some(country.country), None)
    }
  ).verifying(isValidCountryConstraint, ifAbroadOriginFilled)

  val countryForm = ErrorTransformForm(
    mapping(
      keys.country.key -> optional(countryMapping)
    ) (
      country => InprogressOrdinary(country = country)
    ) (
      inprogress => Some(inprogress.country)
    ) verifying countryIsFilledConstraint
  )
}

