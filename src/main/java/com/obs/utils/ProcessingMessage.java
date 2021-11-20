package com.obs.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.obs.data.OBSProvOrder;

public class ProcessingMessage {

	@SuppressWarnings("unused")
	private PropertiesConfiguration prop;
	static Logger logger = Logger.getLogger(ProcessingMessage.class);

	public ProcessingMessage(PropertiesConfiguration prop2) {
		System.out.println("properties");
		this.prop = prop2;
	}

	public static byte[] registerMAC(OBSProvOrder localProvObj) {
		try {
			StringBuilder registerMACAdreessPacket = new StringBuilder();

			String macAddress = null;

			int devicesize = localProvObj.newProvDeviceInfo.size();
			for (int i = 0; i < devicesize; i++) {
				if (localProvObj.newProvDeviceInfo.get(i).ItemType.equalsIgnoreCase("STB")) {
					macAddress = localProvObj.newProvDeviceInfo.get(i).ProvisioningSerialNo;
					break;
				}
			}

			registerMACAdreessPacket.append(FileUtils.getHexadecimalValue(1, 1)); // protocol version
			registerMACAdreessPacket.append(FileUtils.getHexadecimalValue(48, 1)); // tag
			registerMACAdreessPacket.append(FileUtils.getHexadecimalValue(13, 2)); // command length
			registerMACAdreessPacket.append(FileUtils.getHexadecimalValue(macAddress.length(), 1)); // mac length
			registerMACAdreessPacket.append(FileUtils.convertStringToHex(macAddress, 12)); // mac address

			System.out.println("registerMAC hexString :" + registerMACAdreessPacket.toString());

			byte[] byteArrayData = FileUtils.hexStringToByteArray(registerMACAdreessPacket.toString());

			System.out.println("registerMAC bytes" + Arrays.toString(byteArrayData));
			return byteArrayData;
		} catch (Exception exception) {
			logger.info("Error.:" + exception.getMessage());
			return null;
		}
	}

	public static byte[] userAreaModifying(OBSProvOrder localProvObj) {
		try {
			StringBuilder userAreaModifyingPacket = new StringBuilder();
			String ua = null;

			ua = localProvObj.getResponse();
			if (ua == null) {
				ua = localProvObj.provServiceInfo.network_subscriber_id;
			}

			logger.info("userAreaModifying userInnerId : " + ua);

			userAreaModifyingPacket.append(FileUtils.getHexadecimalValue(1, 1)); // protocol version
			userAreaModifyingPacket.append(FileUtils.getHexadecimalValue(71, 1)); // tag
			userAreaModifyingPacket.append(FileUtils.getHexadecimalValue(13, 2)); // command length
			userAreaModifyingPacket.append(FileUtils.getHexadecimalValue(63, 1)); // provider Id
			userAreaModifyingPacket.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id
			userAreaModifyingPacket.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id
			userAreaModifyingPacket.append(FileUtils.getHexadecimalValue(1, 1)); //
			// userAreaModifyingPacket.append(FileUtils.bytesToHex(ua));
			userAreaModifyingPacket.append(ua); // user unqiue id
			userAreaModifyingPacket.append(FileUtils.getHexadecimalValue(3, 4)); // areaCode

			System.out.println("userAreaModifying hex String :" + userAreaModifyingPacket.toString());

			byte[] byteArrayData = FileUtils.hexStringToByteArray(userAreaModifyingPacket.toString());
			System.out.println("userAreaModifying byte Array" + Arrays.toString(byteArrayData));
			return byteArrayData;
		} catch (Exception exception) {
			logger.info("Error.:" + exception.getMessage());
			return null;
		}
	}

