package uk.gov.gds.ier.validation.constraints

import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.Nationality
import uk.gov.gds.ier.validation.{Key, FormKeys, ErrorMessages}
import scala.collection.immutable.HashMap

import uk.gov.gds.ier.validation.constants.NationalityConstants._

trait NationalityConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val notTooManyNationalities = Constraint[Nationality](keys.nationality.key) {
    nationality =>
      if (nationality.otherCountries.size <= 3) Valid
      else Invalid("You can specifiy no more than five countries", keys.nationality)
  }

  lazy val nationalityIsChosen = Constraint[Nationality](keys.nationality.key) {
    nationality =>
      if (nationality.british == Some(true) || nationality.irish == Some(true)) Valid
      else if (nationality.otherCountries.exists(_.nonEmpty) && nationality.hasOtherCountry.exists(b => b)) Valid
      else if (nationality.noNationalityReason.isDefined) Valid
      else Invalid("Please select your Nationality", keys.nationality)
  }

  lazy val otherCountry0IsValid = otherCountryIsValid(0)
  lazy val otherCountry1IsValid = otherCountryIsValid(1)
  lazy val otherCountry2IsValid = otherCountryIsValid(2)

  private def otherCountryIsValid(i:Int) = Constraint[Nationality](keys.nationality.otherCountries.key) {
    nationality =>
      if (nationality.otherCountries.isEmpty || !nationality.hasOtherCountry.exists(b => b)) Valid
      else if (nationality.otherCountries.size != i+1) Valid
      else if (nationality.otherCountries.size > i
        && countryNameToCodes.contains(nationality.otherCountries(i).toLowerCase)) Valid
      else Invalid("This is not a valid country", keys.nationality.otherCountries.item(i))
  }

}
