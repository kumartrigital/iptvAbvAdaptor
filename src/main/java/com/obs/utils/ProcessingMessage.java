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

	private PropertiesConfiguration prop;
	static Logger logger = Logger.getLogger(ProcessingMessage.class);

	public ProcessingMessage(PropertiesConfiguration prop2) {
		System.out.println("properties");
		this.prop = prop2;
	}

	public static byte[] registerMAC(OBSProvOrder localProvObj) {
		try {
			StringBuilder registerMACAdreessPacket = new StringBuilder();

			// String macAddress = "32116B303EA2";

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

			logger.info("userInnerId : " + ua);

			System.out.println("ProcessingMessage.userAreaModifying()");
			System.out.println("UserId :" + ua.toString());

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

			logger.info("userInnerId : " + ua);

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

	public static byte[] licenseUpdating(OBSProvOrder localProvObj) {

		String ua = null;

		ua = localProvObj.getResponse();
		if (ua == null) {
			ua = localProvObj.provServiceInfo.network_subscriber_id;
		}

		logger.info("userInnerId : " + ua);

		System.out.println("ProcessingMessage.licenseUpdating()");

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

			licenseUpdating.append(FileUtils.getHexadecimalValue(1, 1));//protocol_Id
			licenseUpdating.append(FileUtils.getHexadecimalValue(16, 1));//tag
			licenseUpdating.append(FileUtils.getHexadecimalValue(42, 2));//command length
			licenseUpdating.append(FileUtils.getHexadecimalValue(64, 1));
			licenseUpdating.append(FileUtils.getHexadecimalValue(63, 1)); // providerId
			licenseUpdating.append(FileUtils.getHexadecimalValue(1, 1)); // providerId
			licenseUpdating.append(FileUtils.getHexadecimalValue(1, 1));// providerId
			licenseUpdating.append(ua);//userId
			licenseUpdating.append(FileUtils.DateInHexDateTime(new Date()));// message date time
			licenseUpdating.append(FileUtils.getHexadecimalValue(241, 1));
			licenseUpdating.append(FileUtils.DateInHexDate(new Date())); // start dare
			Date newDate = DateUtils.addMonths(new Date(), 1);

			Date d = new Date(new Date().getTime() + 86400000);

			licenseUpdating.append(FileUtils.DateInHexDate(newDate)); // end date
			licenseUpdating.append(FileUtils.getHexadecimalValue(neproductIds.size(), 1)); // products size

			for (String neproductId : neproductIds) {
				int linkId = Integer.parseInt(neproductId);
				licenseUpdating.append(FileUtils.getHexforProduct(linkId));
			}

			System.out.println("ProcessingMessage.licenseUpdating()" + licenseUpdating.toString());

			byte[] byteArrayData = FileUtils.hexStringToByteArray(licenseUpdating.toString());
			System.out.println("ProcessingMessage.licenseUpdating()" + Arrays.toString(byteArrayData));

			return byteArrayData;

		} catch (Exception exception) {
			logger.info("Error.:" + exception.getMessage());
			return null;
		}
	}

	public static byte[] licenseCanceling(OBSProvOrder localProvObj) {

		String ua = "FF00000566";
		System.out.println("ProcessingMessage.licenseCanceling()");
		System.out.println("InnerUserId : " + ua);
		int[] linkId = { 980, 997, 996 };
		try {
			StringBuilder licenseUpdating = new StringBuilder();

			licenseUpdating.append(FileUtils.getHexadecimalValue(1, 1));
			licenseUpdating.append(FileUtils.getHexadecimalValue(20, 1));
			licenseUpdating.append(FileUtils.getHexadecimalValue(42, 2));
			licenseUpdating.append(FileUtils.getHexadecimalValue(64, 1));
			licenseUpdating.append(FileUtils.getHexadecimalValue(63, 1)); // providerId
			licenseUpdating.append(FileUtils.getHexadecimalValue(1, 1)); // providerId
			licenseUpdating.append(FileUtils.getHexadecimalValue(1, 1));// providerId
			// licenseUpdating.append(FileUtils.bytesToHex(ua));
			licenseUpdating.append(ua);
			licenseUpdating.append(FileUtils.DateInHexDateTime(new Date()));// message date time
			licenseUpdating.append(FileUtils.getHexadecimalValue(241, 1));
			licenseUpdating.append(FileUtils.DateInHexDate(new Date())); // start dare
			licenseUpdating.append(FileUtils.DateInHexDate(new Date())); // end date
			licenseUpdating.append(FileUtils.getHexadecimalValue(linkId.length, 1)); // products size

			for (int i = 0; i < linkId.length; ++i) {
				licenseUpdating.append(FileUtils.getHexforProduct(linkId[i]));
			}

			System.out.println("ProcessingMessage.licenseCanceling()" + licenseUpdating.toString());

			byte[] byteArrayData = FileUtils.hexStringToByteArray(licenseUpdating.toString());
			System.out.println("ProcessingMessage.licenseCanceling()" + Arrays.toString(byteArrayData));

			return byteArrayData;

		} catch (Exception exception) {
			logger.info("Error.:" + exception.getMessage());
			return null;
		}
	}

}
