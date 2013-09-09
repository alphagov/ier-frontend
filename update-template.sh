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


if [ -z "$(cat .gitmodules | grep "https://github.com/alphagov/govuk_template_play.git")" ]; then
  removeSubmodule govuk_template_play
  git submodule add https://github.com/alphagov/govuk_template_play.git  
fi

cd "$thisDir"
echo "Updating govuk_template_play"
git submodule init
git submodule update
