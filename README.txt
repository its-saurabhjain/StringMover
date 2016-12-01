Overview
========================================================
StringMover is a File Delivery Service. It is program that 
watches a directory  for files with  particular extensions.
When a file of interest is found,StringMover moves sends an
MQ message containing  the file data  as MQ  message to the 
specified  Queue Manager and Queue, then  moves the file to
a backup directory. At  this point, StringMover is finished
with  that file. If anything  goes wrong  in the process of 
moving the  file or  sending the  notification message, the 
process is backed out, so that the next time around, the 
file will be retried. StringMover polls the directory at a 
rate that is specified by the user.

The StringMover Process
============================================================
APG drops files into the directory E:\ECLOut\Stage.

StringMover scans the directory for the presence of new files.
When it finds one, it takes the following steps:

  1. It reads the file's record to extract the 1st record from
     the file and create a MQ ping message.If StringMoveris unable 
     to read the file it will skip the file.
  2. It send the ping message to the Upload Q of the WMQ.
  3. Incase there are multiple files availabe, it will create and 
     send ping messages for each file.
  4. If the ping message has been sent to Q and the file has not moved
     duplicate message will not be sent.
  5. The service polls the WMQ download Q to check if a response message
     for the ping message has been received.
  6. CPCS code will uses this ping message to decide and will send the 
     following message as response to Ping message in the Download Q.
     0112016/05/23091215501130RC=xx 
     If
     xx=
     00 good record sent the rest of the string
     04 error – invalid record - don’t send any more of this string.
     08 error – trailer record not sent - don’t send any more of this string.
     12 error – duplicate entry - don’t send any more of this string.
  7. StringMover will receive the response message and once read will remove
     it from the Download Q
  8. If response message has xx==00, the StringMover java code will convert 
     the corresponding file to the below message and will send it to Upload Q
     On successful submission to MQ will move the file to  a backup location. 
  9. Incase response message has xx==04, or 08, 12 will only move the file to
     invalid file location
 10. StringMover periodically checks the configured directory for  
     new files for processing and once sent to MQ moves either to processed 
     directory or invalid location
 11. StringMover logs all activity in rolling log files that change daily.
     Actually, I believe the current setting is for hourly rolling files.
 12. StringMover is configured using a file called StringMover.properties


Configuration File
==================
The operation of StringMover is controlled by the StringMover.properties file 
located in StringMover.jar.  If the contents of StringMover.properties need to be 
modified, StringMover.jar can be unjarred with the following command:

	jar xvf StringMover.jar

This command should be issued in the directory from which StringMover is to
run.  It is acceptable to add a relative or fully qualified path to 
StringMover.jar on the command line to allow the jar command to locate the
file.

If the file is unjarred, StringMover.jar should then be deleted from the 
directory to prevent confusion about which file is being accessed.

With the jar file unjarred and deleted, it is necessary to move to the 
deployment directory and run the program from there.  The command to 
run the unjarred program is:

	java -cp ".;commons-io-1.0.jar;commons-logging.jar;commons-
	logging-api.jar;com.ibm.mq.jar;connector.jar;log4j-1.2.8.jar" 
    	com.xerox.StringMover.StringMover

For convenience, a batch file with this command is included in 
StringMover.jar and named runStringMover.bat.

Configuration Parameters
========================
The configuration file, StringMover.properties, contains comments that 
describe each parameter.  Here is more detail about the parameters 
contained in that file:

	Parameter		Description
	---------		-----------
	path.watch		This is the relative or fully-
					qualified path to the directory that 
					StringMover is to watch for files

	path.send		This is the relative or fully-
					qualified path to the directory that 
					StyringMover copies files to for mainframe 
					retrieval

	poll.period		This is the period, in seconds, 
					between scans of the watch directory
					
	file.expiration		This is the age, in seconds, at
					which a file is considered expired
					and eligible to be deleted.  StringMoverscans
					the path.send directory using the same
					poll.period that it used to look for new
					files in the path.watch directory.  In this
					case, however, StringMover is looking for old
					files that need to be deleted.  Only files
					older than this expiration value will
					be deleted.

	file.extensions		This is a comma-separated list of file 
					extensions that StringMover will operate on 
					in the watch directory.  Do not 
					include the period in the extension, 
					and do not put spaces between 	
					extensions.  For example, this would 
					be a valid value for file.extensions:
					ecp,ecpp,ecpr
					A blank list of file extensions indicates
					that all extensions will be handled by
					StringMover.  Note that a blank list of extensions
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
StringMover was written and tested using Eclipse v4.0.  Given the simplicity 
of the application, no ant script was created.  If Eclipse is not 
available, a simple javac command should be all that is required to 
recompile from the sources.


Deploying the Application
=================================================================================
A simple batch file called deploy.bat is found in the root directory 
of the StringMover project.  This batch file puts all the required files into 
the deploy directory located under the main project directory.

To deploy this application to a server, all that is required is to 
copy the entire contents of the deployment directory to the desired 
location on the server.  Of course, a Java Runtime Environment (JRE) 1.6
is required to run the application on the target machine, as is the 
IBM MQ Series client software.
The application is deployed as a windows NT service using installStringMoverService.bat
and can be undeployed using uninstallStringMoverService.bat under the 
deployment folder.
The service uses wrapper.exe as a wrapper and works for 32 bit, however for 64 bit
another wrapper wrapper-windows-x86-64.exe is required which is not available for community
edition while writing the application.
https://wrapper.tanukisoftware.com/doc/english/download.jsp#downloadNote1

The services writes log in wrapper.log file located in logs folder

Running the Application
=============================================================================================
Running StringMover is as simple as issuing the following command:

java -jar StringMover.jar

It is acceptable to issue this command from a directory other than the 
one in which the jar files were copied, in which case it is necessary 
to add the relative or fully qualified path to StringMover.jar to the above, 
such as
The application can also be executed as a standlone app using runStringMover.bat
	

Logging
==========================================================
By default, StringMover logs it  activities to a  rolling file called 
StringMover.log located in the directory from which the program is 
started, as well as to stdout and chainsaw. All of the logging settings
can be modified in log4j.properties located in StringMover.jar.
