package uk.co.mattcarus.hnklogger.gui.SwingGUI;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import uk.co.mattcarus.hnklogger.Contact;
import uk.co.mattcarus.hnklogger.HNKLogger;
import uk.co.mattcarus.hnklogger.Log;
import uk.co.mattcarus.hnklogger.TextPrompt;
import uk.co.mattcarus.hnklogger.UppercaseDocumentFilter;
import uk.co.mattcarus.hnklogger.Contest;
import uk.co.mattcarus.hnklogger.contests.RsgbSsbFieldDay;
import uk.co.mattcarus.hnklogger.gui.HNKLoggerGUI;
import uk.co.mattcarus.hnklogger.gui.SwingGUI.plugins.GUIPlugin;
import uk.co.mattcarus.hnklogger.plugins.Plugin;

public class SwingGUI extends JFrame implements HNKLoggerGUI {
	Log log;
	int nextSerial = 0;
	
	Container cp;
	
	JPanel mainPanel;
	JPanel historyPanel;
	JPanel inputPanel;
	//JPanel statusPanel;
	JPanel statsPanel;
	
	//JTextArea historyTextArea;
	Object columnNames[] = { "Date/Time", "Serial", "Callsign", "Rpt Sent", "Rpt Rcvd", "Band", "Mode" };
    JTable historyTable = new JTable(new DefaultTableModel(columnNames, 0));
    JScrollPane historyScrollPane;
	
	JLabel fldDate;
	JLabel fldTime;
	JLabel fldSerial;
	//JLabel status;
	JLabel stats;
	
	JTextField fldCallsign;
	JTextField fldRptSent;
	JTextField fldRptRcvd;
	JTextField fldInfoRcvd;
	JTextField fldComments;
	
	JComboBox<String> fldBand;
	JComboBox<String> fldMode;
	
	// Menu items
	JMenuBar menuBar;
	JMenu fileMenu, editMenu, pluginsMenu, submenu;
	JMenuItem menuItem;
	
