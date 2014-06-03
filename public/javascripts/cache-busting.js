// Scripts to ensure requests made to the application from pressing the back button.

// 1. Make browsers that ignore no-cache headers (and still use bfcache) to not use bfcache

if (typeof window.addEventListener !== 'undefined') {
  window.addEventListener('unload', function(){}, false);
}

// 2. Force browsers to honour checked="checked" attributes on radios/checkboxes for fresh-page
//    requests coming from pressing the back button

$('input[type="radio"], input[type="checkbox"]')
  .filter('[checked]')
  .each(function () {
    this.checked = 'checked'; 
  });
