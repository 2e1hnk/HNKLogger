package uk.co.mattcarus.hnklogger.contests;

import java.util.List;

import uk.co.mattcarus.hnklogger.Contact;
import uk.co.mattcarus.hnklogger.Log;
import uk.co.mattcarus.hnklogger.Contest;

import java.util.List;
import java.util.ArrayList;

public class RsgbSsbFieldDay implements Contest {
	
	public float calculatePoints(Log log)
	{
		int points = 0;
		List<String> countries = new ArrayList<String>();
		
		for ( Contact contact : log.getContacts() )
		{
			if ( contact.getAdditionalInfo().get("itu_zone") != null )
			{
				if ( !countries.contains(contact.getAdditionalInfo().get("country")) )
				{
					countries.add(contact.getAdditionalInfo().get("country"));
				}
				
				if ( this.getItuRegion(Integer.parseInt(contact.getAdditionalInfo().get("itu_zone"))) == 1 )
				{
					if ( contact.getCallsign().endsWith("/P") || contact.getCallsign().endsWith("/M") )
					{
						points = points + 5;
					}
					else
					{
						points = points + 2;
					}
				}
				else
				{
					points = points + 3;
				}
			}
		}
		
		return points * countries.size();
	}
	
	public int getItuRegion(int ituZone)
	{
		if ( ituZone >= 1 && ituZone <= 16 )
		{
			return 2;
		}
		else if ( ituZone >= 17 && ituZone <= 39 )
		{
			return 1;
		}
		else if ( ituZone >= 40 && ituZone <= 45 )
		{
			return 3;
		}
		else if ( ituZone >= 46 && ituZone <= 48 )
		{
			return 1;
		}
		else if ( ituZone >= 49 && ituZone <= 51 )
		{
			return 3;
		}
		else if ( ituZone >= 52 && ituZone <= 53)
		{
			return 1;
		}
		else if ( ituZone >= 54 && ituZone <= 56 )
		{
			return 3;
		}
		else if ( ituZone == 57 )
		{
			return 1;
		}
		else if ( ituZone >= 58 && ituZone <= 65 )
		{
			return 3;
		}
		else if ( ituZone >= 66 && ituZone <= 68 )
		{
			return 1;
		}
		else if ( ituZone >= 69 && ituZone <= 71 )
		{
			return 3;
		}
		else if ( ituZone >= 72 && ituZone <= 73 )
		{
			return 2;
		}
		else
		{
			return 0;
		}
	}
}
