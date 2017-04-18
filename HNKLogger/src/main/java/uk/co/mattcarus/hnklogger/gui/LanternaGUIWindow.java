package uk.co.mattcarus.hnklogger.gui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.googlecode.lanterna.gui.*;
import com.googlecode.lanterna.gui.component.*;
import com.googlecode.lanterna.gui.dialog.*;
import com.googlecode.lanterna.gui.layout.*;
import com.googlecode.lanterna.terminal.*;

import uk.co.mattcarus.hnklogger.*;

public class LanternaGUIWindow extends Window {
	
	Log log;
	int nextSerial = 0;
	
	Panel mainPanel;
	Panel historyPanel;
	Panel inputPanel;
	Panel statusPanel;
	Panel statsPanel;
	
	Table historyTable;
	
	Label fldDate;
	Label fldTime;
	Label fldSerial;
	Label status;
	Label stats;
	
	TextBox fldCallsign;
	TextBox fldRptSent;
	TextBox fldRptRcvd;
	TextBox fldInfoRcvd;
	TextBox fldComments;
	
	Button btnLog;
	
	public LanternaGUIWindow(Log log)
    {
        super("HNKLogger");
        
        this.log = log;
        
        mainPanel = new Panel(new Border.Invisible(), Panel.Orientation.VERTICAL);
        
        historyPanel = new Panel(new Border.Bevel(true), Panel.Orientation.VERTICAL);
        inputPanel = new Panel(new Border.Bevel(true), Panel.Orientation.HORISONTAL);
        statusPanel = new Panel(new Border.Bevel(true), Panel.Orientation.HORISONTAL);
        statsPanel = new Panel(new Border.Bevel(true), Panel.Orientation.HORISONTAL);
        
        historyTable = new Table(5, "QSO History");
        
        // Input fields
        fldDate = new Label();
        fldTime = new Label();
        fldSerial = new Label();
		fldCallsign = new TextBox();
		fldRptSent = new TextBox();
		fldRptRcvd = new TextBox();
		//fldBand = new JComboBox<String>(bands);
		//fldBand.setEditable(true);
		fldInfoRcvd = new TextBox();
		fldComments = new TextBox();
		btnLog = new Button("Log", new Action() {
			@Override
            public void doAction() {
               logContact();
            }
		});
		
		status = new Label();
		stats = new Label();
        
		inputPanel.addComponent(fldDate);
		inputPanel.addComponent(fldTime);
		inputPanel.addComponent(fldSerial);
		inputPanel.addComponent(fldCallsign);
		inputPanel.addComponent(fldRptSent);
		inputPanel.addComponent(fldRptRcvd);
		inputPanel.addComponent(fldInfoRcvd);
		inputPanel.addComponent(fldComments);
		inputPanel.addComponent(btnLog);
		
		statusPanel.addComponent(status);
		statsPanel.addComponent(stats);
		
		
		
        Component[] tableRow = {
        	new Label("Date/Time"),
        	new Label("Serial"),
        	new Label("Callsign"),
        	new Label("RST"),
        	new Label("Country")
        };
        
        historyTable.addRow(tableRow);
        
        //historyPanel.addComponent(historyTable);

        mainPanel.addComponent(historyTable);
        mainPanel.addComponent(inputPanel);
        mainPanel.addComponent(statusPanel);
        mainPanel.addComponent(statsPanel);

        addComponent(mainPanel);
        
        this.updateHistory();
        this.updateSerial();
        
	    Timer dateTimeTimer = new Timer(true);
	    dateTimeTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				Date currentDate = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
				
				fldDate.setText(dateFormat.format(currentDate));
				fldTime.setText(timeFormat.format(currentDate));
			}
	    	
	    }, 0, 500);
	    
	 // Start a timer to handle updating the stats fields
	    /*
	    Timer statsTimer = new Timer(true);
	    statsTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				stats.setText("QSO Rate: " + log.contactsInLastHour() + "/h");
			}
	    	
	    }, 0, 5000);
	    */

    }

    public void updateHistory()
    {
    	System.out.println("Starting update...");
    	
    	historyTable.removeAllRows();
    
    	int tableRows = historyTable.getNrOfRows();
    	int contactNum = 1;
    	
    	for ( Contact contact : this.log.getContacts() )
    	{
    		if ( this.log.getContacts().size() - contactNum < 10 )
    		{
	    		Component[] row = {
	    			new Label(contact.getContactTimeFriendly()),
	    			new Label("" + contact.getSerial()),
	    			new Label(contact.getCallsign()),
	    			new Label(contact.getRptSent()),
	    			new Label(contact.getAdditionalInfo().get("country"))
	    		};
	    		historyTable.addRow(row);
    		}
    		contactNum++;
    	}
    	
    }
    
	public void updateSerial()
	{
		this.nextSerial = log.getNextSerialNumber();
		this.fldSerial.setText(String.format("%03d", this.nextSerial));
	}
	
	public void clearInputs()
	{
		fldCallsign.setText("");
		fldRptSent.setText("");
		fldRptRcvd.setText("");
		fldInfoRcvd.setText("");
		fldComments.setText("");
	}
	
	public void logContact()
	{
		// Check we have required info
		if ( fldCallsign.getText().isEmpty() ||
			 fldRptSent.getText().isEmpty() ||
			 fldRptRcvd.getText().isEmpty() )
		{
			// QSO is a bust
			status.setText("Insufficient Information! Contact NOT Logged!");
		}
		else
		{
			// Populate Additional Information
			HashMap<String, String> additionalInfo = HNKLogger.loggerResource.search(fldCallsign.getText());
			
	        log.addContact(new Contact(
	        		(int) (System.currentTimeMillis() / 1000L),
	        		nextSerial,
	        		fldCallsign.getText(),
	        		fldRptSent.getText(),
	        		fldRptRcvd.getText(),
	        		//fldBand.getSelectedItem().toString(),
	        		"20m",
	        		//fldMode.getSelectedItem().toString(),
	        		"SSB",
	        		fldInfoRcvd.getText(),
	        		fldComments.getText(),
	        		additionalInfo
	        ));

	        // Increment Serial
	        updateSerial();
	        
	        // Update Display
	        updateHistory();
	        
	        // Clear Input Fields
	        clearInputs();
	        
	        // Debug
	        log.printLog();

		}		
	}
}
