package uk.co.mattcarus.hooks;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import uk.co.mattcarus.hnklogger.gui.SwingGUI;

public class Clock extends Hook {
	public String name = "Clock";
	
	public String getName() {
		return this.name;
	}
	
	public void initGUI(SwingGUI gui) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
        public void run() {
            final JFrame clockFrame = new JFrame("clock");
            final JTextArea clockTextArea = new JTextArea(10,30);
            clockFrame.add(clockTextArea);
            clockFrame.setVisible(true);
            
            clockTextArea.setText("00:00");
            
            Timer t = new Timer(1000, new ActionListener(){
            	  public void actionPerformed(ActionEvent e) {
            	      clockTextArea.setText(new Date().toString());
            	      clockFrame.pack();
            	  }
            });
            t.start();
            
        }});
		System.out.println("Loaded " + this.getName() + " plugin.");
	}
}
