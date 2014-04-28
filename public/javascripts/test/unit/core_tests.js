describe("ToggleObj", function () {
  // ToggleObj doesn't have its own setup or bindEvent methods so stub them
  GOVUK.registerToVote.ToggleObj.prototype.setup = function () {};
  GOVUK.registerToVote.ToggleObj.prototype.bindEvents = function () {};

  describe("Constructor", function () {
    it("Should produce an instance with the correct interface", function () {
      var elm = document.createElement('div'),
          toggleObj = new GOVUK.registerToVote.ToggleObj(elm, 'optional-section');

      expect(toggleObj.setAccessibilityAPI).toBeDefined();
      expect(toggleObj.toggle).toBeDefined();
      expect(toggleObj.setInitialState).toBeDefined();
    });

    it("Should call the right methods when created", function () {
      var elm = document.createElement('div'),
          toggleObj;
      
      spyOn(GOVUK.registerToVote.ToggleObj.prototype, "setup");
      spyOn(GOVUK.registerToVote.ToggleObj.prototype, "bindEvents");

      toggleObj = new GOVUK.registerToVote.ToggleObj(elm, 'optional-section');
      expect(GOVUK.registerToVote.ToggleObj.prototype.setup).toHaveBeenCalled();
      expect(GOVUK.registerToVote.ToggleObj.prototype.bindEvents).toHaveBeenCalled();
    });
  });

  describe("setAccessibilityAPI method", function () {
    it("Should set the right ARIA attributes for hidden content", function () {
      var toggleMock = {
            "$content" : $("<div></div>"),
            "$toggle" : $("<a><span class='visuallyhidden'></span></a>"),
            "toggleActions" : { "hidden" : "Expand" }
          };

      GOVUK.registerToVote.ToggleObj.prototype.setAccessibilityAPI.call(toggleMock, 'hidden');
      expect(toggleMock.$content[0].getAttribute('aria-hidden')).toEqual('true');
      expect(toggleMock.$content[0].getAttribute('aria-expanded')).toEqual('false');
      expect(toggleMock.$toggle.find('span').text()).toEqual(toggleMock.toggleActions.hidden);
    });

    it("Should set the right ARIA attributes for shown content", function () {
      var toggleMock = {
            "$content" : $("<div></div>"),
            "$toggle" : $("<a><span class='visuallyhidden'></span></a>"),
            "toggleActions" : { "visible" : "Hide" }
          };

      GOVUK.registerToVote.ToggleObj.prototype.setAccessibilityAPI.call(toggleMock, 'visible');
      expect(toggleMock.$content[0].getAttribute('aria-hidden')).toEqual('false');
      expect(toggleMock.$content[0].getAttribute('aria-expanded')).toEqual('true');
      expect(toggleMock.$toggle.find('span').text()).toEqual(toggleMock.toggleActions.visible);
    });
  });

  describe("toggle method", function () {
    var toggleMock = {
          "$content" : $("<div></div>"),
          "$toggle" : $("<a></a>"),
          "toggleClass" : "expanded-section-open"
        },
        cachedTrigger = $.fn.trigger;

    beforeEach(function () {
      toggleMock.setAccessibilityAPI = function () {};
      spyOn(toggleMock, "setAccessibilityAPI");
    });

    afterEach(function () {
      $.fn.trigger = cachedTrigger;
    });

    it("Should set right attributes for elements involved and call the right methods when hidden", function () {
      spyOn($.fn, "trigger");

      toggleMock.$content.css('display', 'none');
      GOVUK.registerToVote.ToggleObj.prototype.toggle.call(toggleMock);

      expect(toggleMock.$content.hasClass(toggleMock.toggleClass)).toBe(true);
      expect(toggleMock.$toggle.hasClass('toggle-open')).toBe(true);
      expect(toggleMock.setAccessibilityAPI).toHaveBeenCalled();
      expect($.fn.trigger).toHaveBeenCalledWith('toggle.open', { '$toggle' : toggleMock.$toggle });
    });

    it("Should set right attributes for elements involved and call the right methods when shown", function () {
      spyOn($.fn, "trigger");

      toggleMock.$content.css('display', 'block');
      GOVUK.registerToVote.ToggleObj.prototype.toggle.call(toggleMock);

      expect(toggleMock.$content.hasClass(toggleMock.toggleClass)).toBe(false);
      expect(toggleMock.$toggle.hasClass('toggle-closed')).toBe(true);
      expect(toggleMock.setAccessibilityAPI).toHaveBeenCalled();
      expect($.fn.trigger).toHaveBeenCalled();
      expect($.fn.trigger).toHaveBeenCalledWith('toggle.closed', { '$toggle' : toggleMock.$toggle });
    });
  });
  describe("setInitialState method", function () {
    it("Should set the elements involved to an open state if the toggleClass is set on content", function () {
      var toggleObjMockOpen = {
            "toggleClass" : "expanded-section-open",
            "$content" : $("<div class='expanded-section-open'></div>"),
            "$toggle" : $("<a class='toggle-closed'></a>"),
            "setAccessibilityAPI" : function () {}
          };

      spyOn(toggleObjMockOpen, "setAccessibilityAPI");

      GOVUK.registerToVote.ToggleObj.prototype.setInitialState.call(toggleObjMockOpen);
      expect(toggleObjMockOpen.$toggle.hasClass("toggle-open")).toBe(true);
      expect(toggleObjMockOpen.setAccessibilityAPI).toHaveBeenCalled();
    });

    it("Should do nothing if the toggleClass is not set on content", function () {
      var toggleObjMockClosed = {
            "toggleClass" : "expanded-section-open",
            "$content" : $("<div></div>"),
            "$toggle" : $("<a class='toggle-closed'></a>"),
            "setAccessibilityAPI" : function () {}
          };

      spyOn(toggleObjMockClosed, "setAccessibilityAPI");
      GOVUK.registerToVote.ToggleObj.prototype.setInitialState.call(toggleObjMockClosed);

      expect(toggleObjMockClosed.$toggle.hasClass("toggle-open")).toBe(false);
      expect(toggleObjMockClosed.setAccessibilityAPI).not.toHaveBeenCalled();
    });
  });
});

