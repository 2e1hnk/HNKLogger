package uk.co.mattcarus.hnklogger.gui.SwingGUI.plugins;

import uk.co.mattcarus.hnklogger.Contact;
import uk.co.mattcarus.hnklogger.gui.HNKLoggerGUI;

public class DxCluster extends GUIPlugin {
	public String name = "DxCluster";
	
	public String getName()
	{
		return this.name;
	}
	
	public void initGUI(HNKLoggerGUI gui) {
		System.out.println("Loaded " + this.getName() + " plugin.");
	}
	
}
