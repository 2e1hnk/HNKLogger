package uk.co.mattcarus.hnklogger.gui.SwingGUI.plugins;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import uk.co.mattcarus.hnklogger.HNKLoggerProperties;
import uk.co.mattcarus.hnklogger.exceptions.HNKPropertyNotFoundException;
import uk.co.mattcarus.hnklogger.gui.SwingGUI.SwingGUI;

public class QRZLookup extends GUIPlugin implements Runnable {
	
	public String name = "QRZ.com Lookup";
	private String identifier = "qrzlookup";

	HashMap<String, String> cache = new HashMap<String, String>();
	
	private String qrz_agent = "HNKLoggerV0.1";
	private String qrz_url = "http://xmldata.qrz.com/xml/current/";
	
	private String sessionKey = "";
	private Client client;
	private String qrz_username;
	private String qrz_password;

    private JFrame infoFrame;
    private JTextPane infoTextPane;

    public String getName() {
		return this.name;
	}

    public String getIdentifier() {
		return this.identifier;
	}

	public void init()
	{
		this.client = Client.create();
	}
	
	public void initProperties(HNKLoggerProperties properties)
	{
		try {
			this.qrz_username = properties.getProperty(this.getIdentifier() + ".username");
			System.out.println("Set QRZ Username to " + this.qrz_username);
			this.qrz_password = properties.getProperty(this.getIdentifier() + ".password");
			System.out.println("Set QRZ Password to " + this.qrz_password);
			this.getSessionKey();
		}
		catch (HNKPropertyNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	public String onCallsignEntered(String callsign)
	{
		// Check if we have this callsign in the cache already
		if ( !this.cache.containsKey(callsign) )
		{
			if ( this.sessionKey != null )
			{
				try {
					String lookupUrl = this.getLookupUrl(callsign);
					System.out.println("Processing QRZ Hook (using url: " + lookupUrl + ")");
					WebResource webResource = client.resource(lookupUrl);
					ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
					
					if (response.getStatus() != 200) {
					   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
					}
					
					Document qrzDocument = response.getEntity(Document.class);
					
					System.out.println("QRZ login debug .... \n");
					System.out.println(this.getXmlString(qrzDocument));
					String info = this.qrzDocumentToString(qrzDocument);
					this.cache.put(callsign, info);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("Not logged in to QRZ.com");
			}
		}
	
		this.updateInfoWindow(this.cache.get(callsign));
	
		return callsign;
	}
	
	private void getSessionKey()
	{
		WebResource webResource = client.resource(this.getLoginUrl());
		ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
		
		if (response.getStatus() != 200) {
		   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
		
		Document output = response.getEntity(Document.class);
		
		try {
			Document qrzLoginDocument = output;
			System.out.println("QRZ login debug (" + this.qrz_username + ") .... \n");
			System.out.println(this.getXmlString(qrzLoginDocument));
			this.sessionKey = this.getXmlValue(qrzLoginDocument, "/ns:QRZDatabase/ns:Session/ns:Key/text()");
			System.out.println("QRZ Session Key: " + this.sessionKey);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private String getLoginUrl()
	{
		return String.format("%s?username=%s;password=%s;agent=%s", this.qrz_url, this.qrz_username, this.qrz_password, this.qrz_agent);
	}
	
	private String getLookupUrl(String callsign)
	{
		return String.format("http://xmldata.qrz.com/xml/current/?s=%s;callsign=%s", this.sessionKey, callsign);
	}
	
	private Document getXmlDocument(String xml) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(xml)));
		return document;
	}
	
	private String getXmlString(Document xml)
	{
		try {
		    Transformer transformer = TransformerFactory.newInstance().newTransformer();
		    StreamResult result = new StreamResult(new StringWriter());
		    DOMSource source = new DOMSource(xml);
		    transformer.transform(source, result);
		    return result.getWriter().toString();
		} catch(TransformerException ex) {
		    ex.printStackTrace();
		    return null;
		}
	}
	
	@SuppressWarnings("finally")
	private String getXmlValue(Document xmlDocument, String xPathString)
	{
		String retValue = "";
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			xPath.setNamespaceContext(new QRZNamespaceContext());
			XPathExpression expr = xPath.compile(xPathString);
	        Node node = (Node) expr.evaluate(xmlDocument, XPathConstants.NODE);
	        retValue = node.getNodeValue();
		}
		catch ( XPathExpressionException e )
		{
			//throw new XPathExpressionException(e.toString());
		}
		catch ( NullPointerException e )
		{
			// Node doesn't exist, don't do anything
		}
		finally {
			return retValue;
		}
	}
	
    private static class QRZNamespaceContext implements NamespaceContext {

        public String getNamespaceURI(String prefix) {
            if("ns".equals(prefix)) {
                return "http://xmldata.qrz.com";
            }
            return null;
        }

        public String getPrefix(String namespaceURI) {
            return null;
        }

        public Iterator getPrefixes(String namespaceURI) {
            return null;
        }

    }
    
	public void initGUI(SwingGUI gui) {
        SwingUtilities.invokeLater(this);
	}


	@Override
	public void run() {
	    infoFrame = new JFrame(this.getName());
	    infoTextPane = new JTextPane();
	    
	    infoFrame.add(infoTextPane);
	    infoFrame.pack();
	    infoFrame.setLocationRelativeTo(null);
        infoFrame.setVisible(true);
	    
        Timer t = new Timer(1000, new ActionListener(){
      	    public void actionPerformed(ActionEvent e) {
				// Do nothing, this just keeps the window open
      	    }
        });
        t.start();
	}
	
	private void updateInfoWindow(String contents)
	{
		infoTextPane.setText(contents);
		infoFrame.pack();
	}

	private String qrzDocumentToString(Document qrzDocument) throws XPathException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.getXmlValue(qrzDocument, "/ns:QRZDatabase/ns:Callsign/ns:fname/text()"));
		sb.append(" ");
		sb.append(this.getXmlValue(qrzDocument, "/ns:QRZDatabase/ns:Callsign/ns:name/text()"));
		sb.append("\n");
		sb.append(this.getXmlValue(qrzDocument, "/ns:QRZDatabase/ns:Callsign/ns:addr2/text()"));
		sb.append(", ");
		sb.append(this.getXmlValue(qrzDocument, "/ns:QRZDatabase/ns:Callsign/ns:state/text()"));
		sb.append(", ");
		sb.append(this.getXmlValue(qrzDocument, "/ns:QRZDatabase/ns:Callsign/ns:country/text()"));
		
		return sb.toString();
	}
}
