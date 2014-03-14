describe("BackButton", function () {

  describe("Creating an instance", function () {
    it("Should have the right interface", function () {
      var backButton = new GOVUK.registerToVote.BackButton();

      expect(backButton.setup).toBeDefined();
      expect(backButton.bindEvents).toBeDefined();
    });

    it("Should call the right methods when the instance is produced", function () {
      var $container = $('<div><header></header></div>'),
          $header = $container.find('header'),
          backButton;

      spyOn(GOVUK.registerToVote.BackButton.prototype, 'setup');
      spyOn(GOVUK.registerToVote.BackButton.prototype, 'bindEvents');
      backButton = new GOVUK.registerToVote.BackButton($header);

      expect(GOVUK.registerToVote.BackButton.prototype.setup).toHaveBeenCalled();
      expect(GOVUK.registerToVote.BackButton.prototype.bindEvents).toHaveBeenCalled();
    });
  });

  describe("Setup method", function () {
    it("Should add the backlink element correctly", function () {
      var $container = $('<div><header></header></div>'),
          $header = $container.find('header'),
          backButtonMock = { '$header' : $header },
          $backLink,
          $childElements,
          backLinkIndex,
          headerIndex;
      
      GOVUK.registerToVote.BackButton.prototype.setup.call(backButtonMock);
      $backLink = $container.find('a.back-to-previous')[0];
      $childElements = $container.children();
      backLinkIndex = $childElements.index($backLink);
      headerIndex = $childElements.index($header);
      expect(backLinkIndex).toBe(headerIndex - 1);
    });
  });

  describe("BindEvents method", function () {
    it("Should add a jQuery click event to the link element", function () {
      var backButtonMock = { '$link' : $('<a></a>') },
          parameters;
      
      spyOn($.fn, "on").and.callFake(function() {
        parameters = arguments;
      });

      GOVUK.registerToVote.BackButton.prototype.bindEvents.call(backButtonMock);
      expect(parameters[0]).toBe('click');
      expect(typeof parameters[1]).toBe('function');
    });

    it("Should call the history.back method when a click is registered", function () {
      var backButtonMock = { '$link' : $('<a></a>') },
          parameters;
      
      spyOn(window.history, "back");
      GOVUK.registerToVote.BackButton.prototype.bindEvents.call(backButtonMock);
      backButtonMock.$link.trigger('click');
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});

describe("ToggleObj", function () {
  // ToggleObj doesn't have its own setup or bindEvent methods so stub them
  GOVUK.registerToVote.ToggleObj.prototype.setup = function () {};
  GOVUK.registerToVote.ToggleObj.prototype.bindEvents = function () {};

  describe("Creating an instance", function () {
    it("Should have the right interface", function () {
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

describe("ConditionalControl", function () {
  describe("Creating an instance", function () {
    var elm;
    beforeEach(function () {
     elm = $('<div></div>');
    });

    it("Should have the right interface", function () {
      var control = new GOVUK.registerToVote.ConditionalControl(elm, 'optional-section');

      expect(control.setup).toBeDefined();
      expect(control.bindEvents).toBeDefined();
      expect(control.adjustVerticalSpace).toBeDefined();
      expect(control.toggle).toBeDefined();
    });

    it("Should call the ToggleObj constructor", function () {
      var cachedToggleObj = GOVUK.registerToVote.ToggleObj,
          control;

      spyOn(GOVUK.registerToVote, "ToggleObj");
      control = new GOVUK.registerToVote.ConditionalControl(elm, 'optional-section');
      expect(GOVUK.registerToVote.ToggleObj).toHaveBeenCalled();

      GOVUK.registerToVote.ToggleObj = cachedToggleObj;
    });
  });

  describe("setup method", function () {
    var cachedGetElementById = document.getElementById,
        conditionalControlMock,
        stubGetElementById;

    stubGetElementById = function (handleElm) { 
      document.getElementById = function (id) {
        var elm = document.createElement("input");

        elm.type = "checkbox";
        elm.id = id;
        if (handleElm) { handleElm(elm); }
        return elm;
      };
    };

    beforeEach(function () {
      conditionalControlMock = {
        "$content" : $("<div id='mock1' data-condition='test-control'></div>"),
        "adjustVerticalSpace" : function () {},
        "toggle" : function () {}
      };
    });

    afterEach(function () {
      document.getElementById = cachedGetElementById;
    });

    it("Should get the toggle control id from an attribute value on the content", function () {
      var sentId,
          storeSentId;

      storeSentId = function (elm) {
        sentId = elm.id 
      };
      stubGetElementById(storeSentId);
      GOVUK.registerToVote.ConditionalControl.prototype.setup.call(conditionalControlMock);

      expect(sentId).toBe('test-control');
    });

    it("Should add an 'aria-controls' attribute linking the toggle control to the content", function () {
      stubGetElementById();
      GOVUK.registerToVote.ConditionalControl.prototype.setup.call(conditionalControlMock);
      expect(conditionalControlMock.$toggle.attr('aria-controls')).toBe('mock1');
    });

    it("Should call adjustVerticalSpace method", function () {
      stubGetElementById();
      spyOn(conditionalControlMock, "adjustVerticalSpace");
      GOVUK.registerToVote.ConditionalControl.prototype.setup.call(conditionalControlMock);
      expect(conditionalControlMock.adjustVerticalSpace).toHaveBeenCalled();
    });

    it("should call the toggle method if control is checked", function () {
      var controlElm,
          setElmAttribute;

      setElmAttribute = function (elm) {
        elm.checked = true;
      };
      stubGetElementById(setElmAttribute);
      spyOn(conditionalControlMock, "toggle");
      GOVUK.registerToVote.ConditionalControl.prototype.setup.call(conditionalControlMock);
      expect(conditionalControlMock.toggle).toHaveBeenCalled();
    });
  });

  describe("bindEvents method", function () {
    var cachedOn = $.fn.on,
        conditionalControlMock;

    beforeEach(function () {
      conditionalControlMock = {
        "$content" : $("<div id='mock1' data-condition='test-control'></div>"),
        "$toggle" : $("<input type='radio' name='mock1' />"),
        "adjustVerticalSpace" : function () {},
        "toggle" : function () {}
      };
    });

    afterEach(function () {
      $.fn.on = cachedOn;
    });

    it("Should add a change event to the toggle control", function () {
      var evtWasBound = null;

      $.fn.on = function (evt, callback) {
        if (evtWasBound === null) {
          evtWasBound = ((evt === "change") && (this === conditionalControlMock.$toggle)); 
        }
      };
 
      GOVUK.registerToVote.ConditionalControl.prototype.bindEvents.call(conditionalControlMock);
      expect(evtWasBound).toBe(true);
    });

    it("Should set a change event to call the toggle method", function () {
      var onChange;

      spyOn(conditionalControlMock, "toggle");
      $.fn.on = function (evt, callback) {
        if ((evt === "change") && (this === conditionalControlMock.$toggle)) {
          onChange = callback;
        }
      };

      GOVUK.registerToVote.ConditionalControl.prototype.bindEvents.call(conditionalControlMock);
      onChange();      
      expect(conditionalControlMock.toggle).toHaveBeenCalled();
    });

    it("Should add a custom event, identified by the radio name, to the document", function () {
      var customName;

      $.fn.on = function (evt, callback) {
        if (this[0] === document) {
          customName = evt;
        }
      }; 

      GOVUK.registerToVote.ConditionalControl.prototype.bindEvents.call(conditionalControlMock);
      expect(customName).toBe("radio:" + conditionalControlMock.$toggle[0].name);
    });

    it("Custom event should call the toggle method", function () {
      var onCustom,
          evtStub = {},
          evtDataStub = { 'selectedRadio' : conditionalControlMock.$toggle };

      spyOn(conditionalControlMock, "toggle");
      // stub the event binding and store the callback
      $.fn.on = function (evt, callback) {
        if (this[0] === document) {
          onCustom = callback;
        }
      };

      GOVUK.registerToVote.ConditionalControl.prototype.bindEvents.call(conditionalControlMock);
      // call the callback to simulate the event
      onCustom(evtStub, evtDataStub);
      expect(conditionalControlMock.toggle).toHaveBeenCalledWith(conditionalControlMock.$toggle);
    });
  });

  describe("adjustVerticalSpace method", function () {
    var conditionalControlMock;

    beforeEach(function () {
      var $container = $(
        "<div>" +
          "<input type='radio' name='mock1' />" +
          "<div id='mock1' data-condition='test-control'></div>" +
        "</div>"
      );

      conditionalControlMock = {
        "$content" : $container.find("input"),
        "$toggle" : $container.find("div"),
        "controlAndContentAreSiblings" : true,
        "marginWhenContentIs" : {
          "hidden" : "20px",
          "shown" : "0px",
        }
      };
    });
    
    it("Should set the margin-bottom of the content to that of the toggle control when the toggle is hidden", function () {
      var expectedMarginBottom = conditionalControlMock.marginWhenContentIs.hidden;

      GOVUK.registerToVote.ConditionalControl.prototype.adjustVerticalSpace.call(conditionalControlMock, 'hidden');
      expect(conditionalControlMock.$toggle.css("margin-bottom")).toBe(expectedMarginBottom);
    });
    
    it("Should remove the margin-bottom of the content when it is visible", function () {
      var expectedMarginBottom = conditionalControlMock.marginWhenContentIs.shown;

      GOVUK.registerToVote.ConditionalControl.prototype.adjustVerticalSpace.call(conditionalControlMock, 'visible');
      expect(conditionalControlMock.$toggle.css("margin-bottom")).toBe(expectedMarginBottom);
    });
    
    it("Should not change the margin-bottom of the content if it and the toggle control are not siblings", function () {
      var expectedMarginBottom = conditionalControlMock.marginWhenContentIs.hidden;

      conditionalControlMock.controlAndContentAreSiblings = false;
      GOVUK.registerToVote.ConditionalControl.prototype.adjustVerticalSpace.call(conditionalControlMock, 'hidden');
      expect(conditionalControlMock.$toggle.css("margin-bottom")).toBe('');
    });
  });

  describe("toggle method", function () {
    var conditionalControlMock,
        cachedJquery = $.fn;

    beforeEach(function () {
      conditionalControlMock = {
        "$content" : $("<div></div>"),
        "$toggle" : $("<input type='radio' id='mock1'>"),
        "toggleClass" : 'expanded-section-open',
        "adjustVerticalSpace" : function () {}
      };
      $(document.body)
        .append("<button id='continue' />");
    });

    afterEach(function () {
      $.fn = cachedJquery;
      $("#continue").remove();
    });

    it("Should hide the content if called from a radio being selected that is not the toggle control", function () {
      var selectedRadio = $("<input type='radio' id='mock2' />")[0],
          showCalledWith;

      spyOn($.fn, "show")
        .and.callFake(
          function () {
            showCalledWith = this.selector;
          }
      );
      spyOn(conditionalControlMock, "adjustVerticalSpace");
      conditionalControlMock.$content.addClass(conditionalControlMock.toggleClass);

      // if a radio input is sent in as the 1st parameter, the method has been called from a radio selection
      GOVUK.registerToVote.ConditionalControl.prototype.toggle.call(conditionalControlMock, selectedRadio); 
      // hiding content
      expect(conditionalControlMock.adjustVerticalSpace).toHaveBeenCalledWith("hidden");
      expect($.fn.show).toHaveBeenCalled();
      expect(showCalledWith).toBe("#continue");
      expect(conditionalControlMock.$content.hasClass(conditionalControlMock.toggleClass)).toBe(false);
      expect(conditionalControlMock.$content.attr('aria-hidden')).toBe('true');
      expect(conditionalControlMock.$content.attr('aria-expanded')).toBe('false');
    });

    it("Should show the content if the toggle control is checked", function () {
      spyOn(conditionalControlMock, "adjustVerticalSpace");
      spyOn($.fn, "trigger");
      conditionalControlMock.$toggle = $("<input type='radio' id='mock2' checked='checked' />");
      conditionalControlMock.$content.addClass(conditionalControlMock.toggleClass);

      GOVUK.registerToVote.ConditionalControl.prototype.toggle.call(conditionalControlMock); 
      // showing content
      expect(conditionalControlMock.adjustVerticalSpace).toHaveBeenCalledWith("shown");
      expect($.fn.trigger).toHaveBeenCalledWith("toggle.open", { "$toggle" : conditionalControlMock.$toggle });
      expect(conditionalControlMock.$content.hasClass(conditionalControlMock.toggleClass)).toBe(true);
      expect(conditionalControlMock.$content.attr('aria-hidden')).toBe('false');
      expect(conditionalControlMock.$content.attr('aria-expanded')).toBe('true');
    });

    it("Should hide the content when toggle is unchecked", function () {
      spyOn($.fn, "trigger");
      spyOn(conditionalControlMock, "adjustVerticalSpace");
      conditionalControlMock.$content.addClass(conditionalControlMock.toggleClass);
      conditionalControlMock.$toggle = $("<input type='radio' id='mock2' />");

      GOVUK.registerToVote.ConditionalControl.prototype.toggle.call(conditionalControlMock); 
      // hiding content
      expect(conditionalControlMock.adjustVerticalSpace).toHaveBeenCalledWith("hidden");
      expect($.fn.trigger).toHaveBeenCalledWith("toggle.closed", { "$toggle" : conditionalControlMock.$toggle });
      expect(conditionalControlMock.$content.hasClass(conditionalControlMock.toggleClass)).toBe(false);
      expect(conditionalControlMock.$content.attr('aria-hidden')).toBe('true');
      expect(conditionalControlMock.$content.attr('aria-expanded')).toBe('false');
    });

    it("Should hide the continue button when toggle is checked & there is a postcode checker in the page", function () {
      var hideCalledWith;

      spyOn($.fn, "hide")
        .and.callFake(
          function () {
            hideCalledWith = this.selector;
          }
      );
      conditionalControlMock.$content
        .addClass(conditionalControlMock.toggleClass)
        .append("<input type='text' class='postcode' />");
      conditionalControlMock.$toggle = $("<input type='radio' id='mock2' checked='checked' />");
      $(document.body).append("<div id='found-addresses'></div>");

      GOVUK.registerToVote.ConditionalControl.prototype.toggle.call(conditionalControlMock); 
      expect($.fn.hide).toHaveBeenCalled();
      expect(hideCalledWith).toBe("#continue");

      $(".postcode").remove();
      $("#found-addresses").remove();
    });
  });
});
