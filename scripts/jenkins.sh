echo "Build IER for Jenkins"

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

echo "Building Package"
./scripts/play/play "dist"
