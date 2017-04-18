package uk.co.mattcarus.hnklogger.gui;

import java.util.HashMap;

import uk.co.mattcarus.hnklogger.Log;

public interface HNKLoggerGUI {
	String[] bands = {"160m", "80m", "40m", "30m", "20m", "17m", "15m", "12m", "10m", "6m", "4m", "2m", "70cm"};
	String[] modes = {"CW", "SSB", "AM", "FM", "ATV", "SSTV", "PSK31", "AX25", "HELL", "RTTY"};
	
	public void startGUI(Log log);
	public void updateHistory();
	public void updateSerial();
	public void clearInputs();
	public void logContact();
}