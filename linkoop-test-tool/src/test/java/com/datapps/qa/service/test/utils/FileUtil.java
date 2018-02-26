package com.datapps.qa.service.test.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

public class FileUtil {

	/**
	 * 读取本文文件中的字符串并返回
	 * 
	 * @param path 文件路径
	 * @return 字符串
	 * @author Matrix42
	 * @throws IOException
	 */
	public static String getStringFromFile(File file) throws IOException {
		StringBuffer result = new StringBuffer();
		// BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));

		InputStreamReader isr = null;
		isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
		BufferedReader reader = new BufferedReader(isr);
		String string;
		// 去除文件中的注释，单行注释//，单独一行的文件
		while ((string = reader.readLine()) != null) {
			string = string.trim();
			// if(string.startsWith("//"))
			if (string.startsWith("//")) {
				if (!(string.indexOf("//") == -1)) {
					string = string.substring(0, string.indexOf("//"));
				}
			}
			result.append(string);
		}
		reader.close();
		// System.out.println("result\n"+result);
		return result.toString();
	}

	/**
	 * 将项目中的字符串保存到文件
	 * 
	 * @param string 要保存的字符串 path 要保存的路径
	 * @author Matrix42
	 * @throws IOException
	 */

	public static void StringTOFile(String string, String path) throws IOException {

		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
		}
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file));
		BufferedWriter writer = new BufferedWriter(osw);
		writer.write(string);
		writer.close();
	}

	/**
	 * 读取配置文件
	 * 
	 * @return Properties对象
	 * @throws IOException
	 */
	public static Properties getProperties() throws IOException {

		Properties info = new Properties();

		InputStream defaultInput = FileUtil.class.getResourceAsStream("/config-default.properties");
		info.load(defaultInput);

		String configLocation = System.getProperty("config.location");
		if (configLocation != null && !configLocation.isEmpty()) {
			System.out.println("load configuration from config.location: " + configLocation);
			try {
				InputStream input = new FileInputStream(configLocation);
				info.load(input);
			} catch (Exception e) {
				System.err.println("load configuration failed.");
			}
		} else {
			System.out.println("load configuration from config.properties");
			try {
				InputStream input = FileUtil.class.getResourceAsStream("/config.properties");
				info.load(input);
			} catch (Exception e) {
				System.err.println("load configuration failed.");
			}
		}

		String version = System.getProperty("version");
		if (version != null && !version.isEmpty()) {
			System.out.println("set version to " + version);
			info.setProperty("Version", version);
		}

		return info;
	}

	public static String getAbsolutePath(String RelativePath) throws IOException {

		return Config.CasePath + "/" + RelativePath;

	}

}
