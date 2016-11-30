package com.xerox.StringMover;

import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TimerTask;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xerox.StringMover.Configurator;
import com.xerox.StringMover.MQMessagingService;
import com.xerox.StringMover.ScanForWork;
import com.xerox.util.io.FileParser;
import com.xerox.util.io.FileRecord;

public class ScanForWork extends TimerTask {

	private static Log log = LogFactory.getFactory().getInstance(ScanForWork.class);
	private static MQMessagingService mqms = null;
	private static Hashtable<String,FileRecord> fileList = new Hashtable<String,FileRecord>();

	public ScanForWork() {
		super();
		if( log.isDebugEnabled() ) {
			log.debug("ScanForWork()");
		}
		mqms = new MQMessagingService();
		
		if( mqms == null ) {
			if( log.isErrorEnabled() ) {
				log.error("Failed to initialize MQ Messaging Service");
			}
		}
		try {
			if( log.isDebugEnabled() ) {
				log.debug("constructing: " + Configurator.getInstance().getMQ_QUEUE_MANAGER());
				log.debug("constructing: " + Configurator.getInstance().getMQ_UPLOAD_QUEUE());
				log.debug("constructing: " + Configurator.getInstance().getMQ_DOWNLOAD_QUEUE());
				log.debug("constructing: " + Configurator.getInstance().getMQ_CHANNEL());
				log.debug("constructing: " + Configurator.getInstance().getMQ_HOST());
				log.debug("constructing: " + Configurator.getInstance().getMQ_PORT());
				log.debug("constructing: " + Configurator.getInstance().getMQ_USERID());
			}
			mqms.setQueueManagerName(Configurator.getInstance().getMQ_QUEUE_MANAGER());
			mqms.setMQChannel(Configurator.getInstance().getMQ_CHANNEL());
			mqms.setMQHost(Configurator.getInstance().getMQ_HOST());
			mqms.setMQPort(Configurator.getInstance().getMQ_PORT());
			mqms.setMQUserid(Configurator.getInstance().getMQ_USERID());
		} catch (Exception e) {
			if( log.isErrorEnabled() ) {
				log.error("Failed to configure MQ Messaging Service");
			}
		}
	}
	public void run() {
		if( log.isDebugEnabled() ) {
			log.debug("ScanForWork.run()");
		}
		boolean messageSent = false;
		// check directory for files with the desired extensions
		File getdir = new File(Configurator.getInstance().getWATCH_DIRECTORY());
		File newloc = new File(Configurator.getInstance().getSEND_DIRECTORY());
		File invalid = new File(Configurator.getInstance().getINVLAID_DIRECTORY());
		try
		{
			Collection files = DirectoryScanner.getFilesInDirectoryFIFO(getdir, Configurator.getInstance().getFILE_EXTENSIONS());
			int settleTime= Configurator.getInstance().getFILE_SETTLE_PERIOD();
			log.debug("Sleeping for " + settleTime + " seconds to let files settle before acting upon them...");
			Thread.sleep(1000L * settleTime);
			// keep trying until some file succeeds or no files are left to try 
			////////////////////////////////////////////////Send Ping Messages///////////////////////////////////
			FileRecord fileRecord;
			FileRecord hasFile;
			Iterator it = files.iterator();
			while(it.hasNext()) {
					// grab the next one
					File file = (File) it.next();
					String name = file.getName();
					// it is necessary to capture the file size here, before it is moved
					String filelen = Long.toString(file.length());
					if( log.isDebugEnabled() ) {
						log.debug("file length = " + file.length());
					}
					//Check if the message is in the process list or not and if not add it to the list, so that it will not be processed again in case of failure
					FileParser fileParser= new FileParser();
					//Parse File and create a Ping MQ Message
					String pingMessage = fileParser.parseFile(file.getAbsolutePath());
					String msgCor = pingMessage.substring(0, 25);
					hasFile = fileList.get(msgCor);
					//send Ping MQ message if it has not been sent
					if(hasFile == null)
					{
						messageSent = sendMessage(pingMessage);
						fileRecord = new FileRecord(name, file.getAbsolutePath());
						fileList.put(msgCor, fileRecord);
						log.debug("Send Ping message to the queue:" + pingMessage);
					}
				}
			////////////////////////////////////////Receive Ping Responses and send the main file and move the files based on response status////////////////////////////
			String receivedMessage = receiveMessage();
			if(receivedMessage != null)
			{
				String responseMessages[] = receivedMessage.split("\\_");
				if(responseMessages.length > 0)
				{
					for (int i= 0; i <= responseMessages.length; i++)
					{
						
						String message [] = responseMessages[i].split("\\=");
						String messageCorResp = message[0].substring(0, 25);
						String messageStatus = message[1];
						log.debug("Response messagefrom Queue & Status:" + messageCorResp + ":" + messageStatus);
						processFile(messageCorResp, messageStatus, newloc, invalid);
					}
				}
			}	
			else
			{
				if( log.isDebugEnabled() ) {
					log.debug("No message received from the Download Queue");
				}
				
			}
		}
		catch(Exception exp)
		{
			if( log.isErrorEnabled() ) {
				log.error("Error while polling the source folder :"+exp.getMessage());
			}
		}
	}
	
