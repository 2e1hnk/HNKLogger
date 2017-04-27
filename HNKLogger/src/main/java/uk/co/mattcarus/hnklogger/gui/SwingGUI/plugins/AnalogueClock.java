package uk.co.mattcarus.hnklogger.gui.SwingGUI.plugins;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.Calendar;
import javax.swing.JFrame;
import javax.swing.JPanel;

import uk.co.mattcarus.hnklogger.HNKLoggerProperties;
import uk.co.mattcarus.hnklogger.exceptions.HNKPropertyNotFoundException;
import uk.co.mattcarus.hnklogger.gui.SwingGUI.SwingGUI;

public class AnalogueClock extends GUIPlugin {
	public static String name = "Analogue Clock";
	public static String identifier = "analogueclock";
	
	private HNKLoggerProperties properties;
	
	private Color BACKGROUND_COLOR; 
	private static JFrame analogueClockFrame;
	
	public String getName() {
		return AnalogueClock.name;
	}
	
	public void initProperties(HNKLoggerProperties properties)
	{
		this.properties = properties;
		try {
			this.BACKGROUND_COLOR = new Color(
				this.properties.getPropertyAsInt(AnalogueClock.identifier + ".background_colour.r"),
				this.properties.getPropertyAsInt(AnalogueClock.identifier + ".background_colour.g"),
				this.properties.getPropertyAsInt(AnalogueClock.identifier + ".background_colour.b")
			);
		}
		catch (HNKPropertyNotFoundException e)
		{
			this.BACKGROUND_COLOR = new Color(24, 116, 205);
		}
	}
	
	public void initGUI(SwingGUI gui) throws Exception {
		AnalogueClock.analogueClockFrame = new JFrame(this.getName());
		AnalogueClockPanel container = new AnalogueClockPanel();
		analogueClockFrame.add(container, BorderLayout.CENTER);
		analogueClockFrame.setBackground(BACKGROUND_COLOR);
		analogueClockFrame.setResizable(false);
		analogueClockFrame.setSize(100, 100);
	    // setUndecorated(true);
		analogueClockFrame.pack();
		analogueClockFrame.setVisible(true);
	}
}

class AnalogueClockPanel extends JPanel implements Runnable {

	  private static final long serialVersionUID = 1L;
	  Thread t = new Thread(this);

	  /** The coordinates used to paint the clock hands. */
	  int xHandSec, yHandSec, xHandMin, yHandMin, xHandHour, yHandHour;

	  /** The size of the clock. */
	  private final int HORIZONTAL_SIZE = 200;
	  private final int VERTICAL_SIZE = 200;

	  /** The length of the clock hands relative to the clock size. */
	  //private final int secondHandLength = (int) (HORIZONTAL_SIZE * 0.3);
	  private final int secondHandLength = 0;
	  private final int minuteHandLength = (int) (HORIZONTAL_SIZE * 0.25);
	  private final int hourHandLength = (int) (HORIZONTAL_SIZE * 0.15);

	  /** The distance of the dots from the origin (center of the clock). */
	  private final int DISTANCE_DOT_FROM_ORIGIN = HORIZONTAL_SIZE / 2 - 40;

	  private final int DIAMETER_BIG_DOT = 8;
	  private final int DIAMETER_SMALL_DOT = 4;
	  
	  private final static Color GREY_COLOR = new Color(160,160,160);

	  public AnalogueClockPanel() {
	    setMinimumSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
	    setMaximumSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
	    setPreferredSize(new Dimension(HORIZONTAL_SIZE, VERTICAL_SIZE));
	    setLayout(null);
	    t.start();
	  }

	  /**
	   * At each iteration we recalculate the coordinates of the clock hands,
	   * and repaint everything.
	   */
	  public void run(){
	    while(true){
	      try{
	        int currentSecond = Calendar.getInstance().get(Calendar.SECOND);
	        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
	        int currentHour = Calendar.getInstance().get(Calendar.HOUR);

	        xHandSec = minToLocation(currentSecond, secondHandLength).x;
	        yHandSec = minToLocation(currentSecond, secondHandLength).y;
	        xHandMin = minToLocation(currentMinute, minuteHandLength).x;
	        yHandMin = minToLocation(currentMinute, minuteHandLength).y;
	        xHandHour = minToLocation(currentHour * 5 + getRelativeHour(currentMinute), hourHandLength).x;
	        yHandHour = minToLocation(currentHour * 5 + getRelativeHour(currentMinute), hourHandLength).y;
	        repaint();
	        Thread.sleep(500);
	      } catch(InterruptedException ie){
	        ie.printStackTrace();
	      }
	    }
	  }


	  /**
	   * Returns how much the hour hand should be ahead
	   * according to the minutes value.
	   * 04:00, return 0.
	   * 04:12, return 1, so that we move the hour handle ahead of one dot. 
	   * @param min The current minute.
	   * @return The relative offset to add to the hour hand. 
	   */
	  private int getRelativeHour(int min) {
	    return min / 12;
	  }

	  protected void paintComponent(Graphics g){
	    Graphics2D g2 = (Graphics2D)g;
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	        RenderingHints.VALUE_ANTIALIAS_ON);

	    g2.clearRect(0, 0, getWidth(), getHeight());

	    // Draw the dots
	    g2.setColor(GREY_COLOR);
	    for (int i = 0; i < 60; i++) {
	      
	      Point dotCoordinates = minToLocation(i, DISTANCE_DOT_FROM_ORIGIN);
	      g2.setColor((i <= Calendar.getInstance().get(Calendar.SECOND)) ? Color.red : GREY_COLOR);
	      
	      if (i % 5 == 0) {
	        // big dot
	        g2.fillOval(dotCoordinates.x - (DIAMETER_BIG_DOT / 2),
	              dotCoordinates.y - (DIAMETER_BIG_DOT / 2),
	              DIAMETER_BIG_DOT,
	              DIAMETER_BIG_DOT);
	      } else {
	        // small dot
	        g2.fillOval(dotCoordinates.x - (DIAMETER_SMALL_DOT / 2),
	              dotCoordinates.y - (DIAMETER_SMALL_DOT / 2),
	              DIAMETER_SMALL_DOT,
	              DIAMETER_SMALL_DOT);
	      }
	    }

	    // Draw the clock hands
	    g2.setColor(Color.black);
	    g2.setStroke(new BasicStroke(3));
	    g2.drawLine(HORIZONTAL_SIZE / 2, VERTICAL_SIZE / 2, xHandSec, yHandSec);
	    g2.drawLine(HORIZONTAL_SIZE / 2, VERTICAL_SIZE / 2, xHandMin, yHandMin);
	    g2.drawLine(HORIZONTAL_SIZE / 2, VERTICAL_SIZE / 2, xHandHour, yHandHour);
	  }

	  /**
	   * Converts current second/minute/hour to x and y coordinates.
	   * @param min The current minute
	   * @param radius The radius length    
	   * @return the coordinates point
	   */
	  private Point minToLocation(int timeStep, int radius) {
	    double t = 2 * Math.PI * (timeStep-15) / 60;
	    int x = (int)(HORIZONTAL_SIZE / 2 + radius * Math.cos(t));
	    int y = (int)(VERTICAL_SIZE / 2 + radius * Math.sin(t));
	    return new Point(x, y);
	  }    
	}
