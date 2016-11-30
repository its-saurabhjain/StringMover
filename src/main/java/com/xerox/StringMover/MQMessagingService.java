
package com.xerox.StringMover;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class MQMessagingService implements IMessagingService {
	
	private static Log log = LogFactory.getFactory().getInstance(MQMessagingService.class);
	
	private String mqChannel = null;
	private String mqHost = null;
	private String mqPort = null;
	private String queueMgrName = "";	// can be an empty string 
	private String queueName = null;
	private String mqUserid = null;
	private MQQueueManager qmgr = null;
	private MQQueue queue = null;

	public MQMessagingService() {
		super();
	}

	public void setMQChannel(String channel) throws Exception {
		mqChannel = channel;
	}

	public void setMQHost(String host) throws Exception {
		mqHost = host;
	}

	public void setMQPort(String port) throws Exception {
		mqPort = port;
	}

	public void setQueueManagerName(String qmgrname) throws Exception {
		queueMgrName = qmgrname;
	}
	
	public void setQueueName(String qname) throws Exception {
		queueName = qname;
	}

	public void setMQUserid(String mqUserid) {
		this.mqUserid = mqUserid;
	}

	public void open(int mode) throws Exception {
		try {
			MQEnvironment.hostname = mqHost;
			MQEnvironment.channel = mqChannel;
			MQEnvironment.port = Integer.parseInt(mqPort);
			MQEnvironment.userID = mqUserid;
				
			MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,MQC.TRANSPORT_MQSERIES);
			qmgr = new MQQueueManager(queueMgrName);
			//openOption for receiving messages
			int openOptions = MQC.MQOO_INQUIRE | MQC.MQOO_INPUT_EXCLUSIVE | MQC.MQOO_BROWSE;
			if( mode == (MQC.MQOO_INPUT_AS_Q_DEF|MQC.MQOO_INQUIRE|MQC.MQOO_OUTPUT))
			{
				//openOption for sending messages
				openOptions = MQC.MQOO_INQUIRE | MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING | MQC.MQOO_BROWSE ;
			}
			queue = qmgr.accessQueue(queueName,openOptions);
			if( log.isDebugEnabled() ) {
				log.debug("queue description: " + queue.getDescription());
			}
		} catch (MQException e) {
			close();
			throw new Exception("MQException: Completion code=" + e.completionCode + " Reason code=" + e.reasonCode, e);
		}
	}

	public void close() throws Exception {
		try {
			if( queue != null ) {
				queue.close();
			}
		} catch (MQException e) {
			throw new Exception("MQException: Completion code=" + e.completionCode + " Reason code=" + e.reasonCode, e);
		}
		queue = null;
		
		if( log.isDebugEnabled() ) {
			log.debug("commit and disconnect qmgr");
		}
		if( qmgr != null ) {
			qmgr.commit();
			qmgr.disconnect();
		}
		qmgr = null;
	}

	public void send(String msg) throws Exception {
		if( log.isDebugEnabled() ) {
			log.debug("sending message " + msg);
		}
		open(MQC.MQOO_INPUT_AS_Q_DEF|MQC.MQOO_INQUIRE|MQC.MQOO_OUTPUT);
		try {
			if( msg.length() > 0 ) {
				MQMessage buf = new MQMessage();
				buf.clearMessage();
				buf.format = MQC.MQFMT_STRING;
				buf.writeString(msg);
				buf.seek(0);		
				if( log.isDebugEnabled() ) {
					log.debug("about to put " + buf);
					if( buf != null ) {
						log.debug("msgLength: " + buf.getMessageLength());
						log.debug("readString: " + buf.readString(buf.getMessageLength()));
					}
					buf.seek(0);		
				}
				MQPutMessageOptions pmo = new MQPutMessageOptions();
				pmo.options += MQC.MQPMO_FAIL_IF_QUIESCING ;
				queue.put(buf, pmo);
				if( log.isInfoEnabled() ) {
					log.info("Sent message: " + msg);
				}
			}
		} catch (MQException e) {
			if( log.isErrorEnabled() ) {
				log.error("MQException: Completion code=" + e.completionCode + " Reason code=" + e.reasonCode, e);
			}
			throw new Exception("MQException: Completion code=" + e.completionCode + " Reason code=" + e.reasonCode, e);
		} catch (Exception e) {
			if( log.isErrorEnabled() ) {
				log.error("Exception " + e.getMessage(), e);
			}
		} finally {
			if( log.isDebugEnabled() ) {
				log.debug("in finally... closing...");
			}
			close();
		}
	}
	public String receive() throws Exception {
		if( log.isDebugEnabled() ) {
			log.debug("receiving message");
		}
		String msgText= null;	
		open(MQC.MQOO_INQUIRE | MQC.MQOO_INPUT_EXCLUSIVE | MQC.MQOO_BROWSE);
		try {
				
				StringBuilder sb = new StringBuilder();
				MQMessage buf = new MQMessage();
				MQGetMessageOptions gmo = new MQGetMessageOptions();
				gmo.options=MQC.MQGMO_WAIT | MQC.MQGMO_BROWSE_FIRST;
				gmo.matchOptions=MQC.MQMO_NONE;
				gmo.waitInterval=5000;
				int counter = 1;
				int messages = queue.getCurrentDepth();
				while(counter <= messages)
				{
					messages = queue.getCurrentDepth();
					gmo.options = MQC.MQGMO_WAIT | MQC.MQGMO_BROWSE_NEXT;
					queue.get(buf, gmo);
					//msgText = buf.readString(buf.getMessageLength());
					sb.append(buf.readString(buf.getMessageLength()));
					sb.append("_");
			        System.out.println("msg text: "+msgText);
			        gmo.options = MQC.MQGMO_WAIT | MQC.MQGMO_MSG_UNDER_CURSOR;
			        //gmo.options = MQC.MQGMO_WAIT | MQC.MQGMO_BROWSE_MSG_UNDER_CURSOR;
			        queue.get(buf, gmo);
			        counter ++;
			        if( log.isInfoEnabled() ) {
						log.info("Message: " + buf.putApplicationName + "|" + buf.putDateTime.getTime() + "|" + buf.readLine());
					}
				}
				if(sb.length() > 0)
				{
					sb.delete(sb.length()-1, sb.length());
					msgText = sb.toString();
				}
				messages = queue.getCurrentDepth();
			
			return msgText;
		} catch (MQException e) {
			if( e.reasonCode == MQException.MQRC_NO_MSG_AVAILABLE ) {
				if( log.isInfoEnabled() ) {
					log.info("No messages in queue");
				}
				int messages = queue.getCurrentDepth();
				
			} else {
				throw new Exception("MQException: Completion code=" + e.completionCode + " Reason code=" + e.reasonCode, e);
			}
		} finally {
			if( log.isDebugEnabled() )
				log.debug("in finally... closing...");
			close();
			
		}
		return msgText;
	}
}
