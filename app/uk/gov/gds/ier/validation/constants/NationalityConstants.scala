package uk.gov.gds.ier.validation.constants

import scala.collection.immutable.HashMap

object NationalityConstants {

  type Franchise = String
  object Franchise {
    def apply(str:String) = {
      require(
        str == "EU" || 
        str == "Commonwealth" || 
        str == "BritishTerritories" ||
        str == "CrownDependencies"
      )
      str
    }
    lazy val european = Franchise("EU")
    lazy val commonwealth = Franchise("Commonwealth")
    lazy val britishTerritories = Franchise("BritishTerritories")
    lazy val crownDependencies = Franchise("CrownDependencies")
  }

  case class ISOCountry(isoCode:String, franchise: List[Franchise])
  
  lazy val countryNameToCodes = HashMap(
    "afghanistan" -> ISOCountry("AF", List()),
    "albania" -> ISOCountry("AL", List()),
    "algeria" -> ISOCountry("DZ", List()),
    "american samoa" -> ISOCountry("AS", List()),
    "andorra" -> ISOCountry("AD", List()),
    "angola" -> ISOCountry("AO", List()),
    "anguilla" -> ISOCountry("AI", List()),
    "antigua and barbuda" -> ISOCountry("AG", List()),
    "argentina" -> ISOCountry("AR", List()),
    "armenia" -> ISOCountry("AM", List()),
    "aruba" -> ISOCountry("AW", List()),
    "australia" -> ISOCountry("AU", List(Franchise.european)),
    "austria" -> ISOCountry("AT", List()),
    "azerbaijan" -> ISOCountry("AZ", List()),
    "bahamas" -> ISOCountry("BS", List()),
    "bahrain" -> ISOCountry("BH", List()),
    "bangladesh" -> ISOCountry("BD", List()),
    "barbados" -> ISOCountry("BB", List()),
    "belarus" -> ISOCountry("BY", List()),
    "belgium" -> ISOCountry("BE", List(Franchise.european)),
    "belize" -> ISOCountry("BZ", List()),
    "benin" -> ISOCountry("BJ", List()),
    "bermuda" -> ISOCountry("bm", List()),
    "bhutan" -> ISOCountry("BT", List()),
    "bolivia" -> ISOCountry("BO", List()),
    "bonaire/st eustatius/saba" -> ISOCountry("BQ", List()),
    "bosnia and herzegovina" -> ISOCountry("BA", List()),
    "botswana" -> ISOCountry("BW", List()),
    "brazil" -> ISOCountry("BR", List()),
    "british indian ocean territory" -> ISOCountry("IO", List()),
    "british virgin islands" -> ISOCountry("VG", List()),
    "brunei" -> ISOCountry("BN", List(Franchise.european)),
    "bulgaria" -> ISOCountry("BG", List()),
    "burkina faso" -> ISOCountry("BF", List()),
    "burma" -> ISOCountry("MM", List()),
    "burundi" -> ISOCountry("bi", List()),
    "cambodia" -> ISOCountry("KH", List()),
    "cameroon" -> ISOCountry("CM", List()),
    "canada" -> ISOCountry("CA", List()),
    "cape verde" -> ISOCountry("CV", List()),
    "cayman islands" -> ISOCountry("KY", List()),
    "central african republic" -> ISOCountry("CF", List()),
    "chad" -> ISOCountry("td", List()),
    "chile" -> ISOCountry("CL", List()),
    "china" -> ISOCountry("CN", List()),
    "colombia" -> ISOCountry("CO", List()),
    "comoros" -> ISOCountry("KM", List()),
    "congo" -> ISOCountry("CG", List()),
    "costa rica" -> ISOCountry("CR", List()),
    "cote d’ivoire" -> ISOCountry("CI", List()),
    "croatia" -> ISOCountry("HR", List(Franchise.european)),
    "cuba" -> ISOCountry("CU", List()),
    "curaçao" -> ISOCountry("CW", List()),
    "cyprus" -> ISOCountry("CY", List(Franchise.european)),
    "czech republic" -> ISOCountry("CZ", List(Franchise.european)),
    "democratic republic of congo" -> ISOCountry("CD", List()),
    "denmark" -> ISOCountry("DK", List(Franchise.european)),
    "djibouti" -> ISOCountry("DJ", List()),
    "dominica" -> ISOCountry("DM", List()),
    "dominican republic" -> ISOCountry("DO", List()),
    "ecuador" -> ISOCountry("EC", List()),
    "egypt" -> ISOCountry("EG", List()),
    "el salvador" -> ISOCountry("sv", List()),
    "equatorial guinea" -> ISOCountry("GQ", List()),
    "eritrea" -> ISOCountry("ER", List()),
    "estonia" -> ISOCountry("EE", List(Franchise.european)),
    "ethiopia" -> ISOCountry("ET", List()),
    "falkland islands" -> ISOCountry("fk", List()),
    "fiji" -> ISOCountry("FJ", List()),
    "finland" -> ISOCountry("FI", List(Franchise.european)),
    "france" -> ISOCountry("FR", List(Franchise.european)),
    "french guiana" -> ISOCountry("GF", List()),
    "french polynesia" -> ISOCountry("PF", List()),
    "gabon" -> ISOCountry("GA", List()),
    "gambia" -> ISOCountry("GM", List()),
    "georgia" -> ISOCountry("GE", List()),
    "germany" -> ISOCountry("DE", List(Franchise.european)),
    "ghana" -> ISOCountry("GH", List()),
    "gibraltar" -> ISOCountry("GI", List()),
    "greece" -> ISOCountry("GR", List(Franchise.european)),
    "grenada" -> ISOCountry("GD", List()),
    "guadeloupe" -> ISOCountry("GP", List()),
    "guatemala" -> ISOCountry("GT", List()),
    "guinea" -> ISOCountry("gn", List()),
    "guinea-bissau" -> ISOCountry("GW", List()),
    "guyana" -> ISOCountry("GY", List()),
    "haiti" -> ISOCountry("HT", List()),
    "holy see" -> ISOCountry("VA", List()),
    "honduras" -> ISOCountry("HN", List()),
    "hong kong" -> ISOCountry("HK", List()),
    "hungary" -> ISOCountry("HU", List(Franchise.european)),
    "iceland" -> ISOCountry("IS", List()),
    "india" -> ISOCountry("IN", List()),
    "indonesia" -> ISOCountry("ID", List()),
    "iran" -> ISOCountry("IR", List()),
    "iraq" -> ISOCountry("IQ", List()),
    "ireland" -> ISOCountry("IE", List(Franchise.european)),
    "israel" -> ISOCountry("IL", List()),
    "italy" -> ISOCountry("IT", List(Franchise.european)),
    "jamaica" -> ISOCountry("JM", List()),
    "japan" -> ISOCountry("JP", List()),
    "jordan" -> ISOCountry("JO", List()),
    "kazakhstan" -> ISOCountry("KZ", List()),
    "kenya" -> ISOCountry("KE", List()),
    "kiribati" -> ISOCountry("KI", List()),
    "kuwait" -> ISOCountry("KW", List()),
    "kyrgyzstan" -> ISOCountry("kg", List()),
    "laos" -> ISOCountry("LA", List()),
    "latvia" -> ISOCountry("LV", List(Franchise.european)),
    "lebanon" -> ISOCountry("LB", List()),
    "lesotho" -> ISOCountry("LS", List()),
    "liberia" -> ISOCountry("LR", List()),
    "libya" -> ISOCountry("LY", List()),
    "liechtenstein" -> ISOCountry("LI", List()),
    "lithuania" -> ISOCountry("LT", List(Franchise.european)),
    "luxembourg" -> ISOCountry("LU", List(Franchise.european)),
    "macao" -> ISOCountry("MO", List()),
    "macedonia" -> ISOCountry("MK", List()),
    "madagascar" -> ISOCountry("MG", List()),
    "malawi" -> ISOCountry("MW", List()),
    "malaysia" -> ISOCountry("MY", List()),
    "maldives" -> ISOCountry("MV", List()),
    "mali" -> ISOCountry("ML", List()),
    "malta" -> ISOCountry("MT", List(Franchise.european)),
    "marshall islands" -> ISOCountry("MH", List()),
    "martinique" -> ISOCountry("MQ", List()),
    "mauritania" -> ISOCountry("mr", List()),
    "mauritius" -> ISOCountry("MU", List()),
    "mayotte" -> ISOCountry("YT", List()),
    "mexico" -> ISOCountry("MX", List()),
    "micronesia" -> ISOCountry("FM", List()),
    "moldova" -> ISOCountry("MD", List()),
    "monaco" -> ISOCountry("MC", List()),
    "mongolia" -> ISOCountry("MN", List()),
    "montenegro" -> ISOCountry("ME", List()),
    "montserrat" -> ISOCountry("MS", List()),
    "morocco" -> ISOCountry("MA", List()),
    "mozambique" -> ISOCountry("MZ", List()),
    "namibia" -> ISOCountry("NA", List()),
    "nauru" -> ISOCountry("NR", List()),
    "nepal" -> ISOCountry("NP", List()),
    "netherlands" -> ISOCountry("NL", List(Franchise.european)),
    "new caledonia" -> ISOCountry("NC", List()),
    "new zealand" -> ISOCountry("NZ", List()),
    "nicaragua" -> ISOCountry("NI", List()),
    "niger" -> ISOCountry("NE", List()),
    "nigeria" -> ISOCountry("NG", List()),
    "north korea" -> ISOCountry("KP", List()),
    "norway" -> ISOCountry("NO", List()),
    "oman" -> ISOCountry("OM", List()),
    "pakistan" -> ISOCountry("PK", List()),
    "palau" -> ISOCountry("PW", List()),
    "panama" -> ISOCountry("PA", List()),
    "papua new guinea" -> ISOCountry("PG", List()),
    "paraguay" -> ISOCountry("PY", List()),
    "peru" -> ISOCountry("PE", List()),
    "philippines" -> ISOCountry("PH", List()),
    "pitcairn island" -> ISOCountry("pn", List()),
    "poland" -> ISOCountry("PL", List(Franchise.european)),
    "portugal" -> ISOCountry("PT", List(Franchise.european)),
    "qatar" -> ISOCountry("QA", List()),
    "réunion" -> ISOCountry("RE", List()),
    "romania" -> ISOCountry("RO", List(Franchise.european)),
    "russia" -> ISOCountry("RU", List()),
    "rwanda" -> ISOCountry("RW", List()),
    "samoa" -> ISOCountry("WS", List()),
    "san marino" -> ISOCountry("SM", List()),
    "são tomé and principe" -> ISOCountry("ST", List()),
    "saudi arabia" -> ISOCountry("SA", List()),
    "senegal" -> ISOCountry("SN", List()),
    "serbia" -> ISOCountry("RS", List()),
    "seychelles" -> ISOCountry("SC", List()),
    "sierra leone" -> ISOCountry("SL", List()),
    "singapore" -> ISOCountry("SG", List()),
    "slovakia" -> ISOCountry("SK", List(Franchise.european)),
    "slovenia" -> ISOCountry("SI", List(Franchise.european)),
    "solomon islands" -> ISOCountry("SB", List()),
    "somalia" -> ISOCountry("SO", List()),
    "south africa" -> ISOCountry("ZA", List()),
    "south georgia and the south sandwich islands" -> ISOCountry("GS", List()),
    "south korea" -> ISOCountry("KR", List()),
    "south sudan" -> ISOCountry("SS", List()),
    "spain" -> ISOCountry("ES", List(Franchise.european)),
    "sri lanka" -> ISOCountry("LK", List()),
    "st helena, ascension and tristan da cunha" -> ISOCountry("sh", List()),
    "st kitts and nevis" -> ISOCountry("KN", List()),
    "st lucia" -> ISOCountry("lc", List()),
    "st maarten" -> ISOCountry("MF", List()),
    "st pierre & miquelon" -> ISOCountry("PM", List()),
    "st vincent and the grenadines" -> ISOCountry("VC", List()),
    "sudan" -> ISOCountry("SD", List()),
    "suriname" -> ISOCountry("SR", List()),
    "swaziland" -> ISOCountry("SZ", List()),
    "sweden" -> ISOCountry("SE", List(Franchise.european)),
    "switzerland" -> ISOCountry("CH", List()),
    "syria" -> ISOCountry("SY", List()),
    "taiwan" -> ISOCountry("TW", List()),
    "tajikistan" -> ISOCountry("TJ", List()),
    "tanzania" -> ISOCountry("TZ", List()),
    "thailand" -> ISOCountry("TH", List()),
    "the occupied palestinian territories" -> ISOCountry("PS", List()),
    "timor leste" -> ISOCountry("TL", List()),
    "togo" -> ISOCountry("TG", List()),
    "tonga" -> ISOCountry("TO", List()),
    "trinidad and tobago" -> ISOCountry("TT", List()),
    "tunisia" -> ISOCountry("TN", List()),
    "turkey" -> ISOCountry("TR", List()),
    "turkmenistan" -> ISOCountry("TM", List()),
    "turks and caicos islands" -> ISOCountry("TC", List()),
    "tuvalu" -> ISOCountry("TV", List()),
    "uganda" -> ISOCountry("UG", List()),
    "ukraine" -> ISOCountry("UA", List()),
    "united arab emirates" -> ISOCountry("AE", List()),
    "united kingdom" -> ISOCountry("GB", List(Franchise.european)),
    "uruguay" -> ISOCountry("UY", List()),
    "usa" -> ISOCountry("US", List()),
    "uzbekistan" -> ISOCountry("UZ", List()),
    "vanuatu" -> ISOCountry("VU", List()),
    "venezuela" -> ISOCountry("VE", List()),
    "vietnam" -> ISOCountry("VN", List()),
    "wallis and futuna" -> ISOCountry("WF", List()),
    "western sahara" -> ISOCountry("EH", List()),
    "yemen" -> ISOCountry("YE", List()),
    "zambia" -> ISOCountry("ZM", List()),
    "zimbabwe" -> ISOCountry("ZW", List())
  )
}
