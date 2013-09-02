echo "Build IER for Jenkins"

echo "Compiling Ier Frontend";
./play compile;

echo "Testing Ier Frontend";
./play test