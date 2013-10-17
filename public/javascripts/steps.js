window.GOVUK = window.GOVUK || {};

(function () {
  "use strict"
  var root = this,
      $ = root.jQuery,
      countries;

  countries = ["Afghanistan","Åland Islands","Albania","Algeria","American Samoa","Andorra","Angola","Anguilla","Antarctica","Antigua and Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia","Bonaire","Bosnia and Herzegovina","Botswana","Bouvet Island","Brazil","British Indian Ocean Territory","Brunei Darussalam","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon","Canada","Cape Verde","Cayman Islands","Central African Republic","Chad","Chile","China","Christmas Island","Cocos (Keeling) Islands","Colombia","Comoros","Republic of the Congo","Democratic Republic of the Congo","Cook Islands","Costa Rica","Côte d'Ivoire","Croatia","Cuba","Curaçao","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Falkland Islands (Malvinas)","Faroe Islands","Fiji","Finland","France","French Guiana","French Polynesia","French Southern Territories","Gabon","Gambia","Georgia","Germany","Ghana","Gibraltar","Greece","Greenland","Grenada","Guadeloupe","Guam","Guatemala","Guernsey","Guinea","Guinea-Bissau","Guyana","Haiti","Heard Island and McDonald Islands","Vatican City","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Isle of Man","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kiribati","Kuwait","Kyrgyzstan","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macao","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands","Martinique","Mauritania","Mauritius","Mayotte","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Myanmar (Burma)","Namibia","Nauru","Nepal","Netherlands","New Caledonia","New Zealand","Nicaragua","Niger","Nigeria","Niue","Norfolk Island","North Korea","Northern Mariana Islands","Norway","Oman","Pakistan","Palau","Palestinian Territory","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Pitcairn","Poland","Portugal","Puerto Rico","Qatar","Réunion","Romania","Russian Federation","Rwanda","Saint Barthélemy","Saint Helena","Saint Kitts and Nevis","Saint Lucia","Saint Martin","Saint Pierre and Miquelon","Saint Vincent and the Grenadines","Samoa","San Marino","São Tomé and Príncipe","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Sint Maarten","Slovakia","Slovenia","Solomon Islands","Somalia","South Africa","South Georgia","South Korea","South Sudan","Spain","Sri Lanka","Sudan","Suriname","Svalbard and Jan Mayen","Swaziland","Sweden","Switzerland","Syrian Arab Republic","Taiwan","Tajikistan","Tanzania","Thailand","Timor-Leste","Togo","Tokelau","Tonga","Trinidad and Tobago","Tunisia","Turkey","Turkmenistan","Turks and Caicos Islands","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom","United States","United States Minor Outlying Islands","Uruguay","Uzbekistan","Vanuatu","Venezuela","Viet Nam","Virgin Islands (British)","Virgin Islands (U.S.)","Wallis and Futuna","Western Sahara","Yemen","Zambia","Zimbabwe"];

  var toggleObj,
      optionalInformation,
      conditionalControl,
      duplicateField,
      markSelected,
      monitorRadios,
      postcodeLookup,
      autocomplete,
      validation;

  toggleObj = function (elm) {
    if (elm) {
      this.$content = $(elm);
      this.setup();
      this.bindEvents();
    }
  };
  toggleObj.prototype.toggle = function () {
    if (this.$content.css("display") === "none") {
      this.$content.show();
      this.$toggle.removeClass("toggle-closed");
      this.$toggle.addClass("toggle-open");
      $(document).trigger('toggle.open', { '$toggle' : this.$toggle });
    } else {
      this.$content.hide();
      this.$toggle.removeClass("toggle-open");
      this.$toggle.addClass("toggle-closed");
      $(document).trigger('toggle.closed', { '$toggle' : this.$toggle });
    }
  };
  toggleObj.prototype.setup = function () {
    this.$heading = this.$content.find("h1,h2,h3,h4").first();
    this.$toggle = $('<a href="#" class="toggle">' + this.$heading.text() + '</a>');
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
    var headingText;
    this.$heading = this.$content.find("h1,h2,h3,h4").first();
    this.$toggle = $('<a href="#" class="toggle toggle-closed">' + this.$heading.text() + '</a>');
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
    this.$toggle = $(document.getElementById(this.$content.data('condition')));
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
        hasAddresses = $postcodeSearch.closest('fieldset').find('select').length > 0;

    if (selectedRadio !== undefined) {
      if (this.$toggle.attr('id') !== selectedRadio.id) {
        this.$content.hide();
        $('#continue').show();
      }
    } else {
      if (this.$toggle.is(":checked")) {
        this.$content.show();
        $(document).trigger('toggle.open', { '$toggle' : this.$toggle });
        if (isPostcodeLookup && !hasAddresses) {
          $('#continue').hide();
        }
      } else {
        this.$content.hide();
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
                    '<input id="{% id %}" name="{% name %}" class="text country-autocomplete" value="{% value %}" />',
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
    add : function ($input) {
      $input.typeahead({
        hint: false,
        name: 'countries',
        local: countries,
        limit: 5
      });
    },
    remove : function ($input) {
      $input.parent('.twitter-typeahead').remove();
    }
  };

  markSelected = function (elm) {
    var inst = this;

    this.$label = $(elm);
    this.$control = this.$label.find('input[type=radio], input[type=checkbox]');
    this.$label.on('click', function () {
      inst.toggle();
    });
    if (this.$control.attr('type') === 'radio') {
      $(document).on('radio:' + this.$control.attr('name'), function (e, data) {
        inst.toggle(data.selectedRadio);
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
          $(elm).closest('label').removeClass('selected');
        }
      });
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

      if ($.inArray(radioGroups, groupName) === -1) {
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
    this.$searchButton = $(searchButton);
    this.$searchInput = this.$searchButton.closest('fieldset').find('input.postcode');
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
    this.bindEvents();
    if (this.$searchButton.closest('.optional-section').length === 0) {
      $('#continue').hide();
    }
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
    $(resultStr).insertAfter(this.$searchButton);
    new GOVUK.registerToVote.optionalInformation(this.$searchButton.closest('fieldset').find('.help-content')[0]);
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
      if ($optionalInfo.length) {
        $optionalInfo.siblings('label[for="' + $optionalInfo.attr('id') + '"], div.help-content, a.toggle').remove();
        $optionalInfo.remove();
      }
      $.ajax({
        url : URL,
        dataType : 'json',
        timeout : 10000
      }).
      done(function (data, status, xhrObj) {
        inst.addLookup(data);
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
      var getFakeData,
          name,
          rules = [],
          rulesStr = "",
          parentObj = this;

      getFakeData = function (str) {
        var regEx = new RegExp(str + "\\(([a-zA-Z]+)\\)", "g"),
            match = regEx.exec($source[0].className);

        if (match) {
          return match[1];
        }
      };
      name = getFakeData('validation-name');
      rules = getFakeData('validation-rules');

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
        txt : 'Country',
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
    });
    $('#find-address').each(function (idx, elm) {
      new GOVUK.registerToVote.postcodeLookup(elm, "address.address");
    });
    $('#find-previous-address').each(function (idx, elm) {
      new GOVUK.registerToVote.postcodeLookup(elm, "previousAddress.previousAddress.address");
    });
    $('.country-autocomplete').each(function (idx, elm) {
      GOVUK.registerToVote.autocomplete.add($(elm));
    });
    $(document).bind('contentUpdate', function (e, data) {
      var context = data.context;

      $('.country-autocomplete', context).each(function (idx, elm) {
        GOVUK.registerToVote.autocomplete.add($(elm));
      });
    });
    $(document).bind('typeahead:opened', function (e) {
      var $input = $(e.target);
      $(e.target).parent().find('.tt-dropdown-menu').css('width', $input.innerWidth() + 'px');
    });
    GOVUK.registerToVote.validation.init();
  });
}.call(this));
