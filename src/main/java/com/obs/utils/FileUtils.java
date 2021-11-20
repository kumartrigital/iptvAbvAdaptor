
package com.obs.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.obs.consumer.HSCASConsumer;
import com.obs.data.HSCASVisionFPInfo;
import com.obs.data.HSCASVisionMAILInfo;
import com.obs.data.HSCASVisionOSDInfo;
import com.obs.data.OBSProvOrder;
import com.obs.data.ProvClientInfo;
import com.obs.data.ProvDeviceInfo;
import com.obs.data.ProvProduct;
import com.obs.data.ProvProductInfo;
import com.obs.data.ProvServiceInfo;
import com.obs.data.ProvTaskInfo;

public class FileUtils {
	static Logger logger = Logger.getLogger(HSCASConsumer.class);
	static char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String decToHex(int dec, int hexStringLength) {

		StringBuilder hexBuilder = new StringBuilder(hexStringLength);
		hexBuilder.setLength(hexStringLength);
		for (int i = hexStringLength - 1; i >= 0; --i) {
			int j = dec & 0x0F;
			hexBuilder.setCharAt(i, hexDigit[j]);
			dec >>= 4;
		}
		return hexBuilder.toString();
	}

	public static String hexValue(String value) {
		StringBuffer sb = new StringBuffer();
		char ch[] = value.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			String hexString = Integer.toHexString(ch[i]);
			sb.append(hexString);
		}
		return sb.toString();
	}

	public static byte[] hexStringToByteArray(String hexEncodedBinary) {

		if (hexEncodedBinary.length() % 2 == 0) {
			char[] sc = hexEncodedBinary.toCharArray();
			byte[] ba = new byte[sc.length / 2];

			for (int i = 0; i < ba.length; i++) {
				int nibble0 = Character.digit(sc[i * 2], 16);
				int nibble1 = Character.digit(sc[i * 2 + 1], 16);
				if (nibble0 == -1 || nibble1 == -1) {
					throw new IllegalArgumentException("Hex-encoded binary string contains an invalid hex digit in '"
							+ sc[i * 2] + sc[i * 2 + 1] + "'");
				}
				ba[i] = (byte) ((nibble0 << 4) | (nibble1));
			}

			return ba;
		} else {
			throw new IllegalArgumentException("Hex-encoded binary string contains an uneven no. of digits");
		}
	}

//byte to hexString
	public static Long getDecimal(String hex) {
		String digits = "0123456789ABCDEF";
		hex = hex.toUpperCase();
		Long val = 0l;
		for (int i = 0; i < hex.length(); i++) {
			char c = hex.charAt(i);
			int d = digits.indexOf(c);
			val = 16 * val + d;
		}
		return val;
	}

	public static String bytesToHex(byte[] b, int off, int len) {

		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < len; j++)
			buf.append(byteToHex(b[off + j]));
		return buf.toString();
	}

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String byteToHex(byte b) {
		char[] a = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };

		return new String(a);
	}

	public static String dateToHex(Date date) throws Exception {

		Long decimal = date.getTime();

		int new_x = Integer.parseInt(Long.toString(decimal).substring(0, 10));
		return Long.toHexString(new_x).toUpperCase();
	}

	public static void updateDB_id(int Db_id, PropertiesConfiguration prop) throws ConfigurationException {
		prop.setProperty("DB_ID", Db_id + 1);
		prop.save();

	}

	public static Date getDateTimePlusOneDay(Date dt) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		calendar.add(Calendar.DATE, 1);
		dt = calendar.getTime();
		System.out.println(dt);
		return dt;

	}

	public static Date getDateTimePlusOneWeek(Date dt) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		calendar.add(Calendar.DATE, 7);
		dt = calendar.getTime();
		System.out.println(dt);
		return dt;

	}

	public static Date getDateTimePlusThreeDays(Date dt) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		calendar.add(Calendar.DATE, 3);
		dt = calendar.getTime();
		System.out.println(dt);
		return dt;

	}

	public static Date getDateTimePlusOnehour(Date dt) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		calendar.add(Calendar.HOUR, 1);
		dt = calendar.getTime();
		System.out.println(dt);
		return dt;

	}

	public static Date getDateTimeMinusOneDay(Date dt) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		calendar.add(Calendar.DATE, -1);
		dt = calendar.getTime();
		return dt;

	}

	// dataBody.append(FileUtils.decToHex(prop.getInt("DB_ID") + 1,4)); //Db_id
