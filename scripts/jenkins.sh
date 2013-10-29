#!/usr/bin/env bash
echo "Build IER for Jenkins"

read -d '' version << VERSION
gds.BuildNumber="$BUILD_NUMBER"
gds.GitCommit="$GIT_COMMIT"
gds.GitBranch="$GIT_BRANCH"
gds.BuildTime="$BUILD_ID"
VERSION

echo "$version" > conf/version.conf

echo "Created conf/version.conf with content:"
cat conf/version.conf

echo "Install Sass Gem"
bundle install --quiet
echo "Sass Gem Install"

echo "Rest of build done in Jenkins Shell"