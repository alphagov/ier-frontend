#!/bin/bash

thisDir="$( dirname "$0")"
playdir="$thisDir/../bin"
playsource="http://downloads.typesafe.com/play/2.1.5/"
playartifact="play-2.1.5"
playextension=".zip"
playcache="download-cache"

mkdir -p "$playdir/$playcache"

playzip=$playartifact$playextension

if [ ! -f "$playdir/$playcache/$playzip" ]; then
	dest=$playdir/$playcache/$playzip
	echo "Downloading $playsource/$playzip"
	curl -# -o $dest "$playsource$playzip"
fi
if [ ! -f "$playdir/$playartifact" ]; then
  echo "Unzipping to $playdir"
	unzip -q -d $playdir "$playdir/$playcache/$playzip"
fi

echo "Play is installed"
