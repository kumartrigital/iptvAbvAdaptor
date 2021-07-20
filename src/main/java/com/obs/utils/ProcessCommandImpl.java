package com.obs.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import com.obs.consumer.HSCASConsumer;
import com.obs.data.OBSProvOrder;
import com.obs.data.ProcessRequestData;

import au.com.bytecode.opencsv.CSVReader;

public class ProcessCommandImpl {

	private static DataInputStream dataInputStream;
	private static DataOutputStream dataOutputStream;
	private static Socket socket = null;
	static Logger logger = Logger.getLogger(HSCASConsumer.class);
	private PropertiesConfiguration prop;
	public Timer timer;
	private String number;
	private Long id;
	public int timePeriod;
	public static int wait;
	private String ROOT_KEY;
	private String strCmd = "";
	public int iCmdLength = 0;
	private ProcessingMessage processingMessage = null;
	private int messageId;
	public String reservedBits8 = "11111111";
	private String SMS_SAS_LinkId;
	static String dataToBeSent = null;

	static String hexResponse = null;

	public ProcessCommandImpl() {

	}

	@SuppressWarnings("static-access")
	public ProcessCommandImpl(Socket requestSocket, PropertiesConfiguration prop2) {
		try {
			boolean status = false;
			String ret_code = "";
			this.socket = requestSocket;
			this.prop = prop2;
			wait = prop.getInt("ThreadSleep_period");
			timePeriod = prop.getInt("TimePeriod");
			ROOT_KEY = prop.getString("Root_Key");

			ByteArrayOutputStream connectionCommand = new ByteArrayOutputStream();
			connectionCommand.write(01);// Protocol Version
			connectionCommand.write(01);// Tag
			connectionCommand.write(00);// Length
			connectionCommand.write(05);// Length
			connectionCommand.write(00);// SMS ProviderUnique ID
			connectionCommand.write(63);// SMS ProviderUnique ID
			connectionCommand.write(01);// SMS ProviderUnique ID
			connectionCommand.write(01);// SMS ProviderUnique ID
			connectionCommand.write(-1);// Reserved bits

			String streamData = connectionCommand.toString();
			logger.info("First Connection Request string:" + streamData);
			dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

			byte[] request = connectionCommand.toByteArray();

			this.sendData(dataOutputStream, request);
			this.receiveData();

			this.processingMessage = new ProcessingMessage(this.prop);

			logger.info("[SYSTEM]Adater successfully connected to provisioning server.");
		} catch (IOException e) {
			logger.error("unable to read or write file " + e.getMessage());
		} catch (Exception e) {
			logger.error(e.getCause().getLocalizedMessage());
		}
	}

	public void setSocket(Socket requestSocket) {
		ProcessCommandImpl.socket = requestSocket;
	}

	public void append(String content, boolean bAddLength) throws Exception {
		if ((content == null) || (content.length() == 0)) {
			throw new RuntimeException("Content cannot be Null ");
		}
		try {
			this.strCmd = this.strCmd.concat(content);
			if (bAddLength) {
				this.iCmdLength += content.length();
			}
		} catch (Exception exp) {
			exp.printStackTrace();
			logger.info("Exception in append: " + exp.getMessage());
			throw new RuntimeException("Unable to concat the content");
		}
	}

	public static void getconnectiontemp() throws UnknownHostException, IOException {
		Socket requestSocket = new Socket("192.168.5.28", 2202);
		ProcessCommandImpl p = new ProcessCommandImpl(requestSocket, null);

	}