//filewriter

	public static String chHexaStringValue(String value, int noOfDigits) {
		if ((value == null) || (value.trim().equals(""))) {
			return value;
		}
		if (value.length() < noOfDigits) {
			int offset = noOfDigits - value.length();
			StringBuffer newValue = new StringBuffer(value);
			for (int i = 0; i < offset; i++) {
				newValue.insert(0, '0');
			}
			return newValue.toString();
		}
		return value;
	}

	public static String getHexadecimalValue(int value, int noOfBytes) {
		String hexaString = Integer.toHexString((Integer) value).toUpperCase();		
		if (2 * noOfBytes != hexaString.length()) {
			hexaString = chHexaStringValue(hexaString, 2 * noOfBytes);
		}
		return hexaString;
	}

	public static String getHexadecimalValue(long value, int noOfBytes) {
		String hexaString = Long.toHexString(value).toUpperCase();
		if (2 * noOfBytes != hexaString.length()) {
			hexaString = chHexaStringValue(hexaString, 2 * noOfBytes);
		}
		return hexaString;
	}

	public static String convertBinaryToHex(String strBinary) {
		int i = Integer.parseInt(strBinary, 2);

		return getHexadecimalValue(i, 1);
	}

	/*
	 * public static byte[] hexStringToByteArray(String s) { int len = s.length();
	 * byte[] data = new byte[len / 2]; for (int i = 0; i < len; i += 2) { data[(i /
	 * 2)] = ((byte)((Character.digit(s.charAt(i), 16) << 4) +
	 * Character.digit(s.charAt(i + 1), 16))); } return data; }
	 */

	public static String convertStringToHex(String base, int noOfBytes) {
		String hexaString = convertStringToHex(base);
		if (2 * noOfBytes != hexaString.length()) {
			hexaString = getNDSHexaStringValue(hexaString, 2 * noOfBytes);
		}
		return hexaString;
	}

	public static byte[] getHexadecimalValue(String hexaString, int noOfBytes) {
		if (2 * noOfBytes != hexaString.length()) {
			String newValue = getNDSHexaStringValue(hexaString, 2 * noOfBytes);

			return convert(newValue);
		}
		System.out.println("FileUtils.getHexadecimalValue()" + hexaString);
		return convert(hexaString);
	}

	public static String convertStringToHex(String base) {
		StringBuffer buffer = new StringBuffer();
		for (int x = 0; x < base.length(); x++) {
			int cursor = 0;
			int intValue = base.charAt(x);
			String hexChar = new String(Integer.toHexString(intValue));
			String binaryChar = new String(Integer.toBinaryString(base.charAt(x)));
			buffer.append(hexChar);
		}
		return buffer.toString().toUpperCase();
	}

	private static Date cvtToGmt(Date date) {
		TimeZone tz = TimeZone.getDefault();
		Date ret = new Date(date.getTime() - tz.getRawOffset());
		if (tz.inDaylightTime(ret)) {
			Date dstDate = new Date(ret.getTime() - tz.getDSTSavings());
			if (tz.inDaylightTime(dstDate)) {
				ret = dstDate;
			}
		}
		return ret;
	}

	private static int[] getTimeStamp(boolean isGMT, boolean isTimeReq, Date date) {
		GregorianCalendar calender = new GregorianCalendar();
		calender.setTime(date);
		if (isTimeReq) {
			/*
			 * int[] timeStamp = { calender.get(5), calender.get(2) + 1, calender.get(1) -
			 * 1980, calender.get(11), calender.get(12) };
			 */

			int[] timeStamp = { calender.YEAR, calender.MONTH, calender.DATE, calender.HOUR, calender.MINUTE,
					calender.SECOND };

			System.out.println("time stamp " + timeStamp);
			return timeStamp;
		}
		int[] timeStamp = { calender.get(5), calender.get(2) + 1, calender.get(1) - 1980 };

		return timeStamp;
	}

	public static String getDate(boolean isGMT, boolean isTimeReq, Date date) {
		try {
			int[] timeStamp = getTimeStamp(isGMT, isTimeReq, date);
			System.out.println("timestamp length " + timeStamp.length + "timespam" + timeStamp);

			String dateTime = "";
			for (int i = 0; i < timeStamp.length; i++) {
				int value = timeStamp[i];
				dateTime = dateTime.concat(getHexadecimalValue(value, 4));
			}
			return dateTime;
		} catch (Exception exp) {
		}
		return null;
	}

	public static String getDatetime(Date date) {

		try {
			DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String testDateString = dateformat.format(date);
			System.out.println("expire date" + testDateString);
			return FileUtils.convertStringToHex(testDateString, 4);

		} catch (Exception exp) {
		}
		return null;
	}

	public static byte[] convert(String str) {
		byte[] byteArray = new byte[str.length() / 2];
		int i = 0;
		for (int j = 0; i < str.length(); j++) {
			int k = Integer.parseInt(str.substring(i, i + 2), 16);
			byteArray[j] = ((byte) (k > 127 ? k - 256 : k));
			i += 2;
		}
		return byteArray;
	}

	public static String getNDSHexaStringValue(String value, int noOfDigits) {
		if ((value == null) || (value.trim().equals(""))) {
			return value;
		}
		if (value.length() < noOfDigits) {
			int offset = noOfDigits - value.length();
			StringBuffer newValue = new StringBuffer(value);
			for (int i = 0; i < offset; i++) {
				newValue.insert(0, '0');
			}
			System.out.println("new value " + newValue.toString());
			return newValue.toString();
		}
		return value;
	}

	public static OBSProvOrder jsonToProvObj(JSONObject jsonObj1) {

		OBSProvOrder provObj = null;
		/*
		 * String jsonString = "{" + "\"ProvOrderID\": 868112," +
		 * "\"requestType\": \"ACTIVATION\"," + "\"clientId\": 1085807," +
		 * "\"taskNo\":1," + "\"ClientInfo\": [{" + "\"officeId\": 1," +
		 * "\"email\": \"kkt@gmail.com\",\"selfcareUsername\": \"kkt@gmail.com\",\"password\":\"xxxxx\",\"selfcarePassword\": \"ffjxvosh\","
		 * +
		 * "\"displayName\": \"New SEcond\",\"firstName\": \"New SEcond\",\"login\": \"1085807\",\"lastName\": \"BOX\"}"
		 * + "]," +
		 * "\"ServiceInfo\": [{\"serviceId\":3,\"clientServiceId\":868304,\"Network_node\":\"212\"}],"
		 * + "\"NewDeviceInfo\":[" +
		 * "{\"ItemType\":\"STB\",\"SerialNo\":\"21017070150197\",\"ProvisioningSerialNo\":\"21017070150197\"},"
		 * +
		 * "{\"ItemType\":\"VC\",\"SerialNo\":\"8006064215450951\",\"ProvisioningSerialNo\":\"8006064215450951\"}],"
		 * + "\"OldDeviceInfo\":[" +
		 * "{\"ItemType\":\"STB\",\"SerialNo\":\"21017070150197\",\"ProvisioningSerialNo\":\"21017070150197\"},"
		 * +
		 * "{\"ItemType\":\"VC\",\"SerialNo\":\"8006064215450951\",\"ProvisioningSerialNo\":\"8006064215450951\"}],"
		 * + "\"NewOrderList\":[" +
		 * "{\"planId\":4,\"orderId\":868115,\"orderStartDate\":\"02/11/2017\",\"orderEndDate\":\"30/11/2018\",\"Products\": [{\"neProductId\":\"995\",\"productName\":\"basic\"}]}],"
		 * + "\"OldOrderList\": [" +
		 * "{\"planId\":4,\"orderId\":868115,\"orderStartDate\":\"02/11/2017\",\"orderEndDate\":\"30/11/2018\",\"Products\": [{\"neProductId\":\"995\",\"productName\":\"premium\"}]}],"
		 * + "\"TaskList\":[" + "{\"task_id\":1,\"task_name\":\"ADD_CARD\"}," +
		 * "{\"task_id\":2,\"task_name\":\"BIND_STB\"}," +
		 * "{\"task_id\":3,\"task_name\":\"ADD_ENTITLEMENT\"}]}";
		 */

		// System.out.println(jsonObj1.toString());
		try {
			JSONObject jsonObj2 = new JSONObject(jsonObj1.toString());
			JSONObject jsonObj = new JSONObject();
			System.out.println("This the actual object " + jsonObj2.toString());
			String jsondetails_str = jsonObj2.getString("jsonDetails");
			System.out.println("This the final object " + jsondetails_str);
			JSONObject jsondetails = new JSONObject(jsondetails_str);
			jsonObj.put("requestType", jsonObj2.getString("requestType"));
			jsonObj.put("ProvOrderID", jsonObj2.getInt("prdetailsId"));
			jsonObj.put("taskNo", jsonObj2.getInt("taskNo"));
			if (jsondetails.has("type")) {
				jsonObj.put("type", jsondetails.optString("type"));
			}
			if (jsondetails.has("serviceInfo")) {
				jsonObj.put("serviceInfo", (Object) jsondetails.getJSONArray("serviceInfo"));
			}
			if (jsondetails.has("clientInfo")) {
				jsonObj.put("clientInfo", (Object) jsondetails.getJSONArray("clientInfo"));
			}

			if (jsondetails.has("newDeviceInfo")) {
				jsonObj.put("newDeviceInfo", (Object) jsondetails.getJSONArray("newDeviceInfo"));
			}

			if (jsondetails.has("oldDeviceInfo")) {
				jsonObj.put("oldDeviceInfo", (Object) jsondetails.getJSONArray("oldDeviceInfo"));
			}

			if (jsondetails.has("newOrderList")) {
				jsonObj.put("newOrderList", (Object) jsondetails.getJSONArray("newOrderList"));
			}

			if (jsondetails.has("oldOrderList")) {
				jsonObj.put("oldOrderList", (Object) jsondetails.getJSONArray("oldOrderList"));
			}

			if (jsondetails.has("TaskList")) {
				jsonObj.put("TaskList", (Object) jsondetails.getJSONArray("TaskList"));
			}

			if (jsondetails.has("paramsDetailsInfo")) {
				jsonObj.put("paramsDetailsInfo", (Object) jsondetails.getJSONArray("paramsDetailsInfo"));
			}

			System.out.println("JSON STRING:" + jsonObj.toString());

			// String newstr = jsonObj1.toString().replace("\\", "");
			provObj = new OBSProvOrder();
			if (jsonObj.has("ProvOrderID")) {
				provObj.ProvOrderID = jsonObj.getLong("ProvOrderID");
			}
			if (jsonObj.has("requestType")) {
				provObj.requestType = jsonObj.getString("requestType");
			}
			if (jsonObj.has("clientId")) {
				provObj.clientId = jsonObj.getLong("clientId");
			}
			if (jsonObj.has("taskNo")) {
				provObj.taskNo = jsonObj.getLong("taskNo");
			}

			if (jsonObj.has("type")) {
				provObj.type = jsonObj.getString("type");
			}

			// Fetch Customer info

			JSONArray arr = null;
			JSONArray arr1 = null;
			if (jsonObj.has("clientInfo")) {
				arr = jsonObj.getJSONArray("clientInfo");
				if (arr.length() >= 1) {
					ProvClientInfo provClientInfo_l = new ProvClientInfo();
					provClientInfo_l.displayName = arr.getJSONObject(0).getString("displayName");
					provClientInfo_l.email = arr.getJSONObject(0).getString("email");
					provClientInfo_l.firstName = arr.getJSONObject(0).getString("firstName");
					provClientInfo_l.lastName = arr.getJSONObject(0).getString("lastName");
					provClientInfo_l.officeId = arr.getJSONObject(0).getLong("officeId");
					/*
					 * provClientInfo_l.login = arr.getJSONObject(0).getString("login");
					 * provClientInfo_l.password = arr.getJSONObject(0).getString("password");
					 * provClientInfo_l.selfcarePassword =
					 * arr.getJSONObject(0).getString("selfcarePassword");
					 * provClientInfo_l.selfcareUsername =
					 * arr.getJSONObject(0).getString("selfcareUsername");
					 */
					provObj.provClientInfo = provClientInfo_l;
				}

			}

			// Fetch Service info
			if (jsonObj.has("serviceInfo")) {
				arr = jsonObj.getJSONArray("serviceInfo");
				if (arr.length() >= 1) {

					ProvServiceInfo provServiceInfo_l = new ProvServiceInfo();
					provServiceInfo_l.clientServiceId = arr.getJSONObject(0).getLong("clientServiceId");
					provServiceInfo_l.serviceId = arr.getJSONObject(0).getLong("serviceId");
					provServiceInfo_l.Network_node = arr.getJSONObject(0).getString("Network_node");
					provServiceInfo_l.network_subscriber_id = arr.getJSONObject(0).getString("networkSubscriberId");
					provObj.provServiceInfo = provServiceInfo_l;
				}

			}

			// Fetch NewDevice info
			if (jsonObj.has("newDeviceInfo")) {
				arr = jsonObj.getJSONArray("newDeviceInfo");
				ProvDeviceInfo ProvDeviceInfo_l = null;
				for (int i = 0; i < arr.length(); i++) {
					ProvDeviceInfo_l = new ProvDeviceInfo();
					ProvDeviceInfo_l.ItemType = arr.getJSONObject(i).getString("ItemType");
					ProvDeviceInfo_l.ProvisioningSerialNo = arr.getJSONObject(i).getString("ProvisioningSerialNo");
					ProvDeviceInfo_l.SerialNo = arr.getJSONObject(i).getString("SerialNo");
					provObj.newProvDeviceInfo.add(ProvDeviceInfo_l);
				}

			}

			// Fetch OldDevice info
			if (jsonObj.has("oldDeviceInfo")) {
				arr = jsonObj.getJSONArray("oldDeviceInfo");
				ProvDeviceInfo ProvDeviceInfo_l = null;
				for (int i = 0; i < arr.length(); i++) {
					ProvDeviceInfo_l = new ProvDeviceInfo();
					ProvDeviceInfo_l.ItemType = arr.getJSONObject(i).getString("ItemType");
					ProvDeviceInfo_l.ProvisioningSerialNo = arr.getJSONObject(i).getString("ProvisioningSerialNo");
					ProvDeviceInfo_l.SerialNo = arr.getJSONObject(i).getString("SerialNo");
					provObj.oldProvDeviceInfo.add(ProvDeviceInfo_l);
				}

			}

			// Fetch NewProduct info
			if (jsonObj.has("newOrderList")) {
				arr = jsonObj.getJSONArray("newOrderList");
				ProvProductInfo provProductInfo_l = null;
				for (int i = 0; i < arr.length(); i++) {
					provProductInfo_l = new ProvProductInfo();

					provProductInfo_l.orderId = arr.getJSONObject(i).getLong("orderId");
					provProductInfo_l.planId = arr.getJSONObject(i).getLong("planId");
					if (arr.getJSONObject(i).has("orderStartDate")) {
						provProductInfo_l.orderStartDate = new SimpleDateFormat("dd/MM/yyyy")
								.parse(arr.getJSONObject(i).getString("orderStartDate"));
					} else {
						provProductInfo_l.orderStartDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/12/2017");

					}
					if (arr.getJSONObject(i).has("orderEndDate")) {
						provProductInfo_l.orderEndDate = new SimpleDateFormat("dd/MM/yyyy")
								.parse(arr.getJSONObject(i).getString("orderEndDate"));
					} else {
						provProductInfo_l.orderEndDate = new SimpleDateFormat("dd/MM/yyyy").parse("31/12/2030");
					}

					JSONObject jsonObjprod = arr.getJSONObject(i);
					System.out.println("Innner json = " + jsonObjprod.toString());
					arr1 = jsonObjprod.getJSONArray("products");
					for (int j = 0; j < arr1.length(); j++) {
						ProvProduct provProduct_l = new ProvProduct();
						provProduct_l.neProductId = arr1.getJSONObject(j).getString("neProductId");
						provProduct_l.productName = arr1.getJSONObject(j).getString("productName");
						provProductInfo_l.products.add(j, provProduct_l);
					}
					provObj.newProvProductInfo.add(i, provProductInfo_l);
				}

			}

			if (jsonObj.has("paramsDetailsInfo")) {
				if (jsonObj2.getString("requestType").equals("FINGERPRINT")) {
					arr = jsonObj.getJSONArray("paramsDetailsInfo");
					System.out.println("json array : " + arr.toString());
					System.out.println("json length : " + arr.length());
					if (arr.length() >= 1) {
						HSCASVisionFPInfo provFPInfo_l = new HSCASVisionFPInfo();
						for (int j = 0; j < arr.length(); j++) {
							if (arr.getJSONObject(j).has("xaxis")) {
								provFPInfo_l.xaxis = arr.getJSONObject(j).getInt("xaxis");
							}
							if (arr.getJSONObject(j).has("yaxis")) {
								provFPInfo_l.yaxis = arr.getJSONObject(j).getInt("yaxis");
							}
							if (arr.getJSONObject(j).has("duration")) {
								provFPInfo_l.duration = arr.getJSONObject(j).getInt("duration");
							}
							if (arr.getJSONObject(j).has("repetition")) {
								provFPInfo_l.repetition = arr.getJSONObject(j).getInt("repetition");
							}
							if (arr.getJSONObject(j).has("interval")) {
								provFPInfo_l.interval = arr.getJSONObject(j).getInt("interval");
							}
							if (arr.getJSONObject(j).has("textsize")) {
								provFPInfo_l.textsize = arr.getJSONObject(j).getInt("textsize");
							}
							if (arr.getJSONObject(j).has("textcolor")) {
								provFPInfo_l.textcolor = arr.getJSONObject(j).getString("textcolor");
							}
							if (arr.getJSONObject(j).has("bgcolor")) {
								provFPInfo_l.bgcolor = arr.getJSONObject(j).getString("bgcolor");
							}
							if (arr.getJSONObject(j).has("fptype")) {
								provFPInfo_l.fptype = arr.getJSONObject(j).getInt("fptype");
							}
							if (arr.getJSONObject(j).has("iscovertfp")) {
								provFPInfo_l.iscovertfp = arr.getJSONObject(j).getInt("iscovertfp");
							}

						}

						provObj.provFPInfo = provFPInfo_l;
						System.out.println(provObj.provFPInfo.toString());
					}
				}
				if (jsonObj2.getString("requestType").equals("OSD")) {
					// Fetch OSD info
					arr = jsonObj.getJSONArray("paramsDetailsInfo");
					if (arr.length() >= 1) {
						HSCASVisionOSDInfo provOSDInfo_l = new HSCASVisionOSDInfo();
						for (int j = 0; j < arr.length(); j++) {
							if (arr.getJSONObject(j).has("duration")) {
								provOSDInfo_l.duration = arr.getJSONObject(j).getInt("duration");
							}
							if (arr.getJSONObject(j).has("repetition")) {
								provOSDInfo_l.repetition = arr.getJSONObject(j).getInt("repetition");
							}
							if (arr.getJSONObject(j).has("interval")) {
								provOSDInfo_l.Interval = arr.getJSONObject(j).getInt("interval");
							}
							if (arr.getJSONObject(j).has("messagetitle")) {
								provOSDInfo_l.messagetitle = arr.getJSONObject(j).getString("messagetitle");
							}
							if (arr.getJSONObject(j).has("message")) {
								provOSDInfo_l.message = arr.getJSONObject(j).getString("message");
							}
							if (arr.getJSONObject(j).has("priority")) {
								provOSDInfo_l.priority = arr.getJSONObject(j).getInt("priority");
							}

						}
						provObj.provOSDInfo = provOSDInfo_l;
					}

				}
				if (jsonObj2.getString("requestType").equals("BMAIL")) {
					// Fetch BMAIL info
					arr = jsonObj.getJSONArray("paramsDetailsInfo");
					if (arr.length() >= 1) {
						HSCASVisionMAILInfo provBMAILInfo_l = new HSCASVisionMAILInfo();
						for (int j = 0; j < arr.length(); j++) {
							if (arr.getJSONObject(j).has("sender")) {
								provBMAILInfo_l.sender = arr.getJSONObject(j).getString("sender");
							}
							if (arr.getJSONObject(j).has("subject")) {
								provBMAILInfo_l.subject = arr.getJSONObject(j).getString("subject");
							}
							if (arr.getJSONObject(j).has("message")) {
								provBMAILInfo_l.message = arr.getJSONObject(j).getString("message");
							}
						}
						provObj.provBMAILInfo = provBMAILInfo_l;
					}
				}
			}
			// Fetch OldProduct info
			if (jsonObj.has("oldOrderList")) {
				arr = jsonObj.getJSONArray("oldOrderList");
				ProvProductInfo provProductInfo_l = null;
				for (int i = 0; i < arr.length(); i++) {
					provProductInfo_l = new ProvProductInfo();
					provProductInfo_l.orderId = arr.getJSONObject(i).getLong("orderId");
					provProductInfo_l.planId = arr.getJSONObject(i).getLong("planId");

					if (arr.getJSONObject(i).has("orderStartDate")) {
						provProductInfo_l.orderStartDate = new SimpleDateFormat("dd/MM/yyyy")
								.parse(arr.getJSONObject(i).getString("orderStartDate"));
					} else {
						provProductInfo_l.orderStartDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/12/2017");
					}
					if (arr.getJSONObject(i).has("orderEndDate")) {
						provProductInfo_l.orderEndDate = new SimpleDateFormat("dd/MM/yyyy")
								.parse(arr.getJSONObject(i).getString("orderEndDate"));
					} else {
						provProductInfo_l.orderEndDate = new SimpleDateFormat("dd/MM/yyyy").parse("31/12/2030");

					}
					JSONObject jsonObjprod = arr.getJSONObject(i);
					System.out.println("Innner json = " + jsonObjprod.toString());
					arr1 = jsonObjprod.getJSONArray("products");
					for (int j = 0; j < arr1.length(); j++) {
						ProvProduct provProduct_l = new ProvProduct();
						provProduct_l.neProductId = arr1.getJSONObject(j).getString("neProductId");
						provProduct_l.productName = arr1.getJSONObject(j).getString("productName");
						provProductInfo_l.products.add(j, provProduct_l);
					}
					provObj.oldProvProductInfo.add(i, provProductInfo_l);
				}

				// Ftech task list

				// Fetch OldProduct info
				if (jsonObj.has("TaskList")) {
					arr = jsonObj.getJSONArray("TaskList");
					ProvTaskInfo provTaskInfo_l = null;
					for (int i = 0; i < arr.length(); i++) {
						provTaskInfo_l = new ProvTaskInfo();
						provTaskInfo_l.task_id = arr.getJSONObject(i).getLong("task_id");
						provTaskInfo_l.task_name = arr.getJSONObject(i).getString("task_name");
						provObj.provTaskInfoList.add(provTaskInfo_l);
					}

				}

			}
		} catch (JSONException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return provObj;
	}

	public static String getTerminalId(String serialNO) {
		Long decimalValue = FileUtils.getDecimal(serialNO);
		String appendDecimalValue = Integer.toString(3) + decimalValue;
		String terminalId = Long.toHexString(Long.parseLong(appendDecimalValue));
		return terminalId.toUpperCase();
	}

	public static Date appendDateTime(Date date) {
		Calendar t = Calendar.getInstance();

		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
		Time localTime = Time.valueOf(sdfTime.format(new Date().getTime()).toString());
		t.setTime(localTime);

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, t.get(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, t.get(Calendar.MINUTE));
		c.set(Calendar.SECOND, t.get(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, t.get(Calendar.MILLISECOND));
		System.out.println("FileUtils.appendDateTime()" + c.getTime());
		return c.getTime();
	}

	public static Date appendOneHourToDateTime(Date date) {
		Calendar t = Calendar.getInstance();

		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
		Time localTime = Time.valueOf(sdfTime.format(new Date().getTime()).toString());
		t.setTime(localTime);

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, t.get(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, t.get(Calendar.MINUTE));
		c.set(Calendar.SECOND, t.get(Calendar.SECOND));
		c.set(Calendar.MILLISECOND, t.get(Calendar.MILLISECOND));
		return c.getTime();
	}

	public static String getInnerUserIdformStream(final InputStream inStream) {

		System.out.println("FileUtils.getInnerUserIdformStream()" + inStream);
		return inStream.toString();
	}

	public static byte[] getDateTimeArray(final Date dt) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		final int day = cal.get(5);
		final int month = cal.get(2) + 1;
		final int year = cal.get(1) - 1980;
		final int hour = cal.get(11);
		final int minute = cal.get(12);
		return new byte[] { (byte) day, (byte) month, (byte) year, (byte) hour, (byte) minute };
	}

	public static byte[] getDateArray(final Date dt) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		final int day = cal.get(5);
		final int month = cal.get(2) + 1;
		final int year = cal.get(1) - 1980;
		final int hour = cal.get(11);
		final int minute = cal.get(12);
		return new byte[] { (byte) day, (byte) month, (byte) year };
	}

	public static String DateInHexDateTime(Date date) {
		final byte[] messageDate = FileUtils.getDateTimeArray(new Date());
		return FileUtils.bytesToHex(messageDate);
	}

	public static String DateInHexDate(Date date) {
		final byte[] messageDate = FileUtils.getDateArray(date);
		return FileUtils.bytesToHex(messageDate);
	}

	public static String productByte(byte[] command) {
		return FileUtils.bytesToHex(command);
	}

	public static String getHexforProductAdd(int i) {
		final ByteArrayOutputStream streamOut = new ByteArrayOutputStream();

		streamOut.write(i >>> 8 & 0xFF);
		streamOut.write(i & 0xFF);
		streamOut.write(0);
		streamOut.write(i >>> 8 & 0xFF);
		streamOut.write(i & 0xFF);
		streamOut.write(0);
		streamOut.write(0);
		streamOut.write(0);
		streamOut.write(i >>> 8 & 0xFF);
		streamOut.write(i & 0xFF);

		return productByte(streamOut.toByteArray());
	}

	public static String getHexforProductcancel(int i) {
		final ByteArrayOutputStream streamOut = new ByteArrayOutputStream();

		streamOut.write(i >>> 8 & 0xFF);
		streamOut.write(i & 0xFF);

		return productByte(streamOut.toByteArray());
	}

	public static void main(String args[]) throws Exception {
		/*
		 * byte[] b = { (byte) 255, (byte) 0, (byte) 0, (byte) 6, (byte) 252 };
		 * 
		 * String str = "FF00000566"; byte[] val = new byte[str.length() / 2]; for (int
		 * i = 0; i < val.length; i++) { int index = i * 2; int j =
		 * Integer.parseInt(str.substring(index, index + 2), 16); val[i] = (byte) j; }
		 * System.out.println(Arrays.toString(val));
		 * 
		 */
		/*
		 * String hexString = "01310012FF000005660C333231313642333033454132"; String j =
		 * hexString.substring(8, 18);
		 * 
		 * byte[] bytes = Hex.decodeHex(j.toCharArray()); InputStream targetStream = new
		 * ByteArrayInputStream(bytes);
		 * System.out.println("FileUtils.main()"+FileUtils.getInnerUserIdformStream(
		 * targetStream));
		 */
		/*
		 * final ByteArrayOutputStream streamOut = new ByteArrayOutputStream(); int
		 * linkId[] = { 997 }; for (int i = 0; i < linkId.length; ++i) {
		 * 
		 * streamOut.write(linkId[i] >>> 8 & 0xFF); streamOut.write(linkId[i] & 0xFF);
		 * streamOut.write(0); streamOut.write(linkId[i] >>> 8 & 0xFF);
		 * streamOut.write(linkId[i] & 0xFF); streamOut.write(0); streamOut.write(0);
		 * streamOut.write(0); streamOut.write(linkId[i] >>> 8 & 0xFF);
		 * streamOut.write(linkId[i] & 0xFF); }
		 */
		/*
		 * System.out.println("FileUtils.main()" + FileUtils.getHexforProduct(997));
		 * Date newDate = DateUtils.addMonths(new Date(), 1);
		 * System.out.println("FileUtils.main()" + newDate);
		 */
	
		System.out.println("FileUtils.main()"+FileUtils.getDecimal("FF"));
		System.out.println("FileUtils.main()"+FileUtils.getHexadecimalValue(3, 4));

	}

}
