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


$('#step-previous-name #input-name-change-yes').on('click', function(e) {
    $('#step-previous-name #optionalSectionPreviousName').show();
});

$('#step-previous-name #input-name-change-no').on('click', function(e) {
    $('#step-previous-name #optionalSectionPreviousName').hide();
});

$('#step-address #find-address').on('click', function(e) {
    e.preventDefault();
    addressLookup($('#step-address'));
});

$('#step-previous-address #input-previous-address-no').on('click', function(e) {
    $('#optional-section-previous-address').hide();
});

$('#step-previous-address #input-previous-address-yes').on('click', function(e) {
    $('#optional-section-previous-address').show();
});

$('#step-previous-address #find-previous-address').on('click', function(e) {
    e.preventDefault();
    addressLookup($('#step-previous-address'));
});

$('#step-other-address #input-other-address-no').on('click', function(e) {
    $('#optional-section-previous-address').hide();
});

$('#step-other-address #input-other-address-yes').on('click', function(e) {
    $('#optional-section-previous-address').show();
});

$('#step-other-address #find-other-address').on('click', function(e) {
    e.preventDefault();
    addressLookup($('#step-other-address'));
});

function addressLookup($step) {

    var postcode = $step.find('.postcode').val();
    if(!postcode || postcode == ''){
        console.log('no postcode entered - cannot run addressLookup');
        return;
    }

    $.ajax({
        url : '/address/' + postcode.replace(/\s/g,'')
    }).done(function(data){

            $step.find('.address-step-2').show();
            $step.find('.postcode-search-results').show();
            $step.find('.type-in-address').hide();

            var addressesHTML = '<option>Select your address</option><option>';
            var addresses = [];

            if (data.addresses[0].addressLine){
                for (var i = 0; i < data.addresses.length; i++){
                    addresses.push(data.addresses[i].addressLine);
                }
            } else {
                addresses = data.addresses;
            }

            addressesHTML += addresses.join('</option><option>') + '</option>';
            $step.find('.addressList').html(addressesHTML);
        });
}