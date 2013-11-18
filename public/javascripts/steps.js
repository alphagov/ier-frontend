window.GOVUK = window.GOVUK || {};

(function () {
  "use strict"
  var root = this,
      $ = root.jQuery,
      countries,
      ENTER = 13;

  countries = [
    { "name" : "AG", "tokens" : ["Antigua and Barbuda"], "value" : "Antigua and Barbuda" },
    { "name" : "AI", "tokens" : ["Anguilla"], "value" : "Anguilla" },
    { "name" : "AQ", "tokens" : ["British Antarctic Territory"], "value" : "British Antarctic Territory" },
    { "name" : "AT", "tokens" : ["Austria"], "value" : "Austria" },
    { "name" : "AU", "tokens" : ["Australia"], "value" : "Australia" },
    { "name" : "BB", "tokens" : ["Barbados"], "value" : "Barbados" },
    { "name" : "BD", "tokens" : ["Bangladesh"], "value" : "Bangladesh" },
    { "name" : "BE", "tokens" : ["Belgium"], "value" : "Belgium" },
    { "name" : "BG", "tokens" : ["Bulgaria"], "value" : "Bulgaria" },
    { "name" : "BM", "tokens" : ["Bermuda"], "value" : "Bermuda" },
    { "name" : "BN", "tokens" : ["Brunei Darussalam"], "value" : "Brunei Darussalam" },
    { "name" : "BS", "tokens" : ["The Bahamas"], "value" : "The Bahamas" },
    { "name" : "BW", "tokens" : ["Botswana"], "value" : "Botswana" },
    { "name" : "BZ", "tokens" : ["Belize"], "value" : "Belize" },
    { "name" : "CA", "tokens" : ["Canada"], "value" : "Canada" },
    { "name" : "CI", "tokens" : ["Channel Islands"], "value" : "Channel Islands" },
    { "name" : "CM", "tokens" : ["Cameroon"], "value" : "Cameroon" },
    { "name" : "CY", "tokens" : ["Cyprus"], "value" : "Cyprus" },
    { "name" : "CZ", "tokens" : ["Czech Republic"], "value" : "Czech Republic" },
    { "name" : "DE", "tokens" : ["Germany"], "value" : "Germany" },
    { "name" : "DK", "tokens" : ["Denmark"], "value" : "Denmark" },
    { "name" : "DM", "tokens" : ["Dominica"], "value" : "Dominica" },
    { "name" : "EE", "tokens" : ["Estonia"], "value" : "Estonia" },
    { "name" : "ES", "tokens" : ["Spain"], "value" : "Spain" },
    { "name" : "FI", "tokens" : ["Finland"], "value" : "Finland" },
    { "name" : "FJ", "tokens" : ["Fiji Islands"], "value" : "Fiji Islands" },
    { "name" : "FK", "tokens" : ["Falkland Islands"], "value" : "Falkland Islands" },
    { "name" : "FR", "tokens" : ["France"], "value" : "France" },
    { "name" : "GB", "tokens" : ["United Kingdom"], "value" : "United Kingdom" },
    { "name" : "GD", "tokens" : ["Grenada"], "value" : "Grenada" },
    { "name" : "GH", "tokens" : ["Ghana"], "value" : "Ghana" },
    { "name" : "GI", "tokens" : ["Gibraltar"], "value" : "Gibraltar" },
    { "name" : "GM", "tokens" : ["The Gambia"], "value" : "The Gambia" },
    { "name" : "GR", "tokens" : ["Greece"], "value" : "Greece" },
    { "name" : "GS", "tokens" : ["South Georgia and the South Sandwich Islands"], "value" : "South Georgia and the South Sandwich Islands" },
    { "name" : "GY", "tokens" : ["Guyana"], "value" : "Guyana" },
    { "name" : "HK", "tokens" : ["Hong Kong"], "value" : "Hong Kong" },
    { "name" : "HU", "tokens" : ["Hungary"], "value" : "Hungary" },
    { "name" : "IE", "tokens" : ["Ireland"], "value" : "Ireland" },
    { "name" : "IM", "tokens" : ["Isle of Man"], "value" : "Isle of Man" },
    { "name" : "IN", "tokens" : ["India"], "value" : "India" },
    { "name" : "IO", "tokens" : ["British Indian Ocean Territory"], "value" : "British Indian Ocean Territory" },
    { "name" : "IT", "tokens" : ["Italy"], "value" : "Italy" },
    { "name" : "JM", "tokens" : ["Jamaica"], "value" : "Jamaica" },
    { "name" : "KE", "tokens" : ["Kenya"], "value" : "Kenya" },
    { "name" : "KI", "tokens" : ["Kiribati"], "value" : "Kiribati" },
    { "name" : "KN", "tokens" : ["St Kitts & Nevis"], "value" : "St Kitts & Nevis" },
    { "name" : "KY", "tokens" : ["Cayman Islands"], "value" : "Cayman Islands" },
    { "name" : "LC", "tokens" : ["St Lucia"], "value" : "St Lucia" },
    { "name" : "LK", "tokens" : ["Sri Lanka"], "value" : "Sri Lanka" },
    { "name" : "LS", "tokens" : ["Lesotho"], "value" : "Lesotho" },
    { "name" : "LT", "tokens" : ["Lithuania"], "value" : "Lithuania" },
    { "name" : "LU", "tokens" : ["Luxemburg"], "value" : "Luxemburg" },
    { "name" : "LV", "tokens" : ["Latvia"], "value" : "Latvia" },
    { "name" : "MS", "tokens" : ["Montserrat"], "value" : "Montserrat" },
    { "name" : "MT", "tokens" : ["Malta"], "value" : "Malta" },
    { "name" : "MU", "tokens" : ["Mauritius"], "value" : "Mauritius" },
    { "name" : "MV", "tokens" : ["Maldives"], "value" : "Maldives" },
    { "name" : "MW", "tokens" : ["Malawi"], "value" : "Malawi" },
    { "name" : "MY", "tokens" : ["Malaysia"], "value" : "Malaysia" },
    { "name" : "MZ", "tokens" : ["Mozambique"], "value" : "Mozambique" },
    { "name" : "NA", "tokens" : ["Namibia"], "value" : "Namibia" },
    { "name" : "NG", "tokens" : ["Nigeria"], "value" : "Nigeria" },
    { "name" : "NL", "tokens" : ["Netherlands"], "value" : "Netherlands" },
    { "name" : "NR", "tokens" : ["Nauru"], "value" : "Nauru" },
    { "name" : "NZ", "tokens" : ["New Zealand"], "value" : "New Zealand" },
    { "name" : "PG", "tokens" : ["Papua New Guinea"], "value" : "Papua New Guinea" },
    { "name" : "PK", "tokens" : ["Pakistan"], "value" : "Pakistan" },
    { "name" : "PL", "tokens" : ["Poland"], "value" : "Poland" },
    { "name" : "PN", "tokens" : ["Pitcairn Island"], "value" : "Pitcairn Island" },
    { "name" : "PT", "tokens" : ["Portugal"], "value" : "Portugal" },
    { "name" : "RO", "tokens" : ["Romania"], "value" : "Romania" },
    { "name" : "RW", "tokens" : ["Rwanda"], "value" : "Rwanda" },
    { "name" : "SB", "tokens" : ["Solomon Islands"], "value" : "Solomon Islands" },
    { "name" : "SC", "tokens" : ["Seychelles"], "value" : "Seychelles" },
    { "name" : "SE", "tokens" : ["Sweden"], "value" : "Sweden" },
    { "name" : "SG", "tokens" : ["Singapore"], "value" : "Singapore" },
    { "name" : "SH", "tokens" : ["St Helena and dependencies (Ascension Island and Tristan da Cunha)"], "value" : "St Helena and dependencies (Ascension Island and Tristan da Cunha)" },
    { "name" : "SI", "tokens" : ["Slovenia"], "value" : "Slovenia" },
    { "name" : "SK", "tokens" : ["Slovakia"], "value" : "Slovakia" },
    { "name" : "SL", "tokens" : ["Sierra Leone"], "value" : "Sierra Leone" },
    { "name" : "SZ", "tokens" : ["Swaziland"], "value" : "Swaziland" },
    { "name" : "TC", "tokens" : ["Turks and Caicos Islands"], "value" : "Turks and Caicos Islands" },
    { "name" : "TO", "tokens" : ["Tonga"], "value" : "Tonga" },
    { "name" : "TT", "tokens" : ["Trinidad & Tobago"], "value" : "Trinidad & Tobago" },
    { "name" : "TV", "tokens" : ["Tuvalu"], "value" : "Tuvalu" },
    { "name" : "TZ", "tokens" : ["United Republic of Tanzania"], "value" : "United Republic of Tanzania" },
    { "name" : "UG", "tokens" : ["Uganda"], "value" : "Uganda" },
    { "name" : "VC", "tokens" : ["St Vincent & The Grenadines"], "value" : "St Vincent & The Grenadines" },
    { "name" : "VG", "tokens" : ["British Virgin Islands"], "value" : "British Virgin Islands" },
    { "name" : "VU", "tokens" : ["Vanuatu"], "value" : "Vanuatu" },
    { "name" : "WS", "tokens" : ["Samoa"], "value" : "Samoa" },
    { "name" : "ZA", "tokens" : ["South Africa"], "value" : "South Africa" },
    { "name" : "ZM", "tokens" : ["Zambia"], "value" : "Zambia" },
    { "name" : "ZW", "tokens" : ["Zimbabwe"], "value" : "Zimbabwe" }
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
      'updated' : 'onUpdate',
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
      new GOVUK.registerToVote.postcodeLookup(elm, "address.address");
    });
    $('#find-previous-address').each(function (idx, elm) {
      new GOVUK.registerToVote.postcodeLookup(elm, "previousAddress.previousAddress.address");
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
