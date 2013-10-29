/* Scripts used throughout IER */

// header search toggle
$('.js-header-toggle').on('click', function(e) {
  e.preventDefault();
  $($(e.target).attr('href')).toggleClass('js-visible');
  $(this).toggleClass('js-hidden');
});

var $searchFocus = $('.js-search-focus');
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
