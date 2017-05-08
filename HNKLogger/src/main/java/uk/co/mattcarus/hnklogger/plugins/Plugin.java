package uk.co.mattcarus.hnklogger.plugins;

import uk.co.mattcarus.hnklogger.Contact;
import uk.co.mattcarus.hnklogger.HNKLoggerProperties;
import uk.co.mattcarus.hnklogger.gui.HNKLoggerGUI;

public abstract class Plugin {
	public static final Integer CAPABILITY_GUI = 1;
	
	public String name;
	public static String identifier;	// Should be filename-friendly (i.e. no slashes etc)
	
	public Integer[] capabilities;
	
	public abstract String getName();
	public abstract String getIdentifier();
	
	public abstract Integer[] getCapabilities();
	
	public void init() {
		
	}
	
	public void initProperties(HNKLoggerProperties properties)
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
