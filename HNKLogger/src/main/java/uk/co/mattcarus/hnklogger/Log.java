package uk.co.mattcarus.hnklogger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.TreeSet;
import java.util.Vector;

public class Log {
	private TreeSet<Contact> contacts;
	private TreeSet<String> headings;
	
	private String filename;
	
	public Log()
	{
		this.filename = "log.ser";
		
		this.contacts = new TreeSet<Contact>();
		this.headings = new TreeSet<String>();
		
		// Attempt to load existing contacts from log file
		File f = new File(filename);
		
		if ( f.exists() && !f.isDirectory() )
		{
			this.load(filename);
		}
		else
		{
			// Populate with some dummy data for testing
			System.out.println("Populating Dummy Contact #1");
			Contact dummyContact1 = new Contact(1441021740, 1, "G3MXH", "59", "59", "20m", "SSB", "001", "", HNKLogger.loggerResource.search("G3MXH"));
			this.addContact(dummyContact1);
			
			System.out.println("Populating Dummy Contact #2");
			Contact dummyContact2 = new Contact(1441021800, 2, "G8HJS", "59", "59", "2m", "SSB", "001", "", HNKLogger.loggerResource.search("G8HJS"));
			this.addContact(dummyContact2);
			
			System.out.println("Populating Dummy Contact #3");
			Contact dummyContact3 = new Contact(1441021860, 3, "M2H", "59", "59", "80m", "SSB", "001", "", HNKLogger.loggerResource.search("M2H"));
			this.addContact(dummyContact3);
		}
		
		// Add the log Headings
		this.headings.add("Date");
		this.headings.add("Serial");
		this.headings.add("RST (S)");
		this.headings.add("RST (R)");
		this.headings.add("Info (R)");
		this.headings.add("Comments");
		
	}
	
	public TreeSet<Contact> getContacts()
	{
		return this.contacts;
	}
	
	@SuppressWarnings("rawtypes")
	public Vector<Vector> getContactsAsVector()
	{
		Vector<Vector> logVector = new Vector<Vector>();
		for ( Contact contact : this.getContacts() )
		{
			logVector.add(contact.toVector());
		}
		return logVector;
	}
	
	public TreeSet<String> getHeadings()
	{
		return this.headings;
	}
	
	public Vector<String> getHeadingsAsVector()
	{
		return (new Vector<String>(this.headings));
	}
	
	public void addContact(Contact contact)
	{
		HNKLogger.hooks.run("onBeforeLogContact", contact);
		
		
		this.contacts.add(contact);
		
		// Save logfile to disk
		this.save(this.filename);
	}
	
	public int getMaxSerialNumber()
	{
		int maxSerial = 0;
		
		for ( Contact contact : this.contacts )
		{
			if ( contact.getSerial() > maxSerial )
			{
				maxSerial = contact.getSerial();
			}
		}
		
		return maxSerial;
	}
	
	public int getNextSerialNumber()
	{
		return this.getMaxSerialNumber() + 1;
	}
	
	public Contact getLastContact()
	{
		return this.contacts.last();
	}
	
	public void deleteLastContact()
	{
		Contact removedContact = this.contacts.pollLast();
		System.out.println("Removed: " + removedContact.toString());
		this.save(this.filename);
	}
	
	public boolean isCallsignInLog(String callsign)
	{
		for ( Contact contact : this.contacts)
		{
			if ( contact.getCallsign().equals(callsign) )
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isDupe(String callsign, String dupeType)
	{
		if ( dupeType.equals("allLog"))
		{
			return this.isCallsignInLog(callsign);
		}
		return false;
	}
	
	public int contactsInLastHour()
	{
		Date timeNow = new Date();
		int contacts = 0;
		for ( Contact contact : this.contacts )
		{
			Date contactTime = new Date(contact.getTime() * 1000L);
			
			if ( timeNow.getTime() - contactTime.getTime() < 3600000 )
			{
				contacts++;
			}
		}
		return contacts;
	}
	
	public void printLog()
	{
		System.out.println("-------------- Starting Log Dump --------------");
		for ( Contact contact : this.contacts )
		{
			contact.printContact();
		}
		System.out.println("-------------------- Stats --------------------");
		System.out.println("QSOs in last hour: " + this.contactsInLastHour());
	}
	
	public String toString()
	{
		return this.toString(0);
	}
	
	public String toString(int contactLength)
	{
		StringBuilder logAsString = new StringBuilder();
		for ( Contact contact : this.contacts )
		{
			logAsString.append(contact.toString(contactLength) + "\n");
			
		}
		logAsString.deleteCharAt(logAsString.length()-1);
		return logAsString.toString();
	}
	
	public void save()
	{
		this.save(this.filename);
	}
	
	public void save(String filename)
	{
		try
		{
	      OutputStream file = new FileOutputStream(filename);
	      OutputStream buffer = new BufferedOutputStream(file);
	      ObjectOutput output = new ObjectOutputStream(buffer);
	    
	      try
	      {
	    	  output.writeObject(this.contacts);
	      }
	      finally
	      {
	          output.close();
	      }
	    }  
	    catch(IOException ex)
	    {
	      ex.printStackTrace();
	    }

	}
	
	public void load(String filename)
	{
		try
		{
	      InputStream file = new FileInputStream(filename);
	      InputStream buffer = new BufferedInputStream(file);
	      ObjectInput input = new ObjectInputStream (buffer);
	    
	      // Deserialize the List
	      TreeSet<Contact> recoveredContacts = (TreeSet<Contact>)input.readObject();
	      // Display its data
	      for(Contact recoveredContact : recoveredContacts){
	        System.out.println(recoveredContact.toString());
	        this.contacts.add(recoveredContact);
	        //this.addContact(recoveredContact);
	      }
	    }
	    catch(Exception ex){
	      ex.printStackTrace();
	    }
	}
	
	public void export(String filename)
	{
		/*
		adifWriter = new ADIFWriter();
		for ( Contact contact : this.contacts )
		{
			adifContact = new ADIFRecord();
			
		}
		*/
	}
}
