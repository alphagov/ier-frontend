thisDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


function removeSubmodule() {
  submodule="$(cat $thisDir/.gitmodules | grep "path = .*$1" | grep -Eo "\S*$1")"
  if [[ -n $submodule ]]; then 
    rm -rv "$thisDir/$submodule"
    rm -rv "$thisDir/.gitmodules"
    rm -rv "$thisDir/.git/modules/$1"
    git rm "$thisDir/$submodule"
  fi
}

function checkSubmodule() {
  submoduleUrl=$1
  submoduleName=$2
  if [ -z "$(cat .gitmodules | grep "$submoduleUrl")" ]; then
    removeSubmodule "$submoduleName"
    git submodule add "$submoduleUrl"
  fi
}

checkSubmodule "https://github.com/alphagov/govuk_template_play.git" "govuk_template_play"
checkSubmodule "https://github.com/alphagov/govuk_frontend_toolkit.git" "govuk_frontend_toolkit"

cd "$thisDir"
echo "Updating govuk_template_play"
git submodule init
git submodule update
