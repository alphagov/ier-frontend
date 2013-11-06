window.GOVUK = window.GOVUK || {};

(function () {
  "use strict"
  var root = this,
      $ = root.jQuery,
      countries,
      ENTER = 13;

/*
  countries = ["Afghanistan","Åland Islands","Albania","Algeria","American Samoa","Andorra","Angola","Anguilla","Antarctica","Antigua and Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia","Bonaire","Bosnia and Herzegovina","Botswana","Bouvet Island","Brazil","British Indian Ocean Territory","Brunei Darussalam","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon","Canada","Cape Verde","Cayman Islands","Central African Republic","Chad","Chile","China","Christmas Island","Cocos (Keeling) Islands","Colombia","Comoros","Republic of the Congo","Democratic Republic of the Congo","Cook Islands","Costa Rica","Côte d'Ivoire","Croatia","Cuba","Curaçao","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Falkland Islands (Malvinas)","Faroe Islands","Fiji","Finland","France","French Guiana","French Polynesia","French Southern Territories","Gabon","Gambia","Georgia","Germany","Ghana","Gibraltar","Greece","Greenland","Grenada","Guadeloupe","Guam","Guatemala","Guernsey","Guinea","Guinea-Bissau","Guyana","Haiti","Heard Island and McDonald Islands","Vatican City","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Isle of Man","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kiribati","Kuwait","Kyrgyzstan","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macao","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands","Martinique","Mauritania","Mauritius","Mayotte","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Myanmar (Burma)","Namibia","Nauru","Nepal","Netherlands","New Caledonia","New Zealand","Nicaragua","Niger","Nigeria","Niue","Norfolk Island","North Korea","Northern Mariana Islands","Norway","Oman","Pakistan","Palau","Palestinian Territory","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Pitcairn","Poland","Portugal","Puerto Rico","Qatar","Réunion","Romania","Russian Federation","Rwanda","Saint Barthélemy","Saint Helena","Saint Kitts and Nevis","Saint Lucia","Saint Martin","Saint Pierre and Miquelon","Saint Vincent and the Grenadines","Samoa","San Marino","São Tomé and Príncipe","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Sint Maarten","Slovakia","Slovenia","Solomon Islands","Somalia","South Africa","South Georgia","South Korea","South Sudan","Spain","Sri Lanka","Sudan","Suriname","Svalbard and Jan Mayen","Swaziland","Sweden","Switzerland","Syrian Arab Republic","Taiwan","Tajikistan","Tanzania","Thailand","Timor-Leste","Togo","Tokelau","Tonga","Trinidad and Tobago","Tunisia","Turkey","Turkmenistan","Turks and Caicos Islands","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom","United States","United States Minor Outlying Islands","Uruguay","Uzbekistan","Vanuatu","Venezuela","Viet Nam","Virgin Islands (British)","Virgin Islands (U.S.)","Wallis and Futuna","Western Sahara","Yemen","Zambia","Zimbabwe"];
*/

  countries = [
    { "value" : "AG", "tokens" : ["Antigua and Barbuda"], "name" : "Antigua and Barbuda" },
    { "value" : "AI", "tokens" : ["Anguilla"], "name" : "Anguilla" },
    { "value" : "AQ", "tokens" : ["British Antarctic Territory"], "name" : "British Antarctic Territory" },
    { "value" : "AT", "tokens" : ["Austria"], "name" : "Austria" },
    { "value" : "AU", "tokens" : ["Australia"], "name" : "Australia" },
    { "value" : "BB", "tokens" : ["Barbados"], "name" : "Barbados" },
    { "value" : "BD", "tokens" : ["Bangladesh"], "name" : "Bangladesh" },
    { "value" : "BE", "tokens" : ["Belgium"], "name" : "Belgium" },
    { "value" : "BG", "tokens" : ["Bulgaria"], "name" : "Bulgaria" },
    { "value" : "BM", "tokens" : ["Bermuda"], "name" : "Bermuda" },
    { "value" : "BN", "tokens" : ["Brunei Darussalam"], "name" : "Brunei Darussalam" },
    { "value" : "BS", "tokens" : ["The Bahamas"], "name" : "The Bahamas" },
    { "value" : "BW", "tokens" : ["Botswana"], "name" : "Botswana" },
    { "value" : "BZ", "tokens" : ["Belize"], "name" : "Belize" },
    { "value" : "CA", "tokens" : ["Canada"], "name" : "Canada" },
    { "value" : "CI", "tokens" : ["Channel Islands"], "name" : "Channel Islands" },
    { "value" : "CM", "tokens" : ["Cameroon"], "name" : "Cameroon" },
    { "value" : "CY", "tokens" : ["Cyprus"], "name" : "Cyprus" },
    { "value" : "CZ", "tokens" : ["Czech Republic"], "name" : "Czech Republic" },
    { "value" : "DE", "tokens" : ["Germany"], "name" : "Germany" },
    { "value" : "DK", "tokens" : ["Denmark"], "name" : "Denmark" },
    { "value" : "DM", "tokens" : ["Dominica"], "name" : "Dominica" },
    { "value" : "EE", "tokens" : ["Estonia"], "name" : "Estonia" },
    { "value" : "ES", "tokens" : ["Spain"], "name" : "Spain" },
    { "value" : "FI", "tokens" : ["Finland"], "name" : "Finland" },
    { "value" : "FJ", "tokens" : ["Fiji Islands"], "name" : "Fiji Islands" },
    { "value" : "FK", "tokens" : ["Falkland Islands"], "name" : "Falkland Islands" },
    { "value" : "FR", "tokens" : ["France"], "name" : "France" },
    { "value" : "GB", "tokens" : ["United Kingdom"], "name" : "United Kingdom" },
    { "value" : "GD", "tokens" : ["Grenada"], "name" : "Grenada" },
    { "value" : "GH", "tokens" : ["Ghana"], "name" : "Ghana" },
    { "value" : "GI", "tokens" : ["Gibraltar"], "name" : "Gibraltar" },
    { "value" : "GM", "tokens" : ["The Gambia"], "name" : "The Gambia" },
    { "value" : "GR", "tokens" : ["Greece"], "name" : "Greece" },
    { "value" : "GS", "tokens" : ["South Georgia and the South Sandwich Islands"], "name" : "South Georgia and the South Sandwich Islands" },
    { "value" : "GY", "tokens" : ["Guyana"], "name" : "Guyana" },
    { "value" : "HK", "tokens" : ["Hong Kong"], "name" : "Hong Kong" },
    { "value" : "HU", "tokens" : ["Hungary"], "name" : "Hungary" },
    { "value" : "IE", "tokens" : ["Ireland"], "name" : "Ireland" },
    { "value" : "IM", "tokens" : ["Isle of Man"], "name" : "Isle of Man" },
    { "value" : "IN", "tokens" : ["India"], "name" : "India" },
    { "value" : "IO", "tokens" : ["British Indian Ocean Territory"], "name" : "British Indian Ocean Territory" },
    { "value" : "IT", "tokens" : ["Italy"], "name" : "Italy" },
    { "value" : "JM", "tokens" : ["Jamaica"], "name" : "Jamaica" },
    { "value" : "KE", "tokens" : ["Kenya"], "name" : "Kenya" },
    { "value" : "KI", "tokens" : ["Kiribati"], "name" : "Kiribati" },
    { "value" : "KN", "tokens" : ["St Kitts & Nevis"], "name" : "St Kitts & Nevis" },
    { "value" : "KY", "tokens" : ["Cayman Islands"], "name" : "Cayman Islands" },
    { "value" : "LC", "tokens" : ["St Lucia"], "name" : "St Lucia" },
    { "value" : "LK", "tokens" : ["Sri Lanka"], "name" : "Sri Lanka" },
    { "value" : "LS", "tokens" : ["Lesotho"], "name" : "Lesotho" },
    { "value" : "LT", "tokens" : ["Lithuania"], "name" : "Lithuania" },
    { "value" : "LU", "tokens" : ["Luxemburg"], "name" : "Luxemburg" },
    { "value" : "LV", "tokens" : ["Latvia"], "name" : "Latvia" },
    { "value" : "MS", "tokens" : ["Montserrat"], "name" : "Montserrat" },
    { "value" : "MT", "tokens" : ["Malta"], "name" : "Malta" },
    { "value" : "MU", "tokens" : ["Mauritius"], "name" : "Mauritius" },
    { "value" : "MV", "tokens" : ["Maldives"], "name" : "Maldives" },
    { "value" : "MW", "tokens" : ["Malawi"], "name" : "Malawi" },
    { "value" : "MY", "tokens" : ["Malaysia"], "name" : "Malaysia" },
    { "value" : "MZ", "tokens" : ["Mozambique"], "name" : "Mozambique" },
    { "value" : "NA", "tokens" : ["Namibia"], "name" : "Namibia" },
    { "value" : "NG", "tokens" : ["Nigeria"], "name" : "Nigeria" },
    { "value" : "NL", "tokens" : ["Netherlands"], "name" : "Netherlands" },
    { "value" : "NR", "tokens" : ["Nauru"], "name" : "Nauru" },
    { "value" : "NZ", "tokens" : ["New Zealand"], "name" : "New Zealand" },
    { "value" : "PG", "tokens" : ["Papua New Guinea"], "name" : "Papua New Guinea" },
    { "value" : "PK", "tokens" : ["Pakistan"], "name" : "Pakistan" },
    { "value" : "PL", "tokens" : ["Poland"], "name" : "Poland" },
    { "value" : "PN", "tokens" : ["Pitcairn Island"], "name" : "Pitcairn Island" },
    { "value" : "PT", "tokens" : ["Portugal"], "name" : "Portugal" },
    { "value" : "RO", "tokens" : ["Romania"], "name" : "Romania" },
    { "value" : "RW", "tokens" : ["Rwanda"], "name" : "Rwanda" },
    { "value" : "SB", "tokens" : ["Solomon Islands"], "name" : "Solomon Islands" },
    { "value" : "SC", "tokens" : ["Seychelles"], "name" : "Seychelles" },
    { "value" : "SE", "tokens" : ["Sweden"], "name" : "Sweden" },
    { "value" : "SG", "tokens" : ["Singapore"], "name" : "Singapore" },
    { "value" : "SH", "tokens" : ["St Helena and dependencies (Ascension Island and Tristan da Cunha)"], "name" : "St Helena and dependencies (Ascension Island and Tristan da Cunha)" },
    { "value" : "SI", "tokens" : ["Slovenia"], "name" : "Slovenia" },
    { "value" : "SK", "tokens" : ["Slovakia"], "name" : "Slovakia" },
    { "value" : "SL", "tokens" : ["Sierra Leone"], "name" : "Sierra Leone" },
    { "value" : "SZ", "tokens" : ["Swaziland"], "name" : "Swaziland" },
    { "value" : "TC", "tokens" : ["Turks and Caicos Islands"], "name" : "Turks and Caicos Islands" },
    { "value" : "TO", "tokens" : ["Tonga"], "name" : "Tonga" },
    { "value" : "TT", "tokens" : ["Trinidad & Tobago"], "name" : "Trinidad & Tobago" },
    { "value" : "TV", "tokens" : ["Tuvalu"], "name" : "Tuvalu" },
    { "value" : "TZ", "tokens" : ["United Republic of Tanzania"], "name" : "United Republic of Tanzania" },
    { "value" : "UG", "tokens" : ["Uganda"], "name" : "Uganda" },
    { "value" : "VC", "tokens" : ["St Vincent & The Grenadines"], "name" : "St Vincent & The Grenadines" },
    { "value" : "VG", "tokens" : ["British Virgin Islands"], "name" : "British Virgin Islands" },
    { "value" : "VU", "tokens" : ["Vanuatu"], "name" : "Vanuatu" },
    { "value" : "WS", "tokens" : ["Samoa"], "name" : "Samoa" },
    { "value" : "ZA", "tokens" : ["South Africa"], "name" : "South Africa" },
    { "value" : "ZM", "tokens" : ["Zambia"], "name" : "Zambia" },
    { "value" : "ZW", "tokens" : ["Zimbabwe"], "name" : "Zimbabwe" }
  ];

  var toggleObj,
      optionalInformation,
      conditionalControl,
      duplicateField,
      markSelected,
      monitorRadios,
      postcodeLookup,
      validation,
      autocomplete;

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
                    '<input type="text" id="{% id %}" name="{% name %}" class="text country-autocomplete long" value="{% value %}" />',
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

  autocomplete = {
    menuIsShowing : false,
    statusText : '{{results}} results are available, use up and down arrow keys to navigate.',
    resultHTML : '<p id="{{value}}">{{name}}</p>',
    add : function ($input) {
      var compiledTemplate = Mustache.compile(this.resultHTML);

      $input.typeahead({
        hint: false,
        name: 'countries',
        local: countries,
        template: compiledTemplate,
        engine: Mustache,
        limit: 5
      });
    },
    remove : function ($input) {
      $input.parent('.twitter-typeahead').remove();
    },
    onInitialized : function (e) {
      var $input = $(e.target),
          autocompleteObj = this,
          menuId = 'typeahead-suggestions';

      $input = $input;
      this.$menu = $input.parent().find('.tt-dropdown-menu');
      this.$status = $('<span role="status" aria-live="polite" class="typeahead-status visuallyhidden" />');
      this.$status.insertAfter($input);
      this.compiledStatusText = Mustache.compile(this.statusText);
      $input.attr({
        'aria-autocomplete' : 'list',
        'aria-haspopup' : menuId
      });
      this.$menu
        .css('width', $input.innerWidth() + 'px')
        .attr('id', menuId);
      $input.on('keydown', function (e) { 
        var keypressed = e.which;
        if (keypressed === ENTER) {
          return autocompleteObj.onEnter(e); 
        }
        return true;
      });
    },
    onMenuOpen : function () {
      var suggestions = this.$menu.find('.suggestions').length;

      this.$status
        .text(this.compiledStatusText({ 'results' : suggestions }))
        .show();
      this.menuIsShowing = true;
    },
    onMenuClosed: function () {
      var suggestions = this.$menu.find('.suggestions').length;

      this.$status.hide();
      this.menuIsShowing = false;
    },
    onSelected : function (e, countryObj) {
      var $input = $(e.target);

      $input.attr('aria-activedescendant', countryObj.value);
    },
    onEnter : function (e) {
      if (this.menuIsShowing) {
        this.$menu.hide();
        this.menuIsShowing = false;
        return false;
      } 
      return true;
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
      'label' : '<label for="input-address-list">Select your address</label>',
      'select' : [
        '<select id="input-address-list" name="'+inputName+'" class="lonely">' +
          '<option value="">Please select...</option>',
        '</select>'
      ],
      'help' : '<div class="help-content">' +
                  '<h2>My address is not listed</h2>' +
                  '<label for="input-address-text">Enter your address</label>' +
                  '<textarea id="input-address-text" name="'+inputName+'" class="small"></textarea>' +
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
  postcodeLookup.prototype.addLookup = function (data) {
    var resultStr;

    resultStr = this.fragment.label + this.fragment.select[0];
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
        inst.addLookup(data);
        $('#possibleAddresses_jsonList').val(xhrObj.responseText)
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
    "autocomplete" : autocomplete,
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
      new GOVUK.registerToVote.postcodeLookup(elm, "address.address");
    });
    $('#find-previous-address').each(function (idx, elm) {
      new GOVUK.registerToVote.postcodeLookup(elm, "previousAddress.previousAddress.address");
    });
    GOVUK.registerToVote.validation.init();
    $('.country-autocomplete').each(function (idx, elm) {
      GOVUK.registerToVote.autocomplete.add($(elm));
    });
    $(document).bind('typeahead:initialized', function (e) {
      GOVUK.registerToVote.autocomplete.onInitialized(e);
    });
    $(document).bind('typeahead:opened', function (e) {
      GOVUK.registerToVote.autocomplete.onMenuOpen();
    });
    $(document).bind('typeahead:closed', function (e) {
      GOVUK.registerToVote.autocomplete.onMenuClosed();
    });
    $(document).bind('typeahead:selected', function (e, countryObj) {
      GOVUK.registerToVote.autocomplete.onSelected(e, countryObj);
    });
    $(document).bind('contentUpdate', function (e, data) {
      var context = data.context;

      $('.country-autocomplete', context).each(function (idx, elm) {
        GOVUK.registerToVote.autocomplete.add($(elm));
      });
    });
    GOVUK.registerToVote.validation.init();
  });
}.call(this));
