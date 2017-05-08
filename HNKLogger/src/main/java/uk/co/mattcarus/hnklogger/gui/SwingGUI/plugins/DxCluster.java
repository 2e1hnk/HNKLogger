package uk.co.mattcarus.hnklogger.gui.SwingGUI.plugins;

import uk.co.mattcarus.hnklogger.Contact;
import uk.co.mattcarus.hnklogger.gui.HNKLoggerGUI;
import uk.co.mattcarus.hnklogger.plugins.Plugin;

public class DxCluster extends GUIPlugin {
	public String name = "DxCluster";
	public String identifier = "dxCluster";

	private static final Integer[] capabilities = { Plugin.CAPABILITY_GUI };
	
	public Integer[] getCapabilities() {
		return DxCluster.capabilities;
	}

	public String getName()
	{
		return this.name;
	}
	
	public String getIdentifier()
	{
		return this.identifier;
	}
	
	public void initGUI(HNKLoggerGUI gui) {
		System.out.println("Loaded " + this.getName() + " plugin.");
	}
	
}
