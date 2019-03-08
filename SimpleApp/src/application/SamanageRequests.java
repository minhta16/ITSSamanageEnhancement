package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class SamanageRequests {
	public static void newIncident(String userToken, String incidentName, String description) {
		try {
			String url = "https://api.samanage.com/incidents.xml";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setDoOutput(true);

			conn.setRequestMethod("POST");
			conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
			conn.setRequestProperty("Accept", "application/vnd.samanage.v2.1+xml");
			conn.setRequestProperty("Content-Type", "text/xml");

			String data1 = "<incident>" +
					" <name>Test</name>" +
					" <priority>Medium</priority>" +
					" <requester><email>MINHTA16@augustana.edu</email></requester>" +
					" <category><name>Meetings  (ITS use only)</name></category>" +
					" <subcategory>" +
					"      <name>Training/Workshops</name>" +
					" </subcategory>" +
					" <cc type=\"array\">" + 
					"   <cc>MINHTA16@augustana.edu</cc>" + 
					" </cc>" + 
					" <description>" + description + "</description>" + 
					" <due_at>Mar 8, 2019</due_at>" + 
					" <assignee><email>MINHTA16@augustana.edu</email></assignee>" + 
					" <incidents type=\"array\">" + 
					"   <incident><number>1474</number></incident>" + 
					"   <incident><number>1475</number></incident>" + 
					" </incidents>" + 
					" <assets type=\"array\">" + 
					"   <asset><id>275498</id></asset>" + 
					" </assets>" + 
					" <problem><number>445</number></problem>" + 
					" <solutions type=\"array\">" + 
					"   <solution><number>34</number></solution>" + 
					" </solutions>" + 
					" <configuration_items type=\"array\">" + 
					"   <configuration_item><id>27234</id></configuration_item>" + 
					" </configuration_items>" + 
					" <custom_fields_values>" + 
					"   <custom_fields_value>" + 
					"     <name>field name</name>" + 
					"     <value>content</value>" + 
					"   </custom_fields_value>" + 
					"   <custom_fields_value>" + 
					"     <name>field name</name>" + 
					"     <value>content</value>" + 
					"   </custom_fields_value>" + 
					" </custom_fields_values>" + 
					"</incident>";
			
			String data = "<incident>"
					+ " <name>" + incidentName +"</name>"
					+ " <priority>Medium</priority>"
					+ " <requester><email>MINHTA16@augustana.edu</email></requester>"
					+ " <category><name>Meetings  (ITS use only)</name></category>"
					+ " <subcategory>"
					+ "      <name>Training/Workshops</name>"
					+ " </subcategory>"
					+ " <description>" + description + "</description>"
					+ " <assignee><email>MINHTA16@augustana.edu</email></assignee>"
					+ "</incident>";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			
			new InputStreamReader(conn.getInputStream());
			conn.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateState(String userToken, int incidentID, String state) {
		try {
			String url = "https://api.samanage.com/incidents/" + incidentID + ".xml";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setDoOutput(true);

			conn.setRequestMethod("PUT");
			conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
			conn.setRequestProperty("Accept", "application/vnd.samanage.v2.1+xml");
			conn.setRequestProperty("Content-Type", "text/xml");

			String data = 	"<incident>" + 
							" <state>" + state + "</state>" + 
							"</incident>";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			
			new InputStreamReader(conn.getInputStream());
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getID(String userToken) {
		try {
			String url = "https://api.samanage.com/incidents.xml?per_page=1";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setDoOutput(true);

			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
			conn.setRequestProperty("Accept", "application/vnd.samanage.v2.1+xml");
			conn.setRequestProperty("Content-Type", "application/xml");

			BufferedReader br;
			if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			StringBuffer xml = new StringBuffer();
			String output;
			while ((output = br.readLine()) != null) {
				xml.append(output);
			}

			// got from https://stackoverflow.com/questions/4076910/how-to-retrieve-element-value-of-xml-using-java
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xml.toString())));
			Element rootElement = document.getDocumentElement();
			
			int incidentID = Integer.parseInt(getString("id", rootElement));
			conn.disconnect();
			return incidentID;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void addTimeTrack(String userToken, int incidentID, String trackCmt, int time) {

		try {
			String url = "https://api.samanage.com/incidents/" + incidentID + "/time_tracks.xml";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setDoOutput(true);

			conn.setRequestMethod("POST");
			conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
			conn.setRequestProperty("Accept", "application/vnd.samanage.v2.1+xml");
			conn.setRequestProperty("Content-Type", "text/xml");

			String data = "<time_track>" 
					+ "<name>" + trackCmt + "</name>"
					+ "<creator_id>1753326</creator_id>"
					+ "<minutes_parsed>"
					+ time
					+ "</minutes_parsed>"
					+ "</time_track>";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			
			new InputStreamReader(conn.getInputStream());
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// got from https://stackoverflow.com/questions/4076910/how-to-retrieve-element-value-of-xml-using-java
	protected static String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }
}
