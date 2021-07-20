package com.obs.utils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
public class HSCASConnection {
	
	private static PropertiesConfiguration prop;
	static Logger logger = Logger.getLogger("");
	Socket requestSocket = null;
	String hostAddress = null;
	int port = 0;
	
	public HSCASConnection ()
	{
		try {
			logger.info("Connecting with Server ...");	
			hostAddress = prop.getString("host_address");
			port = prop.getInt("port_number");
			requestSocket = new Socket(hostAddress, port);
		} catch (UnknownHostException e) {
			logger.error("UnknownHostException:" + e.getMessage() + ". The host_address or port_number is invalid . Verify the Details... ");
			return;
		} catch (IOException e) {
			logger.error(" Connection to the CAS server is Not Established .... , " + e.getMessage());
		}catch (NullPointerException e) {
			logger.error("NullPointerException:" + e.getMessage() + " ");
			return;
		}
	}

	public void closeConnection()
	{
		
		try {
			requestSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}