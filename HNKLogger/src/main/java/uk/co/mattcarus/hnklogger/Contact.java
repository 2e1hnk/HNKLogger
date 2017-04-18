package uk.co.mattcarus.hnklogger;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Vector;

public class Contact implements Comparable<Contact>, Serializable {
	private static final long serialVersionUID = -1383580252237834773L;
	
	private int time;
	private int serial;
	private String callsign;
	private String rptSent;
	private String rptRcvd;
	private String band;
	private String mode;
	private String infoRcvd;
	private String comments;
	private HashMap<String, String> additionalInfo;
	
	public Contact() {
		this.additionalInfo = new HashMap<String, String>();
	}
	
	public Contact(int time, int serial, String callsign, String rptSent, String rptRcvd, String band, String mode, String infoRcvd, String comments) {
		this.setTime(time);
		this.setSerial(serial);
		this.setCallsign(callsign);
		this.setRptSent(rptSent);
		this.setRptRcvd(rptRcvd);
		this.setBand(band);
		this.setMode(mode);
		this.setInfoRcvd(infoRcvd);
		this.setComments(comments);
		
		this.additionalInfo = new HashMap<String, String>();
	}
	
	public Contact(int time, int serial, String callsign, String rptSent, String rptRcvd, String band, String mode, String infoRcvd, String comments, HashMap<String, String> additionalInfo) {
		this.setTime(time);
		this.setSerial(serial);
		this.setCallsign(callsign);
		this.setRptSent(rptSent);
		this.setRptRcvd(rptRcvd);
		this.setBand(band);
		this.setMode(mode);
		this.setInfoRcvd(infoRcvd);
		this.setComments(comments);
		this.additionalInfo = additionalInfo;
	}
	
	public Vector<String> toVector()
	{
		Vector<String> contactVector = new Vector<String>();
		
		contactVector.add(0, Integer.toString(this.time));
		contactVector.add(1, Integer.toString(this.serial));
		contactVector.add(2, this.callsign);
		contactVector.add(2, this.rptSent);
		contactVector.add(2, this.rptRcvd);
		contactVector.add(2, this.infoRcvd);
		contactVector.add(2, this.comments);
		
		return contactVector;
	}
	
	public void setTime(int time)
	{
		this.time = time;
	}
	public int getTime()
	{
		return this.time;
	}
	
	public void setSerial(int serial)
	{
		this.serial = serial;
	}
	public int getSerial()
	{
		return this.serial;
	}
	
	public void setCallsign(String callsign)
	{
		this.callsign = callsign;
	}
	public String getCallsign()
	{
		return this.callsign;
	}
	
	public void setRptSent(String rptSent)
	{
		this.rptSent = rptSent;
	}
	public String getRptSent()
	{
		return this.rptSent;
	}
	
	public void setRptRcvd(String rptRcvd)
	{
		this.rptRcvd = rptRcvd;
	}
	public String getRptRcvd()
	{
		return this.rptRcvd;
	}
	
	public void setBand(String band)
	{
		this.band = band;
	}
	public String getBand()
	{
		return this.band;
	}
	
	public void setMode(String mode)
	{
		this.mode = mode;
	}
	public String getMode()
	{
		return this.mode;
	}
	
	public void setInfoRcvd(String infoRcvd)
	{
		this.infoRcvd = infoRcvd;
	}
	public String getInfoRcvd()
	{
		return this.infoRcvd;
	}
	
	public void setComments(String comments)
	{
		this.comments = comments;
	}
	public String getComments()
	{
		return this.comments;
	}
	
	public HashMap<String, String> getAdditionalInfo()
	{
		return this.additionalInfo;
	}
	
	public String getContactTimeFriendly()
	{
		Date date = new Date(this.time*1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(date);
	}
	
	public void printContact()
	{
		System.out.println(this.toString());
	}
	
	public String toString()
	{
		return this.toString(0);
	}

	public String toString(int maxChars)
	{
		if ( maxChars >= 80 )
		{
			return String.format("%-20s %03d %-10s %-5s %-5s %-5s %-5s %-10s %-10s %-10s", this.getContactTimeFriendly(), this.serial, this.callsign, this.rptRcvd, this.rptSent, this.band, this.mode, this.infoRcvd, this.comments, this.additionalInfo.get("country"));
		}
		else if ( maxChars >= 69 )
		{
			return String.format("%-20s %03d %-10s %-5s %-5s %-10s %-10s", this.getContactTimeFriendly(), this.serial, this.callsign, this.rptRcvd, this.rptSent, this.infoRcvd, this.comments);
		}
		else if ( maxChars >= 58 )
		{
			return String.format("%-20s %03d %-10s %-5s %-5s %-10s", this.getContactTimeFriendly(), this.serial, this.callsign, this.rptRcvd, this.rptSent, this.infoRcvd);
		}
		
		// 47 chars
		return String.format("%-20s %03d %-10s %-5s %-5s", this.getContactTimeFriendly(), this.serial, this.callsign, this.rptRcvd, this.rptSent);
		
	}

	@Override
	public int compareTo(Contact contact) {
		return this.time - contact.time;
	}
}
