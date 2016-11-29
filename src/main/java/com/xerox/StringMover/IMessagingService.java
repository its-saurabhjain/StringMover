package com.xerox.StringMover;

public interface IMessagingService {

	// MQ for Java configuration properties
	public void setMQChannel(String channel) throws Exception;
	public void setMQHost(String host) throws Exception;
	public void setMQPort(String port) throws Exception;
	public void setQueueManagerName(String qmgrname) throws Exception;
	public void setQueueName(String qname) throws Exception;
	
	public void open(int mode) throws Exception;
	public void close() throws Exception;
	public void send(String msg) throws Exception;
	public String receive() throws Exception;
}
