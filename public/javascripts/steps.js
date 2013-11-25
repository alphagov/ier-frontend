window.GOVUK = window.GOVUK || {};

(function () {
  "use strict"
  var root = this,
      $ = root.jQuery,
      countries,
      ENTER = 13;

  countries = [
    { "name": "AF", "value": "Afghanistan", "tokens": ["Afghanistan"] },
    { "name": "AL", "value": "Albania", "tokens": ["Albania"] },
    { "name": "DZ", "value": "Algeria", "tokens": ["Algeria"] },
    { "name": "AS", "value": "American Samoa", "tokens": ["American Samoa"] },
    { "name": "AD", "value": "Andorra", "tokens": ["Andorra"] },
    { "name": "AO", "value": "Angola", "tokens": ["Angola"] },
    { "name": "AI", "value": "Anguilla", "tokens": ["Anguilla"] },
    { "name": "AG", "value": "Antigua and Barbuda", "tokens": ["Antigua and Barbuda"] },
    { "name": "AR", "value": "Argentina", "tokens": ["Argentina"] },
    { "name": "AM", "value": "Armenia", "tokens": ["Armenia"] },
    { "name": "AW", "value": "Aruba", "tokens": ["Aruba"] },
    { "name": "AU", "value": "Australia", "tokens": ["Australia"] },
    { "name": "AT", "value": "Austria", "tokens": ["Austria"] },
    { "name": "AZ", "value": "Azerbaijan", "tokens": ["Azerbaijan"] },
    { "name": "BS", "value": "Bahamas", "tokens": ["Bahamas"] },
    { "name": "BH", "value": "Bahrain", "tokens": ["Bahrain"] },
    { "name": "BD", "value": "Bangladesh", "tokens": ["Bangladesh"] },
    { "name": "BB", "value": "Barbados", "tokens": ["Barbados"] },
    { "name": "BY", "value": "Belarus", "tokens": ["Belarus"] },
    { "name": "BE", "value": "Belgium", "tokens": ["Belgium"] },
    { "name": "BZ", "value": "Belize", "tokens": ["Belize"] },
    { "name": "BJ", "value": "Benin", "tokens": ["Benin"] },
    { "name": "bm", "value": "Bermuda", "tokens": ["Bermuda"] },
    { "name": "BT", "value": "Bhutan", "tokens": ["Bhutan"] },
    { "name": "BO", "value": "Bolivia", "tokens": ["Bolivia"] },
    { "name": "BQ", "value": "Bonaire/St Eustatius/Saba", "tokens": ["Bonaire/St Eustatius/Saba"] },
    { "name": "BA", "value": "Bosnia and Herzegovina", "tokens": ["Bosnia and Herzegovina"] },
    { "name": "BW", "value": "Botswana", "tokens": ["Botswana"] },
    { "name": "BR", "value": "Brazil", "tokens": ["Brazil"] },
    { "name": "IO", "value": "British Indian Ocean Territory", "tokens": ["British Indian Ocean Territory"] },
    { "name": "VG", "value": "British Virgin Islands", "tokens": ["British Virgin Islands"] },
    { "name": "BN", "value": "Brunei", "tokens": ["Brunei"] },
    { "name": "BG", "value": "Bulgaria", "tokens": ["Bulgaria"] },
    { "name": "BF", "value": "Burkina Faso", "tokens": ["Burkina Faso"] },
    { "name": "MM", "value": "Burma", "tokens": ["Burma"] },
    { "name": "bi", "value": "Burundi", "tokens": ["Burundi"] },
    { "name": "KH", "value": "Cambodia", "tokens": ["Cambodia"] },
    { "name": "CM", "value": "Cameroon", "tokens": ["Cameroon"] },
    { "name": "CA", "value": "Canada", "tokens": ["Canada"] },
    { "name": "CV", "value": "Cape Verde", "tokens": ["Cape Verde"] },
    { "name": "KY", "value": "Cayman Islands", "tokens": ["Cayman Islands"] },
    { "name": "CF", "value": "Central African Republic", "tokens": ["Central African Republic"] },
    { "name": "td", "value": "Chad", "tokens": ["Chad"] },
    { "name": "CL", "value": "Chile", "tokens": ["Chile"] },
    { "name": "CN", "value": "China", "tokens": ["China"] },
    { "name": "CO", "value": "Colombia", "tokens": ["Colombia"] },
    { "name": "KM", "value": "Comoros", "tokens": ["Comoros"] },
    { "name": "CG", "value": "Congo", "tokens": ["Congo"] },
    { "name": "CR", "value": "Costa Rica", "tokens": ["Costa Rica"] },
    { "name": "CI", "value": "Cote d’Ivoire", "tokens": ["Cote d’Ivoire"] },
    { "name": "HR", "value": "Croatia", "tokens": ["Croatia"] },
    { "name": "CU", "value": "Cuba", "tokens": ["Cuba"] },
    { "name": "CW", "value": "Curaçao", "tokens": ["Curaçao"] },
    { "name": "CY", "value": "Cyprus", "tokens": ["Cyprus"] },
    { "name": "CZ", "value": "Czech Republic", "tokens": ["Czech Republic"] },
    { "name": "CD", "value": "Democratic Republic of Congo", "tokens": ["Democratic Republic of Congo"] },
    { "name": "DK", "value": "Denmark", "tokens": ["Denmark"] },
    { "name": "DJ", "value": "Djibouti", "tokens": ["Djibouti"] },
    { "name": "DM", "value": "Dominica", "tokens": ["Dominica"] },
    { "name": "DO", "value": "Dominican Republic", "tokens": ["Dominican Republic"] },
    { "name": "EC", "value": "Ecuador", "tokens": ["Ecuador"] },
    { "name": "EG", "value": "Egypt", "tokens": ["Egypt"] },
    { "name": "sv", "value": "El Salvador", "tokens": ["El Salvador"] },
    { "name": "GQ", "value": "Equatorial Guinea", "tokens": ["Equatorial Guinea"] },
    { "name": "ER", "value": "Eritrea", "tokens": ["Eritrea"] },
    { "name": "EE", "value": "Estonia", "tokens": ["Estonia"] },
    { "name": "ET", "value": "Ethiopia", "tokens": ["Ethiopia"] },
    { "name": "fk", "value": "Falkland Islands", "tokens": ["Falkland Islands"] },
    { "name": "FJ", "value": "Fiji", "tokens": ["Fiji"] },
    { "name": "FI", "value": "Finland", "tokens": ["Finland"] },
    { "name": "FR", "value": "France", "tokens": ["France"] },
    { "name": "GF", "value": "French Guiana", "tokens": ["French Guiana"] },
    { "name": "PF", "value": "French Polynesia", "tokens": ["French Polynesia"] },
    { "name": "GA", "value": "Gabon", "tokens": ["Gabon"] },
    { "name": "GM", "value": "Gambia", "tokens": ["Gambia"] },
    { "name": "GE", "value": "Georgia", "tokens": ["Georgia"] },
    { "name": "DE", "value": "Germany", "tokens": ["Germany"] },
    { "name": "GH", "value": "Ghana", "tokens": ["Ghana"] },
    { "name": "GI", "value": "Gibraltar", "tokens": ["Gibraltar"] },
    { "name": "GR", "value": "Greece", "tokens": ["Greece"] },
    { "name": "GD", "value": "Grenada", "tokens": ["Grenada"] },
    { "name": "GP", "value": "Guadeloupe", "tokens": ["Guadeloupe"] },
    { "name": "GT", "value": "Guatemala", "tokens": ["Guatemala"] },
    { "name": "gn", "value": "Guinea", "tokens": ["Guinea"] },
    { "name": "GW", "value": "Guinea-Bissau", "tokens": ["Guinea-Bissau"] },
    { "name": "GY", "value": "Guyana", "tokens": ["Guyana"] },
    { "name": "HT", "value": "Haiti", "tokens": ["Haiti"] },
    { "name": "VA", "value": "Holy See", "tokens": ["Holy See"] },
    { "name": "HN", "value": "Honduras", "tokens": ["Honduras"] },
    { "name": "HK", "value": "Hong Kong", "tokens": ["Hong Kong"] },
    { "name": "HU", "value": "Hungary", "tokens": ["Hungary"] },
    { "name": "IS", "value": "Iceland", "tokens": ["Iceland"] },
    { "name": "IN", "value": "India", "tokens": ["India"] },
    { "name": "ID", "value": "Indonesia", "tokens": ["Indonesia"] },
    { "name": "IR", "value": "Iran", "tokens": ["Iran"] },
    { "name": "IQ", "value": "Iraq", "tokens": ["Iraq"] },
    { "name": "IE", "value": "Ireland", "tokens": ["Ireland"] },
    { "name": "IL", "value": "Israel", "tokens": ["Israel"] },
    { "name": "IT", "value": "Italy", "tokens": ["Italy"] },
    { "name": "JM", "value": "Jamaica", "tokens": ["Jamaica"] },
    { "name": "JP", "value": "Japan", "tokens": ["Japan"] },
    { "name": "JO", "value": "Jordan", "tokens": ["Jordan"] },
    { "name": "KZ", "value": "Kazakhstan", "tokens": ["Kazakhstan"] },
    { "name": "KE", "value": "Kenya", "tokens": ["Kenya"] },
    { "name": "KI", "value": "Kiribati", "tokens": ["Kiribati"] },
    { "name": "KW", "value": "Kuwait", "tokens": ["Kuwait"] },
    { "name": "kg", "value": "Kyrgyzstan", "tokens": ["Kyrgyzstan"] },
    { "name": "LA", "value": "Laos", "tokens": ["Laos"] },
    { "name": "LV", "value": "Latvia", "tokens": ["Latvia"] },
    { "name": "LB", "value": "Lebanon", "tokens": ["Lebanon"] },
    { "name": "LS", "value": "Lesotho", "tokens": ["Lesotho"] },
    { "name": "LR", "value": "Liberia", "tokens": ["Liberia"] },
    { "name": "LY", "value": "Libya", "tokens": ["Libya"] },
    { "name": "LI", "value": "Liechtenstein", "tokens": ["Liechtenstein"] },
    { "name": "LT", "value": "Lithuania", "tokens": ["Lithuania"] },
    { "name": "LU", "value": "Luxembourg", "tokens": ["Luxembourg"] },
    { "name": "MO", "value": "Macao", "tokens": ["Macao"] },
    { "name": "MK", "value": "Macedonia", "tokens": ["Macedonia"] },
    { "name": "MG", "value": "Madagascar", "tokens": ["Madagascar"] },
    { "name": "MW", "value": "Malawi", "tokens": ["Malawi"] },
    { "name": "MY", "value": "Malaysia", "tokens": ["Malaysia"] },
    { "name": "MV", "value": "Maldives", "tokens": ["Maldives"] },
    { "name": "ML", "value": "Mali", "tokens": ["Mali"] },
    { "name": "MT", "value": "Malta", "tokens": ["Malta"] },
    { "name": "MH", "value": "Marshall Islands", "tokens": ["Marshall Islands"] },
    { "name": "MQ", "value": "Martinique", "tokens": ["Martinique"] },
    { "name": "mr", "value": "Mauritania", "tokens": ["Mauritania"] },
    { "name": "MU", "value": "Mauritius", "tokens": ["Mauritius"] },
    { "name": "YT", "value": "Mayotte", "tokens": ["Mayotte"] },
    { "name": "MX", "value": "Mexico", "tokens": ["Mexico"] },
    { "name": "FM", "value": "Micronesia", "tokens": ["Micronesia"] },
    { "name": "MD", "value": "Moldova", "tokens": ["Moldova"] },
    { "name": "MC", "value": "Monaco", "tokens": ["Monaco"] },
    { "name": "MN", "value": "Mongolia", "tokens": ["Mongolia"] },
    { "name": "ME", "value": "Montenegro", "tokens": ["Montenegro"] },
    { "name": "MS", "value": "Montserrat", "tokens": ["Montserrat"] },
    { "name": "MA", "value": "Morocco", "tokens": ["Morocco"] },
    { "name": "MZ", "value": "Mozambique", "tokens": ["Mozambique"] },
    { "name": "NA", "value": "Namibia", "tokens": ["Namibia"] },
    { "name": "NR", "value": "Nauru", "tokens": ["Nauru"] },
    { "name": "NP", "value": "Nepal", "tokens": ["Nepal"] },
    { "name": "NL", "value": "Netherlands", "tokens": ["Netherlands"] },
    { "name": "NC", "value": "New Caledonia", "tokens": ["New Caledonia"] },
    { "name": "NZ", "value": "New Zealand", "tokens": ["New Zealand"] },
    { "name": "NI", "value": "Nicaragua", "tokens": ["Nicaragua"] },
    { "name": "NE", "value": "Niger", "tokens": ["Niger"] },
    { "name": "NG", "value": "Nigeria", "tokens": ["Nigeria"] },
    { "name": "KP", "value": "North Korea", "tokens": ["North Korea"] },
    { "name": "NO", "value": "Norway", "tokens": ["Norway"] },
    { "name": "OM", "value": "Oman", "tokens": ["Oman"] },
    { "name": "PK", "value": "Pakistan", "tokens": ["Pakistan"] },
    { "name": "PW", "value": "Palau", "tokens": ["Palau"] },
    { "name": "PA", "value": "Panama", "tokens": ["Panama"] },
    { "name": "PG", "value": "Papua New Guinea", "tokens": ["Papua New Guinea"] },
    { "name": "PY", "value": "Paraguay", "tokens": ["Paraguay"] },
    { "name": "PE", "value": "Peru", "tokens": ["Peru"] },
    { "name": "PH", "value": "Philippines", "tokens": ["Philippines"] },
    { "name": "pn", "value": "Pitcairn Island", "tokens": ["Pitcairn Island"] },
    { "name": "PL", "value": "Poland", "tokens": ["Poland"] },
    { "name": "PT", "value": "Portugal", "tokens": ["Portugal"] },
    { "name": "QA", "value": "Qatar", "tokens": ["Qatar"] },
    { "name": "RE", "value": "Réunion", "tokens": ["Réunion"] },
    { "name": "RO", "value": "Romania", "tokens": ["Romania"] },
    { "name": "RU", "value": "Russia", "tokens": ["Russia"] },
    { "name": "RW", "value": "Rwanda", "tokens": ["Rwanda"] },
    { "name": "WS", "value": "Samoa", "tokens": ["Samoa"] },
    { "name": "SM", "value": "San Marino", "tokens": ["San Marino"] },
    { "name": "ST", "value": "São Tomé and Principe", "tokens": ["São Tomé and Principe"] },
    { "name": "SA", "value": "Saudi Arabia", "tokens": ["Saudi Arabia"] },
    { "name": "SN", "value": "Senegal", "tokens": ["Senegal"] },
    { "name": "RS", "value": "Serbia", "tokens": ["Serbia"] },
    { "name": "SC", "value": "Seychelles", "tokens": ["Seychelles"] },
    { "name": "SL", "value": "Sierra Leone", "tokens": ["Sierra Leone"] },
    { "name": "SG", "value": "Singapore", "tokens": ["Singapore"] },
    { "name": "SK", "value": "Slovakia", "tokens": ["Slovakia"] },
    { "name": "SI", "value": "Slovenia", "tokens": ["Slovenia"] },
    { "name": "SB", "value": "Solomon Islands", "tokens": ["Solomon Islands"] },
    { "name": "SO", "value": "Somalia", "tokens": ["Somalia"] },
    { "name": "ZA", "value": "South Africa", "tokens": ["South Africa"] },
    { "name": "GS", "value": "South Georgia and the South Sandwich Islands", "tokens": ["South Georgia and the South Sandwich Islands"] },
    { "name": "KR", "value": "South Korea", "tokens": ["South Korea"] },
    { "name": "SS", "value": "South Sudan", "tokens": ["South Sudan"] },
    { "name": "ES", "value": "Spain", "tokens": ["Spain"] },
    { "name": "LK", "value": "Sri Lanka", "tokens": ["Sri Lanka"] },
    { "name": "sh", "value": "St Helena, Ascension and Tristan da Cunha", "tokens": ["St Helena, Ascension and Tristan da Cunha"] },
    { "name": "KN", "value": "St Kitts and Nevis", "tokens": ["St Kitts and Nevis"] },
    { "name": "lc", "value": "St Lucia", "tokens": ["St Lucia"] },
    { "name": "MF", "value": "St Maarten", "tokens": ["St Maarten"] },
    { "name": "PM", "value": "St Pierre & Miquelon", "tokens": ["St Pierre & Miquelon"] },
    { "name": "VC", "value": "St Vincent and The Grenadines", "tokens": ["St Vincent and The Grenadines"] },
    { "name": "SD", "value": "Sudan", "tokens": ["Sudan"] },
    { "name": "SR", "value": "Suriname", "tokens": ["Suriname"] },
    { "name": "SZ", "value": "Swaziland", "tokens": ["Swaziland"] },
    { "name": "SE", "value": "Sweden", "tokens": ["Sweden"] },
    { "name": "CH", "value": "Switzerland", "tokens": ["Switzerland"] },
    { "name": "SY", "value": "Syria", "tokens": ["Syria"] },
    { "name": "TW", "value": "Taiwan", "tokens": ["Taiwan"] },
    { "name": "TJ", "value": "Tajikistan", "tokens": ["Tajikistan"] },
    { "name": "TZ", "value": "Tanzania", "tokens": ["Tanzania"] },
    { "name": "TH", "value": "Thailand", "tokens": ["Thailand"] },
    { "name": "PS", "value": "The Occupied Palestinian Territories", "tokens": ["The Occupied Palestinian Territories"] },
    { "name": "TL", "value": "Timor Leste", "tokens": ["Timor Leste"] },
    { "name": "TG", "value": "Togo", "tokens": ["Togo"] },
    { "name": "TO", "value": "Tonga", "tokens": ["Tonga"] },
    { "name": "TT", "value": "Trinidad and Tobago", "tokens": ["Trinidad and Tobago"] },
    { "name": "TN", "value": "Tunisia", "tokens": ["Tunisia"] },
    { "name": "TR", "value": "Turkey", "tokens": ["Turkey"] },
    { "name": "TM", "value": "Turkmenistan", "tokens": ["Turkmenistan"] },
    { "name": "TC", "value": "Turks and Caicos Islands", "tokens": ["Turks and Caicos Islands"] },
    { "name": "TV", "value": "Tuvalu", "tokens": ["Tuvalu"] },
    { "name": "UG", "value": "Uganda", "tokens": ["Uganda"] },
    { "name": "UA", "value": "Ukraine", "tokens": ["Ukraine"] },
    { "name": "AE", "value": "United Arab Emirates", "tokens": ["United Arab Emirates"] },
    { "name": "GB", "value": "United Kingdom", "tokens": ["United Kingdom"] },
    { "name": "UY", "value": "Uruguay", "tokens": ["Uruguay"] },
    { "name": "US", "value": "USA", "tokens": ["USA"] },
    { "name": "UZ", "value": "Uzbekistan", "tokens": ["Uzbekistan"] },
    { "name": "VU", "value": "Vanuatu", "tokens": ["Vanuatu"] },
    { "name": "VE", "value": "Venezuela", "tokens": ["Venezuela"] },
    { "name": "VN", "value": "Vietnam", "tokens": ["Vietnam"] },
    { "name": "WF", "value": "Wallis and Futuna", "tokens": ["Wallis and Futuna"] },
    { "name": "EH", "value": "Western Sahara", "tokens": ["Western Sahara"] },
    { "name": "YE", "value": "Yemen", "tokens": ["Yemen"] },
    { "name": "ZM", "value": "Zambia", "tokens": ["Zambia"] },
    { "name": "ZW", "value": "Zimbabwe", "tokens": ["Zimbabwe"] }
  ];

  var toggleObj,
      optionalInformation,
      conditionalControl,
      duplicateField,
      markSelected,
      monitorRadios,
      postcodeLookup,
      validation,
      autocomplete,
      autocompletes;

  toggleObj = function (elm) {
    if (elm) {
      this.$content = $(elm);
      this.toggleActions = {
        'hidden': 'Expand',
        'visible': 'Hide'
      };
      this.setup();
      this.bindEvents();
    }
  };
  toggleObj.prototype.setAccessibilityAPI = function (state) {
    if (state === 'hidden') {
      this.$content.attr('aria-hidden', true);
      this.$toggle.attr('aria-expanded', true);
      this.$toggle.find('span.visuallyhidden').eq(0).text(this.toggleActions.hidden);
    } else {
      this.$content.attr('aria-hidden', false);
      this.$toggle.attr('aria-expanded', false);
      this.$toggle.find('span.visuallyhidden').eq(0).text(this.toggleActions.visible);
    }
  };
  toggleObj.prototype.toggle = function () {
    if (this.$content.css("display") === "none") {
      this.$content.show();
      this.setAccessibilityAPI('visible');
      this.$toggle.removeClass("toggle-closed");
      this.$toggle.addClass("toggle-open");
      $(document).trigger('toggle.open', { '$toggle' : this.$toggle });
    } else {
      this.$content.hide();
      this.setAccessibilityAPI('hidden');
      this.$toggle.removeClass("toggle-open");
      this.$toggle.addClass("toggle-closed");
      $(document).trigger('toggle.closed', { '$toggle' : this.$toggle });
    }
  };
  toggleObj.prototype.setup = function () {
    var contentId = this.$content.attr('id');

    this.$heading = this.$content.find("h1,h2,h3,h4").first();
    this.$toggle = $('<a href="#" class="toggle"><span class="visuallyhidden">Show</span> ' + this.$heading.text() + ' <span class="visuallyhidden">section</span></a>');
    if (contentId) { this.$toggle.attr('aria-controls', contentId); }
    this.$toggle.insertBefore(this.$content);
  };
  toggleObj.prototype.bindEvents = function () {
    var inst = this;

    this.$toggle.on('click', function () {
      inst.toggle();
      return false;
    });
  };

  optionalInformation = function () {
    toggleObj.apply(this, arguments);
  };
  $.extend(optionalInformation.prototype, new toggleObj());
  optionalInformation.prototype.setup = function () {
    var contentId = this.$content.attr('id'),
        headingText;

    this.$heading = this.$content.find("h1,h2,h3,h4").first();
    this.$toggle = $('<a href="#" class="toggle"><span class="visuallyhidden">Show</span> ' + this.$heading.text() + ' <span class="visuallyhidden">section</span></a>');
    if (contentId) { this.$toggle.attr('aria-controls', contentId); }
    this.$toggle.insertBefore(this.$content);
    this.$heading.addClass("visuallyhidden");
  };
  optionalInformation.prototype.bindEvents = function () {
    var inst = this;

    this.$toggle.on('click', function () {
      inst.toggle();
      return false;
    });
    this.$toggle.trigger('click');
  };

  conditionalControl = function () {
    toggleObj.apply(this, arguments);
  };
  $.extend(conditionalControl.prototype, new toggleObj());
  conditionalControl.prototype.setup = function () {
    var contentId = this.$content.attr('id');

    this.$toggle = $(document.getElementById(this.$content.data('condition')));
    if (contentId) { this.$toggle.attr('aria-controls', contentId); }
  }; 
  conditionalControl.prototype.bindEvents = function () {
    var inst = this,
        toggleName = this.$toggle.attr('name');

    this.$toggle.on('change', function () {
      inst.toggle();
    });
    if (this.$toggle.attr('type') === 'radio') {
      $(document).on('radio:' + toggleName, function (e, data) {
        inst.toggle(data.selectedRadio);
      });
    }
    this.$toggle.trigger("change");
  };
  conditionalControl.prototype.clearContent = function (controlId) {
    if (controlId !== this.$toggle.attr('id')) {
      this.$content.hide();
    }
  };
  conditionalControl.prototype.toggle = function (selectedRadio) {
    var $postcodeSearch = this.$content.find('.postcode'),
        isPostcodeLookup = $postcodeSearch.length > 0,
        hasAddresses = $('#found-addresses select').length > 0;

    if (selectedRadio !== undefined) {
      if (this.$toggle.attr('id') !== selectedRadio.id) {
        this.$content.hide();
        this.$toggle.attr('aria-expanded', false);
        $('#continue').show();
      }
    } else {
      if (this.$toggle.is(":checked")) {
        this.$content.show();
        this.$content.attr('aria-hidden', false);
        this.$toggle.attr('aria-expanded', true);
        $(document).trigger('toggle.open', { '$toggle' : this.$toggle });
        if (isPostcodeLookup && !hasAddresses) {
          $('#continue').hide();
        }
      } else {
        this.$content.hide();
        this.$content.attr('aria-hidden', true);
        this.$toggle.attr('aria-expanded', true);
        $(document).trigger('toggle.closed', { '$toggle' : this.$toggle });
        $('#continue').show();
      }
    }
  };

  duplicateField = function (control, copyClass, labelObj) {
    var fieldId,
        inst = this;

    this.$control = $(control);
    this.copyClass = copyClass;
    this.label = labelObj;
    this.fieldId = this.$control.data('field');
    this.idPattern = this.fieldId;
    this.namePattern = this.fieldId.replace('_', '.');
    this.$field = $(document.getElementById(this.fieldId));
    this.$label = this.$field.prev('label');
    this.$duplicationIntro = this.$label.parent().find('.duplication-intro');
    this.$label.parent()
      .on('click', function (e) {
        var className = e.target.className;
        if (className.indexOf('duplicate-control') !== -1) {
          inst.duplicate();
        }
        else if (className.indexOf('remove-field') !== -1) {
          inst.removeDuplicate($(e.target).siblings('label').attr('for'));
        }
        return false;
      })
      .find('.added-country').each(function (idx, elm) {
        var $elm = $(elm);

        // remove this, fixing some odd scala formatting
        var input = document.getElementById($(elm).find('label').attr('for'));
        input.value = input.value.replace(/^\s+|\s+$/g, '');
      });
  };
  duplicateField.prototype.makeField = function (fieldNum, fieldValue) {
    var inst = this,
        $container = this.$label.parent(),
        fragment = '<label for="{% id %}" class="{% labelClass %}">{% labelText %}</label>' +
                    '<a href="#" class="remove-field">Remove<span class="visuallyhidden"> {% labelText %}</span></a>' +
                    '<input type="text" id="{% id %}" name="{% name %}" class="text country-autocomplete long" value="{% value %}" autocomplete="off" />',
        wrapperDiv = document.createElement('div'),
        options = {
          'id' : this.getFieldId(fieldNum),
          'labelClass' : this.label.className,
          'labelText' : this.label.txt + " " + fieldNum,
          'name' : this.getFieldName(fieldNum),
          'value' : (fieldValue !== undefined) ? fieldValue : ""
        };

    fragment = fragment.replace(/{%\s([a-zA-Z]+)\s%}/g, function () {
      var attribute = arguments[1];
      return options[attribute];
    });

    wrapperDiv.className = this.copyClass;
    return $(wrapperDiv).html(fragment);
  };
  duplicateField.prototype.getFieldId = function (idx) {
    return this.idPattern.replace(/\[\d+\]/, '[' + idx + ']');
  };
  duplicateField.prototype.getFieldName = function (idx) {
    return this.namePattern.replace(/\[\d+\]/, '[' + idx + ']');
  };
  duplicateField.prototype.removeDuplicate = function (id) {
    var inst = this,
        $container = this.$label.parent(),
        $copies = $container.find("." + this.copyClass),
        $addAnotherLink = $container.find('a.duplicate-control'),
        $previousCopy,
        values = [],
        getRemainingValues,
        targetNum,
        fragment = document.createDocumentFragment();

    getRemainingValues = function (idx, elm) {
      var $elm = $(elm),
          fieldId = $elm.find('label').attr('for');

      if (fieldId !== id) {
        values.push(document.getElementById(fieldId).value);
      } else {
        targetNum = idx + 1;
      }
    };

    $copies.each(getRemainingValues).remove();
    if (values.length === 0) {
      $addAnotherLink.remove();
      this.$duplicationIntro.show();
      this.$field.focus();
      return;
    } else {
      $.each(values, function (idx, item) {
        fragment.appendChild(inst.makeField(idx + 1, item)[0]);
      });
      $addAnotherLink[0].parentNode.insertBefore(fragment, $addAnotherLink[0]);
      if (targetNum === 1) {
        this.$field.focus();
      } else {
        $previousCopy = $container.find('.' + this.copyClass).eq(targetNum - 2);
        $(document.getElementById($previousCopy.find('label').attr('for'))).focus();
      }
    }
  };
  duplicateField.prototype.duplicate = function () {
    var inst = this,
        $container = this.$label.parent(),
        countryIdx = $container.find("." + this.copyClass).length + 1,
        $newField = this.makeField(countryIdx),
        $addAnotherLink;

    if (this.$duplicationIntro.is(':visible')) {
      $addAnotherLink = $('<a href="#" class="duplicate-control">Add another ' + this.label.txt + '</a>');
      this.$duplicationIntro.hide();
      $container.append($addAnotherLink);
    } else {
      $addAnotherLink = $container.find('a.duplicate-control');
    }
    $newField.insertBefore($addAnotherLink);
    $(document).trigger('contentUpdate', { 'context' : $newField });
    document.getElementById(this.getFieldId(countryIdx)).focus();
  };

  autocomplete = function ($input) {
    this.$input = $input;
    this.menuIsShowing = false;
    this.$input.typeahead({
      hint: false,
      name: 'countries',
      local: countries,
      template: this.compiledTemplate,
      engine: Mustache,
      limit: 5
    });
  };
  autocomplete.prototype.getMenuId = (function () {
    var id = 0;

    return function () {
      id = id + 1;
      return this.menuIdPrefix + '-' + id;
    };
  }());
  autocomplete.prototype.menuIdPrefix = 'typeahead-suggestions';
  autocomplete.prototype.compiledStatusText = Mustache.compile('{{results}} {{#describe}}{{results}}{{/describe}} available, use up and down arrow keys to navigate.');
  autocomplete.prototype.compiledTemplate = Mustache.compile('<p role="presentation" id="{{name}}">{{value}}</p>');
  autocomplete.prototype.updateStatus = function (suggestions) {
    var statusText;

    if (suggestions.length > 0) {
      statusText = this.compiledStatusText({ 
        'results' : suggestions.length, 
        'describe' : function () {
          return function (results) {
            return (this.results > 1) ? 'results are' : 'result is';
          }
        }
      });
      this.$status.text(statusText);
    }
  };
  autocomplete.prototype.events = {
    onInitialized : function (e) {
      var autocompleteObj = this.getAutocompleteObj($(e.target)),
          menuId = this.getMenuId();

      this.$menu = this.$input.parent().find('.tt-dropdown-menu');
      this.$status = $('<span role="status" aria-live="polite" class="typeahead-status visuallyhidden" />');
      this.$status.insertAfter(this.$input);
      this.$input.attr({
        'aria-autocomplete' : 'list',
        'aria-haspopup' : menuId
      });
      this.lastInputValue = this.$input.val();
      this.$menu
        .css('width', this.$input.innerWidth() + 'px')
        .attr('id', menuId);
      this.$input.on('keydown', function (e) { 
        var keypressed = e.which,
            currentInputValue = $(this).val();

        if (keypressed === ENTER) {
          return autocompleteObj.events.onEnter.call(autocompleteObj, e); 
        }
        return true;
      });
    },
    onMenuOpen : function () {
      this.menuIsShowing = true;
    },
    onMenuClosed : function () {
      this.menuIsShowing = false;
    },
    onMoveTo : function (e, countryObj) {
      var idOfSelected = (countryObj !== null) ? countryObj.value : "";
      this.$input.attr('aria-activedescendant', idOfSelected);
    },
    onEnter : function (e) {
      if (this.menuIsShowing) {
        this.$menu.hide();
        this.menuIsShowing = false;
        return false;
      } 
      return true;
    },
    onUpdate : function (e, suggestions) {
      this.updateStatus(suggestions);
    }
  };
  autocomplete.prototype.getAutocompleteObj = function ($input) {
    return autocompletes.existingObj($input);
  };

  autocompletes = {
    $currentInput : null,
    cache : {},
    methods : {
      'initialized' : 'onInitialized',
      'opened' : 'onMenuOpen',
      'closed' : 'onMenuClosed',
      'movedto' : 'onMoveTo',
      'updated' : 'onUpdate'
    },
    setCurrentInput : function ($input) {
      this.$currentInput = $input;
    },
    existingId : function ($input) {
      var inputId = $input.attr('id');

      if (typeof this.cache[inputId] !== 'undefined') {
        return inputId;
      }
      return false;
    },
    existingObj : function ($input) {
      return this.cache[$input.attr('id')];
    },
    trigger : function (eventName) {
      var autocompletesObj = this;

      return {
        'andSend' : function (e) {
          var existingObj = autocompletesObj.existingObj($(e.target)),
              method = autocompletesObj.methods[eventName];

          if (existingObj) {
            existingObj.events[method].apply(existingObj, arguments);
          }
        }
      };
    },
    add : function ($input) {
      var existingId = this.existingId($input);

      if (!existingId) {
        this.cache[$input.attr('id')] = new autocomplete($input);
      }
    },
    remove : function ($input) {
      var existing = this.getExisting($input);

      $input.typeahead('destroy');
      if (existing) { delete this.cache[existing]; }
    }
  };

  markSelected = function (elm) {
    var inst = this;

    this.$label = $(elm);
    this.$control = this.$label.find('input[type=radio], input[type=checkbox]');
    if (this.$control.attr('type') === 'radio') {
      $(document).on('radio:' + this.$control.attr('name'), function (e, data) {
        inst.toggle(data.selectedRadio);
      });
    } else {
      this.$label.on('click', function () {
        inst.toggle();
      });
    }
    if (this.$control.is(':checked')) {
      this.$label.addClass('selected');
    }
  };

  markSelected.prototype.toggle = function (selectedRadio) {
    var isChecked = this.$control.is(':checked');

    // called by a change on a radio group
    if (selectedRadio !== undefined) {
      $(selectedRadio).closest('fieldset').find('input[type=radio]').each(function (idx, elm) {
        if (elm.name === selectedRadio.name) {
          $(elm).parent('label').removeClass('selected');
        }
      })
      $(selectedRadio).parent('label').addClass('selected');
    } else { // called from a control selection
      if (isChecked) {
        this.$label.addClass('selected');
      } else {
        this.$label.removeClass('selected');
      }
    }
  };

  monitorRadios = (function () {
    var radioGroups = [];

    return (function (elm) {
      var groupName = elm.name,
          $fieldset = $(elm).closest('fieldset');

      if ($.inArray(groupName, radioGroups) === -1) {
        radioGroups.push(groupName);
        $fieldset.on('change', function (e) {
          var target = e.target;
          if (target.type && target.type === 'radio') {
            $(document).trigger('radio:' + target.name, { "selectedRadio" : target });
          }
        });
      }
    });
  }());

  postcodeLookup = function (searchButton, inputName) {
    var allowSubmission = function ($searchButton) {
      var $optionalSectionContainer = $searchButton.closest('.optional-section'),
          isInOptionalSection = ($optionalSectionContainer.length > 0),
          optionalSectionIsHidden = false;

      if (isInOptionalSection) { 
        optionalSectionIsHidden = $optionalSectionContainer.css('display') === 'none';
      }
      if (isInOptionalSection && optionalSectionIsHidden) {
        return true;
      } else { // lookup is visible
        if (!this.hasAddresses) {
          return false;
        }
      }
      return true;
    };

    this.$searchButton = $(searchButton);
    this.$searchInput = this.$searchButton.closest('fieldset').find('input.postcode');
    this.$targetElement = $('#found-addresses');
    this.hasAddresses = ($('#input-address-list').length > 0);
    this.$waitMessage = $('<p id="wait-for-request">Finding address</p>');
    this.fragment = {
      'hiddenLabel' : '<label for="input-address-postcode" class="hidden">' + 
                         'Postcode' + 
                      '</label>',
      'hiddenInput' : [
        '<input type="hidden" id="input-address-postcode" name="'+inputName+'.postcode" value="',
        '" class="text hidden">'
      ],
      'selectLabel' : '<label for="input-address-list">Select your address</label>',
      'select' : [
        '<select id="input-address-list" name="'+inputName+'.address" class="lonely">' +
          '<option value="">Please select...</option>',
        '</select>'
      ],
      'help' : '<div class="help-content">' +
                  '<h2>My address is not listed</h2>' +
                  '<label for="input-address-text">Enter your address</label>' +
                  '<textarea id="input-address-text" name="'+inputName+'.address" class="small" autocomplete="off"></textarea>' +
                '</div>'
    };
    this.$searchButton.attr('aria-controls', this.$targetElement.attr('id'));
    this.$targetElement.attr({
      'aria-live' : 'polite',
      'role' : 'region'
    });

    if (!allowSubmission.apply(this, [this.$searchButton])) {
      $('#continue').hide();
    }

    this.bindEvents();
    $(document).bind('toggle.open', function (e, data) {
      var $select;

      if (data.$toggle.text() === 'My address is not listed') {
        data.$toggle.remove();
        $select = $('#input-address-list');
        $select.siblings('label[for="input-address-list"]').remove();
        $select.remove();
        $('#input-address-text').focus();
      }
    });
  };
  postcodeLookup.prototype.bindEvents = function () {
    var inst = this;

    this.$searchButton.on('click', function () {
      inst.getAddresses();
      return false;
    });
  };
  postcodeLookup.prototype.onTimeout = function (xhrObj) {
    
  };
  postcodeLookup.prototype.onError = function (status, errorStr) {

  };
  postcodeLookup.prototype.onEmpty = function () {
    $(document).trigger('validation.invalid', { source : this.$searchInput }); 
  };
  postcodeLookup.prototype.addLookup = function (data, postcode) {
    var resultStr;

    resultStr = this.fragment.hiddenLabel;
    resultStr += this.fragment.hiddenInput[0] + postcode + this.fragment.hiddenInput[1];
    resultStr += this.fragment.selectLabel + this.fragment.select[0];
    $(data.addresses).each(function (idx, entry) {
     resultStr += '<option>' + entry.addressLine + '</option>'
    });
    resultStr += this.fragment.select[1] + this.fragment.help;
    this.$targetElement.html(resultStr);
    new GOVUK.registerToVote.optionalInformation(this.$targetElement.find('.help-content'));
    this.hasAddresses = true;
    $('#continue').show();
  };
  postcodeLookup.prototype.getAddresses = function () {
    var inst = this,
        postcode = this.$searchInput.val(),
        URL = '/address/' + postcode.replace(/\s/g,''),
        $optionalInfo = this.$searchButton.closest('fieldset').find('div.help-content'),
        $addressSelect = $optionalInfo.siblings('select');

    if (postcode === "") { 
      this.onEmpty();
    } else {
      this.$waitMessage.insertAfter(this.$searchButton);
      $.ajax({
        url : URL,
        dataType : 'json',
        timeout : 10000
      }).
      done(function (data, status, xhrObj) {
        inst.addLookup(data, postcode);
        $('#possibleAddresses_jsonList').val(xhrObj.responseText);
        $('#possibleAddresses_postcode').val(postcode);
      }).
      fail(function (xhrObj, status, errorStr) {
        if (status === 'timeout' ) {
          inst.onTimeout(xhrObj);
        } else {
          inst.onError(status, errorStr);
        }
      }).
      always(function () {
        inst.$waitMessage.remove();
      });
    }
  };

  validation = {
    init : function () {
      var parentObj = this,
          bindRulesToObject = function () {
            var func,
                rule;

            for (func in parentObj.rules) {
              var rule = parentObj.rules[func];
              parentObj.rules[func] = function () { 
                rule.apply(parentObj, arguments); 
              };
            }
          };

      bindRulesToObject();
      $(document).bind('validation.invalid', function (e, data) {
        parentObj.handler('invalid', data.source);
      });
    },
    handler : function (state, $source) {
      var name,
          rules = [],
          rulesStr = "",
          parentObj = this;

      name = $source.data('validation-name');
      rules = $source.data('validation-rules');

      if (rules) { 
        rules = rules.split(' '); 
        $.each(rules, function (idx, rule) {
          if (typeof parentObj.rules[rule] !== 'undefined') {
            parentObj.rules[rule]($source, name);
          }
        });
      }
    },
    message : {
      existing : {},
      exists : function (messageTxt) {
        return typeof this.existing[messageTxt] !== 'undefined'
      },
      add : function (messageTxt) {
        if (!this.exists(messageTxt)) {
          this.existing[messageTxt] = {
            $elm : $('<div class="validation-message visible">' + messageTxt + '</div>').insertBefore('#continue')
          };
        }
      },
      remove : function (messageTxt) {
        if (exists(messageTxt)) {
          this.existing[messageTxt].$elm.remove();
          delete existing[messageTxt];
        }
      }
    },
    rules : {
     'nonEmpty' : function ($source, name) { this.message.add('Please enter your ' + name) }
    }
  };

  GOVUK.registerToVote = {
    "optionalInformation" : optionalInformation,
    "conditionalControl" : conditionalControl,
    "duplicateField" : duplicateField,
    "markSelected" : markSelected,
    "monitorRadios" : monitorRadios,
    "postcodeLookup" : postcodeLookup,
    "autocompletes" : autocompletes,
    "validation" : validation
  };

  $(document).on('ready', function () { 
    $('.help-content').each(function (idx, elm) {
      new GOVUK.registerToVote.optionalInformation(elm);
    });
    $('.optional-section, .optional-section-binary').each(function (idx, elm) {
      if ($(elm).data('condition') !== undefined) {
        new GOVUK.registerToVote.conditionalControl(elm);
      } else {
        new GOVUK.registerToVote.optionalInformation(elm);
      }
    });
    $('.duplicate-control-initial').each(function (idx, elm) {
      var labelOpts = {
        txt : 'country',
        className : 'country-label'
      };
      new GOVUK.registerToVote.duplicateField(elm, 'added-country', labelOpts);
    });
    $('.selectable').each(function (idx, elm) {
      var $label = $(elm),
          $control = $label.find('input[type=radio], input[type=checkbox]'),
          controlName = $control.attr('name');

      if ($control.attr('type') === 'radio') {
        // set up event monitoring for radios sharing that name
        GOVUK.registerToVote.monitorRadios($control[0]);
      }
      new GOVUK.registerToVote.markSelected(elm);
      $control.on('focus', function () {
        $(this).parent('label').addClass('selectable-focus');
      });
      $control.on('blur', function () {
        $(this).parent('label').removeClass('selectable-focus');
      });
    });
    $('#find-address').each(function (idx, elm) {
      new GOVUK.registerToVote.postcodeLookup(elm, "address");
    });
    $('#find-previous-address').each(function (idx, elm) {
      new GOVUK.registerToVote.postcodeLookup(elm, "previousAddress.previousAddress");
    });
    GOVUK.registerToVote.validation.init();
    $('.country-autocomplete').each(function (idx, elm) {
      GOVUK.registerToVote.autocompletes.add($(elm));
    });
    $.each(['initialized', 'opened', 'closed', 'movedto', 'updated'], function (idx, evt) {
      $(document).bind('typeahead:' + evt, function () {
        var autocompleteEvent = GOVUK.registerToVote.autocompletes.trigger(evt);

        autocompleteEvent.andSend.apply(GOVUK.registerToVote.autcompletes, arguments);
      });
    });
    $(document).bind('contentUpdate', function (e, data) {
      var context = data.context;

      $('.country-autocomplete', context).each(function (idx, elm) {
        GOVUK.registerToVote.autocompletes.add($(elm));
      });
    });
    GOVUK.registerToVote.validation.init();
  });
}.call(this));
