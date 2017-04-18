package uk.co.mattcarus.hnklogger;

import java.util.*;
import java.io.*;

public class HNKLoggerProperties {
	Properties properties;
	String result = "";
	InputStream inputStream;
 
	public HNKLoggerProperties() throws IOException {
 
		try {
			properties = new Properties();
			String propFileName = "config.properties";
 
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
			if (inputStream != null) {
				properties.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			inputStream.close();
		}	
	}
	
	public Properties getProperties()
	{
		return this.properties;
	}
}

