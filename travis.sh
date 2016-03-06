#Install dependency not found in maven central
mvn install:install-file -Dfile=dependencies/org.eclipse.cdt.core-5.3.1.201109151620.jar -DpomFile=dependencies/org.eclipse.cdt.core-5.3.1.201109151620.pom

#Run test in all modules
mvn clean test
