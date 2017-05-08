package uk.co.mattcarus.hnklogger.plugins;

import uk.co.mattcarus.hnklogger.gui.SwingGUI.plugins.GUIPlugin;

public class QRZLogbook extends GUIPlugin {
	public String name = "Create QRZ.com Logbook Entry";
	public String identifier = "qrzLogbookEntry";

	private static final Integer[] capabilities = { };
	
	public Integer[] getCapabilities() {
		return QRZLogbook.capabilities;
	}

	public String getName() {
		return name;
	}
	
	public String getIdentifier() {
		return identifier;
	}
}
