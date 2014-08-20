var gulp = require('gulp');
var uglify = require('gulp-uglifyjs');
var concat = require('gulp-concat');
var deleteFiles = require('gulp-rimraf');
var sass = require('gulp-ruby-sass');

var assetsFolder = '../../assets';
var jsSourceFiles = [
  assetsFolder + '/javascripts/cache-busting.js',
  assetsFolder + '/javascripts/vendor/jquery/jquery-1.10.1.min.js',
  assetsFolder + '/javascripts/vendor/jquery/typeahead.js',
  assetsFolder + '/javascripts/vendor/mustache.js',
  assetsFolder + '/javascripts/core.js',
  assetsFolder + '/javascripts/countries.js',
  assetsFolder + '/javascripts/validation.js',
  assetsFolder + '/javascripts/forms.js',
  assetsFolder + '/javascripts/onready.js',
];
var cssSourceFiles = [
  assetsFolder + '/stylesheets/print.scss',
  assetsFolder + '/stylesheets/mainstream.scss',
  assetsFolder + '/stylesheets/application-ie6.scss',
  assetsFolder +  '/stylesheets/application-ie7.scss',
  assetsFolder + '/stylesheets/application-ie8.scss',
  assetsFolder + '/stylesheets/application.scss'
];

var toolkit = '../../app/assets/govuk_frontend_toolkit/stylesheets';
var jsTargetFile = 'application.js';
var jsTargetFolder = '../../public/javascripts';
var cssTargetFolder = '../../public/stylesheets';

gulp.task('cleanJs', function () {
  return gulp.src(jsTargetFolder + '/*.js', { read: false })
      .pipe(deleteFiles({ force: true }));
});

gulp.task('cleanCss', function () {
  return gulp.src(cssTargetFolder + '/*.css', { read: false })
      .pipe(deleteFiles({ force: true }));
});

gulp.task('clean', function () {
  gulp.start('cleanJs', 'cleanCss');
});

gulp.task('sass', function () {
  return gulpSrc = gulp.src(cssSourceFiles)
    .pipe(sass({
      style: 'compressed',
      lineNumbers: true,
      loadPath: toolkit
    }))
    .on('error', function (err) { console.log(err.message); })
    .pipe(gulp.dest(cssTargetFolder));
});

gulp.task('js', function () {
  return gulp.src(jsSourceFiles)
    .pipe(concat(jsTargetFile))
    .pipe(uglify(jsTargetFile, {
      'mangle' : false
    }))
    .pipe(gulp.dest(jsTargetFolder))
});

gulp.task('watch', ['build'], function () {
  var jsWatcher = gulp.watch([ assetsFolder + '/**/*.js' ], ['js']);
  var cssWatcher = gulp.watch([ assetsFolder + '/**/*.scss' ], ['sass']);
  var notice = function (event) {
    console.log('File ' + event.path + ' was ' + event.type + ' running tasks...');
  }

  cssWatcher.on('change', notice); 
  jsWatcher.on('change', notice); 
});

gulp.task('build', ['clean'], function () {
  gulp.start('sass', 'js');
});