	public static byte[] userBinding(OBSProvOrder localProvObj) {
		try {
			String macAddress = null;
			String ua = null;

			ua = localProvObj.getResponse();
			if (ua == null) {
				ua = localProvObj.provServiceInfo.network_subscriber_id;
			}

			logger.info("userBinding userInnerId :" + ua + "mac address :" + macAddress);

			int devicesize = localProvObj.newProvDeviceInfo.size();

			for (int i = 0; i < devicesize; i++) {
				if (localProvObj.newProvDeviceInfo.get(i).ItemType.equalsIgnoreCase("STB")) {
					macAddress = localProvObj.newProvDeviceInfo.get(i).ProvisioningSerialNo;
					break;
				}
			}

			System.out.println("ProcessingMessage.userBinding()");
			System.out.println("UserId :" + ua.toString());

			StringBuilder userBindingPacket = new StringBuilder();
			userBindingPacket.append(FileUtils.getHexadecimalValue(1, 1)); // protocol Version
			userBindingPacket.append(FileUtils.getHexadecimalValue(69, 1)); // tag
			userBindingPacket.append(FileUtils.getHexadecimalValue(21, 2)); // command Length
			userBindingPacket.append(FileUtils.getHexadecimalValue(63, 1)); // providerId
			userBindingPacket.append(FileUtils.getHexadecimalValue(1, 1)); // providerId
			userBindingPacket.append(FileUtils.getHexadecimalValue(1, 1));// providerId
			// userBindingPacket.append(FileUtils.bytesToHex(ua)); //user unqiue id
			userBindingPacket.append(ua); // user unqiue id
			userBindingPacket.append(FileUtils.getHexadecimalValue(macAddress.length(), 1)); // mac_length
			userBindingPacket.append(FileUtils.convertStringToHex(macAddress, 12)); // mac_address

			System.out.println("userBinding hexString:" + userBindingPacket.toString());

			byte[] byteArrayData = FileUtils.hexStringToByteArray(userBindingPacket.toString());

			System.out.println("userBinding byte Array" + Arrays.toString(byteArrayData));

			return byteArrayData;
		} catch (Exception exception) {
			logger.info("Error.:" + exception.getMessage());
			return null;
		}
	}

	public static byte[] userUnBind(OBSProvOrder localProvObj) {
		try {
			String ua = localProvObj.provServiceInfo.network_subscriber_id;
			System.out.println("ProcessingMessage.userUnBind()");
			System.out.println("UserId :" + ua.toString());

			StringBuilder userBindingPacket = new StringBuilder();
			userBindingPacket.append(FileUtils.getHexadecimalValue(1, 1)); // protocol Version
			userBindingPacket.append(FileUtils.getHexadecimalValue(70, 1)); // tag
			userBindingPacket.append(FileUtils.getHexadecimalValue(8, 2)); // command Length
			userBindingPacket.append(FileUtils.getHexadecimalValue(63, 1)); // providerId
			userBindingPacket.append(FileUtils.getHexadecimalValue(1, 1)); // providerId
			userBindingPacket.append(FileUtils.getHexadecimalValue(1, 1));// providerId
			userBindingPacket.append(ua); // user unqiue id

			System.out.println("userUnBind hexString:" + userBindingPacket.toString());

			byte[] byteArrayData = FileUtils.hexStringToByteArray(userBindingPacket.toString());

			System.out.println("userUnBind byte Array" + Arrays.toString(byteArrayData));

			return byteArrayData;
		} catch (Exception exception) {
			logger.info("Error.:" + exception.getMessage());
			return null;
		}
	}

	public static byte[] userDelete(OBSProvOrder localProvObj) {
		try {
			String macAddress = null;
			String ua = null;

			ua = localProvObj.getResponse();
			if (ua == null) {
				ua = localProvObj.provServiceInfo.network_subscriber_id;
			}

			logger.info("userBinding userInnerId :" + ua + "mac address :" + macAddress);

			int devicesize = localProvObj.newProvDeviceInfo.size();

			for (int i = 0; i < devicesize; i++) {
				if (localProvObj.newProvDeviceInfo.get(i).ItemType.equalsIgnoreCase("STB")) {
					macAddress = localProvObj.newProvDeviceInfo.get(i).ProvisioningSerialNo;
					break;
				}
			}

			System.out.println("ProcessingMessage.userBinding()");
			System.out.println("UserId :" + ua.toString());

			StringBuilder userBindingPacket = new StringBuilder();
			userBindingPacket.append(FileUtils.getHexadecimalValue(1, 1)); // protocol Version
			userBindingPacket.append(FileUtils.getHexadecimalValue(65, 1)); // tag
			userBindingPacket.append(FileUtils.getHexadecimalValue(5, 2)); // command Length
			userBindingPacket.append(ua); // user unqiue id

			System.out.println("userBinding hexString:" + userBindingPacket.toString());

			byte[] byteArrayData = FileUtils.hexStringToByteArray(userBindingPacket.toString());

			System.out.println("userBinding byte Array" + Arrays.toString(byteArrayData));

			return byteArrayData;
		} catch (Exception exception) {
			logger.info("Error.:" + exception.getMessage());
			return null;
		}
	}