describe("OptionalInformation", function () {
  describe("Constructor", function () {
    var elm;

    beforeEach(function () {
      elm = document.createElement('div');
    });

    it("Should produce an instance with the correct interface", function () {
      var optionalSection = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section');

      expect(optionalSection.setup).toBeDefined();
      expect(optionalSection.bindEvents).toBeDefined();
      expect(optionalSection.setAccessibilityAPI).toBeDefined();
      expect(optionalSection.toggle).toBeDefined();
      expect(optionalSection.setInitialState).toBeDefined();
    });

    it("Should call the ToggleObj constructor with the same arguments sent to the OptionalInformation constructor", function () {
      var optionalSection;

      spyOn(GOVUK.registerToVote, "ToggleObj");
      optionalSection = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section');
      expect(GOVUK.registerToVote.ToggleObj).toHaveBeenCalledWith(elm, 'optional-section');
    });
  });

  describe("setup", function () {
    var elm,
        toggleLink = '<a href="#" class="toggle toggle-closed"><span class="visuallyhidden">Show</span> Help <span class="visuallyhidden">section</span></a>';

    beforeEach(function () {
      elm = $(
            "<div class='optional-section'>" +
              "<h2>Help</h2>" +
              "</p>Some content</p>" +
            "</div>"
            );

      $(document.body).append(elm);
    });

    afterEach(function () {
      $(elm).remove();
    });

    it("Should insert a toggle link before the optional section", function () {
      var optionalSection;

      expect($(elm).prev('a.toggle').length).toEqual(0);
      optionalSection = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section')
      expect($(elm).prev('a.toggle').length).toEqual(1);
    });

    it("Should visually hide the section's initial heading", function () {
      var optionalSection;

      optionalSection = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section')
      expect($(elm).find('h2').hasClass('visuallyhidden')).toBe(true);
    });

    it("Should call the setInitialState method", function () {
      var optionalSection;

      spyOn(GOVUK.registerToVote.OptionalInformation.prototype, "setInitialState");

      optionalSection = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section')
      expect(GOVUK.registerToVote.OptionalInformation.prototype.setInitialState).toHaveBeenCalled();
    });
  });

  describe("bindEvents", function () {
    var elm;

    beforeEach(function () {
      elm = document.createElement('div');
    });

    it("Should add a click event to the toggle link that calls the toggle method", function () {
      var optionalSection,
          evtCallback;

      spyOn(GOVUK.registerToVote.OptionalInformation.prototype, "toggle");
      spyOn($.fn, "on").and.callFake(function (evt, callback) {
        evtCallback = callback;
      });

      optionalSection = new GOVUK.registerToVote.OptionalInformation(elm, 'optional-section')
      expect($.fn.on).toHaveBeenCalled();
      evtCallback();
      expect(GOVUK.registerToVote.OptionalInformation.prototype.toggle).toHaveBeenCalled();
    });
  });
});
