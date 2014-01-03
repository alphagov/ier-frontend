package uk.gov.gds.ier.service

import uk.gov.gds.ier.validation.constraints.NationalityConstraints
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.validation.constants.NationalityConstants._
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
    val isoCountries = nationalities.flatMap{
      country => countryNameToCodes.get(country.toLowerCase)
    }
    val isoCodes = isoCountries match {
      case Nil => None
      case list => Some(list.map(_.isoCode))
    }
    nationality.copy(countryIsos = isoCodes)
  }

  def getFranchises(nationality:Nationality):List[Franchise] = {
    val nationalities = nationality.checkedNationalities ++ nationality.otherCountries
    val isoCodes = nationalities.flatMap{
      country => countryNameToCodes.get(country.toLowerCase)
    }
    val franchises = isoCodes.flatMap(_.franchise)
    franchises.distinct
  }
}
