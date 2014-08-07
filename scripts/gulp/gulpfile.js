var gulp = require('gulp');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');

var sourceFiles = [
              '../../assets/javascripts/cache-busting.js',
              '../../assets/javascripts/vendor/jquery/jquery-1.10.1.min.js',
              '../../assets/javascripts/vendor/jquery/typeahead.js',
              '../../assets/javascripts/vendor/mustache.js',
              '../../assets/javascripts/core.js',
              '../../assets/javascripts/countries.js',
              '../../assets/javascripts/validation.js',
              '../../assets/javascripts/forms.js',
              '../../assets/javascripts/onready.js',
            ];
var targetFile = 'application.js';
var targetFolder = '../../public/javascripts';

gulp.task('default', function () {
  gulp.src(sourceFiles)
    .pipe(uglify())
    .pipe(concat(targetFile))
    .pipe(gulp.dest(targetFolder))
});
