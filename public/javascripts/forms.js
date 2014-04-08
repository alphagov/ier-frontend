(function () {
  "use strict"

  var root = this,
      $ = root.jQuery,
      GOVUK = root.GOVUK,
      countries = GOVUK.registerToVote.countries,
      validation = GOVUK.registerToVote.validation,
      ENTER = 13,
      ConditionalControl,
      DuplicateField,
      MarkSelected,
      Autocomplete,
      autocompletes,
      PostcodeLookup,
      monitorRadios,
      BackButton;

  BackButton = function (elm) {
    if (elm) {
      this.$header = $(elm);
      this.setup();
      this.bindEvents();
    }
  };
  BackButton.prototype.setup = function () {
    this.$link = $('<a class="back-to-previous" href="#">' +
      'Back <span class="visuallyhidden"> to the previous question</span></a>');
    this.$header.before(this.$link);
    this.$header.removeClass('no-back-link');
  };
  BackButton.prototype.bindEvents = function () {
    this.$link.on("click", function(e) {
      e.preventDefault();
      root.history.back();
      return false;
    });
  };

  // Contructor for controlling parts of a form based on the state of one of its elements (ie. radio button)
  // Uses GOVUK.registerToVote.ToggleObj for its base prototype
  ConditionalControl = function () {
    GOVUK.registerToVote.ToggleObj.apply(this, arguments);
  };
  $.extend(ConditionalControl.prototype, new GOVUK.registerToVote.ToggleObj());
  ConditionalControl.prototype.setup = function () {
    var contentId = this.$content.attr('id'),
        loadedState = 'hidden',
        _this = this,
        _bindControlAndContentMargins;
    
    // if both are siblings, make controls inherit the margin-bottom of the content
    _bindControlAndContentMargins = function () {
      var $controlWrapper = _this.$toggle.parent('.selectable'),
          $contentValidation = _this.$content.parent('.validation-wrapper'),
          $control = ($controlWrapper.length) ? $controlWrapper : _this.$toggle,
          $content = ($contentValidation.length) ? $contentValidation : _this.$content;
 
      if ($control.siblings().index($content) !== -1) {
        _this.controlAndContentAreSiblings = true;
        _this.marginWhenContentIs = {
          'shown' : $control.css('margin-bottom'),
          'hidden' : $content.css('margin-bottom') 
        };
      }
    };
    // fix for having periods in id names breaking sizzle
    this.$toggle = $(document.getElementById(this.$content.data('condition'))); 
    if (contentId) { this.$toggle.attr('aria-controls', contentId); }
    _bindControlAndContentMargins();
    if (this.$content.hasClass(this.toggleClass)) {
      loadedState = 'shown';
    }
    this.adjustVerticalSpace(loadedState);
    // deal with controls that are selected onload
    if (this.$toggle.is(':checked')) {
      this.toggle();
    }
  }; 
  ConditionalControl.prototype.bindEvents = function () {
    var _this = this,
        toggleName = this.$toggle.attr('name');

    this.$toggle.on('change', function () {
      _this.toggle();
    });
    if (this.$toggle.attr('type') === 'radio') {
      $(document).on('radio:' + toggleName, function (e, data) {
        _this.toggle(data.selectedControl);
      });
    }
  };
  ConditionalControl.prototype.adjustVerticalSpace = function (content) {
    var $controlWrapper = this.$toggle.parent('.selectable'),
        $control = ($controlWrapper.length) ? $controlWrapper : this.$toggle;

    if (!this.controlAndContentAreSiblings) { return; }
    if (content === 'hidden') {
      $control.css('margin-bottom', this.marginWhenContentIs.hidden);
    } else {
      $control.css('margin-bottom', this.marginWhenContentIs.shown);
    }
  };
  ConditionalControl.prototype.toggle = function (selectedControl) {
    var $postcodeSearch = this.$content.find('.postcode'),
        isPostcodeLookup = $postcodeSearch.length > 0,
        hasAddresses = $('#found-addresses select').length > 0,
        _this = this,
        _hideContent,
        _showContent;

    _hideContent = function () {
      _this.$content.removeClass(_this.toggleClass);
      _this.$content.attr({
        'aria-hidden' : true,
        'aria-expanded' : false
      });
    };

    _showContent = function () {
      _this.$content.addClass(_this.toggleClass);
      _this.$content.attr({
        'aria-hidden' : false,
        'aria-expanded' : true
      });
    };

    /* 
      Every time a change is detected in a group of radio buttons the following will happen:

      1. this method will be called with no parameter (from a change event on the selected radio)
      2. this method will be called with the selectedControl parameter (from a change event on the group)

      We use 2. to close our content if the selected radio is not ours.
    */
    if (selectedControl !== undefined) {
      if (this.$toggle.attr('id') !== selectedControl.id) {
        _hideContent();
        this.adjustVerticalSpace('hidden');
        $('#continue').show();
      }
    } else {
      if (this.$toggle.is(":checked")) {
        _showContent();
        this.adjustVerticalSpace('shown');
        $(document).trigger('toggle.open', { '$toggle' : this.$toggle });
        if (isPostcodeLookup && !hasAddresses) {
          $('#continue').hide();
        }
      } else {
        _hideContent();
        this.adjustVerticalSpace('hidden');
        $(document).trigger('toggle.closed', { '$toggle' : this.$toggle });
        $('#continue').show();
      }
    }
  };

  // Constructor to add the ability to duplicate a single field multiple times
  DuplicateField = function (control, copyClass, labelObj) {
    var fieldId,
        _this = this;

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
          _this.duplicate();
        }
        else if (className.indexOf('remove-field') !== -1) {
          _this.removeDuplicate($(e.target).siblings('label').attr('for'));
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
  DuplicateField.prototype.makeField = function (fieldNum, fieldValue) {
    var _this = this,
        $container = this.$label.parent(),
        fragment = '<label for="{{ id }}" class="{{ labelClass }}">{{ labelText }}</label>' +
                    '<a href="#" class="remove-field">Remove<span class="visuallyhidden"> {{ labelText }}</span></a>' +
                    '<input type="text" id="{{ id }}" name="{{ name }}" class="text country-autocomplete long" value="{{ value }}" Autocomplete="off" />',
        wrapperDiv = document.createElement('div'),
        options = {
          'id' : this.getFieldId(fieldNum),
          'labelClass' : this.label.className,
          'labelText' : this.label.txt + " " + fieldNum,
          'name' : this.getFieldName(fieldNum),
          'value' : (fieldValue !== undefined) ? fieldValue : ""
        };

    wrapperDiv.className = this.copyClass;
    return $(wrapperDiv).html(Mustache.render(fragment, options));
  };
  DuplicateField.prototype.getFieldId = function (idx) {
    return this.idPattern.replace(/\[\d+\]/, '[' + idx + ']');
  };
  DuplicateField.prototype.getFieldName = function (idx) {
    return this.namePattern.replace(/\[\d+\]/, '[' + idx + ']');
  };
  DuplicateField.prototype.removeDuplicate = function (id) {
    var _this = this,
        $container = this.$label.parent(),
        $copies = $container.find("." + this.copyClass),
        $addAnotherLink = $container.find('a.duplicate-control'),
        $previousCopy,
        values = [],
        _getRemainingValues,
        targetNum,
        fragment = document.createDocumentFragment();

    _getRemainingValues = function (idx, elm) {
      var $elm = $(elm),
          fieldId = $elm.find('label').attr('for');

      if (fieldId !== id) {
        values.push(document.getElementById(fieldId).value);
      } else {
        targetNum = idx + 1;
      }
      $(document).trigger('contentRemoval', { 'context' : elm });
    };

    $copies.each(_getRemainingValues).remove();
    if (values.length === 0) {
      $addAnotherLink.remove();
      this.$duplicationIntro.show();
      this.$field.focus();
      return;
    } else {
      $.each(values, function (idx, item) {
        fragment.appendChild(_this.makeField(idx + 1, item)[0]);
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
  DuplicateField.prototype.duplicate = function () {
    var _this = this,
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

  // Constructor to allow the label wrapping radios/checkboxes to be styled to reflect their status
  // Uses this document-level event:
  //
  //  radio:*radio-name-attribute*
  //
  //  ie. radio:address.address
  MarkSelected = function (elm) {
    var _this = this,
        controlType,
        isMonitored;

    this.$label = $(elm);
    this.$control = this.$label.find('input[type=radio], input[type=checkbox]');
    controlType = this.$control.attr('type');
    if (controlType === 'radio') {
      $(document).on('radio:' + this.$control.attr('name'), function (e, data) {
        _this.toggle(data);
      });
    }
    if (controlType === 'checkbox') {
      this.$control.on('click', function () {
        _this.toggle({
          'selectedControl' : _this.$control
        });
      });
    }
    if (this.$control.is(':checked')) {
      this.$label.addClass('selected');
    }
  };

  MarkSelected.prototype.toggle = function (eventData) {
    var isChecked = this.$control.is(':checked');

    if (isChecked) {
      this.$label.addClass('selected');
    } else {
      this.$label.removeClass('selected');
    }
  };

  // Constructor to give a textbox autocomplete functionality
  Autocomplete = function ($input) {
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
  Autocomplete.prototype.getMenuId = (function () {
    var id = 0;

    return function () {
      id = id + 1;
      return Autocomplete.menuIdPrefix + '-' + id;
    };
  }());
  Autocomplete.menuIdPrefix = 'typeahead-suggestions';
  Autocomplete.prototype.compiledStatusText = Mustache.compile('{{results}} {{#describe}}{{results}}{{/describe}} available, use up and down arrow keys to navigate.');
  Autocomplete.prototype.compiledTemplate = Mustache.compile('<p role="presentation" id="{{name}}">{{value}}</p>');
  Autocomplete.prototype.updateStatus = function (suggestions) {
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
  Autocomplete.prototype.events = {
    onInitialized : function (e) {
      var AutocompleteObj = this.getAutocompleteObj($(e.target)),
          menuId = this.getMenuId();

      this.$menu = this.$input.parent().find('.tt-dropdown-menu');
      this.$status = $('<span role="status" aria-live="polite" class="typeahead-status visuallyhidden" />');
      this.$status.insertAfter(this.$input);
      this.$input.attr({
        'aria-Autocomplete' : 'list',
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
          return AutocompleteObj.events.onEnter.call(AutocompleteObj, e); 
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
  Autocomplete.prototype.getAutocompleteObj = function ($input) {
    return autocompletes.existingObj($input);
  };

  // Object to control all autocompletes in the page
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
    createEvent : function (eventName) {
      var autocompletesObj = this;

      return {
        'trigger' : function (e) {
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
        this.cache[$input.attr('id')] = new Autocomplete($input);
      }
    },
    remove : function ($input) {
      var existing = this.existingId($input);

      $input.typeahead('destroy');
      if (existing) { delete this.cache[existing]; }
    }
  };

  PostcodeLookup = function (searchButton, inputName) {
    var inputId = inputName.replace(/\./g, "_"),
        _allowSubmission;

    _allowSubmission = function ($searchButton) {
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
    this.hasAddresses = ($('#'+inputId+'_uprn_select').length > 0);
    this.$waitMessage = $('<p id="wait-for-request">Finding address</p>');
    this.addressIsPrevious = (this.$searchInput.siblings('label').text().indexOf('previous address') !== -1);
    this.$searchButton.attr('aria-controls', this.$targetElement.attr('id'));
    this.$targetElement.attr({
      'aria-live' : 'polite',
      'role' : 'region'
    });
    this.fragment =
        '<label for="'+inputId+'_postcode" class="hidden">' +
           'Postcode' + 
        '</label>' +
        '<input type="hidden" id="input-address-postcode" name="'+inputName+'.postcode" value="{{postcode}}" class="text hidden">' +
        '<label for="'+inputId+'_uprn_select">{{selectLabel}}</label>' +
        '<div class="validation-wrapper">' +
          '<select id="'+inputId+'_uprn_select" name="'+inputName+'.uprn" class="lonely validate" ' +
          'data-validation-name="addressSelect" data-validation-type="field" data-validation-rules="nonEmpty"' +
          '>' +
          '<option value="">{{defaultOption}}</option>' +
          '{{#options}}' +
            '<option value="{{uprn}}">{{addressLine}}</option>' +
          '{{/options}}' +
          '</select>' +
        '</div>' +
        '<div class="optional-section" id="cant-find-address">' +
          '<h2>{{excuseToggle}}</h2>' +
          '<label for="'+inputId+'_manualAddress">{{excuseLabel}}</label>' +
          '<textarea name="'+inputName+'.manualAddress" id="'+inputId+'_manualAddress" class="small validate" maxlength=500  autocomplete="off" ' +
          'data-validation-name="addressExcuse" data-validation-type="field" data-validation-rules="nonEmpty"' +
          '></textarea>' +
        '</div>' +
        '<input type="hidden" id="possibleAddresses_postcode" name="possibleAddresses.postcode" value="{{postcode}}" />' +
        '<input type="hidden" id="possibleAddresses_jsonList" name="possibleAddresses.jsonList" value="{{resultsJSON}}" />' +
        '<button type="submit" id="continue" class="button next validation-submit" data-validation-sources="postcode address">Continue</button>';
    this.addressIsPrevious = (this.$searchInput.siblings('label').text().indexOf('previous address') !== -1);
    this.$searchButton.attr('aria-controls', this.$targetElement.attr('id'));
    this.$targetElement.attr({
      'aria-live' : 'polite',
      'role' : 'region'
    });

    if (!_allowSubmission.apply(this, [this.$searchButton])) {
      $('#continue').hide();
    }

    this.bindEvents();
    $(document).bind('toggle.open', function (e, data) {
      var $select;

      if (data.$toggle.text() === "I can't find my address in the list") {
        data.$toggle.remove();
        $select = $('#'+inputId+'_uprn_select');
        $select.siblings('label[for="'+inputId+'_uprn_select"]').remove();
        $select.remove();
        $('#'+inputId).focus();
      }
    });
  };
  PostcodeLookup.prototype.bindEvents = function () {
    var _this = this;

    this.$searchButton.on('click', function () {
      _this.getAddresses();
      return false;
    });
  };
  PostcodeLookup.prototype.onTimeout = function (xhrObj) {
    
  };
  PostcodeLookup.prototype.onError = function (status, errorStr) {

  };
  PostcodeLookup.prototype.addLookup = function (data, postcode) {
    var addressNum = data.addresses.length,
        defaultOption = (addressNum === 1) ? addressNum + ' address found' : addressNum + ' addresses found',
        htmlData = {
          'postcode' : postcode,
          'selectLabel' : 'Select your address',
          'defaultOption' : defaultOption,
          'options' : data.addresses,
          'excuseToggle' : 'I can\'t find my address in the list',
          'excuseLabel' : 'Enter your address',
          'resultsJSON' : data.rawJSON
        },
        $results;

    if (this.addressIsPrevious) {
      htmlData.selectLabel = 'Select your previous address';
      htmlData.excuseToggle = 'I can\'t find my previous address in the list'; 
      htmlData.excuseLabel = 'Enter your previous address';
    }
    // To be removed once all address pages shared the same HTML
    $results = $(Mustache.render(this.fragment, htmlData));
    if (!this.$targetElement.closest('.optional-section-core-content').length) {
      $results = $('<form action="' + window.location + '" method="POST"></form>').append($results);
    }
    this.$targetElement
      .append($results)
      .addClass('contains-addresses');
    new GOVUK.registerToVote.OptionalInformation(this.$targetElement.find('.optional-section'), 'optional-section');
    this.hasAddresses = true;
  };
  PostcodeLookup.prototype.getAddresses = function () {
    var _this = this,
        postcode = this.$searchInput.val(),
        URL = '/address/' + postcode.replace(/\s/g,''),
        $optionalInfo = this.$searchButton.closest('fieldset').find('div.help-content'),
        $addressSelect = $optionalInfo.siblings('select'),
        fieldValidationName = this.$searchButton.data("validationSources").split(' '),
        _notifyInvalidPostcode,
        _clearExistingResults,
        _setValidation;

    _notifyInvalidPostcode = function () {
      validation.makeInvalid([{
        'name' : 'postcode',
        'rule' : 'postcode',
        '$source' : _this.$searchInput
      }], _this.$searchButton);
    };

    _clearExistingResults = function () {
      if (_this.$targetElement.html() !== '') {
        validation.fields.remove('address');
        validation.fields.remove('addressSelect');
        validation.fields.remove('addressExcuse');
        _this.$targetElement.html('');
        $('#continue').remove();
      }
    };

    _setValidation = function () {
      _this.$targetElement.attr({
        'data-validation-name' : 'address',
        'data-validation-type' : 'fieldset',
        'data-validation-rules' : 'fieldOrExcuse',
        'data-validation-children' : 'addressSelect addressExcuse'
      });
      _this.$targetElement.find('.validate:not([type="hidden"])')
        .each(function (idx, elm) {
          validation.fields.add($(elm));
        });
      validation.fields.add(_this.$targetElement);
    };

    if (validation.validate(fieldValidationName, _this.$searchButton) === true) {
      this.$waitMessage.insertAfter(this.$searchButton);
      $.ajax({
        url : URL,
        dataType : 'json',
        timeout : 10000
      }).
      done(function (data, status, xhrObj) {
        data.rawJSON = xhrObj.responseText;
        if (!data.addresses.length) { 
          _notifyInvalidPostcode();
          return;
        }
        _clearExistingResults();
        _this.addLookup(data, postcode);
        _setValidation();
      }).
      fail(function (xhrObj, status, errorStr) {
        if (status === 'timeout' ) {
          _this.onTimeout(xhrObj);
        } else {
          _this.onError(status, errorStr);
        }
      }).
      always(function () {
        _this.$waitMessage.remove();
      });
    }
  };

  monitorRadios = (function () {
    var monitor;

    monitor = function (elm) {
      var groupName = elm.name,
          $fieldset = $(elm).closest('fieldset');

      if ($.inArray(groupName, monitor.radioGroups) === -1) {
        monitor.radioGroups.push(groupName);
        // older browsers can not detect change events on radio buttons attach a click also
        $fieldset.on('click change', function (e) {
          var target = e.target;
          if (target.type && target.type === 'radio') {
            $(document).trigger('radio:' + target.name,
              { 
                "selectedControl" : target,
                "fieldset" : this
              }
            );
          }
        });
      }
    };
    monitor.radioGroups = [];
    return monitor;
  }());

  GOVUK.registerToVote.ConditionalControl = ConditionalControl;
  GOVUK.registerToVote.DuplicateField = DuplicateField;
  GOVUK.registerToVote.MarkSelected = MarkSelected;
  GOVUK.registerToVote.Autocomplete = Autocomplete;
  GOVUK.registerToVote.autocompletes = autocompletes;
  GOVUK.registerToVote.monitorRadios = monitorRadios;
  GOVUK.registerToVote.PostcodeLookup = PostcodeLookup;
  GOVUK.registerToVote.BackButton = BackButton;
}.call(this));
