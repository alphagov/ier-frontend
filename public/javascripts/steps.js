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
    this.$toggle = $('#' + this.$content.data('condition'));
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
    if (selectedRadio !== undefined) {
      if (this.$toggle.attr('id') !== selectedRadio.id) {
        this.$content.hide();
      }
    } else {
      if (this.$toggle.is(":checked")) {
        this.$content.show();
      } else {
        this.$content.hide();
      }
    }
  };

  duplicateField = function (control) {
    var fieldId,
        inst = this;

    this.$control = $(control);
    fieldId = this.$control.data('field');
    this.$field = $('#' + fieldId);
    this.$label = this.$control.closest('fieldset').find('label[for=' + fieldId +']');
    this.$control.closest('.optional-section').on('click', function (e) {
      if (e.target.className !== inst.$control[0].className) {
        return false;
      }
      inst.duplicate();
      return false;
    });
  };
  duplicateField.prototype.removeDuplicate = function (id) {
    var $input = $('#' + id),
        $label = $input.siblings('label[for=' + id + ']');

    $input.next('a.duplicate-control').remove();
    $label.next('a.remove-field').remove();
    $input.remove();
    $label.remove();
  };
  duplicateField.prototype.duplicate = function () {
    var inst = this,
        newField = document.createDocumentFragment(),
        country = this.$control.closest('.optional-section').find('input').length + 1,
        newId = this.$field.attr('id') + '-' + country,
        $newLabel = this.$label.clone().text('Country ' + country).attr('for', newId),
        $newInput = this.$field.clone().attr('id', newId),
        $newControl = this.$control.clone().attr('for', newId),
        $removalControl = $('<a href="#" class="remove-field">Remove<span class="visuallyhidden"> Country' + country + '</span></a>');

    newField.appendChild($newLabel[0]);
    newField.appendChild($removalControl[0]);
    newField.appendChild($newInput[0]);
    newField.appendChild($newControl[0]);
    this.$label[0].parentNode.appendChild(newField.cloneNode(true));
    $('#' + newId).prev('a.remove-field').on('click', function (e) {
      inst.removeDuplicate(newId);
      return false;
    });
  };

  markSelected = function (elm) {
    var inst = this;

    this.$label = $(elm);
    this.$control = $(document.getElementById(this.$label.attr('for')));
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
    this.fragment = [
      '<label for="input-address-list">Select your address</label>' +
      '<select id="input-address-list" name="address.address" class="lonely">' +
        '<option value="">Please select...</option>',
      '</select>' +
      '<div class="help-content">' +
        '<h2>My address is not listed</h2>' +
        '<label id="input-address-text">Enter your address</label>' +
        '<textarea id="input-address-text" name="address.address" class="small"></textarea>' +
      '</div>' +
      '<div class="validation-message"></div>' +
      '<input data-next="step-name" type="submit" class="button next" value="Continue" />'
    ];
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
    var resultStr = this.fragment[0];

    $(data.addresses).each(function (idx, address) {
     resultStr += '<option>' + address + '</option>'
    });
    resultStr += this.fragment[1];
    $(resultStr).insertAfter(this.$searchButton);
    new GOVUK.registerToVote.optionalInformation(this.$searchButton.closest('fieldset').find('.help-content')[0]);
  };
  postcodeLookup.prototype.getAddresses = function () {
    var inst = this,
        postcode = this.$searchInput.val(),
        URL = '/address/' + postcode;

    if (postcode === "") { 
      this.onEmpty();
    } else {
      postcode = postcode.replace(/\s/g,'');
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
          $control = $label.find('input#' + $label.attr('for')),
          controlName = $control.attr('name');

      if ($control.attr('type') === 'radio') {
        // set up event monitoring for radios sharing that name
        GOVUK.registerToVote.monitorRadios(elm);
      }
      new GOVUK.registerToVote.markSelected(elm);
    });
    $('.search').each(function (idx, elm) {
      new GOVUK.registerToVote.postcodeLookup(elm);
    });
  });
}.call(this));

$('body').on('change', 'input[type="checkbox"]', function(e){
    var $this = $(this);
    $this.closest('label').toggleClass('selected', $this.is(':checked'));
});

$('.back').on('click', function(e){
    e.preventDefault();
    window.history.back();
});

$('#step-nationality #no-nationality-link').on('click', function(e) {
    e.preventDefault();
    show($('#optional-section-no-nationality'));
});

$('#step-previous-name #input-name-change-yes').on('click', function(e) {
    show($('#optionalSectionPreviousName'));
});

$('#step-previous-name #input-name-change-no').on('click', function(e) {
    hide($('#optionalSectionPreviousName'));
});

$('#step-address #find-address').on('click', function(e) {
    e.preventDefault();
    addressLookup($('#step-address'));
});

$('#step-previous-address #input-previous-address_false').on('click', function(e) {
    hide($('#optional-section-previous-address'));
});

$('#step-previous-address #input-previous-address_true').on('click', function(e) {
    show($('#optional-section-previous-address'));
});

$('#step-previous-address #find-previous-address').on('click', function(e) {
    e.preventDefault();
    addressLookup($('#step-previous-address'));
});

$('#step-other-address #input-other-address-no').on('click', function(e) {
    hide($('#optional-section-other-address'));
});

$('#step-other-address #input-other-address-yes').on('click', function(e) {
    show($('#optional-section-other-address'));
});

$('#step-other-address #find-other-address').on('click', function(e) {
    e.preventDefault();
    addressLookup($('#step-other-address'));
});

$('#step-contact #contact-post').on('click', function(e) {
    show($('#optional-section-contact-post'));
    hide($('#optional-section-contact-phone'));
    hide($('#optional-section-contact-email'));
    hide($('#optional-section-contact-text'));
})

$('#step-contact #contact-phone').on('click', function(e) {
    show($('#optional-section-contact-phone'));
    hide($('#optional-section-contact-email'));
    hide($('#optional-section-contact-text'));
    hide($('#optional-section-contact-post'));
})

$('#step-contact #contact-email').on('click', function(e) {
    hide($('#optional-section-contact-phone'));
    show($('#optional-section-contact-email'));
    hide($('#optional-section-contact-text'));
    hide($('#optional-section-contact-post'));
})

$('#step-contact #contact-text').on('click', function(e) {
    hide($('#optional-section-contact-phone'));
    hide($('#optional-section-contact-email'));
    show($('#optional-section-contact-text'));
    hide($('#optional-section-contact-post'));
})

function show($block) {
    $block.addClass('visible');
    $block.removeClass('hidden');
}

function hide($block) {
    $block.addClass('hidden');
    $block.removeClass('visible');
}

function addressLookup($step) {

    var postcode = $step.find('.postcode').val();
    if(!postcode || postcode == ''){
        console.log('no postcode entered - cannot run addressLookup');
        return;
    }

    $.ajax({
        url : '/address/' + postcode.replace(/\s/g,'')
    }).done(function(data){

            $step.find('.address-step-2').show();
            $step.find('.postcode-search-results').show();
            $step.find('.type-in-address').hide();

            var addressesHTML = '<option>Select your address</option><option>';
            var addresses = [];

            if (data.addresses[0].addressLine){
                for (var i = 0; i < data.addresses.length; i++){
                    addresses.push(data.addresses[i].addressLine);
                }
            } else {
                addresses = data.addresses;
            }

            addressesHTML += addresses.join('</option><option>') + '</option>';
            $step.find('.addressList').html(addressesHTML);
        });
}
