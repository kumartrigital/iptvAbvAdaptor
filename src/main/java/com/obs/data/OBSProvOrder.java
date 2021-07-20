package com.obs.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Vector;

public class OBSProvOrder {

	public Long ProvOrderID;
	public String requestType;
	public Long clientId;
	public Long taskNo;
	public String type;
	public ProvClientInfo provClientInfo;
	public ProvServiceInfo provServiceInfo;
	public Vector<ProvDeviceInfo> newProvDeviceInfo = new Vector<ProvDeviceInfo>(10);

	public Vector<ProvDeviceInfo> oldProvDeviceInfo = new Vector<ProvDeviceInfo>(10);
	public Vector<ProvProductInfo> newProvProductInfo = new Vector<ProvProductInfo>(100);
	public Vector<ProvProductInfo> oldProvProductInfo = new Vector<ProvProductInfo>(100);
	public Vector<ProvTaskInfo> provTaskInfoList = new Vector<ProvTaskInfo>(10);
	public HSCASVisionOSDInfo provOSDInfo;
	public HSCASVisionMAILInfo provBMAILInfo;
	public HSCASVisionFPInfo provFPInfo;

	public byte[] innerUserId;

	private int macLength;
	private String macAddress;

	private String response;

	private String uA;

	public String getuA() {
		return uA;
	}

	public void setuA(String uA) {
		this.uA = uA;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

}
