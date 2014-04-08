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

describe("DuplicateField", function () {
  var copyClass = "added-country",
      labelObj = {
        "txt" : "country",
        "className" : "country-label"
      };

  describe("Creating an instance", function () {
    var duplicateFieldMock

    beforeEach(function () {
      duplicateFieldMock = $(
        "<div class='duplication-intro'>" +
          "<label for='field_1'></label>" +
          "<input type='text' id='field_1' />" +
          "<a data-field='field_1' class='duplicate-control'></a>" +
        "</div>"
      );
      $(document.body).append(duplicateFieldMock);
    });

    afterEach(function () {
      $(".duplication-intro").remove();
    });

    it("Should have the right interface", function () {
      var duplicateField;

      duplicateField = new GOVUK.registerToVote.DuplicateField(duplicateFieldMock.find("a")[0], copyClass, labelObj);
      expect(duplicateField.$control).toBeDefined();
      expect(duplicateField.copyClass).toBeDefined();
      expect(duplicateField.label).toBeDefined();
      expect(duplicateField.fieldId).toBeDefined();
      expect(duplicateField.idPattern).toBeDefined();
      expect(duplicateField.namePattern).toBeDefined();
      expect(duplicateField.$field).toBeDefined();
      expect(duplicateField.$label).toBeDefined();
      expect(duplicateField.$duplicationIntro).toBeDefined();
    });

    it("Should add a click event to the duplication link", function () {
      var duplicateField,
          eventCalled;

      spyOn($.fn, "on").and.callFake(
        function (evt) {
          eventCalled = evt; 
          return this;
        }
      );
      duplicateField = new GOVUK.registerToVote.DuplicateField(duplicateFieldMock.find("a")[0], copyClass, labelObj);
      expect($.fn.on).toHaveBeenCalled();
      expect(eventCalled).toBe("click");
    });

    it("Click event should call the duplicate method if from the 'add another country' link", function () {
      var duplicateField,
          eventCalled;

      spyOn(GOVUK.registerToVote.DuplicateField.prototype, "duplicate");
      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          eventCalled = evt;
          if (eventCalled === 'click') {
            callback({
              "target" : { "className" : "duplicate-control" }
            });
          }
          return this;
        }
      );
      duplicateField = new GOVUK.registerToVote.DuplicateField(duplicateFieldMock.find("a")[0], copyClass, labelObj);
      expect(GOVUK.registerToVote.DuplicateField.prototype.duplicate).toHaveBeenCalled();
    });

    it("Click event should call the removeDuplicate method if from the 'Remove' link", function () {
      var duplicateField,
          eventCalled;

      spyOn(GOVUK.registerToVote.DuplicateField.prototype, "removeDuplicate");
      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          eventCalled = evt;
          if (eventCalled === 'click') {
            callback({
              "target" : { "className" : "remove-field" }
            });
          }
          return this;
        }
      );
      duplicateField = new GOVUK.registerToVote.DuplicateField(duplicateFieldMock.find("a")[0], copyClass, labelObj);
      expect(GOVUK.registerToVote.DuplicateField.prototype.removeDuplicate).toHaveBeenCalled();
    });
  });

  describe("makeField method", function () {
    var duplicateFieldMock;

    beforeEach(function () {
      $container = $(
        "<div>" +
          "<label></label>" +
        "</div>"
      );

      duplicateFieldMock = {
        "getFieldId" : function () { return "field[2]"; },
        "$label" : $container.find('label'),
        "label" : labelObj,
        "getFieldName" : function () { return "field[2]"; }, 
        "copyClass" : copyClass
      };
    });

    it("Should call the getFieldId and getFieldName methods", function () {
      var field;

      spyOn(duplicateFieldMock, "getFieldId").and.callThrough;
      spyOn(duplicateFieldMock, "getFieldName").and.callThrough;
      field = GOVUK.registerToVote.DuplicateField.prototype.makeField.call(duplicateFieldMock, 2, "France");
      
      expect(duplicateFieldMock.getFieldId).toHaveBeenCalled();
      expect(duplicateFieldMock.getFieldName).toHaveBeenCalled();
    });

    it("Should return the correct element given 'France' as the first duplicate", function () {
      var expectedElement = $(
            "<div class='" + copyClass + "'>" +
              "<label for='field[2]' class='" + labelObj.className + "'>" + labelObj.txt + " 2</label>" +
              "<a href='#' class='remove-field'>Remove<span class='visuallyhidden'> " + labelObj.txt + " 2</span></a>" +
              "<input type='text' id='field[2]' name='field[2]' class='text country-autocomplete long' value='France' Autocomplete='off' />",
            "</div>"
          ),
          field = GOVUK.registerToVote.DuplicateField.prototype.makeField.call(duplicateFieldMock, 2, "France");

      expect(field[0].nodeName.toLowerCase()).toBe('div');
      expect(field.html()).toEqual(expectedElement.html());
    });

    it("Should return the correct element given 'Sweden' as the second duplicate", function () {
      var expectedElement = $(
            "<div class='" + copyClass + "'>" +
              "<label for='field[2]' class='" + labelObj.className + "'>" + labelObj.txt + " 3</label>" +
              "<a href='#' class='remove-field'>Remove<span class='visuallyhidden'> " + labelObj.txt + " 3</span></a>" +
              "<input type='text' id='field[2]' name='field[2]' class='text country-autocomplete long' value='Sweden' Autocomplete='off' />",
            "</div>"
          ),
          field = GOVUK.registerToVote.DuplicateField.prototype.makeField.call(duplicateFieldMock, 3, "Sweden");

      expect(field[0].nodeName.toLowerCase()).toBe('div');
      expect(field.html()).toEqual(expectedElement.html());
    });
  });

  describe("getFieldId method", function () {
    var duplicateFieldMock;

    beforeEach(function () {
      duplicateFieldMock = {
        "idPattern" : "field[1]",
        "getFieldId" : function () {}
      };
    });

    it("Should form an id string from an integer", function () {
      var idString = GOVUK.registerToVote.DuplicateField.prototype.getFieldId.call(duplicateFieldMock, 3);

      expect(idString).toBe("field[3]");
    })
  });

  describe("getFieldName method", function () {
    var duplicateFieldMock;

    beforeEach(function () {
      duplicateFieldMock = {
        "namePattern" : "field[1]",
        "getFieldId" : function () {}
      };
    });

    it("Should form an id string from an integer", function () {
      var idString = GOVUK.registerToVote.DuplicateField.prototype.getFieldName.call(duplicateFieldMock, 3);

      expect(idString).toBe("field[3]");
    })
  });

  describe("removeDuplicate method", function () {
    var duplicateFieldMock,
        setUpMock,
        emptyContainer,
        containerWithOneDuplicate,
        containerWithTwoDuplicates;

    setUpMock = function (mock, $container) {
      mock.$label = $container.find('label');
      mock.$duplicationIntro = $container.find('.duplication-intro');
      mock.$field = $container.find("input:first");
    }

    emptyContainer = 
      "<div id='duplication'>" +
        "<label for='field[0]'></label>" +
        "<input type='text' id='field[0]' name='field[0]' class='text country-autocomplete long' value='Algeria' Autocomplete='off' />" +
        "<p class='duplication-intro' style='display: block;'>" +
          "If you have dual nationality" +
          "<a class='duplicate-control-initial' href='#' data-field='field[0]'>add another country</a>." +
        "</p>" +
      "</div>";

    containerWithOneDuplicate = 
      "<div id='duplication'>" +
        "<label for='field[0]'></label>" +
        "<input type='text' id='field[0]' name='field[0]' class='text country-autocomplete long' value='Algeria' Autocomplete='off' />" +
        "<p class='duplication-intro' style='display: block;'>" +
          "If you have dual nationality" +
          "<a class='duplicate-control-initial' href='#' data-field='field[0]'>add another country</a>." +
        "</p>" +
        "<div class='" + copyClass + "'>" +
          "<label for='field[1]' class='country-label'>country 1</label>" +
          "<a href='#' class='remove-field'>Remove<span class='visuallyhidden'> country 1</span></a>" +
          "<input type='text' id='field[1]' name='field[1]' class='text country-autocomplete long' value='France' Autocomplete='off' />" +
        "</div>" +
        "<a href='#' class='duplicate-control'>Add another country</a>" +
      "</div>";

    containerWithTwoDuplicates = 
      "<div id='duplication'>" +
        "<label for='field[0]'></label>" +
        "<input type='text' id='field[0]' name='field[0]' class='text country-autocomplete long' value='Algeria' Autocomplete='off' />" +
        "<p class='duplication-intro' style='display: block;'>" +
          "If you have dual nationality" +
          "<a class='duplicate-control-initial' href='#' data-field='field[0]'>add another country</a>." +
        "</p>" +
        "<div class='" + copyClass + "'>" +
          "<label for='field[1]' class='country-label'>country 1</label>" +
          "<a href='#' class='remove-field'>Remove<span class='visuallyhidden'> country 1</span></a>" +
          "<input type='text' id='field[1]' name='field[1]' class='text country-autocomplete long' value='France' Autocomplete='off' />" +
        "</div>" +
        "<div class='" + copyClass + "'>" +
          "<label for='field[2]' class='country-label'>country 1</label>" +
          "<a href='#' class='remove-field'>Remove<span class='visuallyhidden'> country 2</span></a>" +
          "<input type='text' id='field[2]' name='field[2]' class='text country-autocomplete long' value='Sweden' Autocomplete='off' />" +
        "</div>" +
        "<a href='#' class='duplicate-control'>Add another country</a>" +
      "</div>";

    beforeEach(function () {
      duplicateFieldMock = {
        "copyClass" : copyClass,
        "label" : labelObj,
        "makeField" : GOVUK.registerToVote.DuplicateField.prototype.makeField,
        "getFieldId" : GOVUK.registerToVote.DuplicateField.prototype.getFieldId,
        "getFieldName" : GOVUK.registerToVote.DuplicateField.prototype.getFieldName,
        "idPattern" : "field[0]",
        "namePattern" : "field[0]"
      };
    });

    afterEach(function () {
      $("#duplication").remove();
    });

    it("Should remove a duplicate correctly when it's the only one", function () {
      var $container = $(containerWithOneDuplicate);

      setUpMock(duplicateFieldMock, $container);
      $(document.body).append($container);

      GOVUK.registerToVote.DuplicateField.prototype.removeDuplicate.call(duplicateFieldMock, "field[1]");
      expect($container.html()).toEqual($(emptyContainer).html());
    });

    it("Should remove a duplicate correctly when it's one of two", function () {
      var $container = $(containerWithTwoDuplicates);

      $(document.body).append($container);
      setUpMock(duplicateFieldMock, $container);

      GOVUK.registerToVote.DuplicateField.prototype.removeDuplicate.call(duplicateFieldMock, "field[2]");
      expect($container.html()).toEqual($(containerWithOneDuplicate).html());
    });

    it("Should trigger an event to the document when a duplicate is removed", function () {
      var $container = $(containerWithOneDuplicate),
          eventCalled = false;

      $(document.body).append($container);
      setUpMock(duplicateFieldMock, $container);
      spyOn($.fn, "trigger").and.callFake(
        function (evt) {
          if (this[0] === document) {
            eventCalled = evt;
          }
        }
      );

      GOVUK.registerToVote.DuplicateField.prototype.removeDuplicate.call(duplicateFieldMock, "field[1]");
      expect($.fn.trigger).toHaveBeenCalled();
      expect(eventCalled).not.toBe(false);
      expect(eventCalled).toBe("contentRemoval");
    });

    it("Should shift focus to the original textbox if only one duplicate is left", function () {
      var $container = $(containerWithOneDuplicate);

      $(document.body).append($container);
      setUpMock(duplicateFieldMock, $container);

      GOVUK.registerToVote.DuplicateField.prototype.removeDuplicate.call(duplicateFieldMock, "field[1]");
      expect($container.find("input:first").is(":focus")).toBe(true);
    });
  });

  describe("duplicate method", function () {
    var $addAnotherLink = "<a href='#' class='duplicate-control'>Add another '" + labelObj.txt + "'</a>",
        $container,
        duplicateFieldMock;

    beforeEach(function () {
      $container = $(
        "<div id='duplication'>" +
          "<label for='field[0]'></label>" +
          "<input type='text' id='field[0]' name='field[0]' class='text country-autocomplete long' value='Algeria' Autocomplete='off' />" +
          "<p class='duplication-intro' style='display: block;'>" +
            "If you have dual nationality" +
            "<a class='duplicate-control-initial' href='#' data-field='field[0]'>add another country</a>." +
          "</p>" +
        "</div>"
      );

      duplicateFieldMock = {
        "$label" : $container.find("label"),
        "label" : labelObj,
        "makeField" : function (idx) {
          return $(
            "<div class='" + copyClass + "'>" +
              "<input id='field[1]' />" +
            "</div>"
          )
        },
        "getFieldId" : function (idx) {
          return "field[1]";
        },
        "$duplicationIntro" : $container.find(".duplication-intro")
      };
    });

    afterEach(function () {
      $("#duplication").remove();
    });

    it("Should add the duplicate to the containing div", function () {
      $(document.body).append($container);
      GOVUK.registerToVote.DuplicateField.prototype.duplicate.call(duplicateFieldMock);
      
      expect(document.getElementById("field[1]")).not.toEqual(null);
    });

    it("Should hide the 'add another country' intro paragraph", function () {
      $(document.body).append($container);
      GOVUK.registerToVote.DuplicateField.prototype.duplicate.call(duplicateFieldMock);

      expect($container.find('.duplication-intro').css("display")).toEqual("none");
    });

    it("Should add the 'add another' link to the bottom of the duplicates added", function () {
      $(document.body).append($container);
      GOVUK.registerToVote.DuplicateField.prototype.duplicate.call(duplicateFieldMock);

      expect($container.find("a.duplicate-control").length).toEqual(1);
    });
  });
});

