(function(){"use strict";var validation,root=this,$=root.jQuery,GOVUK=root.GOVUK,message=GOVUK.registerToVote.messages;validation={init:function(){{var _this=this;$(".validation-submit")}this.fields.init(),$(".validate").each(function(idx,elm){_this.fields.add($(elm),_this)}),this.events.bind("validate",function(e,eData){return _this.handler(eData.$source)}),$(document).on("click",".validation-submit",function(e){return _this.handler($(e.target))}).on("click",".validation-message a",function(e){return _this.goToControl($(e.target))})},handler:function($source){{var names;validation.getFormFromField($source).action}return names=$source.data("validationSources"),names&&null!==names?(names=names.split(" "),this.validate(names,$source)):!0},getFormFromField:function($field){return"form"===$field[0].nodeName.toLowerCase()?$field[0]:"undefined"!=typeof $field[0].form?$field[0].form:$field.closest("form")[0]},fields:function(){var items=[],ItemObj=function(props){var prop;for(prop in props)props.hasOwnProperty(prop)&&(this[prop]=props[prop])},objTypes={field:function(props){ItemObj.call(this,props)},fieldset:function(props){ItemObj.call(this,props)},association:function(props){ItemObj.call(this,props)}},_makeItemObj=function($source,obj){return obj.name=$source.data("validationName"),obj.rules=$source.data("validationRules").split(" "),new objTypes[obj.type](obj)};return{init:function(){var objType,rules,rule;for(objType in objTypes){rules=validation.rules[objType];for(rule in rules)objTypes[objType].prototype[rule]=rules[rule]}},addField:function($source){var itemObj=_makeItemObj($source,{type:"field",$source:$source});items.push(itemObj)},addFieldset:function($source){var itemObj,childNames=$source.data("validationChildren").split(" ");itemObj=_makeItemObj($source,{type:"fieldset",$source:$source,children:childNames}),items.push(itemObj)},addAssociation:function($source){var itemObj,memberNames=$source.data("validationMembers").split(" ");itemObj=_makeItemObj($source,{type:"association",members:memberNames}),items.push(itemObj)},remove:function(name){var result=[];$.each(items,function(idx,itemObj){itemObj.name!==name&&result.push(itemObj)}),items=result},add:function($source){var type=$source.data("validationType");if(null!==type)switch(type){case"association":this.addAssociation($source);break;case"fieldset":this.addFieldset($source);break;case"field":this.addField($source);break;default:return}},getNames:function(names){var name,a,b,i,j,result=[];for(a=0,b=names.length;b>a;a++)for(name=names[a],i=0,j=items.length;j>i;i++)items[i].name===name&&result.push(items[i]);return result},getAll:function(){return items}}}(),applyRules:function(fieldObj){var $source,i,j,failedRules=!1,sourcesToMark={association:"members",fieldset:"$source",field:"$source"};for($source=fieldObj[sourcesToMark[fieldObj.type]],i=0,j=fieldObj.rules.length;j>i;i++){var rule=fieldObj.rules[i];if(failedRules=fieldObj[rule](),failedRules.length)break}return failedRules},fieldsetCascade:function(name,rule){var cascades=validation.cascades,exists=function(obj,prop){return"undefined"!=typeof obj[prop]};return exists(cascades,name)&&exists(cascades[name],rule)?cascades[name][rule]:!1},validate:function(names,$triggerElement){var _addAnyCascades,fields,_this=this,invalidFields=[];return _addAnyCascades=function(field,rule,invalidFields){{var fieldsetCascade=validation.fieldsetCascade(field.name,rule);validation.rules[field.type][rule].prefix}return fieldsetCascade&&$.merge(invalidFields,fieldsetCascade.apply(field)),invalidFields},fields=this.fields.getNames(names),$.each(fields,function(idx,field){var failedRules;failedRules=_this.applyRules(field),failedRules.length&&$.merge(invalidFields,failedRules)}),invalidFields.length?(this.mark.invalidFields(invalidFields),this.notify(invalidFields,$triggerElement),this.events.trigger("invalid",{invalidFields:invalidFields}),!1):(this.unMark.validFields(),this.notify([],$triggerElement),!0)},makeInvalid:function(invalidFields,$triggerElement){this.mark.invalidFields(invalidFields),this.notify(invalidFields,$triggerElement),this.events.trigger("invalid",{invalidFields:invalidFields})},events:{trigger:function(evt,eData){$(document).trigger("validation."+evt,eData)},bind:function(evt,func){$(document).on("validation."+evt,func)}},mark:{field:function(fieldObj){var $validationWrapper=fieldObj.$source.closest(".validation-wrapper");fieldObj.$source.addClass("invalid"),$validationWrapper.length&&$validationWrapper.addClass("invalid")},invalidFields:function(invalidFields){var mark=this;validation.unMark.validFields(),$.each(invalidFields,function(idx,fieldObj){"undefined"!=typeof fieldObj.$source&&mark.field(fieldObj)})}},unMark:{field:function(fieldObj){var $validationWrapper=fieldObj.$source.closest(".validation-wrapper");fieldObj.$source.removeClass("invalid"),$validationWrapper.length&&$validationWrapper.removeClass("invalid")},validFields:function(validFields){var unMark=this;void 0===validFields&&(validFields=validation.fields.getAll()),$.each(validFields,function(idx,fieldObj){"undefined"!=typeof fieldObj.$source&&unMark.field(fieldObj)})}},messageField:function($field,message){var $label=$field.parent("label"),field=$field[0];($label.length||($label=$(field.form).find('label[for="'+field.id+'"]'),$label.length))&&$label.append('<span class="validation-message">'+message+"</span>")},notify:function(invalidFields,$validationTrigger){var $lastElement,name,rule,idToLinkTo,_isTextField,_sourceIsFormControl,_getFirstChild,message=Mustache.compile('<div class="validation-message visible">{{#block}}{{message}}{{/block}}</div>'),_this=this;_isTextField=function($source){var types=["text","tel","email","number"];return-1!==$.inArray($source[0].type,types)},_sourceIsFormControl=function($source){var nodeName=$source[0].nodeName.toLowerCase();switch(nodeName){case"input":return _isTextField($source);case"select":return!0;case"textarea":return!0;default:return!1}},_getFirstChild=function(field){var firstChildName,firstChild,group=_this.fields.getNames([field.name])[0];return firstChildName="association"===group.type?group.members[0]:group.children[0],firstChild=_this.fields.getNames([firstChildName])[0],"field"!==firstChild.type&&(firstChild=_getFirstChild(firstChild)),firstChild},$(".validation-message").remove(),"undefined"!=typeof $validationTrigger[0].form&&invalidFields.length&&($lastElement=$($validationTrigger[0].form).find(".validation-submit").last(),invalidFields.length&&$.each(invalidFields,function(idx,field){var messageData={};name=field.name,rule=field.rule,"undefined"!=typeof _this.messages[name]&&"undefined"!=typeof _this.messages[name][rule]&&(messageData.message=_this.messages[name][rule],field.$source&&_sourceIsFormControl(field.$source)&&field.$source[0].id?(idToLinkTo=field.$source[0].id,_this.messageField(field.$source,_this.messages[name][rule])):idToLinkTo=_getFirstChild(field).$source[0].id,messageData.block=function(){return function(message,render){return'<a href="#'+idToLinkTo+'">'+render(message)+"</a>"}},$(message(messageData)).insertBefore($lastElement))}))},goToControl:function($messageLink){var relatedFormControl=document.getElementById($messageLink.attr("href").split("#")[1]);return null!==relatedFormControl&&relatedFormControl.focus(),!1},rules:function(){var _isTextField,_fieldType,_selectValue,_radioValue,_getFieldValue,_getInvalidDataFromFields,rules;return _isTextField=function(type){var types=["text","tel","email","number"];return-1!==$.inArray(type,types)},_fieldType=function($field){var nodeName=$field[0].nodeName.toLowerCase();return"input"===nodeName?_isTextField(nodeName)?"text":$field[0].type:nodeName},_selectValue=function($field){var idx=$field[0].selectedIndex;return $field.find("option:eq("+idx+")").val()},_radioValue=function($field){var radioName=$field.attr("name"),$radios=$($field[0].form).find("input[type=radio]"),$selectedRadio=!1;return $radios.each(function(idx,elm){elm.name===radioName&&elm.checked&&($selectedRadio=$(elm))}),$selectedRadio?$selectedRadio.val():""},_getFieldValue=function($field){switch(_fieldType($field)){case"text":return $field.val();case"checkbox":return $field.is(":checked")?$field.val():"";case"select":return _selectValue($field);case"radio":return _radioValue($field);default:return $field.val()}},_getInvalidDataFromFields=function(fields,rule){return $.map(fields,function(item){return{name:item.name,rule:rule,$source:item.$source}})},rules={field:{nonEmpty:function(){return this.$source.is(":hidden")?[]:""===_getFieldValue(this.$source)?_getInvalidDataFromFields([this],"nonEmpty"):[]},telephone:function(){var entry=_getFieldValue(this.$source);return this.$source.is(":hidden")?[]:null===entry.replace(/[\s\-\+\(\)\_\A-Z\a-z]/g,"").match(/^[0-9]{5,30}$/)?_getInvalidDataFromFields([this],"telephone"):[]},email:function(){var entry=_getFieldValue(this.$source);return this.$source.is(":hidden")?[]:null===entry.match(/^.+@[^@.]+(\.[^@.]+)+$/)?_getInvalidDataFromFields([this],"email"):[]},nino:function(){var match,entry=_getFieldValue(this.$source);return match=entry.toUpperCase().replace(/[\s|\-]/g,"").match(/^[A-CEGHJ-PR-TW-Za-ceghj-pr-tw-z]{1}[A-CEGHJ-NPR-TW-Za-ceghj-npr-tw-z]{1}[0-9]{6}[A-DFMa-dfm]{0,1}$/),null===match?_getInvalidDataFromFields([this],"nino"):[]},postcode:function(){var match,entry=_getFieldValue(this.$source);return match=entry.toUpperCase().replace(/\s/g,"").match(/^((GIR0AA)|((([A-PR-UW-Z][0-9][0-9]?)|(([A-PR-UW-Z][A-HK-Y][0-9][0-9]?)|(([A-PR-UW-Z][0-9][A-HJKSTUW])|([A-PR-UW-Z][A-HK-Z][0-9][ABEHMNPRVWXY]))))[0-9][A-BD-HJLNP-UW-Z]{2}))$/),null===match?_getInvalidDataFromFields([this],"postcode"):[]},smallText:function(){var entry=_getFieldValue(this.$source),maxLen=256;return entry.length>maxLen?_getInvalidDataFromFields([this],"smallText"):[]},largeText:function(){var entry=_getFieldValue(this.$source),maxLen=500;return entry.length>maxLen?_getInvalidDataFromFields([this],"largeText"):[]},firstNameText:function(){var entry=_getFieldValue(this.$source),maxLen=35;return entry.length>maxLen?_getInvalidDataFromFields([this],"firstNameText"):[]},middleNameText:function(){var entry=_getFieldValue(this.$source),maxLen=100;return entry.length>maxLen?_getInvalidDataFromFields([this],"middleNameText"):[]},lastNameText:function(){var entry=_getFieldValue(this.$source),maxLen=35;return entry.length>maxLen?_getInvalidDataFromFields([this],"lastNameText"):[]},prevFirstNameText:function(){var isRequired=$("#previousName_hasPreviousName_true:checked").length>0,entry=_getFieldValue(this.$source),maxLen=35,result=[];return isRequired&&(0===entry.length?result=_getInvalidDataFromFields([this],"nonEmpty"):entry.length>maxLen&&(result=_getInvalidDataFromFields([this],"prevFirstNameText"))),result},prevMiddleNameText:function(){var entry=_getFieldValue(this.$source),maxLen=100;return entry.length>maxLen?_getInvalidDataFromFields([this],"prevMiddleNameText"):[]},prevLastNameText:function(){var isRequired=$("#previousName_hasPreviousName_true:checked").length>0,entry=_getFieldValue(this.$source),maxLen=35,result=[];return isRequired&&(0===entry.length?result=_getInvalidDataFromFields([this],"nonEmpty"):entry.length>maxLen&&(result=_getInvalidDataFromFields([this],"prevLastNameText"))),result},validCountry:function(){var entry=_getFieldValue(this.$source),countries=GOVUK.registerToVote.countries,isValid=!1;return $.each(countries,function(idx,country){isValid||$.each(country.tokens,function(idx,token){return entry.toLowerCase()===token.toLowerCase()?(isValid=!0,!1):void 0})}),isValid?[]:_getInvalidDataFromFields([this],"validCountry")}},fieldset:{atLeastOneNonEmpty:function(){var _fieldIsShowing,oneFilled=!1,childFields=validation.fields.getNames(this.children);return _fieldIsShowing=function(fieldObj){return!fieldObj.$source.is(":hidden")},this.$source.is(":hidden")?[]:($.each(childFields,function(idx,fieldObj){var isFilledFailedRules,method="nonEmpty";"fieldset"===fieldObj.type&&(method=$.inArray("allNonEmpty",fieldObj.rules)>-1?"allNonEmpty":"radioNonEmpty"),isFilledFailedRules=fieldObj[method](),_fieldIsShowing(fieldObj)&&!isFilledFailedRules.length&&(oneFilled=!0)}),oneFilled?[]:_getInvalidDataFromFields([this],"atLeastOneNonEmpty"))},atLeastOneTextEntry:function(){var _checkChildren,_fieldHasEntry,childFields=validation.fields.getNames(this.children),oneHasEntry=!1;return _checkChildren=function(children){$.each(children,function(idx,child){if(child.children)_checkChildren(validation.fields.getNames(child.children));else if(_fieldHasEntry(child))return!1})},_fieldHasEntry=function(field){var invalidRules,hasRule=$.inArray("nonEmpty",field.rules)>-1,fieldType=field.$source.attr("type");return"text"===fieldType&&hasRule?(invalidRules=field.nonEmpty(),invalidRules.length?!1:(oneHasEntry=!0,!0)):void 0},_checkChildren(childFields),oneHasEntry?[]:_getInvalidDataFromFields([this],"atLeastOneTextEntry")},atLeastOneChecked:function(){var invalidRules=this.atLeastOneNonEmpty();return $.map(invalidRules,function(fieldObj){return"input"!==fieldObj.$source[0].nodeName.toLowerCase()?fieldObj:void 0}),invalidRules.length?invalidRules:[]},radioNonEmpty:function(){var radioOptions=validation.fields.getNames(this.children),oneSelected=!1;return $.each(radioOptions,function(idx,radioOption){return radioOption.$source.is(":checked")?(oneSelected=!0,!1):void 0}),oneSelected?[]:_getInvalidDataFromFields([this],"radioNonEmpty")},checkedOtherIsValid:function(){var otherCountries,otherCountriesFailedRules,i,j,childFields=validation.fields.getNames(this.children),otherIsChecked=!1;for(i=0,j=childFields.length;j>i;i++){var fieldObj=childFields[i];if("otherCountries"===fieldObj.name)otherCountries=fieldObj;else{if("other"!==fieldObj.name)continue;otherIsChecked=""!==_getFieldValue(fieldObj.$source)}}return otherIsChecked?(otherCountriesFailedRules=validation.applyRules(otherCountries),otherCountriesFailedRules.length?otherCountriesFailedRules:[]):[]},allNonEmpty:function(){var rulesToReport,_fieldIsShowing,fieldsetObj,i,j,childFields=validation.fields.getNames(this.children),childFailedRules=[],fieldsThatNeedInput=0,emptyFields=0;if(_fieldIsShowing=function(fieldObj){return!fieldObj.$source.is(":hidden")},!_fieldIsShowing(this))return[];for(i=0,j=childFields.length;j>i;i++){var isFilledFailedRules,fieldObj=childFields[i],method="nonEmpty";"fieldset"===fieldObj.type&&(method=$.inArray("allNonEmpty",fieldObj.rules)>-1?"allNonEmpty":"radioNonEmpty"),isFilledFailedRules=fieldObj[method](),-1!==$.inArray(method,fieldObj.rules)&&(fieldsThatNeedInput++,isFilledFailedRules=fieldObj[method](),_fieldIsShowing(fieldObj)&&isFilledFailedRules.length&&(emptyFields++,$.merge(childFailedRules,isFilledFailedRules)))}return childFailedRules.length?(fieldsThatNeedInput>emptyFields?(rulesToReport=childFailedRules,fieldsetObj={name:this.name,$source:this.$source}):(rulesToReport=_getInvalidDataFromFields(childFailedRules,"allNonEmpty"),fieldsetObj={name:this.name,rule:"allNonEmpty",$source:this.$source}),this.$source.hasClass("inline-fields")&&rulesToReport.push(fieldsetObj),rulesToReport):[]},fieldOrExcuse:function(){var fieldIsInvalid,excuseIsInvalid,_fieldIsShowing,childFields=validation.fields.getNames(this.children),field=childFields[0],excuse=childFields[1],fieldFailedRules=validation.applyRules(field),excuseFailedRules=validation.applyRules(excuse);return _fieldIsShowing=function(fieldObj){return!fieldObj.$source.is(":hidden")},fieldIsInvalid=fieldFailedRules.length>0&&_fieldIsShowing(field),excuseIsInvalid=excuseFailedRules.length>0,fieldIsInvalid?_fieldIsShowing(excuse)?excuseIsInvalid?void 0:[]:[{name:this.name,rule:"fieldOrExcuse",$source:field.$source}]:[]},atLeastOneCountry:function(){var countryValidationFields,$countryTextboxes,$filledCountries;return this.$source.is(":hidden")?[]:(countryValidationFields=GOVUK.registerToVote.validation.fields.getNames(this.children),$filledCountries=$.map(countryValidationFields,function(field){return $countryTextboxes=void 0===$countryTextboxes?$(field.$source):$countryTextboxes.add(field.$source),""===_getFieldValue(field.$source)?null:field.$source}),0===$filledCountries.length?_getInvalidDataFromFields([this],"atLeastOneCountry"):[])},allCountriesValid:function(){{var countryValidationFields,getInvalidCountries,$invalidCountries;GOVUK.registerToVote.countries}return getInvalidCountries=function(countryValidationFields){var invalidCountryObj,$results;return $.each(countryValidationFields,function(idx,field){var entry=_getFieldValue(field.$source);return""===entry?!0:(invalidCountryObj=field.validCountry(),void(invalidCountryObj.length&&($results=void 0===$results?$(field.$source):$results.add(field.$source))))}),void 0!==$results?$results:!1},this.$source.is(":hidden")?[]:(countryValidationFields=GOVUK.registerToVote.validation.fields.getNames(this.children),$invalidCountries=getInvalidCountries(countryValidationFields),$invalidCountries?_getInvalidDataFromFields([this,{name:"country",$source:$invalidCountries}],"allCountriesValid"):[])},correctAge:function(){var children=validation.fields.getNames(this.children),day=parseInt(_getFieldValue(children[0].$source),10),month=parseInt(_getFieldValue(children[1].$source),10),year=parseInt(_getFieldValue(children[2].$source),10),dob=new Date(year,month-1,day).getTime(),now=(new Date).getTime(),minAge=16,maxAge=115,age=now-dob;return age=Math.floor(age/1e3/60/60/24/365.25),isValid=age>=minAge&&maxAge>=age,isValid?[]:_getInvalidDataFromFields(children,"correctAge")},max5Countries:function(){var totalCountryFields,totalCountries,getPrimaryCountries,getOtherCountries;return getPrimaryCountries=function(){return GOVUK.registerToVote.validation.fields.getNames(["british","irish"])},getOtherCountries=function(){var otherCountries=GOVUK.registerToVote.validation.fields.getNames(["otherCountries"]);return GOVUK.registerToVote.validation.fields.getNames(otherCountries[0].children)},totalCountryFields=$.merge(getPrimaryCountries(),getOtherCountries()),totalCountries=$.grep(totalCountryFields,function(field){return 0===field.nonEmpty().length}),totalCountries.length>5?_getInvalidDataFromFields([this],"max5Countries"):[]},atLeastOneValid:function(){var children=validation.fields.getNames(this.children),totalInvalidRules=[];return $.each(children,function(idx,child){var invalidRules=validation.applyRules(child);invalidRules.length&&$.merge(totalInvalidRules,invalidRules)}),totalInvalidRules},firstChildValid:function(){var children=validation.fields.getNames(this.children),firstChildVaildRules=validation.applyRules(children[0]);return firstChildVaildRules.length?_getInvalidDataFromFields([this],"firstChildValid"):[]}},association:{fieldsetOrExcuse:function(){var fieldsetIsInvalid,excuseIsInvalid,_fieldIsShowing,memberFields=validation.fields.getNames(this.members),fieldset=memberFields[0],excuse=memberFields[1],fieldsetFailedRules=validation.applyRules(fieldset),excuseFailedRules=validation.applyRules(excuse);return _fieldIsShowing=function(fieldObj){return!fieldObj.$source.is(":hidden")},fieldsetIsInvalid=fieldsetFailedRules.length>0&&_fieldIsShowing(fieldset),excuseIsInvalid=excuseFailedRules.length>0,fieldsetIsInvalid?_fieldIsShowing(excuse)?excuseIsInvalid?fieldsetFailedRules:[]:fieldsetFailedRules:[]},dateOfBirthOrExcuse:function(){var _fieldIsShowing,_entryInDateOfBirth,_entryInExcuse,memberFields=validation.fields.getNames(this.members),dateOfBirthField=memberFields[0],excuseField=memberFields[1],dateOfBirthInvalidRules=validation.applyRules(dateOfBirthField),excuseInvalidRules=validation.applyRules(excuseField);return _fieldIsShowing=function(fieldObj){return!fieldObj.$source.is(":hidden")},_entryInDateOfBirth=function(){return dateOfBirthInvalidRules.length<4},_entryInExcuse=function(){return excuseInvalidRules.length<2},_entryInDateOfBirth()?dateOfBirthInvalidRules:_fieldIsShowing(excuseField)&&_entryInExcuse()?excuseInvalidRules:dateOfBirthInvalidRules},allNonEmpty:function(){var rulesToReport,_fieldIsShowing,fieldsetObj,i,j,memberFields=validation.fields.getNames(this.members),memberFailedRules=[],fieldsThatNeedInput=0,emptyFields=0;for(_fieldIsShowing=function(fieldObj){return!fieldObj.$source.is(":hidden")},i=0,j=memberFields.length;j>i;i++){var isFilledFailedRules,fieldObj=memberFields[i],method="nonEmpty";"fieldset"===fieldObj.type&&(method=$.inArray("allNonEmpty",fieldObj.rules)>-1?"allNonEmpty":"radioNonEmpty"),-1!==$.inArray(method,fieldObj.rules)&&(fieldsThatNeedInput++,isFilledFailedRules=fieldObj[method](),_fieldIsShowing(fieldObj)&&isFilledFailedRules.length&&(emptyFields++,$.merge(memberFailedRules,isFilledFailedRules)))}return memberFailedRules.length?(fieldsThatNeedInput>emptyFields?(rulesToReport=memberFailedRules,fieldsetObj={name:this.name,$source:this.$source}):(rulesToReport=_getInvalidDataFromFields(memberFailedRules,"allNonEmpty"),fieldsetObj={name:this.name,rule:"allNonEmpty"}),rulesToReport.push(fieldsetObj),rulesToReport):[]},allValid:function(){var fieldObj,failedRules,_fieldIsShowing,i,j,memberFields=validation.fields.getNames(this.members),memberFailedRules=[];for(_fieldIsShowing=function(fieldObj){return!fieldObj.$source.is(":hidden")},i=0,j=memberFields.length;j>i;i++)fieldObj=memberFields[i],failedRules=validation.applyRules(fieldObj),failedRules.length&&$.merge(memberFailedRules,failedRules);return _fieldIsShowing(fieldObj)?memberFailedRules:[]}}}}(),messages:{fullName:{allNonEmpty:message("ordinary_name_error_enterFullName")},firstName:{nonEmpty:message("ordinary_name_error_enterFirstName"),firstNameText:message("ordinary_name_error_firstNameTooLong")},middleName:{middleNameText:message("ordinary_name_error_middleNamesTooLong")},lastName:{nonEmpty:message("ordinary_name_error_enterLastName"),lastNameText:message("ordinary_name_error_lastNameTooLong")},previousQuestion:{atLeastOneNonEmpty:message("ordinary_previousName_error_answerThis")},previousName:{allNonEmpty:message("ordinary_previousName_error_enterFullName")},previousFirstName:{nonEmpty:message("ordinary_previousName_error_enterFirstName"),prevFirstNameText:message("ordinary_previousName_error_firstNameTooLong")},previousMiddleName:{prevMiddleNameText:message("ordinary_previousName_error_middleNamesTooLong")},previousLastName:{nonEmpty:message("ordinary_previousName_error_enterLastName"),prevLastNameText:message("ordinary_previousName_error_lastNameTooLong")},nameChangeReason:{nonEmpty:"Please provide a reason for changing your name"},dateOfBirthDate:{allNonEmpty:message("ordinary_dob_error_enterDateOfBirth")},day:{nonEmpty:message("ordinary_dob_error_enterDay")},month:{nonEmpty:message("ordinary_dob_error_enterMonth")},year:{nonEmpty:message("ordinary_dob_error_enterYear")},dateOfBirthExcuseReason:{nonEmpty:message("ordinary_dob_error_provideReason")},citizenDetail:{allNonEmpty:"Please answer this question"},citizenDateMulti:{allNonEmpty:"Please provide date you became a British citizen"},citizenDateDay:{nonEmpty:"Please enter a day"},citizenDateMonth:{nonEmpty:"Please enter a month"},citizenDateYear:{nonEmpty:"Please enter a year"},howBecameCitizen:{nonEmpty:"Please provide your explanation of how you became a British Citizen"},birthplace:{nonEmpty:"Please provide your town or city and county of birth"},excuseAgeAttempt:{radioNonEmpty:message("ordinary_dob_error_selectRange")},otherAddressQuestion:{atLeastOneNonEmpty:message("ordinary_otheraddr_error_pleaseAnswer")},contact:{atLeastOneNonEmpty:message("ordinary_contact_error_pleaseAnswer")},phoneNumber:{nonEmpty:message("ordinary_contact_error_enterYourPhoneNo"),telephone:message("ordinary_contact_error_enterValidPhoneNo")},emailAddress:{nonEmpty:message("ordinary_contact_error_enterYourEmail"),email:message("ordinary_contact_error_pleaseEnterValidEmail")},nationality:{atLeastOneNonEmpty:message("ordinary_nationality_error_pleaseAnswer")},otherCountries:{atLeastOneCountry:message("ordinary_nationality_error_pleaseAnswer"),allCountriesValid:message("ordinary_nationality_error_notValid")},ninoCode:{nonEmpty:message("ordinary_nino_error_noneEntered"),nino:message("ordinary_nino_error_incorrectFormat")},postalVote:{atLeastOneNonEmpty:message("ordinary_postalVote_error_answerThis")},waysToVote:{atLeastOneNonEmpty:"Please answer this question"},previouslyRegistered:{atLeastOneNonEmpty:"Please answer this question"},countrySelect:{nonEmpty:"Please select your country"},correspondenceAddressLinesFieldSet:{atLeastOneNonEmpty:"Please enter your address"},deliveryMethod:{atLeastOneNonEmpty:"Please answer this question"},country:{atLeastOneNonEmpty:message("ordinary_country_error_pleaseAnswer"),max5Countries:message("ordinary_nationality_error_noMoreFiveCountries")},postcode:{nonEmpty:message("ordinary_address_error_pleaseEnterYourPostcode"),postcode:message("ordinary_address_error_postcodeIsNotValid")},addressSelect:{nonEmpty:message("ordinary_address_error_pleaseSelectYourAddress")},addressManual:{atLeastOneTextEntry:message("ordinary_address_error_pleaseAnswer")},manualAddressMultiline:{atLeastOneNonEmpty:message("ordinary_address_error_atLeastOneLineIsRequired")},city:{nonEmpty:message("ordinary_address_error_cityIsRequired")},previousAddress:{atLeastOneNonEmpty:message("ordinary_address_error_pleaseAnswer"),nonEqual:message("ordinary_previousAddress_must_differ_error")},previousAddress:{nonEqual:message("ordinary_previousAddress_must_differ_error")},statement:{atLeastOneNonEmpty:"Please answer this question"},BFPOAddressLinesFieldSet:{atLeastOneNonEmpty:"Please enter the address"},BFPOAddressPostcode:{nonEmpty:"Please enter the postcode"},otherAddressLinesFieldSet:{atLeastOneNonEmpty:"Please enter the address"},otherAddressPostcode:{nonEmpty:"Please enter the postcode"},otherAddressCountry:{nonEmpty:"Please enter the country"},contactAddress:{atLeastOneNonEmpty:"Please answer this question"},serviceNumberAndRank:{allNonEmpty:"Please answer this question"},service:{atLeastOneNonEmpty:"Please answer this question"},regiment:{nonEmpty:"Please enter the regiment or corps"},job:{allNonEmpty:"Please answer this question"},jobTitle:{nonEmpty:"Please enter the job title or rank"},govDepartment:{nonEmpty:"Please enter the government department, agency or body"},payrollNumber:{nonEmpty:"Please enter the Payroll number/ Staff ID"},hasUkAddress:{atLeastOneNonEmpty:"Please answer this question"},registeredAbroad:{atLeastOneNonEmpty:"Please answer this question"}}},GOVUK.registerToVote.validation=validation}).call(this);