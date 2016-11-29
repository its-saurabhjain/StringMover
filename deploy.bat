mkdir deploy
copy lib\commons-io-1.0\*.jar deploy
copy lib\commons-logging-1.0.3\*.jar deploy
copy lib\jakarta-log4j-1.2.8\dist\lib\*.jar deploy
copy lib\ibm-mq-for-java\*.jar deploy
copy lib\*.dll deploy
jar cmfv manifest.info deploy\StringMover.jar StringMover.properties log4j.properties runNoJar.bat README.txt -C build .