	public static byte[] licenseUpdating(OBSProvOrder localProvObj) {
		String ua = null;
		ua = localProvObj.getResponse();
		if (ua == null) {
			ua = localProvObj.provServiceInfo.network_subscriber_id;
		}

		logger.info("licenseUpdating userInnerId :" + ua);

		int ordercount = localProvObj.newProvProductInfo.size();
		List<String> neproductIds = new ArrayList<String>();

		for (int i = 0; i < ordercount; i++) {
			int prodcount = localProvObj.newProvProductInfo.get(i).products.size();
			for (int j = 0; j < prodcount; j++) {
				String productId = localProvObj.newProvProductInfo.get(i).products.get(j).neProductId;
				neproductIds.add(productId);
			}
		}

		try {
			StringBuilder licenseUpdating = new StringBuilder();

			licenseUpdating.append(FileUtils.getHexadecimalValue(1, 1));// protocol_Id
			licenseUpdating.append(FileUtils.getHexadecimalValue(16, 1));// tag
			licenseUpdating.append(FileUtils.getHexadecimalValue(32, 2));// command length in request = 42 code = 32
			licenseUpdating.append(FileUtils.getHexadecimalValue(64, 1));
			licenseUpdating.append(FileUtils.getHexadecimalValue(63, 1)); // providerId
			licenseUpdating.append(FileUtils.getHexadecimalValue(1, 1)); // providerId
			licenseUpdating.append(FileUtils.getHexadecimalValue(1, 1));// providerId
			licenseUpdating.append(ua);// userId
			licenseUpdating.append(FileUtils.DateInHexDateTime(new Date()));// message date time
			licenseUpdating.append(FileUtils.getHexadecimalValue(241, 1));
			licenseUpdating.append(FileUtils.DateInHexDate(new Date())); // start dare
			Date newDate = DateUtils.addMonths(new Date(), 1);

			// Date d = new Date(new Date().getTime() + 86400000);

			licenseUpdating.append(FileUtils.DateInHexDate(newDate)); // end date
			licenseUpdating.append(FileUtils.getHexadecimalValue(neproductIds.size(), 1)); // products size

			for (String neproductId : neproductIds) {
				int linkId = Integer.parseInt(neproductId);
				licenseUpdating.append(FileUtils.getHexforProductAdd(linkId));
			}

			byte[] byteArrayData = FileUtils.hexStringToByteArray(licenseUpdating.toString());
			licenseUpdating.replace(4, 6, FileUtils.getHexadecimalValue(byteArrayData.length - 4 >>> 8, 1)); // CommandLengthUpdate
			licenseUpdating.replace(6, 8, FileUtils.getHexadecimalValue(byteArrayData.length - 4, 1));

			logger.info("HexString : " + licenseUpdating.toString());
			byte[] updatedbyteArrayData = FileUtils.hexStringToByteArray(licenseUpdating.toString());

			logger.info("byte array licience update" + Arrays.toString(updatedbyteArrayData));

			return updatedbyteArrayData;

		} catch (Exception exception) {
			logger.info("Error.:" + exception.getMessage());
			return null;
		}
	}

