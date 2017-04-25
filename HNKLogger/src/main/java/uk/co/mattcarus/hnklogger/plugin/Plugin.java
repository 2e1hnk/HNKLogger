package uk.co.mattcarus.hnklogger.plugin;

import uk.co.mattcarus.hnklogger.Contact;
import uk.co.mattcarus.hnklogger.HNKLoggerProperties;
import uk.co.mattcarus.hnklogger.gui.HNKLoggerGUI;

public abstract class Plugin {
	public String name;
	public static String identifier;	// Should be filename-friendly (i.e. no slashes etc)
	
	public String getName()
	{
		return this.name;
	}
	
	public String getIdentifier()
	{
		return Plugin.identifier;
	}
	
	public void init() {
		
	}
	
	public void initProperties(HNKLoggerProperties properties)
	{
		
	}
		
	public void initGUI(HNKLoggerGUI gui)
	{
		
	}
	
	public String onCallsignEntered(String callsign) {
		return callsign;
	}
	
	public Contact onBeforeLogContact(Contact contact)
	{
		return contact;
	}
	
	public Contact onAfterLogContact(Contact contact)
	{
		return contact;
	}
	
	
}
