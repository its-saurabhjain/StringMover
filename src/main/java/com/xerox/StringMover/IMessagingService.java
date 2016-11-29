package com.xerox.StringMover;

public interface IMessagingService {

	
	// MQ for Java configuration properties
	public void setMQChannel(String channel) throws Exception;
	public void setMQHost(String host) throws Exception;
	public void setMQPort(String port) throws Exception;
	public void setQueueManagerName(String qmgrname) throws Exception;
	public void setQueueName(String qname) throws Exception;
	
	// JMS configuration properties
/*	
	public void setTopicConnectionFactory(String tcf) throws Exception;
	public void setTopic(String topic) throws Exception;
*/
	
	// the meat of it //////////////////////////////////////////////////////////
/*
 * 	 TODO Not sure if open and close should be publically accessible or just
 * 	 handled internally within send and receive.  Depends on what open modes
 * 	 are used.  Plus, multiple receives could get very inefficient.  This
 * 	 needs to be reevaluated once I see how this class is used.
 */
	public void open(int mode) throws Exception;
	public void close() throws Exception;
	public void send(String msg) throws Exception;
	public String receive() throws Exception;
}
