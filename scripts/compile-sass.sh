thisDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$thisDir/.."

TOOLKIT=assets/govuk_frontend_toolkit
SASS=assets/sass
CSS=public/stylesheets
SHEETS=( $(ls $SASS | grep -i "scss") )
WORKED=1

for FILE in "${SHEETS[@]}"
do
  SHEET=${FILE%.*}
  echo "Compiling sass from $SASS/$SHEET.scss to $CSS/$SHEET.css"
  sass --style expanded --line-numbers --load-path $TOOLKIT/stylesheets $SASS/$SHEET.scss $CSS/$SHEET.css
  WORKED=$?
  if [ $WORKED -eq 0 ]
  then
    echo "sass $SHEET.scss compiled to $SHEET.css"
  fi
done
