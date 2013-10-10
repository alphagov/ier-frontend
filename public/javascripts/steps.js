window.GOVUK = window.GOVUK || {};

(function () {
  "use strict"
  var root = this,
      $ = root.jQuery;

  var toggleObj,
      optionalInformation,
      conditionalControl,
      duplicateField,
      markSelected,
      monitorRadios,
      postcodeLookup;

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
    } else {
      this.$content.hide();
      this.$toggle.removeClass("toggle-open");
      this.$toggle.addClass("toggle-closed");
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
        if (isPostcodeLookup && !hasAddresses) {
          $('#continue').hide();
        }
      } else {
        this.$content.hide();
        $('#continue').show();
      }
    }
  };

  duplicateField = function (control) {
    var fieldId,
        inst = this;

    this.$control = $(control);
    fieldId = this.$control.data('field');
    this.$field = $(document.getElementById(fieldId));
    this.$label = this.$field.prev('label');
    this.$control.closest('.optional-section').on('click', function (e) {
      if (e.target.className !== inst.$control[0].className) {
        return false;
      }
      inst.duplicate();
      return false;
    });
  };
  duplicateField.prototype.removeDuplicate = function (id) {
    $(document.getElementById(id)).closest('.added-country').remove();
  };
  duplicateField.prototype.duplicate = function () {
    var inst = this,
        newField = document.createDocumentFragment(),
        country = this.$control.closest('.optional-section').find('input').length + 1,
        newId = this.$field.attr('id') + '-' + country,
        $newLabel = this.$label.clone().text('Country ' + country).attr('for', newId),
        $newInput = this.$field.clone().attr('id', newId),
        $newControl = this.$control.clone().attr('for', newId),
        $removalControl = $('<a href="#" class="remove-field">Remove<span class="visuallyhidden"> Country' + country + '</span></a>'),
        wrapperDiv = document.createElement('div');

    wrapperDiv.className = 'added-country';
    newField.appendChild($newLabel[0]);
    newField.appendChild($removalControl[0]);
    newField.appendChild($newInput[0]);
    newField.appendChild($newControl[0]);
    wrapperDiv.appendChild(newField.cloneNode(true));
    this.$label[0].parentNode.appendChild(wrapperDiv);
    $(document.getElementById(newId)).prev('a.remove-field').on('click', function (e) {
      inst.removeDuplicate(newId);
      return false;
    });
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

  postcodeLookup = function (searchButton) {
    this.$searchButton = $(searchButton);
    this.$searchInput = this.$searchButton.closest('fieldset').find('input.postcode');
    this.$waitMessage = $('<p id="wait-for-request">Finding address</p>');
    this.fragment = {
      'label' : '<label for="input-address-list">Select your address</label>',
      'select' : [
        '<select id="input-address-list" name="address.address" class="lonely">' +
          '<option value="">Please select...</option>',
        '</select>'
      ],
      'help' : '<div class="help-content">' +
                  '<h2>My address is not listed</h2>' +
                  '<label id="input-address-text">Enter your address</label>' +
                  '<textarea id="input-address-text" name="address.address" class="small"></textarea>' +
                '</div>'
    };
    this.bindEvents();
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

  };
  postcodeLookup.prototype.addLookup = function (data) {
    var resultStr,
        $existingAddresses = this.$searchButton.closest('fieldset').find('select');

    if ($existingAddresses.length) {
      resultStr = this.fragment.select[0];
    } else {
      resultStr = this.fragment.label + this.fragment.select[0];
    }
    $(data.addresses).each(function (idx, entry) {
     resultStr += '<option>' + entry.addressLine + '</option>'
    });
    if ($existingAddresses.length) {
      resultStr += this.fragment.select[1];
      $existingAddresses.replaceWith(resultStr);
    } else {
      resultStr += this.fragment.select[1] + this.fragment.help;
      $(resultStr).insertAfter(this.$searchButton);
      new GOVUK.registerToVote.optionalInformation(this.$searchButton.closest('fieldset').find('.help-content')[0]);
    }
    $('#continue').show();
  };
  postcodeLookup.prototype.getAddresses = function () {
    var inst = this,
        postcode = this.$searchInput.val(),
        URL = '/address/' + postcode.replace(/\s/g,'');

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

  GOVUK.registerToVote = {
    "optionalInformation" : optionalInformation,
    "conditionalControl" : conditionalControl,
    "duplicateField" : duplicateField,
    "markSelected" : markSelected,
    "monitorRadios" : monitorRadios,
    "postcodeLookup" : postcodeLookup
  };

  $(document).on('ready', function () { 
    $('.help-content').each(function (idx, elm) {
      new GOVUK.registerToVote.optionalInformation(elm);
    });
    $('.optional-section').each(function (idx, elm) {
      if ($(elm).data('condition') !== undefined) {
        new GOVUK.registerToVote.conditionalControl(elm);
      } else {
        new GOVUK.registerToVote.optionalInformation(elm);
      }
    });
    $('.duplicate-control').each(function (idx, elm) {
      new GOVUK.registerToVote.duplicateField(elm);
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
    $('.search').each(function (idx, elm) {
      new GOVUK.registerToVote.postcodeLookup(elm);
    });
  });
}.call(this));
