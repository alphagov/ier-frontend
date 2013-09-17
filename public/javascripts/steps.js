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