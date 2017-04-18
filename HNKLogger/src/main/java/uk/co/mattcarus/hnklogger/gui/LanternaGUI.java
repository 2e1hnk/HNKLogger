package uk.co.mattcarus.hnklogger.gui;

import uk.co.mattcarus.hnklogger.HNKLogger;
import uk.co.mattcarus.hnklogger.Log;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.*;

public class LanternaGUI implements HNKLoggerGUI {
	Log log;
	LanternaGUIWindow lanternaWindow;
    
    public void startGUI(Log log)
    {
    	this.log = log;
    	
		GUIScreen textGUI = TerminalFacade.createGUIScreen();
		
		if(textGUI == null) {
	        System.err.println("Couldn't allocate a terminal!");
	        return;
	    }
	    textGUI.getScreen().startScreen();
	    textGUI.setTitle("HNKLogger");
	    
	    lanternaWindow = new LanternaGUIWindow(this.log);
	    textGUI.showWindow(lanternaWindow, GUIScreen.Position.NEW_CORNER_WINDOW);
	    
    }
    
	public void updateHistory()
	{
		// Update the table here
		this.lanternaWindow.updateHistory();	
	}
	
	public void updateSerial()
	{
		this.lanternaWindow.updateSerial();
	}
	
	public void clearInputs()
	{
		this.lanternaWindow.clearInputs();
	}
	
	public void logContact()
	{
		this.lanternaWindow.logContact();
	}
}