	public boolean sendData(DataOutputStream os, byte[] byteData) {

		boolean status = false;
		logger.info("send Date methos" + os);
		if (this.socket.isClosed()) {
			System.out.println("ProcessCommandImpl.sendData()");
			HSCASConsumer.getConnection();
		}
		try {
			if ((byteData == null) || (byteData.length == 0)) {
				return status;
			}
			logger.info("Message sent :" + Arrays.toString(byteData));
			if (!this.socket.isClosed()) {
				synchronized (this.dataOutputStream) {
					os.write(byteData);
					os.flush();
					status = true;
				}
			}
		} catch (IOException exp) {
			logger.info("ABVAdapter::sendData():IOException: " + exp.getMessage());
			if (exp.getMessage().toUpperCase().contains("CONNECT")) {
				try {
					if (os != null) {
						os.close();
						os = null;
					}

				} catch (Exception e) {
					logger.info(
							"ABVAdapter::sendData():IOException when closing connection object: " + exp.getMessage());
				}
			}
			status = false;
		} catch (Exception exp) {
			logger.info("ABVAdapter::sendData():Exception: " + exp.getMessage());
			if (exp.getMessage().toUpperCase().contains("CONNECT")) {
				try {
					if (os != null) {
						os.close();
						os = null;
					}
					if (this.socket != null) {
						this.socket.close();
						this.socket = null;
					}
				} catch (Exception e) {
					logger.info("ABVAdapter::sendData():Exception when closing connection object: " + exp.getMessage());
				}
			}
			status = false;
		}
		return status;
	}

	public String receiveData() {
		int charCount = 0;
		int start = 0;
		String responseRcvd = "";
		try {
			if (!this.socket.isClosed()) {
				byte[] byteArray = new byte[1024];
				charCount = this.dataInputStream.read(byteArray, start, byteArray.length - start);
				logger.info("charCount" + charCount);
				if (charCount > 0) {
					int protocolVersion = this.dataInputStream.read();
					int tag = this.dataInputStream.read();

					responseRcvd = processReceivedData(byteArray, charCount, tag);
				} else {
					logger.info("charCount" + charCount);

					responseRcvd = "Error";
				}
			}
		} catch (IOException exp) {
			logger.info("ABVAdapter::receiveData():IOException:" + exp.getMessage());
			if (exp.getMessage().toUpperCase().contains("CONNECT")) {
				try {
					if (this.dataInputStream != null) {
						this.dataInputStream.close();
						this.dataInputStream = null;
					}
					if (this.socket != null) {
						this.socket.close();
						this.socket = null;
					}
				} catch (Exception e) {
					logger.info("ABVAdapter::receiveData():IOException when closing connection object: "
							+ exp.getMessage());
				}
			}
			responseRcvd = "Error";
		} catch (Exception exp) {
			logger.info("ABVAdapter::receiveData():Exception: " + exp.getMessage());
			if (exp.getMessage().toUpperCase().contains("CONNECT")) {
				try {
					if (this.dataInputStream != null) {
						this.dataInputStream.close();
						this.dataInputStream = null;
					}
					if (this.socket != null) {
						this.socket.close();
						this.socket = null;
					}
				} catch (Exception e) {
					logger.info(
							"ABVAdapter::receiveData():Exception when closing connection object: " + exp.getMessage());
				}
			}
			responseRcvd = "Error";
		}
		return responseRcvd;
	}

	public String processReceivedData(byte[] byteArray, int charCount, int tag) {

		String errCode = "";

		try {
			String response = "";
			StringBuffer sb = new StringBuffer();
			String str = "";
			response = FileUtils.bytesToHex(byteArray, 0, charCount);

			logger.info(" Response: " + response);
			this.hexResponse = response;

			if (tag == 2) {
				logger.info(" Response: " + response);
				logger.info("Adaptor successfully connected");
				errCode = "00";
			} else if (tag == 49) {
				logger.info(" Response: " + response);
				logger.info("REGISTER_MAC_ADDRESS_RESPONSE successfully done ");
				errCode = "00";
			} else if (tag == 224) {
				logger.info(" Response: " + response);
				errCode = "00";

			} else {
				errCode = Byte.toString(byteArray[5]);

				if (errCode.length() == 1) {
					// errCode = "0" + tag;
					System.out.println("ProcessCommandImpl.processReceivedData()" + "0" + tag);
					errCode = "00";
				}
				if (errCode.equals("00")) {
					logger.info("Successfull command execution");
				} else {
					logger.info("Error command failed: " + HSCASErrorCodes.getErrorDesc(errCode));
					errCode = "00";
				}
				return errCode;
			}
		} catch (

		Exception exp) {
			logger.info("Exception at ABVAdapter:processReceivedData : " + exp.getMessage());
		}
		return errCode;
	}

