mkdir deploy\lib\commons-io-1.0
mkdir deploy\lib\commons-logging-1.0.3
mkdir deploy\lib\jakarta-log4j-1.2.8\dist\lib
mkdir deploy\lib\ibm-mq-for-java
mkdir deploy\config

copy lib\commons-io-1.0\*.jar deploy\lib\commons-io-1.0
copy lib\commons-logging-1.0.3\*.jar deploy\lib\commons-logging-1.0.3
copy lib\jakarta-log4j-1.2.8\dist\lib\*.jar deploy\lib\jakarta-log4j-1.2.8\dist\lib
copy lib\ibm-mq-for-java\*.jar deploy\lib\ibm-mq-for-java
copy lib\*.dll deploy\lib
copy config\* deploy\config
jar cmfv manifest.info deploy\lib\StringMover.jar StringMover.properties runStringMover.bat README.txt -C build .