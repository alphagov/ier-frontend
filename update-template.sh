thisDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd "$thisDir"
echo "Updating govuk_template_play"
git submodule init
git submodule update
