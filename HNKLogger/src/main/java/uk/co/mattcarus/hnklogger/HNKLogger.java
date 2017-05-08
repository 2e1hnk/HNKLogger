package uk.co.mattcarus.hnklogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.SwingUtilities;

import uk.co.mattcarus.hnklogger.exceptions.HNKPropertyNotFoundException;
import uk.co.mattcarus.hnklogger.gui.*;
import uk.co.mattcarus.hnklogger.gui.LanternaGUI.LanternaGUI;
import uk.co.mattcarus.hnklogger.gui.SwingGUI.SwingGUI;
import uk.co.mattcarus.hnklogger.gui.SwingGUI.plugins.*;
import uk.co.mattcarus.hnklogger.plugins.*;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui.*;

public class HNKLogger {
	HNKLoggerProperties properties;
	static HNKLoggerGUI gui;
	Log log;
	int nextSerial = 0;
	public static LoggerResource loggerResource;
	
	
	//public static ObjectList<Plugin> hooks;
	
	public static PluginRegistry<Plugin> pluginRegistry;
	
	
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
		//this.loadHooks();
		this.loadPlugins();

		this.log = new Log();
		this.nextSerial = this.log.getNextSerialNumber();
		
		
		
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
		//HNKLogger.hooks.run("initProperties", properties);
		HNKLogger.pluginRegistry.run("initProperties", properties, true);
		
		//HNKLogger.hooks.run("initGUI", gui);
		HNKLogger.pluginRegistry.run("initGUI", gui);
		
		System.out.println(loggerResource.search("M0SPF").get("country"));
		
	}

	public void loadGUI(HNKLoggerGUI gui)
	{
		gui.startGUI(this.log);
	}
	
	/*
	public void loadHooks()
	{
		hooks = new ObjectList<Plugin>();
		
		//hooks.add( new PostToWebAddress() );
		hooks.add( new Clock() );
		//hooks.add( new QRZLookup() );
		hooks.add( new RigCtl() );
		
		HNKLogger.hooks.run("init");
		HNKLogger.hooks.run("initProperties", this.properties);
	}
	*/
	
	public void loadPlugins()
	{
		pluginRegistry = new PluginRegistry<Plugin>();
		
		pluginRegistry.add( new PostToWebAddress(), 1000, false );
		pluginRegistry.add( new Clock(),            1000, true  );
		pluginRegistry.add( new AnalogueClock(),    1000, false );
		pluginRegistry.add( new QRZLookup(),        500,  false );
		pluginRegistry.add( new RigCtl(),           1,    false );
		
		HNKLogger.pluginRegistry.run("init", true);
		HNKLogger.pluginRegistry.run("initProperties", this.properties, true);
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
	
	public static HNKLoggerGUI getHNKLoggerGUI()
	{
		return gui;
	}
}
