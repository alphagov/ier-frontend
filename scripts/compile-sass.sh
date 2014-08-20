function usage {
  cat << EOF
  Usage: $0 [options]

  Compile SCSS to CSS for the IER project

  OPTIONS:
    -w  watch the files for changes and compile then

EOF
}

thisDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$thisDir"

INPUT=$thisDir/../app/assets/stylesheets
OUTPUT=$thisDir/../target/scala-2.10/resource_managed/main/public/stylesheets
TOOLKIT=$thisDir/../app/assets/govuk_frontend_toolkit/stylesheets
SHEETS=( mainstream application application-ie6 application-ie7 application-ie8)
WORKED=1
WATCH=1

while getopts "hw" OPTION; do
  case $OPTION in
    h )
      usage
      exit 1
      ;;
    w )
      WATCH=0
      ;;
  esac
done
shift $(($OPTIND-1))

for SHEET in "${SHEETS[@]}"
do
  if [ $WATCH -eq 0 ]
  then
    sass --style expanded --line-numbers --load-path $TOOLKIT --watch $INPUT/$SHEET.scss:$OUTPUT/$SHEET.css
  else
    sass --style expanded --line-numbers --load-path $TOOLKIT $INPUT/$SHEET.scss $OUTPUT/$SHEET.css
    WORKED=$?
    if [ $WORKED -eq 0 ]
    then
      echo "sass $SHEET.scss compiled to $SHEET.css"
    fi
    echo "at" `date`
  fi
done
