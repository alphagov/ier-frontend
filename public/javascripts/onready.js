(function () {
  "use strict";

  var root = this,
      $ = root.jQuery,
      GOVUK = root.GOVUK;

  $('.help-content').each(function (idx, elm) {
    new GOVUK.registerToVote.OptionalInformation(elm);
  });
  $('.optional-section, .optional-section-binary').each(function (idx, elm) {
    if ($(elm).data('condition') !== undefined) {
      new GOVUK.registerToVote.ConditionalControl(elm);
    } else {
      new GOVUK.registerToVote.OptionalInformation(elm);
    }
  });
  $('.duplicate-control-initial').each(function (idx, elm) {
    var labelOpts = {
      txt : 'country',
      className : 'country-label'
    };
    new GOVUK.registerToVote.DuplicateField(elm, 'added-country', labelOpts);
  });
  $('.selectable').each(function (idx, elm) {
    var $label = $(elm),
        $control = $label.find('input[type=radio], input[type=checkbox]'),
        controlName = $control.attr('name');

    if ($control.attr('type') === 'radio') {
      // set up event monitoring for radios sharing that name
      GOVUK.registerToVote.monitorRadios($control[0]);
    }
    new GOVUK.registerToVote.MarkSelected(elm);
    $control.on('focus', function () {
      $(this).parent('label').addClass('selectable-focus');
    });
    $control.on('blur', function () {
      $(this).parent('label').removeClass('selectable-focus');
    });
  });
  $('#find-address').each(function (idx, elm) {
    new GOVUK.registerToVote.PostcodeLookup(elm, "address");
  });
  $('#find-previous-address').each(function (idx, elm) {
    new GOVUK.registerToVote.PostcodeLookup(elm, "previousAddress.previousAddress");
  });
  $('.country-autocomplete').each(function (idx, elm) {
    GOVUK.registerToVote.autocompletes.add($(elm));
  });

  // Functionality bound to elements added through a content update

  // Bind all autocomplete events
  $.each(['initialized', 'opened', 'closed', 'movedto', 'updated'], function (idx, evt) {
    $(document).bind('typeahead:' + evt, function () {
      var autocompleteEvent = GOVUK.registerToVote.autocompletes.trigger(evt);

      autocompleteEvent.andSend.apply(GOVUK.registerToVote.autocompletes, arguments);
    });
  });
  $(document).bind('contentUpdate', function (e, data) {
    var context = data.context;

    $('.country-autocomplete', context).each(function (idx, elm) {
      GOVUK.registerToVote.autocompletes.add($(elm));
    });
  });
  GOVUK.registerToVote.validation.init();
}.call(this));
