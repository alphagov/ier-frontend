echo "Build IER for Jenkins"

echo "Running Maven Build";
cd ier-api; mvn clean package;
echo "Packaging Build"
cd ier-api-service/target;
# Delete old zip if it exists
rm -Rf ier-api-service ier-api-service.zip
mkdir -p ier-api-service; cp -R ier-api-service*.jar lib ier-api-service
zip -r ier-api-service.zip ier-api-service/*