	private boolean sendMessage(String mqMessage) {
		boolean success = true;
		try {
				mqms.setQueueName(Configurator.getInstance().getMQ_UPLOAD_QUEUE());
				mqms.send(mqMessage);
			
		} catch (Exception e) {
			success = false;
			if( log.isErrorEnabled() ) {
				log.error("Failed to send MQ message for " + mqMessage, e);
			}
		}
		return success;
	}
	private String receiveMessage()
	{
		String messageReceived = null;
		try{
			mqms.setQueueName(Configurator.getInstance().getMQ_DOWNLOAD_QUEUE());	
			messageReceived = mqms.receive();
		}catch (Exception e){
			if( log.isErrorEnabled() ) {
				log.error("Failed to receive MQ message", e);
			}
		}
		return messageReceived;
	}
	
	private void processFile(String messageCorResp, String messageStatus, File newloc, File invalid)
	{
		//get the file name based on msgCor
		FileRecord rec = fileList.get(messageCorResp);
		FileParser fileParser = new FileParser();
		if(rec != null){
			//Valid record, continue sending the file
			if(messageStatus.contains("00")){
				//create MQ message from file
				File file1 = new File(rec.filePath);
				String mqMessage = fileParser.parseFile2(file1.getAbsolutePath());
				//send the message to MQ
				log.debug("Sending message to Queue:\t\n" + mqMessage);
				boolean messageSent = sendMessage(mqMessage);
				if(messageSent) {
					// move file to archive
					boolean success2 = fileParser.moveFile(file1, newloc, invalid);
					if( success2) {
						fileList.remove(messageCorResp);
						log.debug("File Moved to" + newloc.getAbsolutePath() + "\\" + file1.getName());
						if( log.isErrorEnabled() ) {
							log.error("Moved " + file1.getName() + " back to original position for retry");
						}
					} else {
						if( log.isErrorEnabled() ) {
							log.error(">>>>>                                                                       <<<<<");
							log.error(">>>>> MQ send failed AND file could not be moved to new position! <<<<<");
							log.error(">>>>> Potential loss of data in file " + file1.getName() + " <<<<<");
							log.error(">>>>>                                                                       <<<<<");
						}
					}
				}
			}
			//04:error – invalid record - don’t send any more of this string
			//08:error – trailer record not sent - don’t send any more of this string
			//12:error – duplicate entry - don’t send any more of this string.
			else if (messageStatus.contains("04") ||  messageStatus.contains("08") || messageStatus.contains("12")) 
			{
				File file1 = new File(rec.filePath);
				boolean success2 = fileParser.moveFile(file1, invalid, newloc);
				log.debug("File Moved to" + invalid.getAbsolutePath() + "\\" + file1.getName());
				if(success2) {
					//remove the record from the processing list
					fileList.remove(messageCorResp);
					if( log.isErrorEnabled() ) {
						log.error("Moved " + file1.getName() + " to Invalid Files Location");
					}
				} else {
					if( log.isErrorEnabled() ) {
						log.error(">>>>>                                                                       <<<<<");
						log.error(">>>>> MQ send failed AND file could not be moved back to new position! <<<<<");
						log.error(">>>>> Potential loss of data in file " + file1.getName() + " <<<<<");
						log.error(">>>>>                                                                       <<<<<");
					}
				}
			}
		}
	}
}
