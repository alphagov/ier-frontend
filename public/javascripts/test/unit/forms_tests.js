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
      
      spyOn($.prototype, "on").and.callFake(function() {
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
