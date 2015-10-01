(function(){"use strict";var countries,root=this,GOVUK=root.GOVUK;countries=[{name:"AF",value:"Afghanistan",tokens:["Afghanistan"]},{name:"AL",value:"Albania",tokens:["Albania"]},{name:"DZ",value:"Algeria",tokens:["Algeria"]},{name:"AS",value:"American Samoa",tokens:["American Samoa"]},{name:"AD",value:"Andorra",tokens:["Andorra"]},{name:"AO",value:"Angola",tokens:["Angola"]},{name:"AI",value:"Anguilla",tokens:["Anguilla"]},{name:"AG",value:"Antigua and Barbuda",tokens:["Antigua and Barbuda"]},{name:"AR",value:"Argentina",tokens:["Argentina"]},{name:"AM",value:"Armenia",tokens:["Armenia"]},{name:"AW",value:"Aruba",tokens:["Aruba"]},{name:"AU",value:"Australia",tokens:["Australia"]},{name:"AT",value:"Austria",tokens:["Austria"]},{name:"AZ",value:"Azerbaijan",tokens:["Azerbaijan"]},{name:"BS",value:"Bahamas",tokens:["Bahamas"]},{name:"BH",value:"Bahrain",tokens:["Bahrain"]},{name:"BD",value:"Bangladesh",tokens:["Bangladesh"]},{name:"BB",value:"Barbados",tokens:["Barbados"]},{name:"BY",value:"Belarus",tokens:["Belarus"]},{name:"BE",value:"Belgium",tokens:["Belgium"]},{name:"BZ",value:"Belize",tokens:["Belize"]},{name:"BJ",value:"Benin",tokens:["Benin"]},{name:"bm",value:"Bermuda",tokens:["Bermuda"]},{name:"BT",value:"Bhutan",tokens:["Bhutan"]},{name:"BO",value:"Bolivia",tokens:["Bolivia"]},{name:"BQ",value:"Bonaire/St Eustatius/Saba",tokens:["Bonaire/St Eustatius/Saba"]},{name:"BA",value:"Bosnia and Herzegovina",tokens:["Bosnia and Herzegovina"]},{name:"BW",value:"Botswana",tokens:["Botswana"]},{name:"BR",value:"Brazil",tokens:["Brazil"]},{name:"IO",value:"British Indian Ocean Territory",tokens:["British Indian Ocean Territory"]},{name:"VG",value:"British Virgin Islands",tokens:["British Virgin Islands"]},{name:"BN",value:"Brunei",tokens:["Brunei"]},{name:"BG",value:"Bulgaria",tokens:["Bulgaria"]},{name:"BF",value:"Burkina Faso",tokens:["Burkina Faso"]},{name:"MM",value:"Burma",tokens:["Burma"]},{name:"bi",value:"Burundi",tokens:["Burundi"]},{name:"KH",value:"Cambodia",tokens:["Cambodia"]},{name:"CM",value:"Cameroon",tokens:["Cameroon"]},{name:"CA",value:"Canada",tokens:["Canada"]},{name:"CV",value:"Cape Verde",tokens:["Cape Verde"]},{name:"KY",value:"Cayman Islands",tokens:["Cayman Islands"]},{name:"CF",value:"Central African Republic",tokens:["Central African Republic"]},{name:"td",value:"Chad",tokens:["Chad"]},{name:"CL",value:"Chile",tokens:["Chile"]},{name:"CN",value:"China",tokens:["China"]},{name:"CO",value:"Colombia",tokens:["Colombia"]},{name:"KM",value:"Comoros",tokens:["Comoros"]},{name:"CG",value:"Congo",tokens:["Congo"]},{name:"CR",value:"Costa Rica",tokens:["Costa Rica"]},{name:"CI",value:"Cote d’Ivoire",tokens:["Cote d’Ivoire"]},{name:"HR",value:"Croatia",tokens:["Croatia"]},{name:"CU",value:"Cuba",tokens:["Cuba"]},{name:"CW",value:"Curaçao",tokens:["Curaçao"]},{name:"CY",value:"Cyprus",tokens:["Cyprus"]},{name:"CZ",value:"Czech Republic",tokens:["Czech Republic"]},{name:"CD",value:"Democratic Republic of Congo",tokens:["Democratic Republic of Congo"]},{name:"DK",value:"Denmark",tokens:["Denmark"]},{name:"DJ",value:"Djibouti",tokens:["Djibouti"]},{name:"DM",value:"Dominica",tokens:["Dominica"]},{name:"DO",value:"Dominican Republic",tokens:["Dominican Republic"]},{name:"EC",value:"Ecuador",tokens:["Ecuador"]},{name:"EG",value:"Egypt",tokens:["Egypt"]},{name:"sv",value:"El Salvador",tokens:["El Salvador"]},{name:"GQ",value:"Equatorial Guinea",tokens:["Equatorial Guinea"]},{name:"ER",value:"Eritrea",tokens:["Eritrea"]},{name:"EE",value:"Estonia",tokens:["Estonia"]},{name:"ET",value:"Ethiopia",tokens:["Ethiopia"]},{name:"fk",value:"Falkland Islands",tokens:["Falkland Islands"]},{name:"FJ",value:"Fiji",tokens:["Fiji"]},{name:"FI",value:"Finland",tokens:["Finland"]},{name:"FR",value:"France",tokens:["France"]},{name:"GF",value:"French Guiana",tokens:["French Guiana"]},{name:"PF",value:"French Polynesia",tokens:["French Polynesia"]},{name:"GA",value:"Gabon",tokens:["Gabon"]},{name:"GM",value:"Gambia",tokens:["Gambia"]},{name:"GE",value:"Georgia",tokens:["Georgia"]},{name:"DE",value:"Germany",tokens:["Germany"]},{name:"GH",value:"Ghana",tokens:["Ghana"]},{name:"GI",value:"Gibraltar",tokens:["Gibraltar"]},{name:"GR",value:"Greece",tokens:["Greece"]},{name:"GD",value:"Grenada",tokens:["Grenada"]},{name:"GP",value:"Guadeloupe",tokens:["Guadeloupe"]},{name:"GT",value:"Guatemala",tokens:["Guatemala"]},{name:"gn",value:"Guinea",tokens:["Guinea"]},{name:"GW",value:"Guinea-Bissau",tokens:["Guinea-Bissau"]},{name:"GY",value:"Guyana",tokens:["Guyana"]},{name:"HT",value:"Haiti",tokens:["Haiti"]},{name:"VA",value:"Holy See",tokens:["Holy See"]},{name:"HN",value:"Honduras",tokens:["Honduras"]},{name:"HK",value:"Hong Kong",tokens:["Hong Kong"]},{name:"HU",value:"Hungary",tokens:["Hungary"]},{name:"IS",value:"Iceland",tokens:["Iceland"]},{name:"IN",value:"India",tokens:["India"]},{name:"ID",value:"Indonesia",tokens:["Indonesia"]},{name:"IR",value:"Iran",tokens:["Iran"]},{name:"IQ",value:"Iraq",tokens:["Iraq"]},{name:"IE",value:"Ireland",tokens:["Ireland"]},{name:"IL",value:"Israel",tokens:["Israel"]},{name:"IT",value:"Italy",tokens:["Italy"]},{name:"JM",value:"Jamaica",tokens:["Jamaica"]},{name:"JP",value:"Japan",tokens:["Japan"]},{name:"JO",value:"Jordan",tokens:["Jordan"]},{name:"KZ",value:"Kazakhstan",tokens:["Kazakhstan"]},{name:"KE",value:"Kenya",tokens:["Kenya"]},{name:"KI",value:"Kiribati",tokens:["Kiribati"]},{name:"XK",value:"Kosovo",tokens:["Kosovo"]},{name:"KW",value:"Kuwait",tokens:["Kuwait"]},{name:"kg",value:"Kyrgyzstan",tokens:["Kyrgyzstan"]},{name:"LA",value:"Laos",tokens:["Laos"]},{name:"LV",value:"Latvia",tokens:["Latvia"]},{name:"LB",value:"Lebanon",tokens:["Lebanon"]},{name:"LS",value:"Lesotho",tokens:["Lesotho"]},{name:"LR",value:"Liberia",tokens:["Liberia"]},{name:"LY",value:"Libya",tokens:["Libya"]},{name:"LI",value:"Liechtenstein",tokens:["Liechtenstein"]},{name:"LT",value:"Lithuania",tokens:["Lithuania"]},{name:"LU",value:"Luxembourg",tokens:["Luxembourg"]},{name:"MO",value:"Macao",tokens:["Macao"]},{name:"MK",value:"Macedonia",tokens:["Macedonia"]},{name:"MG",value:"Madagascar",tokens:["Madagascar"]},{name:"MW",value:"Malawi",tokens:["Malawi"]},{name:"MY",value:"Malaysia",tokens:["Malaysia"]},{name:"MV",value:"Maldives",tokens:["Maldives"]},{name:"ML",value:"Mali",tokens:["Mali"]},{name:"MT",value:"Malta",tokens:["Malta"]},{name:"MH",value:"Marshall Islands",tokens:["Marshall Islands"]},{name:"MQ",value:"Martinique",tokens:["Martinique"]},{name:"mr",value:"Mauritania",tokens:["Mauritania"]},{name:"MU",value:"Mauritius",tokens:["Mauritius"]},{name:"YT",value:"Mayotte",tokens:["Mayotte"]},{name:"MX",value:"Mexico",tokens:["Mexico"]},{name:"FM",value:"Micronesia",tokens:["Micronesia"]},{name:"MD",value:"Moldova",tokens:["Moldova"]},{name:"MC",value:"Monaco",tokens:["Monaco"]},{name:"MN",value:"Mongolia",tokens:["Mongolia"]},{name:"ME",value:"Montenegro",tokens:["Montenegro"]},{name:"MS",value:"Montserrat",tokens:["Montserrat"]},{name:"MA",value:"Morocco",tokens:["Morocco"]},{name:"MZ",value:"Mozambique",tokens:["Mozambique"]},{name:"NA",value:"Namibia",tokens:["Namibia"]},{name:"NR",value:"Nauru",tokens:["Nauru"]},{name:"NP",value:"Nepal",tokens:["Nepal"]},{name:"NL",value:"Netherlands",tokens:["Netherlands"]},{name:"NC",value:"New Caledonia",tokens:["New Caledonia"]},{name:"NZ",value:"New Zealand",tokens:["New Zealand"]},{name:"NI",value:"Nicaragua",tokens:["Nicaragua"]},{name:"NE",value:"Niger",tokens:["Niger"]},{name:"NG",value:"Nigeria",tokens:["Nigeria"]},{name:"KP",value:"North Korea",tokens:["North Korea"]},{name:"NO",value:"Norway",tokens:["Norway"]},{name:"OM",value:"Oman",tokens:["Oman"]},{name:"PK",value:"Pakistan",tokens:["Pakistan"]},{name:"PW",value:"Palau",tokens:["Palau"]},{name:"PA",value:"Panama",tokens:["Panama"]},{name:"PG",value:"Papua New Guinea",tokens:["Papua New Guinea"]},{name:"PY",value:"Paraguay",tokens:["Paraguay"]},{name:"PE",value:"Peru",tokens:["Peru"]},{name:"PH",value:"Philippines",tokens:["Philippines"]},{name:"pn",value:"Pitcairn Island",tokens:["Pitcairn Island"]},{name:"PL",value:"Poland",tokens:["Poland"]},{name:"PT",value:"Portugal",tokens:["Portugal"]},{name:"QA",value:"Qatar",tokens:["Qatar"]},{name:"RE",value:"Réunion",tokens:["Réunion"]},{name:"RO",value:"Romania",tokens:["Romania"]},{name:"RU",value:"Russia",tokens:["Russia"]},{name:"RW",value:"Rwanda",tokens:["Rwanda"]},{name:"WS",value:"Samoa",tokens:["Samoa"]},{name:"SM",value:"San Marino",tokens:["San Marino"]},{name:"ST",value:"São Tomé and Principe",tokens:["São Tomé and Principe"]},{name:"SA",value:"Saudi Arabia",tokens:["Saudi Arabia"]},{name:"SN",value:"Senegal",tokens:["Senegal"]},{name:"RS",value:"Serbia",tokens:["Serbia"]},{name:"SC",value:"Seychelles",tokens:["Seychelles"]},{name:"SL",value:"Sierra Leone",tokens:["Sierra Leone"]},{name:"SG",value:"Singapore",tokens:["Singapore"]},{name:"SK",value:"Slovakia",tokens:["Slovakia"]},{name:"SI",value:"Slovenia",tokens:["Slovenia"]},{name:"SB",value:"Solomon Islands",tokens:["Solomon Islands"]},{name:"SO",value:"Somalia",tokens:["Somalia"]},{name:"ZA",value:"South Africa",tokens:["South Africa"]},{name:"GS",value:"South Georgia and the South Sandwich Islands",tokens:["South Georgia and the South Sandwich Islands"]},{name:"KR",value:"South Korea",tokens:["South Korea"]},{name:"SS",value:"South Sudan",tokens:["South Sudan"]},{name:"ES",value:"Spain",tokens:["Spain"]},{name:"LK",value:"Sri Lanka",tokens:["Sri Lanka"]},{name:"sh",value:"St Helena, Ascension and Tristan da Cunha",tokens:["St Helena, Ascension and Tristan da Cunha"]},{name:"KN",value:"St Kitts and Nevis",tokens:["St Kitts and Nevis"]},{name:"lc",value:"St Lucia",tokens:["St Lucia"]},{name:"MF",value:"St Maarten",tokens:["St Maarten"]},{name:"PM",value:"St Pierre & Miquelon",tokens:["St Pierre & Miquelon"]},{name:"VC",value:"St Vincent and The Grenadines",tokens:["St Vincent and The Grenadines"]},{name:"SD",value:"Sudan",tokens:["Sudan"]},{name:"SR",value:"Suriname",tokens:["Suriname"]},{name:"SZ",value:"Swaziland",tokens:["Swaziland"]},{name:"SE",value:"Sweden",tokens:["Sweden"]},{name:"CH",value:"Switzerland",tokens:["Switzerland"]},{name:"SY",value:"Syria",tokens:["Syria"]},{name:"TW",value:"Taiwan",tokens:["Taiwan"]},{name:"TJ",value:"Tajikistan",tokens:["Tajikistan"]},{name:"TZ",value:"Tanzania",tokens:["Tanzania"]},{name:"TH",value:"Thailand",tokens:["Thailand"]},{name:"PS",value:"The Occupied Palestinian Territories",tokens:["The Occupied Palestinian Territories"]},{name:"TL",value:"Timor Leste",tokens:["Timor Leste"]},{name:"TG",value:"Togo",tokens:["Togo"]},{name:"TO",value:"Tonga",tokens:["Tonga"]},{name:"TT",value:"Trinidad and Tobago",tokens:["Trinidad and Tobago"]},{name:"TN",value:"Tunisia",tokens:["Tunisia"]},{name:"TR",value:"Turkey",tokens:["Turkey"]},{name:"TM",value:"Turkmenistan",tokens:["Turkmenistan"]},{name:"TC",value:"Turks and Caicos Islands",tokens:["Turks and Caicos Islands"]},{name:"TV",value:"Tuvalu",tokens:["Tuvalu"]},{name:"UG",value:"Uganda",tokens:["Uganda"]},{name:"UA",value:"Ukraine",tokens:["Ukraine"]},{name:"AE",value:"United Arab Emirates",tokens:["United Arab Emirates"]},{name:"GB",value:"United Kingdom",tokens:["United Kingdom"]},{name:"UY",value:"Uruguay",tokens:["Uruguay"]},{name:"US",value:"United States of America",tokens:["United States of America"]},{name:"UZ",value:"Uzbekistan",tokens:["Uzbekistan"]},{name:"VU",value:"Vanuatu",tokens:["Vanuatu"]},{name:"VE",value:"Venezuela",tokens:["Venezuela"]},{name:"VN",value:"Vietnam",tokens:["Vietnam"]},{name:"WF",value:"Wallis and Futuna",tokens:["Wallis and Futuna"]},{name:"EH",value:"Western Sahara",tokens:["Western Sahara"]},{name:"YE",value:"Yemen",tokens:["Yemen"]},{name:"ZM",value:"Zambia",tokens:["Zambia"]},{name:"ZW",value:"Zimbabwe",tokens:["Zimbabwe"]}],GOVUK.registerToVote.countries=countries}).call(this);