describe("monitorRadios", function () {
  var $radioGroup;

  beforeEach(function () {
    $radioGroup = $(
      "<fieldset>" +
        "<input type='radio' id='field_1' name='field_1' />" +
        "<input type='radio' id='field_2' name='field_1' />" +
      "</fieldset>" 
    );
    GOVUK.registerToVote.monitorRadios.radioGroups = [];
  });

  it("Should set a change event on the fieldset of the radio sent in", function () {
    var radio = $radioGroup.find('#field_1')[0],
        evtCalledOn,
        evtSet;

    spyOn($.fn, "on").and.callFake(
      function (evt, callback) {
        evtCalledOn = this[0];
        evtSet = evt;
        return this;
      }
    );
    GOVUK.registerToVote.monitorRadios(radio);
    expect($.fn.on).toHaveBeenCalled();
    expect(evtCalledOn).toBe($radioGroup[0]);
    expect(evtSet).toBe("click change");
  });

  it("Should trigger a custom event when an radio in the fieldset changes state", function () {
    var radio = $radioGroup.find("#field_1")[0],
        evtSet,
        callbackSet;

    spyOn($.fn, "on").and.callFake(
      function (evt, callback) {
        callbackSet = callback;
      }
    );
    spyOn($.fn, "trigger").and.callFake(
      function (evt, evtData) {
        if (this[0] === document) {
          evtSet = evt;
          dataSent = evtData;
        }
      }
    );
    GOVUK.registerToVote.monitorRadios(radio);
    callbackSet.call($radioGroup[0], { "target" : radio });
    expect($.fn.trigger).toHaveBeenCalled();
    expect(evtSet).toBe("radio:" + radio.name);
    expect(dataSent).toEqual({
      "selectedRadio" : radio,
      "fieldset" : $radioGroup[0]
    });
  });

  it("Should only bind one custom event for each group of radios", function () {
    var radio1 = $radioGroup.find('#field_1')[0],
        radio2 = $radioGroup.find('#field_2')[0],
        evtCalledOn,
        evtSet;

    spyOn($.fn, "on").and.callFake(
      function (evt, callback) {
        evtCalledOn = this[0];
        evtSet = evt;
        return this;
      }
    );

    GOVUK.registerToVote.monitorRadios(radio1);
    GOVUK.registerToVote.monitorRadios(radio2);
    expect(GOVUK.registerToVote.monitorRadios.radioGroups.length).toEqual(1);
  });
});

