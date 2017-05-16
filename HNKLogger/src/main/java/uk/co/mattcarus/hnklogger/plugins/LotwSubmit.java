package uk.co.mattcarus.hnklogger.plugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.util.Date;

import javax.swing.JFrame;

import com.kd7uiy.trustedQsl.HamBand;
import com.kd7uiy.trustedQsl.HamBand.Band;
import com.kd7uiy.trustedQsl.P12Certificate;
import com.kd7uiy.trustedQsl.QsoData;
import com.kd7uiy.trustedQsl.QsoData.Mode;
import com.kd7uiy.trustedQsl.Station;
import com.kd7uiy.trustedQsl.WriteGabbi;

import uk.co.mattcarus.hnklogger.Contact;
import uk.co.mattcarus.hnklogger.HNKLoggerProperties;
import uk.co.mattcarus.hnklogger.exceptions.HNKPropertyNotFoundException;

public class LotwSubmit extends Plugin {

	public static String name = "Logbook-of-the-World Plugin";
	public static String identifier = "lotw";
	
	private HNKLoggerProperties properties;
	private KeyStore keystore;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getIdentifier() {
		return this.identifier;
	}

	@Override
	public Integer[] getCapabilities() {
		return null;
	}
	
	public void initProperties(HNKLoggerProperties properties)
	{
		this.properties = properties;
		try {
			this.keystore = P12Certificate.getKeyStore(this.properties.getProperty(this.getIdentifier() + ".p12-file-location"),"");
		}
		catch (HNKPropertyNotFoundException e)
		{
			// Handle Exception
		}
	}
	
	public void init() {
		System.out.println("running init() for " + this.getIdentifier()));
	}

	public Contact onAfterLogContact(Contact contact)
	{
		try {
			LotwHandler lotw = new LotwHandler(keystore, new char[]{}, "trustedqsl user certificate");
			if (lotw.writeToLotw("test.tq8")==true) {
				System.out.println("File uploaded successfully");
			} else {
				System.out.println("File uploaded failed");
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException | IOException e) {
			e.printStackTrace();
		}
		
		return contact;
	}
	
	public class LotwHandler extends WriteGabbi {
		
		private QsoData data;

		public LotwHandler(KeyStore keystore, char[] password, String alias) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
			super(keystore,password,alias);
		}

		@Override
		protected Station[] getStations() {
			Station station=new Station();
			station.dxcc=291;
			return new Station[]{station};
		}
		
		public void setQsoData(Contact contact)
		{
			this.data = new QsoData();
			this.data.band = HamBand.findBand(contact.getBand());
			this.data.call = contact.getCallsign();
			// Need to change this
			this.data.mode = Mode.SSB;
			this.data.qso_dateTime = new Date(contact.getTime());
		}

		@Override
		protected QsoData[] getQsoData(Station station) {
			if (station.dxcc==291) {
				QsoData data=new QsoData();
				data.band=Band.W2M;
				data.call="KD7UIY";
				data.freq=146.580;
				data.mode=Mode.SSB;
				data.qso_dateTime=new Date();
				return new QsoData[]{data};
			} else {
				return null;
			}
		}

		@Override
		protected void publishProgress(int numComplete) {
			System.out.println("Have written "+ numComplete +" records");
			
		}
		
		//First argument is the location of your .p12 file
		public void run(String[] args) {
			final String alias="trustedqsl user certificate";
			KeyStore keystore=P12Certificate.getKeyStore(args[0],"");
			try {
				LotwHandler writeGabbi= new LotwHandler(keystore,new char[]{},alias);
				if (writeGabbi.writeToLotw("test.tq8")==true) {
					System.out.println("File uploaded successfully");
				} else {
					System.out.println("File uploaded failed");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
