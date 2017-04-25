package uk.co.mattcarus.hnklogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.SwingUtilities;

import uk.co.mattcarus.hnklogger.exceptions.HNKPropertyNotFoundException;
import uk.co.mattcarus.hnklogger.gui.*;
import uk.co.mattcarus.hnklogger.plugin.*;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui.*;

public class HNKLogger {
	HNKLoggerProperties properties;
	Log log;
	int nextSerial = 0;
	public static LoggerResource loggerResource;
	
	public static ObjectList<Plugin> hooks;
	
	
	public HNKLogger()
	{
		// Set system proxies
	    System.setProperty ("http.proxyHost", "black-barracuda.black.ndr");
	    System.setProperty ("http.proxyPort", "3128");
	    
		// Load properties
		
		try {
			properties = new HNKLoggerProperties("config.properties");
			System.out.println(properties.getProperties().getProperty("gui.interface_type"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		this.loadResources();
		this.loadHooks();

		this.log = new Log();
		this.nextSerial = this.log.getNextSerialNumber();
		
		HNKLoggerGUI gui;
		
		try {
			if ( properties.getProperty("gui.interface_type").equals("LanternaGUI") )
			{
				gui = new LanternaGUI();
			}
			else
			{
				// Swing is default
				gui = new SwingGUI();
			}
		}
		catch (Exception e) {
			// Swing is default
			gui = new SwingGUI();
		}
		
		this.loadGUI(gui);
		HNKLogger.hooks.run("initProperties", properties);
		HNKLogger.hooks.run("initGUI", gui);
		
		System.out.println(loggerResource.search("M0SPF").get("country"));
		
	}

	public void loadGUI(HNKLoggerGUI gui)
	{
		gui.startGUI(this.log);
	}
	
	public void loadHooks()
	{
		hooks = new ObjectList<Plugin>();
		
		//hooks.add( new PostToWebAddress() );
		hooks.add( new Clock() );
		hooks.add( new QRZLookup() );
		hooks.add( new AnalogueClock() );
		
		HNKLogger.hooks.run("init");
	}
	
	public static void main(String[] args)
	{
		HNKLogger hnkLogger = new HNKLogger();
		//HNKLoggerGUI gui = new LanternaGUI();
		//hnkLogger.loadGUI(gui);
	    //textGUI.getScreen().stopScreen();
	}
	
	public void loadResources()
	{
		loggerResource = new CSVLoggerResource("cqwwpre3.txt");
	}
}
