#Install dependencies
(cd dependometer-core && mvn install -DskipTests=true)

#Run tests
(cd dependometer-java && mvn test)
 