	@Override
	public void startGUI(final Log log) {
		this.log = log;
		this.createMenus();
		
		cp = this.getContentPane();
		cp.setLayout(new FlowLayout());
		
		mainPanel = new JPanel(new FlowLayout());
		historyPanel = new JPanel(new GridLayout());
		inputPanel = new JPanel(new FlowLayout());
		//statusPanel = new JPanel(new GridLayout());
		statsPanel = new JPanel(new GridLayout());
		
		historyScrollPane = new JScrollPane(historyTable);
		historyScrollPane.setPreferredSize(new Dimension(850, 130));
		
		historyPanel.add(historyScrollPane);
		
		this.updateHistory();
		
		fldDate = new JLabel("Date");
		fldTime = new JLabel("Time");
		fldSerial = new JLabel("#");
		fldCallsign = new JTextField(10);
		fldRptSent = new JTextField(3);
		fldRptRcvd = new JTextField(3);
		fldBand = new JComboBox<String>(bands);
		fldBand.setEditable(true);
		fldMode = new JComboBox<String>(modes);
		fldMode.setEditable(true);
		
		fldInfoRcvd = new JTextField(10);
		fldComments = new JTextField(10);
		JButton btnLog = new JButton("Log");
		//status = new JLabel();
		stats = new JLabel();
		
		// Format the controls
		
		DocumentFilter ucFilter = new UppercaseDocumentFilter();
		((AbstractDocument) fldCallsign.getDocument()).setDocumentFilter(ucFilter);
		
		//historyTextArea.setEditable(false);
		//historyTextArea.setFont(new Font("Courier New", Font.PLAIN, 14));
		
		//status.setSize(100, 20);
		stats.setSize(100, 20);
		
		fldBand.setSize(5, fldBand.getHeight());
		fldBand.setSelectedItem(log.getLastContact().getBand());
		fldMode.setSize(5, fldMode.getHeight());
		fldMode.setSelectedItem(log.getLastContact().getMode());
		
		// Create status bar
		//statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		//cp.add(statusPanel, BorderLayout.SOUTH);
		//statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 16));
		//statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		//JLabel statusLabel = new JLabel("status");
		//statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		//statusPanel.add(statusLabel);
		
		// Add controls to the panel
		
		inputPanel.add(fldDate);
		inputPanel.add(fldTime);
		inputPanel.add(fldSerial);
		inputPanel.add(fldCallsign);
		inputPanel.add(fldRptSent);
		inputPanel.add(fldRptRcvd);
		inputPanel.add(fldBand);
		inputPanel.add(fldMode);
		inputPanel.add(fldInfoRcvd);
		inputPanel.add(fldComments);
		inputPanel.add(btnLog);
		
		//statusPanel.add(status);
		
		statsPanel.add(stats);
		
		this.updateSerial();
		
		// Input Field Action Triggers
		btnLog.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	        	 logContact();
	         }
	      });
		
		fldCallsign.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				checkCallsign(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				checkCallsign(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				checkCallsign(e);
			}
			
			public void checkCallsign(DocumentEvent e)
			{
				String callsign = "";
				try {
					Document doc = (Document) e.getDocument();
	                callsign = doc.getText(0, doc.getLength());
				}
				catch (Exception exception)
				{
					exception.printStackTrace();
				}
				
				HashMap<String, String> info = HNKLogger.loggerResource.search(callsign);
				
				if ( !info.isEmpty() )
				{
					/*
					status.setText("Country: " + info.get("country") + 
								   ", Region: " + info.get("continent") + 
								   ", ITU Zone: " + info.get("itu_zone") + 
								   ", CQ Zone: " + info.get("cq_zone"));
					*/
					if ( log.isCallsignInLog(callsign) )
					{
						//status.setText("Dupe!");
					}
				}
				else
				{
					//status.setText("");
				}
				
			}
		});
		
		fldCallsign.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// Nothing to do here
			}

			@Override
			public void focusLost(FocusEvent e) {
				// Trigger Plugins
				if ( !e.isTemporary() && !fldCallsign.equals("") ) {
					System.out.println("Running onCallsignEntered Hook with: " + fldCallsign.getText());
					HNKLogger.hooks.run("onCallsignEntered", fldCallsign.getText());
				}
			}
			
		});
		
		fldRptSent.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				
			}

			@Override
			public void focusLost(FocusEvent e) {
				// If focus moves off and the field is empty then set it to the default 59
				
				String rpt = fldRptSent.getText();
				
				if ( rpt.equals("") )
				{
					fldRptSent.setText("59");
				}
			}
			
		});
		
		fldRptRcvd.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				
			}

			@Override
			public void focusLost(FocusEvent e) {
				// If focus moves off and the field is empty then set it to the default 59
				
				String rpt = fldRptRcvd.getText();
				
				if ( rpt.equals("") )
				{
					fldRptRcvd.setText("59");
				}
			}
		});
		
		// Detect enter presses in input fields
		Action enterPress = new AbstractAction()
		{
		    @Override
		    public void actionPerformed(ActionEvent e)
		    {
		    	logContact();
		    }
		};
		
		
		fldCallsign.addActionListener(enterPress);
		fldRptSent.addActionListener(enterPress);
		fldRptRcvd.addActionListener(enterPress);
		fldBand.addActionListener(enterPress);
		fldMode.addActionListener(enterPress);
		fldInfoRcvd.addActionListener(enterPress);
		fldComments.addActionListener(enterPress);
		
		
		//Detect Esc presses in input fields
		Action escPress = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				fldCallsign.setText("");
				fldRptSent.setText("");
				fldRptRcvd.setText("");
				fldInfoRcvd.setText("");
				fldComments.setText("");
				fldCallsign.requestFocus();
			}	
		};
		
		// Force scroll to bottom of history table when it updates
		historyTable.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	historyTable.scrollRectToVisible(historyTable.getCellRect(historyTable.getRowCount()-1, 0, true));
		    }
		});
		
		fldCallsign.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), enterPress);
		fldCallsign.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), escPress);
		fldRptSent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), enterPress);
		fldRptSent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), escPress);
		fldRptRcvd.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), enterPress);
		fldRptRcvd.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), escPress);
		fldBand.getEditor().addActionListener(enterPress);
		fldBand.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), escPress);
		fldMode.getEditor().addActionListener(enterPress);
		fldMode.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), escPress);
		fldInfoRcvd.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), enterPress);
		fldInfoRcvd.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), escPress);
		fldComments.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), enterPress);
		fldComments.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), escPress);
		
		// Add TextPrompts to input fields
		TextPrompt promptCallsign = new TextPrompt("Callsign", fldCallsign);
		TextPrompt promptRptSent = new TextPrompt("RST (S)", fldRptSent);
		TextPrompt promptRptRcvd = new TextPrompt("RST (R)", fldRptRcvd);
		TextPrompt promptInfoRcvd = new TextPrompt("Info Rcvd", fldInfoRcvd);
		TextPrompt promptComments = new TextPrompt("Comments", fldComments);
		
		mainPanel.add(historyPanel);
		mainPanel.add(inputPanel);
		//mainPanel.add(statusPanel);
		mainPanel.add(statsPanel);
		
		this.setContentPane(mainPanel);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setTitle("HNKLogger");  // "this" JFrame sets title
	    setSize(850, 240);   // "this" JFrame sets initial size (or pack())
	    setVisible(true);    // show it
	    
	 // Start a timer to handle updating the date/time fields
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
	    Timer statsTimer = new Timer(true);
	    statsTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				Contest contest = new RsgbSsbFieldDay();
				stats.setText("Points: " + Float.toString(contest.calculatePoints(log)) + ", QSO Rate: " + log.contactsInLastHour() + "/h");
			}
	    	
	    }, 0, 5000);
	    
	    // Handle window re-sizes
	    this.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				//historyTextArea.setSize(getBounds().width, historyTextArea.getSize().height);
				System.out.println("Window resized");
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
	    	
	    });
	    
	    // Finally, give the callsign field the focus
	    fldCallsign.requestFocus();

	}

	public void updateHistory()
	{
		// Update the table here
		//historyTextArea.setText(this.log.toString(100));
		DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
		for ( Contact contact : this.log.getContacts() )
		{
			model.addRow(
				new Object[]{
					contact.getContactTimeFriendly(),
					contact.getSerial(),
					contact.getCallsign(),
					contact.getRptSent(),
					contact.getRptRcvd(),
					contact.getBand(),
					contact.getMode()
				}
			);
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
			//status.setText("Insufficient Information! Contact NOT Logged!");
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
	        		fldBand.getSelectedItem().toString(),
	        		fldMode.getSelectedItem().toString(),
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
	        
	        // Move focus to Callsign Input
	        fldCallsign.requestFocus();
	        
	        // Debug
	        log.printLog();

		}		
	}
	
	private void createMenus()
	{
		this.menuBar = new JMenuBar();
		
		// File Menu
		this.fileMenu = new JMenu("File");
		this.fileMenu.setMnemonic(KeyEvent.VK_F);
		this.fileMenu.getAccessibleContext().setAccessibleDescription("File Menu");
		menuBar.add(this.fileMenu);
		
		// Save
		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Save current log");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.save();
			}
		});
		fileMenu.add(menuItem);
		
		// Export
		submenu = new JMenu("Export");
		submenu.setMnemonic(KeyEvent.VK_E);

		menuItem = new JMenuItem("ADIF");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
		submenu.add(menuItem);

		menuItem = new JMenuItem("CABRILLO");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		submenu.add(menuItem);
		
		menuItem = new JMenuItem("HNK XML");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK));
		submenu.add(menuItem);
		
		fileMenu.add(submenu);
		
		// Exit
		fileMenu.addSeparator();
		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Exit HNKLogger");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(menuItem);
		
		// Edit Menu
		this.editMenu = new JMenu("Edit");
		this.editMenu.setMnemonic(KeyEvent.VK_E);
		this.editMenu.getAccessibleContext().setAccessibleDescription("Edit Menu");
		menuBar.add(this.editMenu);
		
		// Remove last QSO
		menuItem = new JMenuItem("Remove last QSO", KeyEvent.VK_R);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Remove the last logged QSO");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				log.deleteLastContact();
				updateHistory();
				updateSerial();
			}
		});
		editMenu.add(menuItem);
		
		// Plugins Menu
		this.pluginsMenu = new JMenu("Plugins");
		this.pluginsMenu.setMnemonic(KeyEvent.VK_P);
		this.pluginsMenu.getAccessibleContext().setAccessibleDescription("Plugins Menu");
		menuBar.add(this.pluginsMenu);
		
		// Add each plugin to menu
		for ( Plugin plugin : HNKLogger.hooks)
		{
			menuItem = new JMenuItem(plugin.getName());
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				}
			});
			pluginsMenu.add(menuItem);
		}
		
		this.setJMenuBar(this.menuBar);
	}

}
