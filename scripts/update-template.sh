thisDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$thisDir/.."

function removeSubmodule() {
  submodule="$(cat .gitmodules | grep "path = .*$1" | grep -Eo "\S*$1")"
  if [[ -n $submodule ]]; then 
    rm -rv "$submodule"
    rm -rv ".gitmodules"
    rm -rv ".git/modules/$1"
    git rm "$submodule"
  fi
}

function checkSubmoduleExists() {
  submoduleUrl=$1
  submoduleName=$2
  if [ -z "$(cat .gitmodules | grep "$submoduleUrl")" ]; then
    removeSubmodule "$submoduleName"
    git submodule add "$submoduleUrl"
  fi
}

checkSubmoduleExists "https://github.com/alphagov/govuk_template_play.git" "govuk_template_play"
checkSubmoduleExists "https://github.com/alphagov/govuk_frontend_toolkit.git" "govuk_frontend_toolkit"

echo "Updating govuk_template_play"
git submodule init
git submodule update
