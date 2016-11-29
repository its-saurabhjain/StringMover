Last update: November 29, 2004
Lee Grey
suntrust@leegrey.com


Overview
========
FANS is a File Availability Notification System.  Simply put, that 
means that FANS is a program that watches a directory for files with 
particular extensions.  When a file of interest is found, FANS moves 
the file to a pick-up directory and sends an MQ message containing the 
name of the file to the specified Queue Manager and Queue.  At this 
point, FANS is finished with that file.  If anything goes wrong in the 
process of moving the file or sending the notification message, the 
process is backed out, so that the next time around, the file will be 
retried.  FANS polls the directory at a rate that is specified by the 
user.

The ECLOut (ECPi Send) Process
==============================
Sierra Xchange drops files into the directory E:\ECLOut\Stage.

FANS periodically scans the directory for the presence of new files.
When it finds one, it takes the following steps:

  1. It reads the file's Type 99 record to extract the
     item count and total amount for the items in the
     file.  If FANS is unable to read the Type 99 record,
     it will skip the file.
  2. It moves the file from E:\ECLOut\Stage up one level to 
     E:\ECLOut.  This is the directory in which Connect:Direct
     looks for files.
  3. It builds an MQ message consisting of the following:

        SUNMQMSG               hard-coded string
	0001                   version id
	MMddyyyyHHmmss00       timestamp
	AFSECPI                hard-coded string
	~                      delimiter
	BBK.GRB.BBBK21.D040518.S74185.T1630.ISIX.ECP
	                       sample filename
	~                      delimiter
	#                      item count indicator
	01234567               8-digit, zero-padded item count
	~                      delimiter
	$                      dollar amount indicator
	00000000123456789      17-digit, zero-padded dollar amount
	~                      delimiter
	012345678              9-digit, zero-padded file size in bytes

   4. It sends this message to BMQM.  If the message send   
      fails, the file is moved back to E:\ECLOut\Stage, so  
      that FANS will reprocess it from scratch on the next  
      go around.

   5. FANS periodically checks the E:\ECLOut directory for  
      expired files and deletes them.  What constitutes an  
      expired file is a configurable parameter.  We are     
      currently using seven days as the age to be deleted.

   6. FANS logs all activity in rolling log files that      
      change daily.  Actually, I believe the current setting
      is for hourly rolling files.

   7. FANS is configured using a file called                
      fans.properties.  Documentation for FANS is attached.


Configuration File
==================
The operation of FANS is controlled by the fans.properties file 
located in fans.jar.  If the contents of fans.properties need to be 
modified, fans.jar can be unjarred with the following command:

	jar xvf fans.jar

This command should be issued in the directory from which fans is to
run.  It is acceptable to add a relative or fully qualified path to 
fans.jar on the command line to allow the jar command to locate the
file.

If the file is unjarred, fans.jar should then be deleted from the 
directory to prevent confusion about which file is being accessed.

With the jar file unjarred and deleted, it is necessary to move to the 
deployment directory and run the program from there.  The command to 
run the unjarred program is:

	java -cp ".;commons-io-1.0.jar;commons-logging.jar;commons-
	logging-api.jar;com.ibm.mq.jar;connector.jar;log4j-1.2.8.jar" 
    com.suntrust.fans.FANS

For convenience, a batch file with this command is included in 
fans.jar and named runNoJar.bat.

Configuration Parameters
========================
The configuration file, fans.properties, contains comments that 
describe each parameter.  Here is more detail about the parameters 
contained in that file:

	Parameter		Description
	---------		-----------
	path.watch		This is the relative or fully-
					qualified path to the directory that 
					FANS is to watch for files

	path.send		This is the relative or fully-
					qualified path to the directory that 
					FANS copies files to for mainframe 
					retrieval

	poll.period		This is the period, in seconds, 
					between scans of the watch directory
					
	file.expiration		This is the age, in seconds, at
					which a file is considered expired
					and eligible to be deleted.  FANS scans
					the path.send directory using the same
					poll.period that it used to look for new
					files in the path.watch directory.  In this
					case, however, FANS is looking for old
					files that need to be deleted.  Only files
					older than this expiration value will
					be deleted.

	file.extensions		This is a comma-separated list of file 
					extensions that FANS will operate on 
					in the watch directory.  Do not 
					include the period in the extension, 
					and do not put spaces between 	
					extensions.  For example, this would 
					be a valid value for file.extensions:
						ecp,ecpp,ecpr
					A blank list of file extensions indicates
					that all extensions will be handled by
					FANS.  Note that a blank list of extensions
					is *not* the same thing as not having the
					file.extensions setting in the properties
					file.  If the setting is not present, a
					default value will be used.  This is not
					recommended.

	mq.channel		MQ Channel
	mq.host			MQ Host
	mq.port			MQ Port
	mq.queuemgr		MQ Queue Manager
	mq.queue		MQ Queue


Building the Application
========================
FANS was written and tested using Eclipse v3.0.  Given the simplicity 
of the application, no ant script was created.  If Eclipse is not 
available, a simple javac command should be all that is required to 
recompile from the sources.

JUnit tests
===========
A fair amount of unit testing is found in the test directory.  Again, 
Eclipse was used to run the unit tests, so no ant script exists.

Deploying the Application
=========================
A simple batch file called deploy.bat is found in the root directory 
of the FANS project.  This batch file puts all the required files into 
the deploy directory located under the main project directory.

To deploy this application to a server, all that is required is to 
copy the entire contents of the deploy directory to the desired 
location on the server.  Of course, a Java Runtime Environment (JRE) 
is required to run the application on the target machine, as is the 
IBM MQ Series client software.

Running the Application
=======================
Running FANS is as simple as issuing the following command:

	java -jar fans.jar

It is acceptable to issue this command from a directory other than the 
one in which the jar files were copied, in which case it is necessary 
to add the relative or fully qualified path to fans.jar to the above, 
such as

	java -jar C:\utilities\fans.jar

Logging
=======
Be default, FANS logs it activities to a rolling file called FANS.log 
located in the directory from which the program is started, as well as 
to stdout and chainsaw.  All of the logging settings can be modified 
in log4j.properties located in fans.jar.
