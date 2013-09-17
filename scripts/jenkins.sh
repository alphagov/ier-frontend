echo "Build IER for Jenkins"

echo "Clean and update the build"
./play clean-files update;

echo "Compiling Ier Frontend"
./play compile;

echo "Testing Ier Frontend"
./play test;

echo "Building Package"
./play -DMODE=CACHED_MODE dist;
