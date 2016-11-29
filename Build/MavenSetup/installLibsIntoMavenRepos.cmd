REM ========================================================
REM Libs
REM ========================================================
call mvn install:install-file -DgroupId=commons-logging -DartifactId=commons-logging -Dversion=1.0.3 -Dpackaging=jar -DgeneratePom=true -Dfile=artifacts\commons-logging\1.0.3\commons-logging.jar
call mvn install:install-file -DgroupId=com.ibm -DartifactId=mq -Dversion=5.300 -Dpackaging=jar -DgeneratePom=true -Dfile=artifacts\ibm-mq-for-java\5.300\com.ibm.mq.jar
call mvn install:install-file -DgroupId=javax.resource -DartifactId=connector -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true -Dfile=artifacts\javax\resource\connector\1.0\connector.jar
call mvn install:install-file -DgroupId=org.apache.commons -DartifactId=io -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true -Dfile=artifacts\commons-io-1.0\commons-io-1.0.jar
call mvn install:install-file -DgroupId=org.apache.commons -DartifactId=commons-lang -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true -Dfile=artifacts\commons-lang-2.1\commons-lang-2.1.jar

pause