	public void processRequest(ProcessRequestData processRequestData) {

		Boolean status = false;
		String ret_code = "";
		Long task_no = (long) 1;
		try {
			OBSProvOrder localProvObj = FileUtils.jsonToProvObj(processRequestData.getJsonObject());
			logger.info("Request Id : " + processRequestData.getId() + " Request Type : "
					+ processRequestData.getRequestType() + " Task Id : " + processRequestData.gettaskId());
			ProcessCommandImpl p = new ProcessCommandImpl();

			p.closeIOFile();
			p.openConnectionTemp();

			if (processRequestData.getRequestType().equalsIgnoreCase(HSCASConstants.REQ_ACTIVATION)) {

				task_no = processRequestData.gettaskId();
				if (task_no == 1) {
					logger.info("Byte data received");
					byte[] bytedataforAddSmartCard = this.processingMessage.registerMAC(localProvObj);
					if ((bytedataforAddSmartCard != null) && (socket != null)) {
						dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
						dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

						try {
							status = sendData(dataOutputStream, bytedataforAddSmartCard);

						} catch (Exception e) {
							processRequestData.setTaskId(task_no);// updating
							closeIOFile();
							processRequest(processRequestData);
						}

						if (!status) {
							logger.error("Unable to send data for Connection request");
							ret_code = "99";
							processResult(processRequestData.getId(), ret_code, task_no, null);
							return;
						}
						ret_code = receiveData();
						localProvObj.setResponse(hexResponse.substring(8, 18));

						logger.info("inner_userId of mac :" + localProvObj.getResponse());

						logger.info("ret_code :" + ret_code);
						if (!ret_code.equals("00")) {
							task_no = (long) 1;
							logger.error("Unable to receive data for Connection request");
							processResult(processRequestData.getId(), ret_code, task_no, null);
							return;
						} else {
							task_no = task_no + 1;
							processResult(processRequestData.getId(), ret_code, task_no, localProvObj.getResponse());
							System.out.println("Successfull done task no : " + task_no);
						}
					} else {
						throw new Exception("Command preperation failed");
					}
					logger.info("register mac address done");
				}
				// TASK2:
				if (task_no == 2) {

					System.out.println("user ID :" + localProvObj.getResponse());

					byte[] bindingByteArrayData = this.processingMessage.userAreaModifying(localProvObj);
					logger.info("Going to prepare bind data");
					if (bindingByteArrayData != null) {
						dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
						dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

						try {
							status = sendData(dataOutputStream, bindingByteArrayData);

						} catch (Exception e) {
							processRequestData.setTaskId(task_no);// updating
							closeIOFile();
							processRequest(processRequestData);
						}

						if (!status) {
							logger.error("Unable to send data for Connection request");
							ret_code = "99";
							processResult(processRequestData.getId(), ret_code, task_no, null);
							return;
						}
						ret_code = receiveData();
						System.out.println("ProcessCommandImpl.processRequest() ret_code :" + ret_code);
						if (!ret_code.equals("00")) {
							logger.error("Error in processing command with Error :"
									+ HSCASErrorCodes.getErrorDesc(ret_code));
							// call api for resposne processing
							processResult(processRequestData.getId(), ret_code, task_no, null);
							return;
						} else {
							task_no = task_no + 1;
							System.out.println("Successfull, task no : " + task_no);
						}
						logger.info("Activation  done in CAS");
					} else {
						throw new Exception("Command preperation failed");
					}
				}
				if (task_no == 3) {
					byte[] bindingByteArrayData = this.processingMessage.userBinding(localProvObj);
					logger.info("Going to prepare bind data");
					if (bindingByteArrayData != null) {
						dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
						dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

						try {
							status = sendData(dataOutputStream, bindingByteArrayData);

						} catch (Exception e) {
							processRequestData.setTaskId(task_no);// updating
							closeIOFile();
							processRequest(processRequestData);
						}

						if (!status) {
							logger.error("Unable to send data for Connection request");
							ret_code = "99";
							processResult(processRequestData.getId(), ret_code, task_no, null);
							return;
						}
						ret_code = receiveData();
						System.out.println("ProcessCommandImpl.processRequest()" + ret_code);
						if (!ret_code.equals("00")) {
							logger.error("Error in processing command with Error :"
									+ HSCASErrorCodes.getErrorDesc(ret_code));
							// call api for resposne processing
							processResult(processRequestData.getId(), ret_code, task_no, null);
							return;
						} else {

							task_no = task_no + 1;
							System.out.println("Successfull, task no : " + task_no);
						}
						logger.info("Activation  done in CAS");
					} else {
						throw new Exception("Command preperation failed");
					}
				}
				if (task_no == 4) {
					byte[] bindingByteArrayData = this.processingMessage.licenseUpdating(localProvObj);
					logger.info("Going to prepare bind data");
					if (bindingByteArrayData != null) {
						dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
						dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

						try {
							status = sendData(dataOutputStream, bindingByteArrayData);

						} catch (Exception e) {
							processRequestData.setTaskId(task_no);// updating
							closeIOFile();
							processRequest(processRequestData);
						}

						if (!status) {
							logger.error("Unable to send data for Connection request");
							ret_code = "99";
							processResult(processRequestData.getId(), ret_code, task_no, null);
							return;
						}
						ret_code = receiveData();
						System.out.println("ProcessCommandImpl.processRequest()" + ret_code);
						if (!ret_code.equals("00")) {
							logger.error("Error in processing command with Error :"
									+ HSCASErrorCodes.getErrorDesc(ret_code));
							// call api for resposne processing
							processResult(processRequestData.getId(), ret_code, task_no, null);
							return;
						} else {

							logger.info("updating details  done in obb query" + ret_code);

							processResult(processRequestData.getId(), ret_code, (long) 0, null);
						}
						logger.info("Activation  done in CAS");
					} else {
						throw new Exception("Command preperation failed");
					}
				}

			} else {
				ret_code = "ZX";
				processResult(processRequestData.getId(), ret_code, task_no, null);
				logger.error("REQUEST TYPE NOT MATCHING or NOT IMPLEMENTED");
			}
		} catch (ConfigurationException e) {
			ret_code = "ZY";
			processResult(processRequestData.getId(), ret_code, task_no, null);
			logger.error("(ConfigurationException) Properties file loading error.. : " + e.getMessage());
		} catch (Exception e) {
			ret_code = "ZZ";
			processResult(processRequestData.getId(), ret_code, task_no, null);
		}
	}

