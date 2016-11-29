package com.xerox.StringMover;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class is implemented as a Singleton to ensure that only one
 * configuration exists within the application.
 */
public class Configurator {
	
	private static Log log = LogFactory.getFactory().getInstance(Configurator.class);
	private static Properties properties;

	private Configurator() {
		properties = loadProperties();
		if( log.isDebugEnabled() ) {
			log.debug(properties);
		}
	}
	
	static private Configurator _instance = null;
	
	static public Configurator getInstance() { 
		if (_instance == null) 
			_instance = new Configurator();
		return _instance;
	}

    private static String PROPERTY_FILE = "StringMover.properties";
    private static String WATCH_DIRECTORY = "path.watch";
    private static String BACKUP_DIRECTORY = "path.backup";
    private static String SEND_DIRECTORY = "path.send";
    private static String INVALID_DIRECTORY = "path.invalid";
    private static String DUPE_DIRECTORY = "path.dupe";
    private static String FILE_EXTENSIONS = "file.extensions";
    private static String POLLING_PERIOD = "poll.period";
    private static String POLLING_PERIOD_EXP = "poll.period.exp";
    private static String HR_BKP_EXP = "hr.bkp_exp";
    private static String MIN_BKP_EXP = "min.bkp_exp";
    private static String FILE_BACKUP_PERIOD = "file.backup";
    private static String FILE_EXPIRATION_PERIOD = "file.expiration";
    private static String MQ_CHANNEL = "mq.channel";
    private static String MQ_HOST = "mq.host";
    private static String MQ_PORT = "mq.port";
    private static String MQ_QUEUE_MANAGER = "mq.queuemgr";
    private static String MQ_UPLOAD_QUEUE = "mq.UploadQueue";
    private static String MQ_DOWNLOAD_QUEUE = "mq.DownloadQueue";
    private static String FILE_SETTLE_PERIOD= "file.settle.period";
    private static String MQ_MSG_POSTFIX_STR = "mq.msg.postfix.string";
    private static String MQ_MSG_PREFIX_STR = "mq.msg.prefix.string";
    private static String MQ_MSG_VERSION_STR = "mq.msg.version.string";
    
    public static String getMQ_MSG_PREFIX_STR() {
		return properties.getProperty(MQ_MSG_PREFIX_STR);
	}
	public static String getMQ_MSG_VERSION_STR() {
		return properties.getProperty(MQ_MSG_VERSION_STR);
	}

	public static String getMQ_MSG_POSTFIX_STR() {
		return properties.getProperty(MQ_MSG_POSTFIX_STR);
	}
	public static int getFILE_SETTLE_PERIOD() {
		return Integer.parseInt(properties.getProperty(FILE_SETTLE_PERIOD));
	}
	public static String getMQ_CHANNEL() {
		return properties.getProperty(MQ_CHANNEL);
	}
	public static String getMQ_HOST() {
		return properties.getProperty(MQ_HOST);
	}
	public static String getMQ_PORT() {
		return properties.getProperty(MQ_PORT);
	}
	public static String getMQ_QUEUE_MANAGER() {
		return properties.getProperty(MQ_QUEUE_MANAGER);
	}
	public static String getMQ_UPLOAD_QUEUE() {
		return properties.getProperty(MQ_UPLOAD_QUEUE);
	}
	public static String getMQ_DOWNLOAD_QUEUE() {
		return properties.getProperty(MQ_DOWNLOAD_QUEUE);
	}
	
