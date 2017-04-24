package uk.co.mattcarus.hnklogger;

import javax.swing.SwingUtilities;

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
		/*
		try {
			properties = new HNKLoggerProperties();
			System.out.println(properties.getProperties().getProperty("gui.interface_type"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
		this.loadResources();
		this.loadHooks();

		this.log = new Log();
		this.nextSerial = this.log.getNextSerialNumber();
		
		HNKLoggerGUI gui;
		
		//if ( properties.getProperties().getProperty("gui.interface_type").equals("LanternaGUI") )
		//{
			//gui = new LanternaGUI();
		//}
		//else
		//{
			// Swing is default
			gui = new SwingGUI();
		//}
		this.loadGUI(gui);
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
		HNKLogger.hooks.run("init");
	}
	
	public static void main(String[] args)
	{
		/*
		 * Swing version
		 *
		 */
		/*
	    // Run GUI codes in Event-Dispatching thread for thread-safety
	    SwingUtilities.invokeLater(new Runnable() {
	         @Override
	         public void run() {
	            new HNKLogger();  // Let the constructor do the job
	         }
	    });
	    */
		
		/*
		 * Console Version
		 */
		
		
		
		HNKLogger hnkLogger = new HNKLogger();
		
		HNKLoggerGUI gui = new LanternaGUI();
		
		//hnkLogger.loadGUI(gui);
	    
	    //textGUI.getScreen().stopScreen();
		
	}
	
	public void loadResources()
	{
		loggerResource = new CSVLoggerResource("cqwwpre3.txt");
		
	}
}
