package uk.co.mattcarus.hnklogger.plugin;

import uk.co.mattcarus.hnklogger.Contact;
import uk.co.mattcarus.hnklogger.gui.HNKLoggerGUI;

public class Hook {
	public String name = "change me";
	
	public String getName()
	{
		return this.name;
	}
	
	public void init() {
		
	}
	
	public void initGUI(HNKLoggerGUI gui) {
		
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
