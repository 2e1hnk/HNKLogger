package uk.co.mattcarus.hnklogger.plugin;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

import javax.ws.rs.core.MediaType;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;

import uk.co.mattcarus.hnklogger.Contact;

public class QRZLookup extends Hook {
	
	public String name = "QRZ.com Lookup";
	private String qrz_agent = "HNKLoggerV0.1";
	private String qrz_url = "http://xmldata.qrz.com/xml/current/";
	
	private String sessionKey = "53166f3ffdf711bb81d423b78145ed15";
	private Client client;
	private String qrz_username = "2E1HNK";
	private String qrz_password = "Suwr1sp1";
	
	public void init()
	{
		this.client = Client.create();
		this.getSessionKey();
	}
	
	public Contact onBeforeLogContact(Contact contact)
	{
		System.out.println("Processing QRZ Hook");
		WebResource webResource = client.resource(this.getLookupUrl(contact.getCallsign()));
		ClientResponse response = webResource.accept(MediaType.APPLICATION_XML_TYPE).get(ClientResponse.class);
		
		if (response.getStatus() != 200) {
		   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
		
		Document output = response.getEntity(Document.class);
		
		try {
			Document qrzDocument = output;
			System.out.println("QRZ login debug .... \n");
			System.out.println(this.getXmlString(qrzDocument));
			//this.sessionKey = this.getXmlValue(qrzDocument, "/ns:QRZDatabase/ns:Session/ns:Key");
			//System.out.println("QRZ Session Key: " + this.sessionKey);
		} catch (Exception e) {
			e.printStackTrace();
		}		

		return contact;
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
			System.out.println("QRZ login debug .... \n");
			System.out.println(this.getXmlString(qrzLoginDocument));
//			this.sessionKey = this.getXmlValue(qrzLoginDocument, "/ns:QRZDatabase/ns:Session/ns:Key");
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
	
	private String getXmlValue(Document xmlDocument, String xPathString) throws XPathExpressionException
	{
		XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new QRZNamespaceContext());
		XPathExpression expr = xPath.compile(xPathString);
        Node node = (Node) expr.evaluate(xmlDocument, XPathConstants.NODE);
        return node.getNodeValue();
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
}
