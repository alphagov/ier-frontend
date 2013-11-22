package uk.gov.gds.ier.service

import uk.gov.gds.ier.validation.constraints.NationalityConstraints
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.Nationality

class IsoCountryService
  extends NationalityConstraints
  with FormKeys
  with ErrorMessages {

  def isValidCountry(country:String):Boolean = {
    countryNameToCodes.contains(country)
  }

  def transformToIsoCode(nationality:Nationality):Nationality = {
    val nationalities = nationality.checkedNationalities ++ nationality.otherCountries
    val isoCodes = nationalities.map(country => countryNameToCodes.get(country.toLowerCase)).filter(_.isDefined).map{
      case Some(country) => country
      case None => ""
    }

    nationality.copy(countryIsos = if (isoCodes.isEmpty) None else Some(isoCodes))
  }
}