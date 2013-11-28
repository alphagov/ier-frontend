package uk.gov.gds.ier.step.country

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{InprogressApplication, Country}
import uk.gov.gds.ier.validation.constraints.CountryConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import play.api.data.Forms._

trait CountryForms extends CountryConstraints {
  self:  FormKeys
    with ErrorMessages =>
  
  lazy val countryMapping = mapping(
    keys.residence.key -> optional(text).verifying("Please answer this question", _.isDefined)
  ) (
    country => Country(country.get)
  ) (
    country => Some(Some(country.country))
  ) verifying isValidCountryConstraint

  val countryForm = ErrorTransformForm(
    mapping(
      keys.country.key -> optional(countryMapping) 
    ) (
      country => InprogressApplication(country = country)
    ) (
      inprogress => Some(inprogress.country)
    ) verifying countryIsFilledConstraint
  )
}

