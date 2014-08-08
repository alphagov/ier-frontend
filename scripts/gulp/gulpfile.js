var gulp = require('gulp');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');
var rimraf = require('gulp-rimraf');
var sass = require('gulp-ruby-sass');

var env = 'production';

var cssAssetsFolder = '../../app/assets';
var jsAssetsFolder = '../../assets';
var jsSourceFiles = [
  jsAssetsFolder + '/javascripts/cache-busting.js',
  jsAssetsFolder + '/javascripts/vendor/jquery/jquery-1.10.1.min.js',
  jsAssetsFolder + '/javascripts/vendor/jquery/typeahead.js',
  jsAssetsFolder + '/javascripts/vendor/mustache.js',
  jsAssetsFolder + '/javascripts/core.js',
  jsAssetsFolder + '/javascripts/countries.js',
  jsAssetsFolder + '/javascripts/validation.js',
  jsAssetsFolder + '/javascripts/forms.js',
  jsAssetsFolder + '/javascripts/onready.js',
];
var cssSourceFiles = [
  cssAssetsFolder + '/stylesheets/print.scss',
  cssAssetsFolder + '/stylesheets/mainstream.scss',
  cssAssetsFolder + '/stylesheets/application-ie6.scss',
  cssAssetsFolder +  '/stylesheets/application-ie7.scss',
  cssAssetsFolder + '/stylesheets/application-ie8.scss',
  cssAssetsFolder + '/stylesheets/application.scss'
];

var toolkit = cssAssetsFolder + '/govuk_frontend_toolkit/stylesheets';
var jsTargetFile = 'application.js';
var jsTargetFolder = '../../public/javascripts';
var cssTargetFolder = '../../target/scala-2.10/resource_managed/main/public/stylesheets';

gulp.task('cleanJs', function () {
  return gulp.src(jsTargetFolder + '/*.js', { read: false })
      .pipe(rimraf({ force: true }));
});

gulp.task('cleanCss', function () {
  return gulp.src(cssTargetFolder + '/*.css', { read: false })
      .pipe(rimraf({ force: true }));
});

gulp.task('clean', function () {
  gulp.start('cleanJs', 'cleanCss');
});

gulp.task('sass', function () {
  return gulpSrc = gulp.src(cssSourceFiles)
    .pipe(sass({
      style: (env === 'production') ? 'compressed' : 'expanded',
      lineNumbers: true,
      loadPath: toolkit
    }))
    .on('error', function (err) { console.log(err.message); })
    .pipe(gulp.dest(cssTargetFolder));
});

gulp.task('js', function () {
  return gulp.src(jsSourceFiles)
    .pipe(uglify())
    .pipe(concat(jsTargetFile))
    .pipe(gulp.dest(jsTargetFolder))
});

gulp.task('watch', ['build'], function () {
  var jsWatcher = gulp.watch([ jsAssetsFolder + '/**/*.js' ], ['js']);
  var cssWatcher = gulp.watch([ cssAssetsFolder + '/**/*.scss' ], ['sass']);
  var notice = function (event) {
    console.log('File ' + event.path + ' was ' + event.type + ' running tasks...');
  }

  env = 'development';
  cssWatcher.on('change', notice); 
  jsWatcher.on('change', notice); 
});

gulp.task('build', ['clean'], function () {
  gulp.start('sass', 'js');
});