	public static byte[] licenseCanceling(OBSProvOrder localProvObj) {

		String ua = null;
		ua = localProvObj.getResponse();
		if (ua == null) {
			ua = localProvObj.provServiceInfo.network_subscriber_id;
		}

		logger.info("licenseCanceling InnerUserId : " + ua);

		int ordercount = localProvObj.newProvProductInfo.size();
		List<String> neproductIds = new ArrayList<String>();

		for (int i = 0; i < ordercount; i++) {
			int prodcount = localProvObj.newProvProductInfo.get(i).products.size();
			for (int j = 0; j < prodcount; j++) {
				String productId = localProvObj.newProvProductInfo.get(i).products.get(j).neProductId;
				neproductIds.add(productId);
			}
		}

		try {
			StringBuilder licenseCanceling = new StringBuilder();

			licenseCanceling.append(FileUtils.getHexadecimalValue(1, 1));
			licenseCanceling.append(FileUtils.getHexadecimalValue(20, 1));
			licenseCanceling.append(FileUtils.getHexadecimalValue(32, 2));
			licenseCanceling.append(FileUtils.getHexadecimalValue(64, 1));
			licenseCanceling.append(FileUtils.getHexadecimalValue(63, 1)); // providerId
			licenseCanceling.append(FileUtils.getHexadecimalValue(1, 1)); // providerId
			licenseCanceling.append(FileUtils.getHexadecimalValue(1, 1));// providerId
			licenseCanceling.append(ua);
			licenseCanceling.append(FileUtils.DateInHexDateTime(new Date()));// message date time
			licenseCanceling.append(FileUtils.getHexadecimalValue(241, 1));
			licenseCanceling.append(FileUtils.DateInHexDate(new Date())); // start dare
			Date newDate = DateUtils.addMonths(new Date(), 1);
			licenseCanceling.append(FileUtils.DateInHexDate(newDate)); // end date
			licenseCanceling.append(FileUtils.getHexadecimalValue(neproductIds.size(), 1)); // products size

			for (String neproductId : neproductIds) {
				int linkId = Integer.parseInt(neproductId);
				licenseCanceling.append(FileUtils.getHexforProductcancel(linkId));
			}

			byte[] byteArrayData = FileUtils.hexStringToByteArray(licenseCanceling.toString());
			licenseCanceling.replace(4, 6, FileUtils.getHexadecimalValue(byteArrayData.length - 4 >>> 8, 1)); // CommandLengthUpdate
			licenseCanceling.replace(6, 8, FileUtils.getHexadecimalValue(byteArrayData.length - 4, 1));

			logger.info("HexString : " + licenseCanceling.toString());
			byte[] updatedbyteArrayData = FileUtils.hexStringToByteArray(licenseCanceling.toString());

			logger.info("byte array licience canceling" + Arrays.toString(updatedbyteArrayData));

			return updatedbyteArrayData;

		} catch (Exception exception) {
			logger.info("Error.:" + exception.getMessage());
			return null;
		}
	}

	public static byte[] userOSD(OBSProvOrder localProvObj) {
		byte[] updatedbyteArrayData = null;
		String ua = localProvObj.provServiceInfo.network_subscriber_id;
		// String ua = "FF00000768";

		StringBuilder osdPacket = new StringBuilder();

		try {
			int messageId = 133;
			int osdDuration = 60;
			int osdRepetition = 1;
			int osdInterval = 70;
			String content = "Hello";
			int codeType = 1;

			osdPacket.append(FileUtils.getHexadecimalValue(1, 1)); // protocol version
			osdPacket.append(FileUtils.getHexadecimalValue(96, 1)); // tag
			osdPacket.append(FileUtils.getHexadecimalValue(32, 2)); // command length
			osdPacket.append(FileUtils.getHexadecimalValue(63, 1)); // provider Id
			osdPacket.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id
			osdPacket.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id

			Date expireDate = DateUtils.addMonths(new Date(), 1);

			osdPacket.append(FileUtils.DateInHexDateTime(expireDate));// message expire time
			osdPacket.append(FileUtils.getHexadecimalValue(1, 1)); // hardCode Id
			osdPacket.append(FileUtils.getHexadecimalValue(2, 1)); // hardCode Id
			osdPacket.append(ua);
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));

