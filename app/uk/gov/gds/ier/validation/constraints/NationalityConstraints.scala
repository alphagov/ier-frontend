package uk.gov.gds.ier.validation.constraints

import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.PartialNationality
import uk.gov.gds.ier.validation.{Key, FormKeys, ErrorMessages}
import scala.collection.immutable.HashMap

trait NationalityConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val notTooManyNationalities = Constraint[PartialNationality](keys.nationality.key) {
    nationality =>
      if (nationality.otherCountries.size <= 3) Valid
      else Invalid("You can specifiy no more than five countries", keys.nationality)
  }

  lazy val nationalityIsChosen = Constraint[PartialNationality](keys.nationality.key) {
    nationality =>
      if (nationality.british == Some(true) || nationality.irish == Some(true)) Valid
      else if (nationality.otherCountries.exists(_.nonEmpty) && nationality.hasOtherCountry.exists(b => b)) Valid
      else if (nationality.noNationalityReason.isDefined) Valid
      else Invalid("Please select your Nationality", keys.nationality)
  }

  lazy val otherCountry0IsValid = otherCountryIsValid(0)
  lazy val otherCountry1IsValid = otherCountryIsValid(1)
  lazy val otherCountry2IsValid = otherCountryIsValid(2)

  private def otherCountryIsValid(i:Int) = Constraint[PartialNationality](keys.nationality.otherCountries.key) {
    nationality =>
      if (nationality.otherCountries.isEmpty || !nationality.hasOtherCountry.exists(b => b)) Valid
      else if (nationality.otherCountries.size != i+1) Valid
      else if (nationality.otherCountries.size > i
        && countryNameToCodes.contains(nationality.otherCountries(i).toLowerCase)) Valid
      else Invalid("This is not a valid country", keys.nationality.otherCountries.item(i))
  }

  lazy val countryNameToCodes = HashMap(
    "afghanistan" -> "AF",
    "albania" -> "AL",
    "algeria" -> "DZ",
    "american samoa" -> "AS",
    "andorra" -> "AD",
    "angola" -> "AO",
    "anguilla" -> "AI",
    "antigua and barbuda" -> "AG",
    "argentina" -> "AR",
    "armenia" -> "AM",
    "aruba" -> "AW",
    "australia" -> "AU",
    "austria" -> "AT",
    "azerbaijan" -> "AZ",
    "bahamas" -> "BS",
    "bahrain" -> "BH",
    "bangladesh" -> "BD",
    "barbados" -> "BB",
    "belarus" -> "BY",
    "belgium" -> "BE",
    "belize" -> "BZ",
    "benin" -> "BJ",
    "bermuda" -> "bm",
    "bhutan" -> "BT",
    "bolivia" -> "BO",
    "bonaire/st eustatius/saba" -> "BQ",
    "bosnia and herzegovina" -> "BA",
    "botswana" -> "BW",
    "brazil" -> "BR",
    "british indian ocean territory" -> "IO",
    "british virgin islands" -> "VG",
    "brunei" -> "BN",
    "bulgaria" -> "BG",
    "burkina faso" -> "BF",
    "burma" -> "MM",
    "burundi" -> "bi",
    "cambodia" -> "KH",
    "cameroon" -> "CM",
    "canada" -> "CA",
    "cape verde" -> "CV",
    "cayman islands" -> "KY",
    "central african republic" -> "CF",
    "chad" -> "td",
    "chile" -> "CL",
    "china" -> "CN",
    "colombia" -> "CO",
    "comoros" -> "KM",
    "congo" -> "CG",
    "costa rica" -> "CR",
    "cote d’ivoire" -> "CI",
    "croatia" -> "HR",
    "cuba" -> "CU",
    "curaçao" -> "CW",
    "cyprus" -> "CY",
    "czech republic" -> "CZ",
    "democratic republic of congo" -> "CD",
    "denmark" -> "DK",
    "djibouti" -> "DJ",
    "dominica" -> "DM",
    "dominican republic" -> "DO",
    "ecuador" -> "EC",
    "egypt" -> "EG",
    "el salvador" -> "sv",
    "equatorial guinea" -> "GQ",
    "eritrea" -> "ER",
    "estonia" -> "EE",
    "ethiopia" -> "ET",
    "falkland islands" -> "fk",
    "fiji" -> "FJ",
    "finland" -> "FI",
    "france" -> "FR",
    "french guiana" -> "GF",
    "french polynesia" -> "PF",
    "gabon" -> "GA",
    "gambia" -> "GM",
    "georgia" -> "GE",
    "germany" -> "DE",
    "ghana" -> "GH",
    "gibraltar" -> "GI",
    "greece" -> "GR",
    "grenada" -> "GD",
    "guadeloupe" -> "GP",
    "guatemala" -> "GT",
    "guinea" -> "gn",
    "guinea-bissau" -> "GW",
    "guyana" -> "GY",
    "haiti" -> "HT",
    "holy see" -> "VA",
    "honduras" -> "HN",
    "hong kong" -> "HK",
    "hungary" -> "HU",
    "iceland" -> "IS",
    "india" -> "IN",
    "indonesia" -> "ID",
    "iran" -> "IR",
    "iraq" -> "IQ",
    "ireland" -> "IE",
    "israel" -> "IL",
    "italy" -> "IT",
    "jamaica" -> "JM",
    "japan" -> "JP",
    "jordan" -> "JO",
    "kazakhstan" -> "KZ",
    "kenya" -> "KE",
    "kiribati" -> "KI",
    "kuwait" -> "KW",
    "kyrgyzstan" -> "kg",
    "laos" -> "LA",
    "latvia" -> "LV",
    "lebanon" -> "LB",
    "lesotho" -> "LS",
    "liberia" -> "LR",
    "libya" -> "LY",
    "liechtenstein" -> "LI",
    "lithuania" -> "LT",
    "luxembourg" -> "LU",
    "macao" -> "MO",
    "macedonia" -> "MK",
    "madagascar" -> "MG",
    "malawi" -> "MW",
    "malaysia" -> "MY",
    "maldives" -> "MV",
    "mali" -> "ML",
    "malta" -> "MT",
    "marshall islands" -> "MH",
    "martinique" -> "MQ",
    "mauritania" -> "mr",
    "mauritius" -> "MU",
    "mayotte" -> "YT",
    "mexico" -> "MX",
    "micronesia" -> "FM",
    "moldova" -> "MD",
    "monaco" -> "MC",
    "mongolia" -> "MN",
    "montenegro" -> "ME",
    "montserrat" -> "MS",
    "morocco" -> "MA",
    "mozambique" -> "MZ",
    "namibia" -> "NA",
    "nauru" -> "NR",
    "nepal" -> "NP",
    "netherlands" -> "NL",
    "new caledonia" -> "NC",
    "new zealand" -> "NZ",
    "nicaragua" -> "NI",
    "niger" -> "NE",
    "nigeria" -> "NG",
    "north korea" -> "KP",
    "norway" -> "NO",
    "oman" -> "OM",
    "pakistan" -> "PK",
    "palau" -> "PW",
    "panama" -> "PA",
    "papua new guinea" -> "PG",
    "paraguay" -> "PY",
    "peru" -> "PE",
    "philippines" -> "PH",
    "pitcairn island" -> "pn",
    "poland" -> "PL",
    "portugal" -> "PT",
    "qatar" -> "QA",
    "réunion" -> "RE",
    "romania" -> "RO",
    "russia" -> "RU",
    "rwanda" -> "RW",
    "samoa" -> "WS",
    "san marino" -> "SM",
    "são tomé and principe" -> "ST",
    "saudi arabia" -> "SA",
    "senegal" -> "SN",
    "serbia" -> "RS",
    "seychelles" -> "SC",
    "sierra leone" -> "SL",
    "singapore" -> "SG",
    "slovakia" -> "SK",
    "slovenia" -> "SI",
    "solomon islands" -> "SB",
    "somalia" -> "SO",
    "south africa" -> "ZA",
    "south georgia and the south sandwich islands" -> "GS",
    "south korea" -> "KR",
    "south sudan" -> "SS",
    "spain" -> "ES",
    "sri lanka" -> "LK",
    "st helena, ascension and tristan da cunha" -> "sh",
    "st kitts and nevis" -> "KN",
    "st lucia" -> "lc",
    "st maarten" -> "MF",
    "st pierre & miquelon" -> "PM",
    "st vincent and the grenadines" -> "VC",
    "sudan" -> "SD",
    "suriname" -> "SR",
    "swaziland" -> "SZ",
    "sweden" -> "SE",
    "switzerland" -> "CH",
    "syria" -> "SY",
    "taiwan" -> "TW",
    "tajikistan" -> "TJ",
    "tanzania" -> "TZ",
    "thailand" -> "TH",
    "the occupied palestinian territories" -> "PS",
    "timor leste" -> "TL",
    "togo" -> "TG",
    "tonga" -> "TO",
    "trinidad and tobago" -> "TT",
    "tunisia" -> "TN",
    "turkey" -> "TR",
    "turkmenistan" -> "TM",
    "turks and caicos islands" -> "TC",
    "tuvalu" -> "TV",
    "uganda" -> "UG",
    "ukraine" -> "UA",
    "united arab emirates" -> "AE",
    "united kingdom" -> "GB",
    "uruguay" -> "UY",
    "usa" -> "US",
    "uzbekistan" -> "UZ",
    "vanuatu" -> "VU",
    "venezuela" -> "VE",
    "vietnam" -> "VN",
    "wallis and futuna" -> "WF",
    "western sahara" -> "EH",
    "yemen" -> "YE",
    "zambia" -> "ZM",
    "zimbabwe" -> "ZW"
  )
}
