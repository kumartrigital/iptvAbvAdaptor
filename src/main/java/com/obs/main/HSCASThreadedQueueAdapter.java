package com.obs.main;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import com.obs.consumer.HSCASConsumer;
import com.obs.data.OBSProvOrder;
import com.obs.data.ProcessRequestData;
import com.obs.producer.HSCASProducer;
import com.obs.utils.FileUtils;

public class HSCASThreadedQueueAdapter {

	public static void main(String[] args) throws InterruptedException {
		try {
			Queue<ProcessRequestData> queue = new ConcurrentLinkedQueue<ProcessRequestData>();
			String path = System.getProperty("user.dir");

			PropertiesConfiguration prop = new PropertiesConfiguration(path + "/HSCASIntegrator.ini");
			String logPath = prop.getString("FilePath");
			File filelocation = new File(logPath);
			if (!filelocation.isDirectory()) {
				filelocation.mkdirs();
			}
			Logger logger = Logger.getRootLogger();
			FileAppender appender = (FileAppender) logger.getAppender("fileAppender");
			appender.setFile(logPath + "adapter.log");
			appender.activateOptions();
			

			int thcount = prop.getInt("threadCount");
			HSCASProducer p = new HSCASProducer(queue, prop);
			Thread t1 = new Thread(p);
			t1.start();
			Thread consumerThreads[] = new Thread[thcount];
			for (int i = 0; i < thcount; i++) {
				System.out.println("Consumer Thread launched : " + i + 1);
				HSCASConsumer c = new HSCASConsumer(queue, prop, i + 1);
				consumerThreads[i] = new Thread(c);
			}
			for (int i = 0; i < thcount; i++) {
				System.out.println("Thread starting : " + i + 1);
				consumerThreads[i].start();
			}
			for (int i = 0; i < thcount; i++) {
				try {
					consumerThreads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (ConfigurationException e) {
			System.out.println("(ConfigurationException) Properties file loading error.... : " + e.getMessage());
			return;
		}

	}

	

}
