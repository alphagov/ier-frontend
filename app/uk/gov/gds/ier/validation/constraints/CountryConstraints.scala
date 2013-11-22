package uk.gov.gds.ier.validation.constraints

import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.{Country, InprogressApplication}
import uk.gov.gds.ier.validation.FormKeys

trait CountryConstraints {
  self: FormKeys =>
  
  lazy val isValidCountryConstraint = Constraint[Country](keys.country.residence.key) { 
    country =>
      if (List("England", "Scotland", "Wales", "Northern Ireland", "British Islands", "Abroad") contains country.country) Valid
      else Invalid("This is not a valid country", keys.country.residence)
  }
  lazy val countryIsFilledConstraint = Constraint[InprogressApplication](keys.country.residence.key) {
    application => 
      if (application.country.isDefined) Valid
      else Invalid("Please answer this question", keys.country.residence)
  }
}
