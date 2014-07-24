#!/bin/bash

thisDir="$( dirname "$0" )"
cd `pwd`

updateTemplate=$thisDir/scripts/update-template.sh
installSBT="$thisDir/scripts/install-sbt.sh"
sbt="$thisDir/scripts/bin/sbt/bin/sbt"

debugOpts="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9999"
repoOpts="-Dsbt.repository.config=$thisDir/project/repositories -Dsbt.override.build.repos=true"

if [ ! -f "$sbt" ]; then 
  echo "SBT not found. Installing SBT"
  echo `$installSBT` 
fi

export SBT_OPTS="$debugOpts $repoOpts"

$updateTemplate
$sbt "$@"
