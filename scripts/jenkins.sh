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

echo "Clean and update the build"
./scripts/play/play "clean-files"
./scripts/play/play "update"

echo "Compiling Ier Frontend"
./scripts/play/play "compile"

echo "Testing Ier Frontend"
./scripts/play/play "test"

echo "Running Code coverage"
./scripts/play/play "jacoco:clean"
./scripts/play/play "jacoco:check"

echo "Building Package"
./scripts/play/play "dist"

