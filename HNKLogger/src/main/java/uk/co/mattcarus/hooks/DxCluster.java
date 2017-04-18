package uk.co.mattcarus.hooks;

import uk.co.mattcarus.hnklogger.Contact;
import uk.co.mattcarus.hnklogger.gui.HNKLoggerGUI;

public class DxCluster extends Hook {
	public String name = "DxCluster";
	
	public String getName()
	{
		return this.name;
	}
	
	public void initGUI(HNKLoggerGUI gui) {
		System.out.println("Loaded " + this.getName() + " plugin.");
	}
	
}
