package com.xerox.util.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.StringTokenizer;
import org.apache.commons.io.FileUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileParser {
	
	private static Log log = LogFactory.getFactory().getInstance(FileParser.class);
	public String parseFile(String fileLoc)
	{
		String messageText = "";
		BufferedReader read = null;
		   try{
			   read = new BufferedReader(new FileReader(fileLoc));
			   String line = null;
			   while( (line = read.readLine()) != null) {
			      StringTokenizer tokens = new StringTokenizer(line);
			      String record = tokens.nextToken();
					      if(!record.startsWith("*"))
					      {
					    	  messageText = record.substring(0, record.length());
					    	  read.close();
					    	  break;
					      }
			   		}
			   }catch (Exception e){
			     System.err.println("Error: " + e.getMessage());
			  }
		   
		return messageText;
	}
	public String parseFile2(String fileLoc)
	{
		String messageText = "";
		StringBuilder builder = new StringBuilder();
		   try{
			   
			   Charset charset = Charset.forName("UTF-8");
			   //byte[] encoded = Files.readAllBytes(Paths.get(fileLoc));
			   //return charset.decode(ByteBuffer.wrap(encoded)).toString();
			   return FileUtils.readFileToString(new File(fileLoc), "UTF-8");
			   
			   }catch (Exception e){
			     System.err.println("Error: " + e.getMessage());
			  }
		return messageText;
	}
	public boolean moveFile(File originalfile, File newlocation, File dupe) {
		// move it to the target location
		boolean success = false;
		//dupeCondition  = false;
		if( log.isInfoEnabled() ) {
			log.info("Moving " + originalfile.getPath() + " to " + newlocation.getAbsolutePath());
		}
		if(newlocation.exists())
		{
			success = originalfile.renameTo(new File(newlocation, originalfile.getName()));
			if( !success ) {
				//dupeCondition  = true;
				if( log.isErrorEnabled() ) {
					log.error("Failed to move file " + originalfile.getPath()+", duplicate condition detected or file is locked.");
				}
				
				File fileUndeDupFolder = new File(new StringBuffer().append(dupe.getAbsolutePath()).append("\\").append
						(originalfile.getName()).toString());
				if(fileUndeDupFolder.exists())
				{
					fileUndeDupFolder.delete();
				}
				if(originalfile.renameTo(new File(dupe, originalfile.getName())))
				{
					if( log.isInfoEnabled()) {
						log.info("File " + originalfile.getPath()+" has been moved to the duplicate folder");
					}
					success = true;
				}
			}
		}
		else
		{
			if( log.isErrorEnabled() ) {
				log.error("Destination folder "+newlocation.getAbsolutePath()+" is unavailable due to Network disconnect" +
						", Failed to move file " + originalfile.getPath());
			}
			success = false;
		}
		return success;
	}
	///Writes string to a file
	public void writeToFile(String mqMessage) throws IOException
	{
		BufferedWriter output = null;
		try {
            File file1 = new File("e:\\example.txt");
            output = new BufferedWriter(new FileWriter(file1));
            output.write(mqMessage);
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
          if ( output != null ) {
            output.close();
          }
        }
	}
}



