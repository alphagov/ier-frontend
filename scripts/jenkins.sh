#!/usr/bin/env bash

echo "Build IER for Jenkins"

read -d '' version << VERSION
BuildNumber="$BUILD_NUMBER"
GitCommit="$GIT_COMMIT"
GitBranch="$GIT_BRANCH"
BuildTime="$BUILD_ID"
VERSION

echo "$version" > conf/version.conf

echo "Created conf/version.conf with content:"
cat conf/version.conf

echo "Install Sass Gem"
bundle install --quiet
echo "Sass Gem Install"

echo "Rest of build done in Jenkins Shell"