	public static String getMQ_USERID() {
		String username = System.getProperty("user.name").toUpperCase();
		if( log.isDebugEnabled() ) {
			log.debug("System gives user.name as " + username);
		}
		return username;
	}
	public static int getPOLLING_PERIOD() {
		return Integer.parseInt(properties.getProperty(POLLING_PERIOD));
	}
	public static int getPOLLING_PERIOD_EXP() {
		return Integer.parseInt(properties.getProperty(POLLING_PERIOD_EXP));
	}
	public static int getHR_BKP_EXP() {
		return Integer.parseInt(properties.getProperty(HR_BKP_EXP));
	}
	public static int getMIN_BKP_EXP() {
		return Integer.parseInt(properties.getProperty(MIN_BKP_EXP));
	}
	public static int getFILE_BACKUP_PERIOD() {
		return Integer.parseInt(properties.getProperty(FILE_BACKUP_PERIOD));
	}
	public static int getFILE_EXPIRATION_PERIOD() {
		return Integer.parseInt(properties.getProperty(FILE_EXPIRATION_PERIOD));
	}
	public static String getWATCH_DIRECTORY() {
		return properties.getProperty(WATCH_DIRECTORY);
	}
	public static String getBACKUP_DIRECTORY() {
		return properties.getProperty(BACKUP_DIRECTORY);
	}
	public static String getSEND_DIRECTORY() {
		return properties.getProperty(SEND_DIRECTORY);
	}
	public static String getINVLAID_DIRECTORY() {
		return properties.getProperty(INVALID_DIRECTORY);
	}
	public static String getDUPE_DIRECTORY() {
		return properties.getProperty(DUPE_DIRECTORY);
	}
	public static String[] getFILE_EXTENSIONS() {
		if( properties.getProperty(FILE_EXTENSIONS).trim().equals("") )
			return null;
		
		return properties.getProperty(FILE_EXTENSIONS).split(",");
	}
	
    private static Properties loadProperties() {
        Properties defaults = new Properties();
        defaults.setProperty(WATCH_DIRECTORY, ".");
        defaults.setProperty(SEND_DIRECTORY, "send");
        defaults.setProperty(INVALID_DIRECTORY, "");
        defaults.setProperty(DUPE_DIRECTORY, "");
        defaults.setProperty(FILE_EXTENSIONS, "");
        defaults.setProperty(BACKUP_DIRECTORY, "");
        defaults.setProperty(POLLING_PERIOD_EXP, String.valueOf(1800));
        defaults.setProperty(POLLING_PERIOD, String.valueOf(30));
        defaults.setProperty(HR_BKP_EXP, String.valueOf(6));
        defaults.setProperty(MIN_BKP_EXP, String.valueOf(0));
        defaults.setProperty(FILE_BACKUP_PERIOD, String.valueOf(3));
        defaults.setProperty(FILE_EXPIRATION_PERIOD, String.valueOf(60*60*24*7));
        defaults.setProperty(MQ_CHANNEL, "MQ_CHANNEL");
        defaults.setProperty(MQ_HOST, "MQ_HOST");
        defaults.setProperty(MQ_PORT, "MQ_PORT");
        defaults.setProperty(MQ_QUEUE_MANAGER, "MQ_QUEUE_MANAGER");
        defaults.setProperty(MQ_UPLOAD_QUEUE, "MQ_UPLOAD_QUEUE");
        defaults.setProperty(MQ_DOWNLOAD_QUEUE, "MQ_DOWNLOAD_QUEUE");
        defaults.setProperty(FILE_SETTLE_PERIOD, String.valueOf(10));
        defaults.setProperty(FILE_SETTLE_PERIOD, String.valueOf(10));
        defaults.setProperty(MQ_MSG_POSTFIX_STR, "APGECPI ");
        defaults.setProperty(MQ_MSG_PREFIX_STR, "SUNMQMSG");
        defaults.setProperty(MQ_MSG_VERSION_STR, "0001");
        
        Properties props = new Properties(defaults);
 
        try {
        	File propsFile = new File(PROPERTY_FILE);
        	System.out.println(propsFile.getAbsolutePath());
            props.load(new FileInputStream(propsFile));
            System.out.println(props);
        } catch (Exception e) {
            System.out.println("Exception while loading properties file " + PROPERTY_FILE);
            e.printStackTrace();
        } 
        return props;
    }

}
