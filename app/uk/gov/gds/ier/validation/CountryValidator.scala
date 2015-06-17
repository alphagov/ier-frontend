package uk.gov.gds.ier.validation

import uk.gov.gds.ier.model.Country

object CountryValidator {

  def isScotland(country: Option[Country]):Boolean = {
    country match {
      case Some(Country("Scotland", _)) => true
      case _ => false
    }
  }
}
