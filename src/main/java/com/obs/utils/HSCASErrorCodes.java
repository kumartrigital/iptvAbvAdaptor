package com.obs.utils;

import java.util.Hashtable;

public class HSCASErrorCodes {
	public static Hashtable<Integer, String> errorCodes = new Hashtable<Integer, String>();

	static {
		errorCodes.put(2, " Packet succesfully executed");
		errorCodes.put(49, "MACAddress succesfully register response");
		errorCodes.put(224, "Success Response Packet");
		errorCodes.put(225, "Error Response Packet");
		errorCodes.put(-1, "Unkown Response Packet");
		errorCodes.put(99, "command failed to get response");


	}

	public static String getErrorDesc(int errorCode) {
		return errorCodes.get(errorCode);
	}

}
