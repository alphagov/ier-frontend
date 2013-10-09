#!/bin/bash

thisDir="$( dirname "$0")"
playVersion=$(<$thisDir/playVersion)
playdir="$thisDir/../bin"
playsource="http://downloads.typesafe.com/play/$playVersion/"
playartifact="play-$playVersion"
playextension=".zip"
playcache="download-cache"

mkdir -p "$playdir/$playcache"

playzip=$playartifact$playextension

if [ ! -f "$playdir/$playcache/$playzip" ]; then
	dest=$playdir/$playcache/$playzip
	echo "Downloading $playsource$playzip"
	curl -# -o $dest "$playsource$playzip"
fi
if [ ! -f "$playdir/$playartifact" ]; then
  echo "Unzipping to $playdir"
	unzip -q -d $playdir "$playdir/$playcache/$playzip"
fi

echo "Play is installed"
