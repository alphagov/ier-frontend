$(function(){

	var countries = ["Afghanistan","Åland Islands","Albania","Algeria","American Samoa","Andorra","Angola","Anguilla","Antarctica","Antigua and Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia","Bonaire","Bosnia and Herzegovina","Botswana","Bouvet Island","Brazil","British Indian Ocean Territory","Brunei Darussalam","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon","Canada","Cape Verde","Cayman Islands","Central African Republic","Chad","Chile","China","Christmas Island","Cocos (Keeling) Islands","Colombia","Comoros","Republic of the Congo","Democratic Republic of the Congo","Cook Islands","Costa Rica","Côte d'Ivoire","Croatia","Cuba","Curaçao","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Ethiopia","Falkland Islands (Malvinas)","Faroe Islands","Fiji","Finland","France","French Guiana","French Polynesia","French Southern Territories","Gabon","Gambia","Georgia","Germany","Ghana","Gibraltar","Greece","Greenland","Grenada","Guadeloupe","Guam","Guatemala","Guernsey","Guinea","Guinea-Bissau","Guyana","Haiti","Heard Island and McDonald Islands","Vatican City","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Isle of Man","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kiribati","Kuwait","Kyrgyzstan","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macao","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands","Martinique","Mauritania","Mauritius","Mayotte","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Myanmar (Burma)","Namibia","Nauru","Nepal","Netherlands","New Caledonia","New Zealand","Nicaragua","Niger","Nigeria","Niue","Norfolk Island","North Korea","Northern Mariana Islands","Norway","Oman","Pakistan","Palau","Palestinian Territory","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Pitcairn","Poland","Portugal","Puerto Rico","Qatar","Réunion","Romania","Russian Federation","Rwanda","Saint Barthélemy","Saint Helena","Saint Kitts and Nevis","Saint Lucia","Saint Martin","Saint Pierre and Miquelon","Saint Vincent and the Grenadines","Samoa","San Marino","São Tomé and Príncipe","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Sint Maarten","Slovakia","Slovenia","Solomon Islands","Somalia","South Africa","South Georgia","South Korea","South Sudan","Spain","Sri Lanka","Sudan","Suriname","Svalbard and Jan Mayen","Swaziland","Sweden","Switzerland","Syrian Arab Republic","Taiwan","Tajikistan","Tanzania","Thailand","Timor-Leste","Togo","Tokelau","Tonga","Trinidad and Tobago","Tunisia","Turkey","Turkmenistan","Turks and Caicos Islands","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom","United States","United States Minor Outlying Islands","Uruguay","Uzbekistan","Vanuatu","Venezuela","Viet Nam","Virgin Islands (British)","Virgin Islands (U.S.)","Wallis and Futuna","Western Sahara","Yemen","Zambia","Zimbabwe"];
	var validCountries = ["Åland Islands","Antigua and Barbuda","Australia","Austria","Bahamas","Bangladesh","Barbados","Belgium","Belize","Bermuda","Botswana","British Indian Ocean Territory","Bulgaria","Cameroon","Canada","Cayman Islands","Cyprus","Czech Republic","Denmark","Dominica","Dominican Republic","Estonia","Falkland Islands (Malvinas)","Faroe Islands","Fiji","Finland","France","Gambia","Germany","Ghana","Gibraltar","Greece","Grenada","Guernsey","Guyana","Hungary","India","Ireland","Isle of Man","Italy","Jamaica","Jersey","Kenya","Kiribati","Latvia","Lesotho","Lithuania","Luxembourg","Malawi","Malaysia","Maldives","Malta","Mauritius","Montserrat","Mozambique","Namibia","Nauru","Netherlands","New Zealand","Nigeria","Pakistan","Papua New Guinea","Pitcairn","Poland","Portugal","Romania","Rwanda","Saint Helena","Saint Kitts and Nevis","Saint Martin","Saint Vincent and the Grenadines","Samoa","Seychelles","Sierra Leone","Slovakia","Slovenia","Solomon Islands","South Africa","Spain","Sri Lanka","Sweden","Tonga","Trinidad and Tobago","Tuvalu","Uganda","United Kingdom","Vanuatu","Virgin Islands (British)","Zambia","Zimbabwe"];

	var upperCaseValidCountries = [];

	for (var i = 0; i<validCountries.length; i++){

		upperCaseValidCountries.push(validCountries[i].toUpperCase());

	}

	var textInputTypes = ['text', 'search', 'number', 'datetime', 'datetime-local', 'date', 'month', 'week', 'time', 'tel', 'url', 'color', 'range'];
	$.expr[':'].textall = function (elem) {
		return $.inArray(elem.type, textInputTypes) != -1;
	};

	mockData = {"steps":{},"nationality":"British","dobDay":"6","dobMonth":"May","dobYear":"1992","firstName":"Joe","middleNames":"","lastName":"Lanman","nameChange":"true","nino":"AB 12 34 56 C","postcode":"n16 8gl","address":"Flat 6, Button Court, 177 Victorian Grove, London","telephone":"","email":"joelanman@gmail.com","voteByPerson":true,"voteByPost":"true","voteByProxy":false,"editedRegisterOptin":"false","hasOtherNationality":"true","otherCountry":"French Polynesia","previousFirstName":"Jim","previousLastName":"Jimson","movedRecently":"true","previousPostcode":"tq4 6db","previousAddress":"5 Alta Vista Road, Paignton","hasOtherAddress":"true","otherPostcode":"n4 4eb","otherAddress":"27 Ferme Park Road, London"};

	var root = '/register-to-vote';

	var makeURL = function(path){
		return (root + path);
	};

	$(window).hashchange( function(){

		console.log('window.statechange');

		var hash = location.hash;

		var path = hash.replace('#', ''),
			step = null;

		console.log(path);

		if (path == '' || path == '/'){

			step = 'step-start';

		} else {

			var pathParts = path.split('/');

			step = pathParts[0];

			if (pathParts[1] == 'edit'){
				$('.next-question .next').hide();
				$('.next-question .save').show();
			} else {
				$('.next-question .next').show();
				$('.next-question .save').hide();
			}
		}

		$('.questions-main .step').hide();
		$('#'+step).show();
		$(window).scrollTop(0);

		//progress

		if (step.indexOf('step') != 0){

			$('.progress').hide();

		} else {

			$('.progress').show();

			var progressStep = $('#'+step + ' .question-number').text();

			$('#progressStep').text(progressStep);

		}

		if (step == 'step-start' || step == 'complete'){

			$('.big-header').show();
			$('.small-header').hide();

		} else {

			$('.big-header').hide();
			$('.small-header').show();

		}

	});


	$('.back').on('click', function(e){
		e.preventDefault();
		History.back();
	});


	$('.show').on('click', function(e){

		e.preventDefault();

		var $ninoWrap = $(this).closest('.detail');

		if ($ninoWrap.find('.data.nino').text() != formData.NINO){

			$('.detail').css({'visibility': 'hidden'});

			$ninoWrap.css({'visibility': 'visible'});

			$ninoWrap.find('.data.nino').text(formData.NINO);

		} else {

			$('.detail').css({'visibility': 'visible'});

			$ninoWrap.find('.data.nino').text(redactedNino());

		}


	});


	// ---------- countries ----------

	$('.input-other-country').typeahead({
		hint: false,
		name: 'countries',
		local: countries,
		limit: 5
	}).on('typeahead:selected', function (e, data) {
		$(this).change();
	});

	var $otherCountryTemplate = $($('#otherCountryTemplate').html());

	var renumberOtherCountries = function(){

		var $otherCountries = $('#otherCountries .other-country:visible');

		$otherCountries.each(function(i){
			var $this = $(this);

			$this.find('.number').text(i+1);
		});

	}

	$('#addCountry').on('click', function(e){

		e.preventDefault();

		var $newCountry = $otherCountryTemplate.clone();

		$newCountry.appendTo($('#otherCountries'));


		$newCountry.find('.input-other-country').typeahead({
			name: 'countries',
			local: countries,
			limit: 5
		}).on('typeahead:selected', function (e, data) {
			$(this).change();
		});

		renumberOtherCountries();

	});

	$('#otherCountries').on('click', '.remove', function(e){

		e.preventDefault();

		var $this = $(this),
			$otherCountryElement = $(this).closest('.other-country'),
			country = $otherCountryElement.find('.input-other-country').val();

		for (var i = 0; i < formData.otherCountry.length; i++){
			if (formData.otherCountry[i] == country){
				formData.otherCountry.splice(i,1);
				renderData();
				break;
			}
		}

		$otherCountryElement.remove();
		renumberOtherCountries();

	});

	var monthNumbers = {
		'January':	0,
		'February': 1,
		'March':	2,
		'April':	3,
		'May':		4,
		'June':		5,
		'July':		6,
		'August':	7,
		'September':8,
		'October':	9,
		'November':	10,
		'December':	11
	}

	var validateStep = function(step, nextStep){

		console.log('validateStep (' + step + ',' + nextStep + ')');

		var $step = $('#step-' + step);
		$step.find('.validation-wrap').removeClass('invalid');
		$step.find('.validation-message').hide();

		var validation = {'valid': true,
						  'highlightFields' : [],
						  'messages' : [],
						  'redirectURL' : null};

		if (step == 'nationality'){

			// nationality

			if (!formData.otherCountry && !formData.nationality && !formData.noNationalityReason) {

				validation.valid = false;
				validation.messages.push('Please select your nationality');

			} else if (formData.otherCountry && !formData.nationality){

				var otherCountries = [].concat(formData.otherCountry),
					validOtherCountry = false;

				for (var i = 0; i<otherCountries.length; i++){

					console.log('checking ' + otherCountries[i]);

					if (upperCaseValidCountries.indexOf(otherCountries[i].toUpperCase()) != -1){
						validOtherCountry = true;
						break;
					}
				}

				if (!validOtherCountry){

					validation.valid = false;
					validation.redirectURL = 'exit-nationality';

				}
			}

		} else if (step == 'date-of-birth'){

			// date of birth

			if (formData.noDOBReason){

				if (!formData.DOBStatement){

					validation.valid = false;
					validation.messages.push('Please enter your age');

				} else if (formData.DOBStatement == 'under18' || formData.DOBStatement == 'dontKnow'){

					validation.valid = false;
					validation.redirectURL = 'exit-unknown-dob';

				}

			} else if (!formData.dobDay || ! formData.dobMonth || !formData.dobYear){

				// missing field

				validation.valid = false;
				validation.messages.push('Please enter your date of birth');

				if (!formData.dobDay){
					validation.highlightFields.push('#input-dob-day');
				}

				if (!formData.dobMonth){
					validation.highlightFields.push('#input-dob-month');
				}

				if (!formData.dobYear){
					validation.highlightFields.push('#input-dob-year');
				}

			} else {

				var dob = new Date(Number(formData.dobYear), monthNumbers[formData.dobMonth], Number(formData.dobDay)),
					now = new Date();

				if (now.getTime() - dob.getTime() < 1000 * 60 * 60 * 24 * 365 * 16){

					validation.valid = false;
					validation.redirectURL = 'exit-dob';
				}

			}

		} else if (step == 'name') {


			if (!formData.firstName || !formData.lastName){
			
				validation.valid = false;

				validation.messages.push('Please enter your name');

				if (!formData.firstName){
					validation.highlightFields.push('#input-first-name');
				}

				if (!formData.lastName){
					validation.highlightFields.push('#input-last-name');
				}

			}

		} else if (step == 'previous-name') {


			if (!formData.nameChange){
			
				validation.valid = false;

				validation.messages.push('Please answer this question');

			} else if (formData.nameChange == 'true' && (!formData.previousFirstName || !formData.previousLastName)){
			
				validation.valid = false;

				validation.messages.push('Please enter your previous name');

				if (!formData.previousFirstName){
					validation.highlightFields.push('#input-previous-first-name');
				}

				if (!formData.previousLastName){
					validation.highlightFields.push('#input-previous-last-name');
				}

			}

		} else if (step == 'nino') {

			if (!formData.NINO && !formData.noNINOReason){
			
				validation.valid = false;

				validation.messages.push('Please enter your National Insurance number');

				validation.highlightFields.push('#input-nino');

			}

		} else if (step == 'address') {


			if (!formData.address){
			
				validation.valid = false;

				validation.messages.push('Please enter your address');

				validation.highlightFields.push('#input-address-list');
				validation.highlightFields.push('#input-address-text');

			}

		} else if (step == 'previous-address') {

			if (!formData.movedRecently){
			
				validation.valid = false;

				validation.messages.push('Please answer this question');

			} else if (formData.movedRecently == 'true' && !formData.previousAddress){
			
				validation.valid = false;

				validation.messages.push('Please enter your previous address');

				validation.highlightFields.push('#input-previous-address-list');
				validation.highlightFields.push('#input-previous-address-text');

			}

		} else if (step == 'other-address') {

			if (!formData.hasOtherAddress){
			
				validation.valid = false;

				validation.messages.push('Please answer this question');

			} else if (formData.hasOtherAddress == 'true' && !formData.otherAddress){
			
				validation.valid = false;

				validation.messages.push('Please enter your second address');

				validation.highlightFields.push('#input-other-address-list');
				validation.highlightFields.push('#input-other-address-text');

			}

		} else if (step == 'open-register') {

			if (!formData.editedRegisterOptin){
			
				validation.valid = false;

				validation.messages.push('Please answer this question');

			}

		}

		if (validation.valid){

			$('#step-'+ step).find('.validation-message').hide();

			window.location.hash = nextStep;

		} else if (validation.redirectURL){

			window.location.hash = validation.redirectURL;

		} else {

			for (var i=0; i < validation.highlightFields.length; i++){

				$(validation.highlightFields[i]).closest('.validation-wrap').addClass('invalid');

			}

			$('#step-'+ step).find('.validation-message')
							 .css({'display':'inline-block'})
							 .text(validation.messages.join('<br/>'));

		}

	};

	$('.next-question .next').on('click', function(e){

		console.log('next-question.next.click');

		e.preventDefault();

		var $this = $(this),
			nextStep = $this.attr('data-next'),
			$currentStep = $this.closest('.step'),
			currentStep = $currentStep.attr('id').replace('step-','');

		validateStep(currentStep, nextStep);

	});

	$('.next-question .save').on('click', function(e){

		console.log('next-question.save.click');

		e.preventDefault();

		var $this = $(this),
			$currentStep = $this.closest('.step'),
			currentStep = $currentStep.attr('id').replace('step-','');

		validateStep(currentStep, "confirmation");

	});


	$('.change').on('click', function(e){

		e.preventDefault();

		History.pushState(null, null, makeURL($(this).attr('href')));

	});


	$('.start-again').on('click', function(e){

		e.preventDefault();

		delete localStorage['formData'];

		formData = $.extend(true, {}, defaultFormData);

		History.pushState(null, null, makeURL(""));

	});


	$('body').on('change', 'input,select,textarea', function(){

		var $this = $(this);

		if ($this.attr('type') == 'checkbox'){

			var $checkboxes = $('input[name="' + $this.attr('name') + '"]:checked');

			if ($checkboxes.length === 1){

				formData[$this.attr('name')] = $checkboxes.first().val();

			} else if ($checkboxes.length > 1) {

				var vals = [];

				$checkboxes.each(function(){
					vals.push($(this).val());
				});

				formData[$this.attr('name')] = vals;

			} else {

				delete formData[$this.attr('name')];

			}

		} else if ($this.is(':textall')){

			// comma seperate text values of the same name

			var $inputs = $('input[name="' + $this.attr('name') + '"]:visible'),
				formVal = null;

			if ($inputs.length === 1){

				formVal = $inputs.first().val() || null;

			} else if ($inputs.length > 1) {

				var vals = [];

				$inputs.each(function(){
					var val = $(this).val();
					if (val){
						vals.push(val);
					}
				});

				formVal = vals;

			}

			if (formVal) {

				formData[$this.attr('name')] = formVal;

			} else {

				delete formData[$this.attr('name')];

			}

		} else {

			formData[$this.attr('name')] = $this.val();

		}

		//localStorage['formData'] = JSON.stringify(formData);

		renderData();

	});


	$('input[type="text"]').on('keypress', function(e) {

		// simulate browser behaviour of submitting a form with one input when enter is pressed.

		var $this = $(this);

		if (e.which == 13) {
			var $step = $this.closest('.step'),
			$inputs = $step.find('input,select,textarea');

			if ($inputs.length == 1){
				$this.blur();
				$step.find('.next-question .button:visible').click();
			}
		}
	});


	$('.tab a').on('click', function(e){

		e.preventDefault();

		var $this = $(this),
			target = $this.attr('data-target'),
			$tabWrap = $this.closest('.tab-wrap'),
			$thisTab = $this.closest('.tab');

		$tabWrap.find('.tab').removeClass('selected');
		$tabWrap.find('.tab-content').hide();
		$thisTab.addClass('selected');
		$tabWrap.find('.tab-content-'+target).show();

		// weird postcode address stuff

		if (target == 'manual') {

			$('.address-step-2').show();

		} else if (target == 'postcode' && $('.postcode-search-results').is(':visible')){

			$('.address-step-2').show();

		} else if (target == 'postcode' && $('.postcode-search-results').is(':visible') == false){


			$('.address-step-2').hide();

		}

	});


	$('#otherCountry').on('click', function(e){

		$('#optionalSectionOtherCountry').toggle();

	})


	$('.address-not-listed').on('click', function(e){

		e.preventDefault();

		var $this = $(this),
			$step = $this.closest('.step');

		$step.find('.type-in-address').show();
		$step.find('.postcode-search-results').hide();

	});


	var addressLookup = function($step){

		if (!$step){

			console.log('no step specified - cannot run addressLookup');
			return;

		}

		var postcode = $step.find('.postcode').val();

		if(!postcode || postcode == ''){

			console.log('no postcode entered - cannot run addressLookup');
			return;

		}

		$.ajax({
			url : '/api/postcodeToAddressList/' /*+ 'wr26nj'*/ + postcode
		}).done(function(data){

			$step.find('.address-step-2').show();
			$step.find('.postcode-search-results').show();
			$step.find('.type-in-address').hide();

			var addressesHTML = '<option>Select your address</option><option>';

			addressesHTML += data.addresses.join('</option><option>') + '</option>';

			$step.find('.addressList').html(addressesHTML);

		});
	};


	$('.address-tabs .search-wrap .button').on('click', function(){

		addressLookup($(this).closest('.step'));

	});

	$('.postcode').on('keypress', function(e) {

		if (e.which == 13) {
			addressLookup($(this).closest('.step'));
		}

	});

	$('body').on('click', '.more-help-link a', function(e){

		e.preventDefault();

		var $this = $(this),
			$moreHelpWrap = $this.closest('.more-help-wrap'),
			$moreHelpContent = $moreHelpWrap.find('.more-help-content');

		$moreHelpContent.toggle();

		if ($moreHelpContent.is(':visible')){

			$moreHelpWrap.find('.more-help-link i').removeClass('icon-caret-right').addClass('icon-caret-down');

		} else {

			$moreHelpWrap.find('.more-help-link i').addClass('icon-caret-right').removeClass('icon-caret-down');

		}

	});


	$('body').on('change', 'input[type="checkbox"]', function(e){

		var $this = $(this);

		$this.closest('label').toggleClass('selected', $this.is(':checked'));

	});

	$('body').on('change', 'input[type="radio"]', function(e){

		var $this = $(this),
			$step = $this.closest('.step');

		$step.find('input[type="radio"][name="' + $this.attr('name') + '"]').closest('label').removeClass('selected');

		$this.closest('label').toggleClass('selected', $this.is(':checked'));

	});

	$('#no-nationality-link').on('click', function(e){
		e.preventDefault();
		$('#optional-section-no-nationality').toggle();
	});

	$('#noNinoLink').on('click', function(e){
		e.preventDefault();
		$('#optional-section-no-nino').toggle();
	});


	$('#no-dob-link').on('click', function(e){
		e.preventDefault();
		$('#optional-section-no-dob').toggle();
	});
	
	formData = null,//localStorage['formData'],
	defaultFormData = {

		'steps':{},

		'nationality': '',

		'dobDay': '',
		'dobMonth': '',
		'dobYear': '',

		'firstName': '',
		'middleNames': '',
		'lastName': '',
		'nameChange': '',

		'nino': '',

		'postcode': '',
		'address': '',

		'telephone': '',
		'email': '',

		'voteByPerson': true,
		'voteByPost': false,
		'voteByProxy': false,

		'editedRegisterOptin': null
	};


	var redactedNino = function(){

		return formData.NINO;

		// not redacting for now

		var redactedNino = '',
			nino = formData.NINO;

		if (nino){
			redactedNino = nino.substring(0,1) + nino.substring(1,nino.length-1).replace(/[a-z0-9]/gi,'*') + nino.substring(nino.length-1,nino.length);
		}
		return redactedNino;
	};

	data = function(newData){

		var render = {

			'nationality': function(){

				var nationalities = [];

				if (formData.nationality){
					nationalities = nationalities.concat(formData.nationality);
				}

				if (formData.otherCountry && formData.hasOtherNationality){
					nationalities = nationalities.concat(formData.otherCountry);
				}

				$('.data.nationality').text(nationalities.join(', '));

			}

		}

		for (name in newData){

			render[name](newData[name]);

		}

	}

	renderData = function(){

		// LOL performance

		var nationalities = [];

		if (formData.nationality){
			nationalities = nationalities.concat(formData.nationality);
		}

		if (formData.otherCountry && formData.hasOtherNationality){
			nationalities = nationalities.concat(formData.otherCountry);
		}

		$('.data.nationality').text(nationalities.join(', '));

		$('.data.name').text(formData.firstName + ' ' + formData.lastName);

		$('.name-change-yes').toggle(formData.nameChange == 'true');
		$('.name-change-no').toggle(formData.nameChange != 'true');

		$('.data.previous-name').text(formData.previousFirstName + ' ' + formData.previousLastName);

		// clear validation if we're toggling a section

		if ($('.optional-section-previous-name').is(':visible') != (formData.nameChange == 'true')){
			var $step = $('#step-previous-name');
			$step.find('.validation-wrap').removeClass('invalid');
			$step.find('.validation-message').hide();
		}

		$('.optional-section-previous-name').toggle(formData.nameChange == 'true');

		$('.data.dob').text(formData.dobDay + ' ' + formData.dobMonth + ' ' + formData.dobYear);

		$('.data.nino').text(redactedNino());

		// address

		$('.data.address').text(formData.address);
		$('.data.postcode').text(formData.postcode);

		// other address

		// clear validation if we're toggling a section

		if ($('.optional-section-other-address').is(':visible') != (formData.hasOtherAddress == 'true')){
			var $step = $('#step-other-address');
			$step.find('.validation-wrap').removeClass('invalid');
			$step.find('.validation-message').hide();
		}

		$('.optional-section-other-address').toggle(formData.hasOtherAddress == 'true');

		$('.other-address-yes').toggle(formData.hasOtherAddress == 'true');
		$('.other-address-no').toggle(formData.hasOtherAddress != 'true');

		$('.data.otherAddress').text(formData.otherAddress);
		$('.data.otherPostcode').text(formData.otherPostcode);

		// previous address

		// clear validation if we're toggling a section

		if ($('.optional-section-previous-address').is(':visible') != (formData.movedRecently == 'true')){
			var $step = $('#step-previous-address');
			$step.find('.validation-wrap').removeClass('invalid');
			$step.find('.validation-message').hide();
		}

		$('.optional-section-previous-address').toggle(formData.movedRecently == 'true');

		$('.moved-recently-yes').toggle(formData.movedRecently == 'true');
		$('.moved-recently-no').toggle(formData.movedRecently != 'true');

		$('.data.previousAddress').text(formData.previousAddress);
		$('.data.previousPostcode').text(formData.previousPostcode);

		//contact

		$('.data.telephone').text(formData.telephone);
		$('.data.email').text(formData.email);

		if (formData.telephone && formData.telephone !== ''){
			$('.data.contact-by-phone-yes').show();
		} else {
			$('.data.contact-by-phone-yes').hide();
		}

		if (formData.email && formData.email !== ''){
			$('.data.contact-by-email-yes').show();
		} else {
			$('.data.contact-by-email-yes').hide();
		}

		if ((!formData.telephone || formData.telephone === '') && (!formData.email || formData.email === '')) {
			$('.data.contact-by-post-yes').show();
		} else {
			$('.data.contact-by-post-yes').hide();
		}

		$('.data.vote-by-post-yes').toggle(formData.voteByPost == 'true');
		$('.data.vote-by-proxy-yes').toggle(formData.voteByProxy == 'true');
		$('.data.vote-by-person-yes').toggle(formData.voteByProxy != 'true' && formData.voteByPost != 'true');

		$('.data.edited-register-optin-yes').toggle(formData.editedRegisterOptin == 'true');

	}

	if (!formData) {
		formData = $.extend(true, {}, defaultFormData);
	} else {
		formData = JSON.parse(formData);
	}

	if (screen.width >= 600) {

		//$('.addressList').attr('size',5);

	}

	$(window).hashchange();

});