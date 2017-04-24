package uk.co.mattcarus.hnklogger.plugin;

import java.io.BufferedReader;
import uk.co.mattcarus.hnklogger.gui.HNKLoggerGUI;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import uk.co.mattcarus.hnklogger.Contact;

public class PostToWebAddress extends Plugin {
	
	public String name = "change me";
	
	private final String USER_AGENT = "Mozilla/5.0";
	private String baseUrl = "http://192.168.1.8";
	
	@Override
	public Contact onBeforeLogContact(Contact contact)
	{
		try {
			this.sendGet(this.baseUrl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contact;
	}
	
	// HTTP GET request
	private void sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());

	}
	
	// HTTP POST request
	private void sendPost(String url, HashMap<String, String> params) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		StringBuilder paramsBuilder = new StringBuilder();
		for ( String key : params.keySet() )
		{
			paramsBuilder.append(key);
			paramsBuilder.append("=");
			paramsBuilder.append(params.get(key));
			paramsBuilder.append("&");
		}
		paramsBuilder.deleteCharAt(paramsBuilder.length() - 1);
		String urlParameters = paramsBuilder.toString();
		
		//String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
		System.out.println(response.toString());

	}
	
	public void initGUI(HNKLoggerGUI gui) {
	}
}