	public void connectionHolding() {
		Boolean status = false;
		String ret_code = "";
		if (socket != null) {

			try {
				StringBuilder createFingerPrintData = new StringBuilder();

				String DBID = FileUtils.getHexadecimalValue(this.prop.getInt("DB_ID"), 2);

				createFingerPrintData.append(DBID.substring(DBID.length() - 4));
				FileUtils.updateDB_id(this.prop.getInt("DB_ID"), prop);

				createFingerPrintData.append(FileUtils.getHexadecimalValue(this.prop.getInt("CAS_Ver"), 1));// cas
																											// system
																											// version

				createFingerPrintData
						.append(FileUtils.getHexadecimalValue(HSCASCommandCodes.COMMAND_START_FINGERPRINT, 1));

				StringBuilder tempcreateFingerPringData = new StringBuilder();

				Long decimalValue = FileUtils.getDecimal("0635BBEB");
				String appendDecimalValue = Integer.toString(3) + decimalValue;
				String terminalId = Long.toHexString(Long.parseLong(appendDecimalValue));

				logger.info("0635BBEB : terminal id :" + terminalId);

				tempcreateFingerPringData.append(terminalId.toUpperCase());// terminal Id

				tempcreateFingerPringData.append(FileUtils.getHexadecimalValue(5, 2));// duration Id

				tempcreateFingerPringData.append(FileUtils.dateToHex(FileUtils.getDateTimePlusOneDay(new Date())));// Product_Begin_Time

				System.out.println(FileUtils.getHexadecimalValue(tempcreateFingerPringData.length() / 2, 2));

				createFingerPrintData.append(FileUtils.getHexadecimalValue(tempcreateFingerPringData.length() / 2, 2));// length

				System.out.println("length of the body" + tempcreateFingerPringData.length() / 2);
				createFingerPrintData.append(tempcreateFingerPringData);

				byte[] byteArrayData = FileUtils.hexStringToByteArray(createFingerPrintData.toString());

				if (byteArrayData != null) {
					dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
					dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
					status = sendData(dataOutputStream, byteArrayData);
					logger.info("fingerprint :" + createFingerPrintData);
					if (!status) {
						logger.info("Unable to send data for Connection request");
						ProcessCommandImpl.closeIOFile();
						return;
					}

					ret_code = receiveData();
					if (!ret_code.equals("00")) {
						logger.info("Failed received heardbeat");
						// ProcessCommandImpl.closeIOFile();
					} else {
						logger.info("Successfully received heardbeat");
					}

				}

			} catch (Exception e) {
				// TODO: handle exception
				ProcessCommandImpl.closeIOFile();

			}
		} else {
			ProcessCommandImpl.closeIOFile();

		}

	}

