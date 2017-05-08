package uk.co.mattcarus.hnklogger.gui.SwingGUI.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import uk.co.mattcarus.hnklogger.gui.SwingGUI.SwingGUI;
import uk.co.mattcarus.hnklogger.plugins.Plugin;

public class Clock extends GUIPlugin {
	public static String name = "Clock";
	public static String identifier = "clock";

	private static final Integer[] capabilities = { Plugin.CAPABILITY_GUI };
	
	public Integer[] getCapabilities() {
		return Clock.capabilities;
	}

	public String getName() {
		return Clock.name;
	}
	
	public String getIdentifier() {
		return Clock.identifier;
	}
	
	public void initGUI(SwingGUI gui) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
        public void run() {
            final JFrame clockFrame = new JFrame(Clock.name);
            final JTextPane clockTextPane = new JTextPane();
            final StyledDocument doc = (StyledDocument) clockTextPane.getDocument();
            
            final SimpleAttributeSet normal = new SimpleAttributeSet();
            StyleConstants.setFontFamily(normal, "SansSerif");
            StyleConstants.setFontSize(normal, 16);

            try {
				doc.insertString(doc.getLength(), "00:00\n", normal);
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            clockFrame.add(clockTextPane);
            clockFrame.pack();
            clockFrame.setLocationRelativeTo(null);
            clockFrame.setVisible(true);
            
            final SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            
            Timer t = new Timer(1000, new ActionListener(){
            	  public void actionPerformed(ActionEvent e) {
						try {
							clockTextPane.setText("");
							doc.insertString(0, dateFormatGmt.format(new Date()), normal);
							clockFrame.pack();
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
            	  }
            });
            t.start();
            
        }});
		System.out.println("Loaded " + this.getName() + " plugin.");
	}
}
