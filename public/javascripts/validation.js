(function () {
  "use strict";

  var root = this,
      $ = root.jQuery,
      GOVUK = root.GOVUK,
      validation;

  validation = {
    init : function () {
      var _this = this,
          $submits = $('.validation-submit');

      this.fields.init();
      $('.validate').each(function (idx, elm) {
        _this.fields.add($(elm), _this);
      });
      this.events.bind('validate', function (e, eData) {
        return _this.handler(eData.$source);
      });
      $(document).on('click', '.validation-submit', function (e) {
        return _this.handler($(e.target));
      });
    },
    handler : function ($source) {
      var formName = validation.getFormFromField($source).action,
          rules = [],
          rulesStr = "",
          names;

      names = $source.data('validationSources');
      if (names !== null) {
        names = names.split(' ');
      } else {
        names = $(this.forms[formName]).$source.data('validationName');
      }
      return this.validate(names);
    },
    getFormFromField : function ($field) {
      if ($field[0].nodeName.toLowerCase() === 'form') {
        return $field[0];
      } else if (typeof $field[0].form !== 'undefined') {
        return $field[0].form;
      } else {
        return $field.closest('form')[0];
      }
    },
    forms : {
      refs : {},
      addName : function ($source) {
        var name = $source.data('validationName'),
            formName;

        formName = validation.getFormFromField($source).action;
        if (typeof this.refs[formName] !== 'undefined') {
          this.refs[formName].push(name);
        } else {
          this.refs[formName] = [name];
        }
      }
    },
    fields : (function () {
      var items = [],
          _this = this,
          ItemObj = function (props) {
            var prop;

            for (prop in props) {
              if (props.hasOwnProperty(prop)) {
                this[prop] = props[prop];       
              }
            };
          },
          objTypes = {
            'field' : function (props) { ItemObj.call(this, props); },
            'fieldset' : function (props) { ItemObj.call(this, props); },
            'association' : function (props) { ItemObj.call(this, props); }
          },
          _makeItemObj = function ($source, obj) {
            obj.name = $source.data('validationName');
            obj.rules = $source.data('validationRules').split(' ');

            return new objTypes[obj.type](obj);
          };
      
      return {
        init : function () {
          var objType,
              rules,
              rule;

          for(objType in objTypes) {
            rules = validation.rules[objType];
            for (rule in rules) {
              objTypes[objType].prototype[rule] = rules[rule];
            }
          };
        },
        addField : function ($source) {
          var itemObj = _makeItemObj($source, {
            'type' : 'field',
            '$source' : $source
          });
          items.push(itemObj);
        },
        addFieldset : function ($source) {
          var childNames = $source.data('validationChildren').split(' '),
              itemObj;

          itemObj = _makeItemObj($source, {
            'type' : 'fieldset',
            '$source' : $source,
            'children' : childNames
          });
          items.push(itemObj);
        },
        addAssociation : function ($source) {
          var memberNames = $source.data('validationMembers').split(' '),
              itemObj;

          itemObj = _makeItemObj($source, {
            'type' : 'association',
            'members' : memberNames
          });
          items.push(itemObj);
        },
        remove : function (name) {
          var result = [],
              item,
              itemObj;

          $.each(items, function(idx, itemObj) {
            if (itemObj.name !== name) {
              result.push(itemObj);
            }
          });
          items = result;
        },
        add : function ($source) {
          var type = $source.data('validationType');

          if (type !== null) {
            validation.forms.addName($source);
            switch (type) {
              case 'association' :
                this.addAssociation($source);
                break;
              case 'fieldset' :
                this.addFieldset($source);
                break;
              case 'field' :
                this.addField($source);
                break;
              default :
                return;
            }
          }
        },
        getNames : function (names) {
          var result = [],
              name,
              item,
              a,b,i,j;

          for (a = 0, b = names.length; a < b; a++) {
            name = names[a];
            for (i = 0, j = items.length; i < j; i++) {
              if (items[i].name === name) {
                result.push(items[i]);
              }
            }
          }
          return result;
        },
        getAll : function () {
          return items;
        }
      };
    }()),
    applyRules : function (fieldObj) {
      var failedRules = false,
          sourcesToMark = {
            'association' : 'members',
            'fieldset' : '$source',
            'field' : '$source'
          },
          $source,
          i,j;

      $source = fieldObj[sourcesToMark[fieldObj.type]];
      // rules are applied in the order given
      for (i = 0, j = fieldObj.rules.length; i < j; i++) {
        var rule = fieldObj.rules[i];

        failedRules = fieldObj[rule]();
        if (failedRules.length) { 
          break;
        }
      }
      return failedRules;
    },
    fieldsetCascade : function (name, rule) {
      var cascades = validation.cascades,
          exists = function (obj, prop) {
            return (typeof obj[prop] !== 'undefined');
          };

      if (exists(cascades, name) && exists(cascades[name], rule)) {
        return cascades[name][rule];
      }
      return false;
    },
    validate : function (names) {
      var _this = this,
          invalidFields = [],
          invalidField,
          _addAnyCascades,
          fields;

      _addAnyCascades = function (field, rule, invalidFields) {
        var fieldsetCascade = validation.fieldsetCascade(field.name, rule),
            prefix = validation.rules[field.type][rule].prefix;

        if (fieldsetCascade) {
          $.merge(invalidFields, fieldsetCascade.apply(field));
        }
        return invalidFields;
      };
      fields = this.fields.getNames(names);
      $.each(fields, function (idx, field) {
        var failedRules;

        failedRules = _this.applyRules(field);
        if (failedRules.length) {
          $.merge(invalidFields, failedRules);
        }
      });

      if (invalidFields.length) {
        this.mark.invalidFields(invalidFields);
        this.notify(invalidFields);
        this.events.trigger('invalid', { 'invalidFields' :  invalidFields });
        return false;
      } else {
        this.unMark.validFields();
        this.notify([]);
        return true;
      }
    },
    events : {
      trigger : function (evt, eData) {
        $(document).trigger('validation.' + evt, eData);
      },
      bind : function (evt, func) {
        $(document).on('validation.' + evt, func);
      }
    },
    mark : {
      field : function (fieldObj) {
        var $validationWrapper = fieldObj.$source.closest('.validation-wrapper');

        fieldObj.$source.addClass('invalid');
        if ($validationWrapper.length) {
          $validationWrapper.addClass('validation-wrapper-invalid');
        }
      },
      invalidFields : function (invalidFields) {
        var mark = this;

        validation.unMark.validFields();
        $.each(invalidFields, function (idx, fieldObj) {
          if (typeof fieldObj.$source !== 'undefined') {
            mark.field(fieldObj);
          }
        });
      }
    },
    unMark : {
      field : function (fieldObj) {
        var $validationWrapper = fieldObj.$source.closest('.validation-wrapper');

        fieldObj.$source.removeClass('invalid');
        if ($validationWrapper.length) {
          $validationWrapper.removeClass('validation-wrapper-invalid');
        }
      },
      validFields : function (validFields) {
        var unMark = this;

        if (validFields === undefined) { validFields = validation.fields.getAll(); }
        $.each(validFields, function (idx, fieldObj) {
          if (typeof fieldObj.$source !== 'undefined') {
            unMark.field(fieldObj);
          }
        });
      }
    },
    notify : function (invalidFields) {
      var _this = this,
          name,
          rule;

      $('#continue').siblings('.validation-message').remove();
      if (invalidFields.length) {
        $.each(invalidFields, function (idx, field) {
          name = invalidFields[idx].name;
          rule = invalidFields[idx].rule;
          if ((typeof _this.messages[name] !== 'undefined') && (typeof _this.messages[name][rule] !== 'undefined')) {
            $('<div class="validation-message visible">' + _this.messages[name][rule] + '</div>').insertBefore('#continue');
          }
        });
      }
    },
    rules : (function () {
      var _fieldType,
          _selectValue,
          _radioValue,
          _getFieldValue,
          _getInvalidDataFromFields,
          rules;

      _fieldType = function ($field) {
        var type = $field[0].nodeName.toLowerCase();
        
        return (type === 'input') ? $field[0].type : type;
      };
      _selectValue = function ($field) {
        var idx = $field[0].selectedIndex;

        return $field.find('option:eq(' + idx + ')').val();
      };
      _radioValue = function ($field) {
        var radioName = $field.attr('name'),
            $radios = $($field[0].form).find('input[type=radio]'),
            $selectedRadio = false;

        $radios.each(function (idx, elm) {
          if ((elm.name === radioName) && elm.checked) { $selectedRadio = $(elm); }
        });

        return ($selectedRadio) ? $selectedRadio.val() : ''; 
      };
      _getFieldValue = function ($field) {
        switch (_fieldType($field)) {
          case 'text':
            return $field.val();
          case 'checkbox':
            return ($field.is(':checked')) ? $field.val() : '';
          case 'select':
            return _selectValue($field);
          case 'radio':
            return _radioValue($field);
          default:
            return $field.val();
        }
      };
      _getInvalidDataFromFields = function (fields, rule) {
        return $.map(fields, function (item, idx) {
          return {
            'name' : item.name,
            'rule' : rule,
            '$source' : item.$source
          };
        });
      };
      /*
       * Rules should run as a field method with access to all the properties of that field type
       * They should return an array of failed rules. If there are no failures this array should be empty.
      */
      rules = {
        'field' : {
          'nonEmpty' : function () {
            if (this.$source.is(':hidden')) { return []; }
            if (_getFieldValue(this.$source) === '') {
              return _getInvalidDataFromFields([this], 'nonEmpty');
            } else {
              return [];
            }
          },
          'atLeastOneCountry' : function () {
            var $countries,
                $filledCountries,
                $selectedCountries;

            if (this.$source.is(':hidden')) { return []; }
            $countries = this.$source.find('.country-autocomplete');
            $filledCountries = $.map($countries, function (elm, idx) {
              return (_getFieldValue($(elm)) === '') ? null : elm;
            });
            if ($filledCountries.length === 0) {
              return _getInvalidDataFromFields([this, { 'name' : 'country', '$source' : $countries }], 'atLeastOneCountry');
            } else {
              return [];
            }
          },
          'telephone' : function () {
            var entry = _getFieldValue(this.$source);
            
            if (entry.replace(/[\s|\-]/g, "").match(/^\+?\d+$/) === null) {
              return _getInvalidDataFromFields([this], 'telephone');
            } else {
              return [];
            }
          },
          'email' : function () {
            var entry = _getFieldValue(this.$source);

            if (entry.match(/\w+@\w+?(?:\.[A-Za-z]{2,3})+/) === null) {
              return _getInvalidDataFromFields([this], 'email');
            } else {
              return [];
            }
          },
          'nino' : function () {
            var entry = _getFieldValue(this.$source),
                match;

            match = entry
                    .toUpperCase()
                    .replace(/[\s|\-]/g, "")
                    .match(/^[A-CEGHJ-PR-TW-Za-ceghj-pr-tw-z]{1}[A-CEGHJ-NPR-TW-Za-ceghj-npr-tw-z]{1}[0-9]{6}[A-DFMa-dfm]{0,1}$/);

            if (match !== null) {
              return _getInvalidDataFromFields([this], 'nino');
            } else {
              return [];
            }
          },
          'postcode' : function () {
            var entry = _getFieldValue(this.$source),
                match;

            match = entry
                      .toUpperCase()
                      .replace(/[\s|\-]/g, "")
                      .match(/((GIR0AA)|((([A-PR-UW-Z][0-9][0-9]?)|(([A-PR-UW-Z]][A-HK-Y][0-9][0-9]?)|(([A-PR-UW-Z][0-9][A-HJKSTUW])|([A-PR-UW-Z][A-HK-Z][0-9][ABEHMNPRVWXY]))))[0-9][A-BD-HJLNP-UW-Z]{2}))/);
            if (match === null) {
              return _getInvalidDataFromFields([this], 'postcode');
            } else {
              return [];
            }
          },
          'smallText' : function () {
            var entry = _getFieldValue(this.$source),
                maxLen = 256;

            if (entry.length > maxLen) {
              return _getInvalidDataFromFields([this], 'largeText');
            } else {
              return [];
            }
          },
          'largeText' : function () {
            var entry = _getFieldValue(this.$source),
                maxLen = 500;

            if (entry.length > maxLen) {
              return _getInvalidDataFromFields([this], 'largeText');
            } else {
              return [];
            }
          }
        },
        'fieldset' : {
          'atLeastOneNonEmpty' : function () {
            var oneFilled = false,
                childFields = validation.fields.getNames(this.children),
                _fieldIsShowing;

            _fieldIsShowing = function (fieldObj) {
              return !fieldObj.$source.is(':hidden');
            };
            if (this.$source.is(':hidden')) { return []; }
            $.each(childFields, function (idx, fieldObj) {
              var method = (fieldObj.type === 'fieldset') ? 'allNonEmpty' : 'nonEmpty',
                  isFilledFailedRules = fieldObj[method]();

              if (_fieldIsShowing(fieldObj) && !isFilledFailedRules.length) {
                oneFilled = true;
              }
            });
            if (!oneFilled) {
              return _getInvalidDataFromFields([this], 'atLeastOneNonEmpty');
            } else {
              return [];
            }
          },
          'atLeastOneChecked' :  function () {
            var invalidRules = this.atLeastOneNonEmpty();

            $.map(invalidRules, function (fieldObj, idx) {
              if (fieldObj.$source[0].nodeName.toLowerCase() !== 'input') { return fieldObj; }
            });
            if (!invalidRules.length) { return []; }
            return invalidRules;
          },
          'checkedOtherHasValue' : function () {
            var childFields = validation.fields.getNames(this.children),
                otherIsChecked = false,
                invalidRules = [],
                i,j;

            for (i = 0, j = childFields.length; i < j; i++) {
              var fieldObj = childFields[i];

              if (fieldObj.name === 'otherCountries') {
                invalidRules = fieldObj.atLeastOneCountry();
              } else if (fieldObj.name === 'other') {
                otherIsChecked = (_getFieldValue(fieldObj.$source) !== '');
              } else {
                if (_getFieldValue(fieldObj.$source) !== '') { return []; }
              }
            }
            if (otherIsChecked && !invalidRules.length) {
              return [];
            } else {
              return invalidRules;
            }
          },
          'allNonEmpty' : function () {
            var childFields = validation.fields.getNames(this.children),
                childFailedRules = [],
                rulesToReport,
                _fieldIsShowing,
                fieldsetObj,
                i,j;

            _fieldIsShowing = function (fieldObj) {
              return !fieldObj.$source.is(':hidden');
            };
            if (this.$source.is(':hidden')) { return []; }
            for (i = 0, j = childFields.length; i < j; i++) {
              var fieldObj = childFields[i],
                  method = (fieldObj.type === 'fieldset') ? 'allNonEmpty' : 'nonEmpty',
                  isFilledFailedRules = fieldObj[method]();

              if (_fieldIsShowing(fieldObj) && isFilledFailedRules.length) {
                $.merge(childFailedRules, isFilledFailedRules);
              }
            }
            if (childFailedRules.length) {
              if (childFailedRules.length < childFields.length) {
                // message for each child field
                rulesToReport = childFailedRules;
                fieldsetObj = {
                  'name' : this.name,
                  '$source' : this.$source
                };
              } else { // message from the fieldset level
                rulesToReport = _getInvalidDataFromFields(childFailedRules, 'allNonEmpty');
                fieldsetObj = {
                  'name' : this.name,
                  'rule' : 'allNonEmpty',
                  '$source' : this.$source
                };
              }
              if (this.$source.hasClass('inlineFields')) {
                rulesToReport.push(fieldsetObj);
              }
              return rulesToReport;
            } else {
              return [];
            }
          },
          'fieldOrExcuse' : function () {
            var childFields = validation.fields.getNames(this.children),
                field = childFields[0],
                excuse = childFields[1],
                fieldFailedRules = validation.applyRules(field),
                excuseFailedRules = validation.applyRules(excuse),
                fieldIsValid,
                excuseIsValid,
                _fieldIsShowing;

            _fieldIsShowing = function (fieldObj) {
              return !fieldObj.$source.is(':hidden');
            };
            fieldIsValid = (!fieldFailedRules.length && _fieldIsShowing(field));
            excuseIsValid = (!excuseFailedRules.length && _fieldIsShowing(excuse));
            if (fieldIsValid) {
              return [];
            } else {
              if (excuseIsValid) {
                return [];
              } else {
                return [{ 'name' : this.name, 'rule' : 'fieldOrExcuse', '$source' : field.$source }];
              }
            }
          },
          'countryNonEmpty' : function () {
            var otherField = validation.fields.getNames(['other'])[0],
                $countryInput = otherField.$source.parent('label')
                                  .siblings('#add-countries')
                                  .find('.country-autocomplete'),
                otherFieldFailed = otherField.nonEmpty();

            if (!otherFieldFailed.length) {
              if (_getFieldValue($countryInput) === '') {
                return _getInvalidDataFromFields([otherField, this], 'countryNonEmpty');
              } else {
                return [];
              }
            } else {
              return [];
            }
          },
          'correctAge' : function () {
            var children = validation.fields.getNames(this.children),
                day = parseInt(_getFieldValue(children[0].$source), 10),
                month = parseInt(_getFieldValue(children[1].$source), 10),
                year = parseInt(_getFieldValue(children[2].$source), 10),
                dob = (new Date(year, (month - 1), day)).getTime(),
                now = (new Date()).getTime(),
                minAge = 16,
                maxAge = 115,
                age = now - dob;

            age = Math.floor((((((age / 1000) / 60) / 60) / 24) / 365.25));
            isValid = ((age >= minAge) && (age <= maxAge));
            if (!isValid) {
              return _getInvalidDataFromFields(children, 'correctAge');
            } else {
              return [];
            }
          }
        },
        'association' : {
          'fieldsetOrExcuse' : function () {
            var memberFields = validation.fields.getNames(this.members),
                fieldset = memberFields[0],
                excuse = memberFields[1],
                fieldsetFailedRules = validation.applyRules(fieldset),
                excuseFailedRules = validation.applyRules(excuse),
                fieldsetIsValid,
                excuseIsValid,
                _fieldIsShowing;

            _fieldIsShowing = function (fieldObj) {
              return !fieldObj.$source.is(':hidden');
            };
            fieldsetIsValid = (!fieldsetFailedRules.length && _fieldIsShowing(fieldset));
            excuseIsValid = (!excuseFailedRules.length && _fieldIsShowing(excuse));
            if (!fieldsetIsValid && !excuseIsValid) {
              return fieldsetFailedRules;
            } else {
              return [];
            }
          },
          'allNonEmpty' : function () {
            var oneEmpty = false,
                memberFields = validation.fields.getNames(this.members),
                _fieldIsShowing,
                i,j;

            _fieldIsShowing = function (fieldObj) {
              return !fieldObj.$source.is(':hidden');
            };
            for (i = 0, j = memberFields.length; i < j; i++) {
              var fieldObj = memberFields[i],
                  method = (fieldObj.type === 'fieldset') ? 'allNonEmpty' : 'nonEmpty',
                  isFilledFailedRules = fieldObj[method]();

              if (_fieldIsShowing(fieldObj) && isFilledFailedRules.length) {
                oneEmpty = true;
                memberFields.push(this)
                return _getInvalidDataFromFields(memberFields, 'allNonEmpty');
              }
            };
            return [];
          }
        }
      };
      return rules;
    }()),
    messages : {
      'fullName' : {
        'allNonEmpty' : 'Please enter your name'
      },
      'firstName' : {
        'smallText' : 'First name can be no longer than 256 characters'
      },
      'middleName' : {
        'smallText' : 'Middle name can be no longer than 256 characters'
      },
      'lastName' : {
        'smallText' : 'Last name can be no longer than 256 characters'
      },
      'previousQuestion' : {
        'atLeastOneNonEmpty' : 'Please answer if you changed your name'
      },
      'previousName' : {
        'allNonEmpty' : 'Please enter your previous name'
      },
      'dateOfBirthDate' : {
        'allNonEmpty' : 'Please enter your date of birth'
      },
      'day' : {
        'nonEmpty' : 'Please enter your day of birth'
      },
      'month' : {
        'nonEmpty' : 'Please enter your month of birth'
      },
      'year' : {
        'nonEmpty' : 'Please enter your year of birth'
      },
      'otherAddressQuestion' : {
        'atLeastOneNonEmpty' : 'Please answer this question'
      },
      'contact' : {
        'atLeastOneNonEmpty' : 'Please answer this question'
      },
      'phoneNumber' : {
        'nonEmpty' : 'Please enter your phone number',
        'telephone' : 'Please enter a valid phone number'
      },
      'smsNumber' : {
        'nonEmpty' : 'Please enter the phone number you use for text messages',
        'telephone' : 'Please enter a valid phone number'
      },
      'emailAddress' : {
        'nonEmpty' : 'Please enter your email address',
        'email' : 'Please enter a valid email address'
      },
      'nationality' : {
        'atLeastOneNonEmpty' : 'Please answer this question'
      },
      'otherCountries' : {
        'atLeastOneCountry' : 'Please enter a country'
      },
      'ninoCode' : {
        'nonEmpty' : 'Please enter your National Insurance number',
        'nino' : 'Please enter a valid National Insurance number'
      },
      'postalVote' : {
        'atLeastOneNonEmpty' : 'Please answer this question'
      },
      'country' : {
        'atLeastOneNonEmpty' : 'Please answer this question'
      },
      'postcode' : {
        'nonEmpty' : 'Please enter a postcode',
        'postcode' : 'Please enter a valid postcode'
      },
      'address' : {
        'fieldOrExcuse' : 'Please select an address'
      },
      'previousAddressQuestion' : {
        'atLeastOneNonEmpty' : 'Please answer this question'
      }
    }
  };

  GOVUK.registerToVote.validation = validation;
}.call(this));