describe("MarkSelected", function () {
  describe("Creating an instance", function () {
    var $radioLabel,
        $checkboxLabel;

    beforeEach(function () {
      $radioLabel = $(
        "<label for='field_1'>" +
          "<input type='radio' id='field_1' name='field_1' />" +
        "</label>"
      );
      $checkboxLabel = $(
        "<label for='field_1'>" +
          "<input type='checkbox' id='field_1' name='field_1' />" +
        "</label>"
      );
    });

    it("Should work with both radios and checkboxes", function () {
      var createInstance = function ($elm) {
            return new GOVUK.registerToVote.MarkSelected($elm);
          };

      expect(function () { createInstance($radioLabel) }).not.toThrow();
      expect(function () { createInstance($checkboxLabel) }).not.toThrow();
    });

    it("Should have the right interface", function () {
      var selectable;

      selectable = new GOVUK.registerToVote.MarkSelected($radioLabel);
      expect(selectable.toggle).toBeDefined();
    });

    it("Should add a custom event to the document if selectable contains a radio", function () {
      var selectable,
          eventBound,
          elementBoundTo;

      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          eventBound = evt;
          elementBoundTo = this[0];
          return this;
        }
      );
      selectable = new GOVUK.registerToVote.MarkSelected($radioLabel);
      expect(elementBoundTo).toBe(document);
      expect(eventBound).toBe("radio:" + $radioLabel.find("input").attr("name"));
    });

    it("The custom event should call the toggle method", function () {
      var inputName = $radioLabel.find("input").attr("name"),
          eventName = "radio:" + inputName,
          selectable,
          eventCallback;

      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          if ((evt === eventName) && (this[0] === document)) {
            eventCallback = callback;
          }
          return this;
        }
      );
      selectable = new GOVUK.registerToVote.MarkSelected($radioLabel);
      spyOn(selectable, "toggle");
      eventCallback(eventName, { "selectedRadio" : $radioLabel });
      expect(selectable.toggle).toHaveBeenCalled();
    });

    it("Should add a click event to the selectable if it contains a checkbox", function () {
      var selectable,
          eventBound,
          elementBoundTo;

      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          eventBound = evt;
          elementBoundTo = this[0];
          return this;
        }
      );
      selectable = new GOVUK.registerToVote.MarkSelected($checkboxLabel);
      expect(elementBoundTo).toBe($checkboxLabel[0]);
      expect(eventBound).toBe("click");
    });

    it("Click event should call the toggle method", function () {
      var selectable,
          eventBound,
          elementBoundTo;

      spyOn($.fn, "on").and.callFake(
        function (evt, callback) {
          eventBound = evt;
          elementBoundTo = this[0];
          return this;
        }
      );
      selectable = new GOVUK.registerToVote.MarkSelected($checkboxLabel);
      expect(elementBoundTo).toBe($checkboxLabel[0]);
      expect(eventBound).toBe("click");
    });

    it("Should add a class to the selectable if it is already chosen", function () {
      var selectedRadio;

      $radioLabel.find("input").attr("checked", true);
      selectable = new GOVUK.registerToVote.MarkSelected($radioLabel);
      expect($radioLabel.hasClass("selected")).toBe(true);
    });
  });

  describe("toggle method", function () {
    var $radioFieldset,
        $checkboxFieldset,
        $radioLabel,
        $checkboxLabel,
        markSelectedMock;

    beforeEach(function () {
      $radioFieldset =
        "<label for='field_1'>" +
          "<input type='radio' id='field_1' name='field_1' />" +
        "</label>" +
        "<label for='field_2'>" +
          "<input type='radio' id='field_2' name='field_2' />" +
        "</label>"
      );
      $checkboxFieldset = $(
        "<label for='field_1'>" +
          "<input type='checkbox' id='field_1' name='field_1' />" +
        "</label>" +
        "<label for='field_2'>" +
          "<input type='checkbox' id='field_2' name='field_2' />" +
        "</label>"
      );
      $radioLabel = $radioFieldset.find('#field_1');
      $checkboxLabel = $checkboxFieldset.find('#field_1');
    });
    
    it("Should add a class to a radio if called by a click which deselects it", function () {
      markSelectedMock = {
        "$control" : $radioLabel.find("input"),
        "$label" : $radioLabel
      };
      $radioLabel
        .addClass("selected")
      expect($radioLabel.hasClass("selected")).toBe(true);
      GOVUK.registerToVote.MarkSelected.prototype.toggle.call(markSelectedMock);
      expect($radioLabel.hasClass("selected")).toBe(false);
    });
  });
});
