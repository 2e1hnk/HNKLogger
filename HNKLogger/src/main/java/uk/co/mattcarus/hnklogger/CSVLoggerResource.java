package uk.co.mattcarus.hnklogger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVLoggerResource implements LoggerResource {

	private HashMap<String, HashMap<String, String>> resource;
	private List<String> resourceKeys;
	
	public CSVLoggerResource(String csvFilename)
	{
		this.resourceKeys = new ArrayList<String>();
		
		this.resourceKeys.add("prefix");
		this.resourceKeys.add("country");
		this.resourceKeys.add("continent");
		this.resourceKeys.add("lat");
		this.resourceKeys.add("lng");
		this.resourceKeys.add("dummy");
		this.resourceKeys.add("itu_zone");
		this.resourceKeys.add("cq_zone");
		
		this.resource = new HashMap<String, HashMap<String, String>>();
		this.load(csvFilename);
	}
	
	@Override
	public HashMap<String, String> search(String searchTerm) {
		
		for ( int i = searchTerm.length(); i> 0; i-- )
		{
			if ( this.resource.containsKey(searchTerm.substring(0, i)) )
			{
				for ( int j = 0; j < this.resource.get(searchTerm.substring(0, i)).size(); j++ )
				{
					return this.resource.get(searchTerm.substring(0, i));
				}
			}
		}
		
		HashMap<String, String> empty = new HashMap<String, String>();
		return empty;
	}

	@Override
	public void load(String csvFile) {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

			    // use comma as separator
				HashMap<String, String> csvLineProcessed = new HashMap<String, String>();
				String[] csvLine = line.split(cvsSplitBy);
				
				for ( int i = 0; i < csvLine.length; i++ )
				{
					csvLineProcessed.put(this.resourceKeys.get(i), this.stripQuotes(csvLine[i]));
					//System.out.print(this.resourceKeys.get(i) + " : " + this.stripQuotes(csvLine[i]) + ", ");
				}
				
				this.resource.put(this.stripQuotes(csvLine[0]), csvLineProcessed);
				
				//System.out.print("\n");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String stripQuotes(String input)
	{
		String output = input;
		char firstChar = input.charAt(0);
		char lastChar = input.charAt(input.length()-1);
		
		if ( firstChar == '"' && lastChar == '"' )
		{
			output = input.substring(1, input.length() - 1);
		}
		
		return output;
	}
}