	public static void processResult(Long id, String value, Long taskid, String innerUserId) {
		try {
			logger.info("output from CAS Server is :" + value + " id : " + id + " task : " + taskid);
			if (value == null) {
				throw new NullPointerException();
			} else {
				HSCASConsumer.sendResponse(id, value, taskid, innerUserId);
			}
		} catch (NullPointerException e) {
			logger.error("NullPointerException : Output from the Oss System Server is : " + value);
		} catch (Exception e) {
			logger.error("Exception : " + e.getMessage());
		}
	}

	public void Reminder(int seconds) {
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		timer.schedule(new RemindTask(), seconds);
	}

	class RemindTask extends TimerTask {
		public void run() {
			connectionHolding();
			Reminder(timePeriod);
		}
	}

	public static String ReadOutput(String output) {
		try {

			CSVReader reader = new CSVReader(new StringReader(output));
			String[] tokens;
			String message = "";
			tokens = reader.readNext();

			if (tokens.length > 1) {
				String mes = tokens[1];

				if (mes.equalsIgnoreCase("0")) {
					message = "success";
				} else {
					String errorid = tokens[1];
					String error = tokens[2];
					message = "failure : Exception error code is : " + errorid + " , Exception/Error Message is : "
							+ error;
				}
			}
			return message;
		} catch (IOException e) {
			return null;
		}
	}

	public static void closeIOFile() {
		try {
			dataInputStream.close();
			dataOutputStream.close();
			Thread.sleep(wait);
			throw new IOException();
		} catch (InterruptedException e) {
			logger.error("thread is Interrupted for the : " + e.getCause().getLocalizedMessage());
		} catch (IOException e) {
			logger.error("The Socket server connection is DisConnected, ReConnect to the Server");
			HSCASConsumer.getConnection();
		}
	}

	public void openConnectionTemp() throws UnknownHostException, IOException {

		this.socket = new Socket("192.168.5.28", 2202);

		this.dataInputStream = new DataInputStream(this.socket.getInputStream());
		this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());

		ByteArrayOutputStream connectionCommand = new ByteArrayOutputStream();
		connectionCommand.write(01);// Protocol Version
		connectionCommand.write(01);// Tag
		connectionCommand.write(00);// Length
		connectionCommand.write(05);// Length
		connectionCommand.write(00);// SMS ProviderUnique ID
		connectionCommand.write(63);// SMS ProviderUnique ID
		connectionCommand.write(01);// SMS ProviderUnique ID
		connectionCommand.write(01);// SMS ProviderUnique ID
		connectionCommand.write(-1);// Reserved bits

		this.dataOutputStream.write(connectionCommand.toByteArray());
		this.dataOutputStream.flush();
	}

	public void openCloseConnection() throws UnknownHostException, IOException {

		this.socket = new Socket("192.168.5.28", 2202);

		this.dataInputStream = new DataInputStream(this.socket.getInputStream());
		this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());

		ByteArrayOutputStream connectionCommand = new ByteArrayOutputStream();
		connectionCommand.write(01);// Protocol Version
		connectionCommand.write(04);// Tag
		connectionCommand.write(00);// Length
		connectionCommand.write(01);// Length
		connectionCommand.write(00);// SMS ProviderUnique ID

		this.dataOutputStream.write(connectionCommand.toByteArray());
		this.dataOutputStream.flush();
	}

}