			osdPacket.append(FileUtils.getHexadecimalValue(messageId >>> 24, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(messageId >>> 16, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(messageId >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(messageId, 1));// messageId

			osdPacket.append(FileUtils.getHexadecimalValue(osdDuration >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(osdDuration, 1));

			osdPacket.append(FileUtils.getHexadecimalValue(osdRepetition >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(osdRepetition, 1));

			osdPacket.append(FileUtils.getHexadecimalValue(osdInterval >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(osdInterval, 1));

			final int contentLength = content.length() + 1;

			osdPacket.append(FileUtils.getHexadecimalValue(contentLength >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(contentLength, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(codeType, 1));

			for (int i = 0; i < contentLength - 1; ++i) {
				osdPacket.append(FileUtils.getHexadecimalValue(content.charAt(i), 1));
			}
			osdPacket.append(FileUtils.getHexadecimalValue(2, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(16, 1));

			osdPacket.append(FileUtils.DateInHexDateTime(new Date()));
			osdPacket.append(FileUtils.getHexadecimalValue(128, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(255, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(255, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(255, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(143, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(50, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(13, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(20, 1));

			byte[] byteArrayData = FileUtils.hexStringToByteArray(osdPacket.toString());

			osdPacket.replace(4, 6, FileUtils.getHexadecimalValue(byteArrayData.length - 4 >>> 8, 1)); // CommandLengthUpdate
			osdPacket.replace(6, 8, FileUtils.getHexadecimalValue(byteArrayData.length - 4, 1));

			osdPacket.replace(38, 40, FileUtils.getHexadecimalValue(byteArrayData.length - 19 - 2 >>> 8, 1));
			osdPacket.replace(40, 42, FileUtils.getHexadecimalValue(byteArrayData.length - 19 - 2, 1));

			System.out.println("ProcessingMessage.userOSD()" + osdPacket.toString());
			updatedbyteArrayData = FileUtils.hexStringToByteArray(osdPacket.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return updatedbyteArrayData;

	}
	
	public static byte[] areaOSD(OBSProvOrder localProvObj) {
		byte[] updatedbyteArrayData = null;
		
		StringBuilder osdPacket = new StringBuilder();

		try {
			int messageId = 133;
			int osdDuration = 60;
			int osdRepetition = 1;
			int osdInterval = 70;
			String content = "Hello";
			int codeType = 1;

			osdPacket.append(FileUtils.getHexadecimalValue(1, 1)); // protocol version
			osdPacket.append(FileUtils.getHexadecimalValue(96, 1)); // tag
			osdPacket.append(FileUtils.getHexadecimalValue(32, 2)); // command length
			osdPacket.append(FileUtils.getHexadecimalValue(63, 1)); // provider Id
			osdPacket.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id
			osdPacket.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id

			Date expireDate = DateUtils.addMonths(new Date(), 1);

			osdPacket.append(FileUtils.DateInHexDateTime(expireDate));// message expire time
			osdPacket.append(FileUtils.getHexadecimalValue(1, 1)); // hardCode Id
			osdPacket.append(FileUtils.getHexadecimalValue(1, 1)); // hardCode Id
			
			osdPacket.append(FileUtils.getHexadecimalValue(3, 4)); // areaCode Id

			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));

			osdPacket.append(FileUtils.getHexadecimalValue(messageId >>> 24, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(messageId >>> 16, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(messageId >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(messageId, 1));// messageId

			osdPacket.append(FileUtils.getHexadecimalValue(osdDuration >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(osdDuration, 1));

			osdPacket.append(FileUtils.getHexadecimalValue(osdRepetition >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(osdRepetition, 1));

			osdPacket.append(FileUtils.getHexadecimalValue(osdInterval >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(osdInterval, 1));

			final int contentLength = content.length() + 1;

			osdPacket.append(FileUtils.getHexadecimalValue(contentLength >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(contentLength, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(codeType, 1));

			for (int i = 0; i < contentLength - 1; ++i) {
				osdPacket.append(FileUtils.getHexadecimalValue(content.charAt(i), 1));
			}
			osdPacket.append(FileUtils.getHexadecimalValue(2, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(16, 1));

			osdPacket.append(FileUtils.DateInHexDateTime(new Date()));
			osdPacket.append(FileUtils.getHexadecimalValue(128, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(255, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(255, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(255, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(143, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(50, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(13, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(20, 1));

			byte[] byteArrayData = FileUtils.hexStringToByteArray(osdPacket.toString());

			osdPacket.replace(4, 6, FileUtils.getHexadecimalValue(byteArrayData.length - 4 >>> 8, 1)); // CommandLengthUpdate
			osdPacket.replace(6, 8, FileUtils.getHexadecimalValue(byteArrayData.length - 4, 1));

			osdPacket.replace(36, 38, FileUtils.getHexadecimalValue(byteArrayData.length - 18 - 2 >>> 8, 1));
			osdPacket.replace(38, 40, FileUtils.getHexadecimalValue(byteArrayData.length - 18 - 2, 1));

			System.out.println("ProcessingMessage.userOSD()" + osdPacket.toString());
			updatedbyteArrayData = FileUtils.hexStringToByteArray(osdPacket.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return updatedbyteArrayData;

	}

	public static byte[] allOSDLocal(OBSProvOrder localProvObj) {
		byte[] updatedbyteArrayData = null;
		String ua = localProvObj.provServiceInfo.network_subscriber_id;
		// String ua = "FF00000768";

		StringBuilder osdPacket = new StringBuilder();

		try {
			int messageId = 133;
			int osdDuration = 60;
			int osdRepetition = 1;
			int osdInterval = 70;
			String content = "Hello";
			int codeType = 18;
			int transparency = 1;
			int fontColor = 1;
			int backgroundColor = 1;
			int fontSize = 1;
			int location = 1;
			boolean displayBackground = false;

			osdPacket.append(FileUtils.getHexadecimalValue(1, 1)); // protocol version
			osdPacket.append(FileUtils.getHexadecimalValue(96, 1)); // tag
			osdPacket.append(FileUtils.getHexadecimalValue(32, 2)); // command length
			osdPacket.append(FileUtils.getHexadecimalValue(63, 1)); // provider Id
			osdPacket.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id
			osdPacket.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id

			Date expireDate = DateUtils.addMonths(new Date(), 1);

			osdPacket.append(FileUtils.DateInHexDateTime(expireDate));// message expire time

			osdPacket.append(FileUtils.getHexadecimalValue(1, 1)); // hardCode Id
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1)); // hardCode Id
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));
			// osdPacket.append(ua);

			osdPacket.append(FileUtils.getHexadecimalValue(messageId >>> 24, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(messageId >>> 16, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(messageId >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(messageId, 1));// messageId
			System.out.println("ProcessingMessage.allOSDLocal()" + osdPacket.toString());
			osdPacket.append(FileUtils.getHexadecimalValue(osdDuration >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(osdDuration, 1));

			osdPacket.append(FileUtils.getHexadecimalValue(osdRepetition >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(osdRepetition, 1));

			osdPacket.append(FileUtils.getHexadecimalValue(osdInterval >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(osdInterval, 1));

			final int contentLength = content.length() + 1;

			osdPacket.append(FileUtils.getHexadecimalValue(contentLength >>> 8, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(contentLength, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(codeType, 1));

			for (int i = 0; i < contentLength - 1; ++i) {
				osdPacket.append(FileUtils.getHexadecimalValue(content.charAt(i), 1));
			}
			osdPacket.append(FileUtils.getHexadecimalValue(2, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(16, 1));

			osdPacket.append(FileUtils.DateInHexDateTime(new Date()));
			osdPacket.append(FileUtils.getHexadecimalValue(128, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(255, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(255, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(255, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(143, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(50, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(13, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(0, 1));
			osdPacket.append(FileUtils.getHexadecimalValue(20, 1));

			byte[] byteArrayData = FileUtils.hexStringToByteArray(osdPacket.toString());

			osdPacket.replace(4, 6, FileUtils.getHexadecimalValue(byteArrayData.length - 4 >>> 8, 1)); // CommandLengthUpdate
			osdPacket.replace(6, 8, FileUtils.getHexadecimalValue(byteArrayData.length - 4, 1));

			osdPacket.replace(28, 30, FileUtils.getHexadecimalValue(byteArrayData.length - 19 - 2 >>> 8, 1));
			osdPacket.replace(30, 32, FileUtils.getHexadecimalValue(byteArrayData.length - 19 - 2, 1));

			System.out.println("ProcessingMessage.userOSD()" + osdPacket.toString());
			updatedbyteArrayData = FileUtils.hexStringToByteArray(osdPacket.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return updatedbyteArrayData;

	}

	public static byte[] userFingerPrintLocal(OBSProvOrder localProvObj) {
		byte[] updatedbyteArrayData = null;
		// String ua = localProvObj.provServiceInfo.network_subscriber_id;
		String ua = "FF00000768";

		StringBuilder userFingerPrintRequest = new StringBuilder();

		try {
			int messageId = 134;
			String content = "Hello";
			int backgroundColor = 0;
			boolean isCoverFP = false;
			boolean isSpark = false;

			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(1, 1)); // protocol version
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(96, 1)); // tag
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(32, 2)); // command length
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(63, 1)); // provider Id
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id

			Date expireDate = DateUtils.addMonths(new Date(), 1);

			userFingerPrintRequest.append(FileUtils.DateInHexDateTime(expireDate));// message expire time
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(3, 1)); // hardCode Id
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(2, 1)); // hardCode Id
			userFingerPrintRequest.append(ua);

			if (isCoverFP) {
				userFingerPrintRequest.append(FileUtils.getHexadecimalValue(0, 1));
			} else {
				userFingerPrintRequest.append(FileUtils.getHexadecimalValue(1, 1));

			}
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(0, 1));
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(0, 1));

			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(messageId >>> 24, 1));
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(messageId >>> 16, 1));
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(messageId >>> 8, 1));
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(messageId, 1));// messageId

			userFingerPrintRequest.append(FileUtils.DateInHexDateTime(new Date()));// currentTime

			userFingerPrintRequest.append(FileUtils.DateInHexDateTime(new Date()));// message expire time

			int xAxis = 255;
			int yAxis = 255;
			int duration = 255;
			int repetition = 255;
			int interval = 0;
			int fontColour = 10;

			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(xAxis, 1));// xAxis
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(yAxis, 1));// yAxis
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(duration, 1));// duration
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(repetition, 1));// repetition
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(interval, 1));// interval
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(fontColour, 1));// fontColour
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(backgroundColor, 1)); // backgroundColor

			if (isSpark) {
				userFingerPrintRequest.append(FileUtils.getHexadecimalValue(1, 1));// isSpark
			} else {
				userFingerPrintRequest.append(FileUtils.getHexadecimalValue(0, 1));// isSpark
			}

			final int contentLength = content.length() + 1;

			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(contentLength >>> 8, 1));
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(contentLength, 1));// contentLength

			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(13, 1));

			for (int i = 0; i < contentLength - 1; ++i) {
				userFingerPrintRequest.append(FileUtils.getHexadecimalValue(content.charAt(i), 1));
			}

			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(2, 1));
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(12, 1));
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(0, 1));
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(0, 1));
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(128, 1));// red
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(0, 1));// green
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(0, 1));// blue
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(255, 1));// bg
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(255, 1));// bg
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(255, 1));// bg

			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(135, 1));// fpLocation
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(239, 1));// visibility
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(50, 1));// transparency
			userFingerPrintRequest.append(FileUtils.getHexadecimalValue(16, 1));// fontSize

			byte[] byteArrayData = FileUtils.hexStringToByteArray(userFingerPrintRequest.toString());

			userFingerPrintRequest.replace(4, 6, FileUtils.getHexadecimalValue(byteArrayData.length - 4 >>> 8, 1)); // CommandLengthUpdate
			userFingerPrintRequest.replace(6, 8, FileUtils.getHexadecimalValue(byteArrayData.length - 4, 1));

			userFingerPrintRequest.replace(40, 42,
					FileUtils.getHexadecimalValue(byteArrayData.length - 20 - 2 >>> 8, 1));
			userFingerPrintRequest.replace(42, 44, FileUtils.getHexadecimalValue(byteArrayData.length - 20 - 2, 1));

			System.out.println("ProcessingMessage.userFingerPrint()" + userFingerPrintRequest.toString());
			updatedbyteArrayData = FileUtils.hexStringToByteArray(userFingerPrintRequest.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return updatedbyteArrayData;

	}

	public static byte[] userMailLocal(OBSProvOrder localProvObj) {
		byte[] updatedbyteArrayData = null;
		String ua = localProvObj.provServiceInfo.network_subscriber_id;
//		String ua = "FF00000768";

		StringBuilder userMailLocal = new StringBuilder();

		try {
			int messageId = 30;

			userMailLocal.append(FileUtils.getHexadecimalValue(1, 1)); // protocol version
			userMailLocal.append(FileUtils.getHexadecimalValue(96, 1)); // tag
			userMailLocal.append(FileUtils.getHexadecimalValue(32, 2)); // command length
			userMailLocal.append(FileUtils.getHexadecimalValue(63, 1)); // provider Id
			userMailLocal.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id
			userMailLocal.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id

			userMailLocal.append(FileUtils.DateInHexDateTime(DateUtils.addMonths(new Date(), 1)));// message expire time
			userMailLocal.append(FileUtils.getHexadecimalValue(0, 1)); // hardCode Id
			userMailLocal.append(FileUtils.getHexadecimalValue(2, 1)); // hardCode Id
			userMailLocal.append(ua);

			userMailLocal.append(FileUtils.getHexadecimalValue(0, 1));
			userMailLocal.append(FileUtils.getHexadecimalValue(0, 1));

			userMailLocal.append(FileUtils.getHexadecimalValue(messageId, 4));// messageId

			userMailLocal.append(FileUtils.DateInHexDateTime(new Date()));// mail date time

			String senderName = "kumar";
			String mailTitle = "hello";
			String mailData = "hai";

			userMailLocal.append(FileUtils.getHexadecimalValue(senderName.length() + 1, 1));
			userMailLocal.append(FileUtils.getHexadecimalValue(18, 1));
			userMailLocal.append(FileUtils.convertStringToHex(senderName, senderName.length()));

			userMailLocal.append(FileUtils.getHexadecimalValue(mailTitle.length() + 1, 1));
			userMailLocal.append(FileUtils.getHexadecimalValue(18, 1));
			userMailLocal.append(FileUtils.convertStringToHex(mailTitle, mailTitle.length()));

			userMailLocal.append(FileUtils.getHexadecimalValue(mailData.length() + 1 >>> 8, 1));
			userMailLocal.append(FileUtils.getHexadecimalValue(mailData.length() + 1, 1));
			userMailLocal.append(FileUtils.getHexadecimalValue(18, 1));
			userMailLocal.append(FileUtils.convertStringToHex(mailData, mailData.length()));

			userMailLocal.append(FileUtils.getHexadecimalValue(0, 1));

			byte[] byteArrayData = FileUtils.hexStringToByteArray(userMailLocal.toString());
			userMailLocal.replace(4, 6, FileUtils.getHexadecimalValue(byteArrayData.length - 4 >>> 8, 1)); // CommandLengthUpdate
			userMailLocal.replace(6, 8, FileUtils.getHexadecimalValue(byteArrayData.length - 4, 1));

			userMailLocal.replace(38, 40, FileUtils.getHexadecimalValue(byteArrayData.length - 19 - 2 >>> 8, 1));
			userMailLocal.replace(40, 42, FileUtils.getHexadecimalValue(byteArrayData.length - 19 - 2, 1));

			System.out.println("ProcessingMessage.userOSD()" + userMailLocal.toString());
			updatedbyteArrayData = FileUtils.hexStringToByteArray(userMailLocal.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return updatedbyteArrayData;

	}

	public static byte[] allAreasMailLocal(OBSProvOrder localProvObj) {
		byte[] updatedbyteArrayData = null;

		StringBuilder userMailLocal = new StringBuilder();

		try {
			int messageId = 126;

			userMailLocal.append(FileUtils.getHexadecimalValue(1, 1)); // protocol version
			userMailLocal.append(FileUtils.getHexadecimalValue(96, 1)); // tag
			userMailLocal.append(FileUtils.getHexadecimalValue(32, 2)); // command length
			userMailLocal.append(FileUtils.getHexadecimalValue(63, 1)); // provider Id
			userMailLocal.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id
			userMailLocal.append(FileUtils.getHexadecimalValue(1, 1)); // provider Id

			userMailLocal.append(FileUtils.DateInHexDateTime(DateUtils.addMonths(new Date(), 1)));// message expire time
			userMailLocal.append(FileUtils.getHexadecimalValue(0, 1)); // commandlength Id
			userMailLocal.append(FileUtils.getHexadecimalValue(0, 1)); // commandlength Id

			userMailLocal.append(FileUtils.getHexadecimalValue(0, 1));
			userMailLocal.append(FileUtils.getHexadecimalValue(0, 1));

			userMailLocal.append(FileUtils.getHexadecimalValue(messageId, 4));// messageId

			userMailLocal.append(FileUtils.DateInHexDateTime(new Date()));// mail date time

			String senderName = "kumar";
			String mailTitle = "hello";
			String mailData = "hai";

			userMailLocal.append(FileUtils.getHexadecimalValue(senderName.length() + 1, 1));
			userMailLocal.append(FileUtils.getHexadecimalValue(0, 1));
			userMailLocal.append(FileUtils.convertStringToHex(senderName, senderName.length()));

			userMailLocal.append(FileUtils.getHexadecimalValue(mailTitle.length() + 1, 1));
			userMailLocal.append(FileUtils.getHexadecimalValue(0, 1));
			userMailLocal.append(FileUtils.convertStringToHex(mailTitle, mailTitle.length()));

			userMailLocal.append(FileUtils.getHexadecimalValue(mailData.length() + 1 >>> 8, 1));
			userMailLocal.append(FileUtils.getHexadecimalValue(mailData.length() + 1, 1));
			userMailLocal.append(FileUtils.getHexadecimalValue(0, 1));
			userMailLocal.append(FileUtils.convertStringToHex(mailData, mailData.length()));

			userMailLocal.append(FileUtils.getHexadecimalValue(0, 1));

			byte[] byteArrayData = FileUtils.hexStringToByteArray(userMailLocal.toString());
			userMailLocal.replace(4, 6, FileUtils.getHexadecimalValue(byteArrayData.length - 4 >>> 8, 1)); // CommandLengthUpdate
			userMailLocal.replace(6, 8, FileUtils.getHexadecimalValue(byteArrayData.length - 4, 1));

			userMailLocal.replace(28, 30, FileUtils.getHexadecimalValue(byteArrayData.length - 19 - 2 >>> 8, 1));
			userMailLocal.replace(30, 32, FileUtils.getHexadecimalValue(byteArrayData.length - 19 - 2, 1));

			System.out.println("ProcessingMessage.userOSD()" + userMailLocal.toString());
			updatedbyteArrayData = FileUtils.hexStringToByteArray(userMailLocal.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return updatedbyteArrayData;

	}

}
