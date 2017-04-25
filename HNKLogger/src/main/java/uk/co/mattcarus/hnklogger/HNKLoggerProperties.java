package uk.co.mattcarus.hnklogger;

import java.util.*;

import uk.co.mattcarus.hnklogger.exceptions.HNKPropertyNotFoundException;

import java.io.*;

public class HNKLoggerProperties {
	Properties properties;
	String result = "";
	InputStream inputStream;
 
	public HNKLoggerProperties(String propFileName) throws IOException {
 
		try {
			properties = new Properties();
			
			inputStream = new FileInputStream(propFileName);
 
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
	
	public String getProperty(String propertyName) throws HNKPropertyNotFoundException
	{
		try {
			return this.getProperties().getProperty(propertyName);
		}
		catch (NullPointerException e) {
			throw new HNKPropertyNotFoundException();
		}
	}
	
	public int getPropertyAsInt(String propertyName) throws HNKPropertyNotFoundException
	{
		return Integer.parseInt(this.getProperty(propertyName));
	}
}

