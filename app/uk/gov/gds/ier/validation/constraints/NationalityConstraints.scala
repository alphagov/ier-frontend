package uk.gov.gds.ier.validation.constraints

import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.Nationality
import uk.gov.gds.ier.validation.{Key, FormKeys, ErrorMessages}
import scala.collection.immutable.HashMap

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

  lazy val countryNameToCodes = HashMap(
    "antigua and barbuda" -> "AG",
    "anguilla" -> "AI",
    "british antarctic territory" -> "AQ",
    "austria" -> "AT",
    "australia" -> "AU",
    "barbados" -> "BB",
    "bangladesh" -> "BD",
    "belgium" -> "BE",
    "bulgaria" -> "BG",
    "bermuda" -> "BM",
    "brunei darussalam" -> "BN",
    "the bahamas" -> "BS",
    "botswana" -> "BW",
    "belize" -> "BZ",
    "canada" -> "CA",
    "channel islands" -> "CI",
    "cameroon" -> "CM",
    "cyprus" -> "CY",
    "czech republic" -> "CZ",
    "germany" -> "DE",
    "denmark" -> "DK",
    "dominica" -> "DM",
    "estonia" -> "EE",
    "spain" -> "ES",
    "finland" -> "FI",
    "fiji islands" -> "FJ",
    "falkland islands" -> "FK",
    "france" -> "FR",
    "united kingdom" -> "GB",
    "british" -> "GB",
    "grenada" -> "GD",
    "ghana" -> "GH",
    "gibraltar" -> "GI",
    "the gambia" -> "GM",
    "greece" -> "GR",
    "south georgia and the south sandwich islands" -> "GS",
    "guyana" -> "GY",
    "hong kong" -> "HK",
    "hungary" -> "HU",
    "ireland" -> "IE",
    "irish" -> "IE",
    "isle of man" -> "IM",
    "india" -> "IN",
    "british indian ocean territory" -> "IO",
    "italy" -> "IT",
    "jamaica" -> "JM",
    "kenya" -> "KE",
    "kiribati" -> "KI",
    "st kitts & nevis" -> "KN",
    "cayman islands" -> "KY",
    "st lucia" -> "LC",
    "sri lanka" -> "LK",
    "lesotho" -> "LS",
    "lithuania" -> "LT",
    "luxemburg" -> "LU",
    "latvia" -> "LV",
    "montserrat" -> "MS",
    "malta" -> "MT",
    "mauritius" -> "MU",
    "maldives" -> "MV",
    "malawi" -> "MW",
    "malaysia" -> "MY",
    "mozambique" -> "MZ",
    "namibia" -> "NA",
    "nigeria" -> "NG",
    "netherlands" -> "NL",
    "nauru" -> "NR",
    "new zealand" -> "NZ",
    "papua new guinea" -> "PG",
    "pakistan" -> "PK",
    "poland" -> "PL",
    "pitcairn island" -> "PN",
    "portugal" -> "PT",
    "romania" -> "RO",
    "rwanda" -> "RW",
    "solomon islands" -> "SB",
    "seychelles" -> "SC",
    "sweden" -> "SE",
    "singapore" -> "SG",
    "st helena and dependencies (ascension island and tristan da cunha)" -> "SH",
    "slovenia" -> "SI",
    "slovakia" -> "SK",
    "sierra leone" -> "SL",
    "swaziland" -> "SZ",
    "turks and caicos islands" -> "TC",
    "tonga" -> "TO",
    "trinidad & tobago" -> "TT",
    "tuvalu" -> "TV",
    "united republic of tanzania" -> "TZ",
    "uganda" -> "UG",
    "st vincent & the grenadines" -> "VC",
    "british virgin islands" -> "VG",
    "vanuatu" -> "VU",
    "samoa" -> "WS",
    "south africa" -> "ZA",
    "zambia" -> "ZM",
    "zimbabwe" -> "ZW"
  )
}
