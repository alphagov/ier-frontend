GOVUK= {};
(function () {
  "use strict"

  var root = this,
      $ = root.jQuery,
      GOVUK = root.GOVUK,
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

  GOVUK.registerToVote = {};
  GOVUK.registerToVote.BackButton = BackButton;
}.call(this));
