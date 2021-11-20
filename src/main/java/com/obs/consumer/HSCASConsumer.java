package com.obs.consumer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;

import javax.management.RuntimeErrorException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.obs.data.ProcessRequestData;
import com.obs.utils.HSCASErrorCodes;
import com.obs.utils.ProcessCommandImpl;

public class HSCASConsumer implements Runnable {

	private static Queue<ProcessRequestData> messageQueue;
	static Socket requestSocket = null;
	private static PropertiesConfiguration prop;
	String message;
	private static HttpPost post;
	private static byte[] encoded;
	private static String tenantIdentifier;
	private static HttpClient httpClient;
	static Logger logger = Logger.getLogger("");
	private static ProcessCommandImpl processCommand;
	public static int wait;
	private int thrdid = 0;

	public static HttpClient wrapClient(HttpClient base) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				@SuppressWarnings("unused")
				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				@SuppressWarnings("unused")
				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			return null;
		}
	}

	int getThreadid() {
		return this.thrdid;
	}

	public static void getConnection() {
		try {
			logger.info("Connecting with Server ...");
			wait = prop.getInt("ThreadSleep_period");
			int portNumber = prop.getInt("port_number");
			String hostAddress = prop.getString("host_address");
			requestSocket = new Socket(hostAddress, portNumber);
			logger.info("Server is Connected with in the Host: '" + hostAddress + "' and PortNumber: '" + portNumber
					+ "'. ");
			if (processCommand == null) {
				processCommand = new ProcessCommandImpl(requestSocket, prop);
				processCommand.Reminder(prop.getInt("TimePeriod"));
			} else {
				processCommand.setSocket(requestSocket);
			}
			;
		} catch (UnknownHostException e) {
			logger.error("UnknownHostException:" + e.getMessage()
					+ ". The host_address or port_number is invalid . Verify the Details... ");
			return;
		} catch (IOException e) {
			logger.error(" Connection to the CAS server is Not Established .... , " + e.getMessage());
			try {
				Thread.sleep(wait);
				HSCASConsumer.getConnection();
			} catch (InterruptedException e1) {
				logger.error("thread is Interrupted for the : " + e.getCause().getLocalizedMessage());
			}
		} catch (NullPointerException e) {
			logger.error("NullPointerException:" + e.getMessage() + " ");
			return;
		}

	}

	@SuppressWarnings("static-access")
	public HSCASConsumer(Queue<ProcessRequestData> queue1, PropertiesConfiguration prop2, int thid) {
		try {
			this.messageQueue = queue1;
			this.prop = prop2;
			this.thrdid = thid;
			System.out.println("Consumer Calling Again +" + thid);
			httpClient = new DefaultHttpClient();
			httpClient = wrapClient(httpClient);
			String username = prop.getString("username");
			String password = prop.getString("password");
			tenantIdentifier = prop.getString("tenantIdentfier");
			String creds = username.trim() + ":" + password.trim();
			encoded = Base64.encodeBase64(creds.getBytes());
			HSCASConsumer.getConnection();
		} catch (Exception e) {
			logger.error("Exception:" + e.getStackTrace());
		}

	}

	public void run() {
		System.out.println("Thread id of consumer = " + this.thrdid);
		while (true) {
			try {
				synchronized (messageQueue) {
					consume();
					messageQueue.wait();
				}
			} catch (InterruptedException ex) {
				logger.error("thread is Interrupted for the : " + ex.getCause().getLocalizedMessage());
			}
		}
	}

	private void consume() throws InterruptedException {
		if (requestSocket != null) {
			logger.info(prop.getString("BSSQuery").trim() + "/" + 1);
			logger.info(messageQueue.size());
			while (!messageQueue.isEmpty()) {
				for (ProcessRequestData processRequestData : messageQueue) {
					messageQueue.poll();
					processCommand.processRequest(processRequestData);
				}
				messageQueue.notifyAll();
			}

		} else {
			Thread.sleep(wait);
			HSCASConsumer.getConnection();
		}

	}

	public static void sendResponse(Long id, String output, Long task_id , String InnerUserId) throws InterruptedException, JSONException {

		try {

			post = new HttpPost(prop.getString("BSSQuery").trim() + "/" + id);
			post.setHeader("Authorization", "Basic " + new String(encoded));
			post.setHeader("Content-Type", "application/json");
			post.addHeader("X-Obs-Platform-TenantId", tenantIdentifier);

			JSONObject object = new JSONObject();

			if (output.equals("00")) {
				object.put("receivedStatus", "0");
				object.put("receiveMessage", output);
				object.put("network_subscriber_id", InnerUserId);
			} else {
				object.put("receivedStatus", task_id);
				object.put("receiveMessage", "failure : " + output + " : " + HSCASErrorCodes.getErrorDesc(Integer.parseInt(output)));
			}
			System.out.println(output);
			System.out.println(object.toString());

			StringEntity se = new StringEntity(object.toString());
			post.setEntity(se);
			HttpResponse response = httpClient.execute(post);

			if (response.getStatusLine().getStatusCode() == 401) {
				logger.error("Authentication Failed : HTTP error code is: " + response.getStatusLine().getStatusCode());
				httpClient.getConnectionManager().shutdown();
				throw new AuthenticationException(
						"AuthenticationException :  BSS system server username (or) password you entered is incorrect . check in the ABVIntegrator.ini file");
			} else if (response.getStatusLine().getStatusCode() == 404) {
				logger.error("Resource Not Found Exception : HTTP error code is: "
						+ response.getStatusLine().getStatusCode());
				logger.error("Resource NotFound Exception :  BSS server system 'BSSQuery' url error.");
				
			} else if (response.getStatusLine().getStatusCode() != 200) {
				logger.error("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
				return;
			} else {
				logger.info("record is Updated Successfully in Bss System");
			}

			if (response.getEntity() != null) {
				response.getEntity().consumeContent();
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("thread is Interrupted for the : " + e.getMessage());
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException : " + e.getCause().getLocalizedMessage());
		} catch (IOException e) {
			logger.error("IOException : " + e.getCause() + ". verify the BSS system server running or not");
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e1) {
				logger.error("thread is Interrupted for the : " + e1.getCause().getLocalizedMessage());
			}
		} catch (AuthenticationException e) {
			logger.error("AuthenticationException: " + e.getLocalizedMessage());
		} catch (RuntimeErrorException e) {
			logger.error("ResourceNotFoundException: " + e.getLocalizedMessage());
		}

	}
}
