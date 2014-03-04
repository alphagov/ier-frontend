/* Scripts used throughout IER */
window.GOVUK = window.GOVUK || {};
window.GOVUK.registerToVote = {};

(function () {
  "use strict";

  var root = this,
      $ = root.jQuery,
      GOVUK = root.GOVUK,
      ToggleObj,
      OptionalInformation,
      $searchFocus;

  // Base contructor for content associated with a 'toggle' element that controls its visiblity
  ToggleObj = function (elm, toggleClass) {
    if (elm) {
      this.$content = $(elm);
      this.toggleActions = {
        'hidden': 'Expand',
        'visible': 'Hide'
      };
      this.toggleClass = toggleClass + '-open';
      this.setup();
      this.bindEvents();
    }
  };
  ToggleObj.prototype.setAccessibilityAPI = function (state) {
    if (state === 'hidden') {
      this.$content.attr('aria-hidden', true);
      this.$content.attr('aria-expanded', false);
      this.$toggle.find('span.visuallyhidden').eq(0).text(this.toggleActions.hidden);
    } else {
      this.$content.attr('aria-hidden', false);
      this.$content.attr('aria-expanded', true);
      this.$toggle.find('span.visuallyhidden').eq(0).text(this.toggleActions.visible);
    }
  };
  ToggleObj.prototype.toggle = function () {
    if (this.$content.css("display") === "none") {
      this.$content.addClass(this.toggleClass);
      this.setAccessibilityAPI('visible');
      this.$toggle.removeClass("toggle-closed");
      this.$toggle.addClass("toggle-open");
      $(document).trigger('toggle.open', { '$toggle' : this.$toggle });
    } else {
      this.$content.removeClass(this.toggleClass);
      this.setAccessibilityAPI('hidden');
      this.$toggle.removeClass("toggle-open");
      this.$toggle.addClass("toggle-closed");
      $(document).trigger('toggle.closed', { '$toggle' : this.$toggle });
    }
  };
  ToggleObj.prototype.setInitialState = function () {
    if (this.$content.hasClass(this.toggleClass)) {
      this.$toggle.addClass('toggle-open');
      this.$toggle.removeClass('toggle-closed');
      this.setAccessibilityAPI('visible');
    }
  };

  // Constructor for controlling the display of content that is additional to the main block
  // Uses ToggleObj for its base prototype
  OptionalInformation = function () {
    ToggleObj.apply(this, arguments);
  };
  $.extend(OptionalInformation.prototype, new ToggleObj());
  OptionalInformation.prototype.setup = function () {
    var contentId = this.$content.attr('id'),
        headingText;

    this.$heading = this.$content.find("h1,h2,h3,h4").first();
    this.$toggle = $('<a href="#" class="toggle toggle-closed"><span class="visuallyhidden">Show</span> ' + this.$heading.text() + ' <span class="visuallyhidden">section</span></a>');
    if (contentId) { this.$toggle.attr('aria-controls', contentId); }
    this.$toggle.insertBefore(this.$content);
    this.$heading.addClass("visuallyhidden");
    this.setInitialState();
  };
  OptionalInformation.prototype.bindEvents = function () {
    var _this = this;

    this.$toggle.on('click', function () {
      _this.toggle();
      return false;
    });
  };

  GOVUK.registerToVote.ToggleObj = ToggleObj;
  GOVUK.registerToVote.OptionalInformation = OptionalInformation;

  // header search toggle
  $('.js-header-toggle').on('click', function(e) {
    e.preventDefault();
    $($(e.target).attr('href')).toggleClass('js-visible');
    $(this).toggleClass('js-hidden');
  });

  $searchFocus = $('.js-search-focus');
  $searchFocus.each(function(i, el){
    if($(el).val() !== ''){
      $(el).addClass('focus');
    }
  });
  $searchFocus.on('focus', function(e){
    $(e.target).addClass('focus');
  });
  $searchFocus.on('blur', function(e){
    if($(e.target).val() === ''){
      $(e.target).removeClass('focus');
    }
  });

  $('.help-content').each(function (idx, elm) {
    new GOVUK.registerToVote.OptionalInformation(elm, 'help-content');
  });
}.call(this));
