import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.Node;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.OutputStreamWriter;
import java.io.StringReader;

public class EverythingAboutCurl {
	public static void main(String[] args) {
		String userToken = "TUlOSFRBMTZAYXVndXN0YW5hLmVkdQ==:eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxNzUzMzI2LCJnZW5lcmF0ZWRfYXQiOiIyMDE5LTAzLTA2IDE5OjEzOjE5In0.DxvUav8KRxixHbAAMZw5n6Kq19mzOJCc58h2cd1uViFqELmhZ2aj7shKuqR-K6Z58K6BsCLdmP4-XpETCtksfg";
		//newIncidentWithTimeTrack(userToken, 30);
		
		 getAllUsers(userToken);
	}
	
	public static void newIncidentWithTimeTrack(String userToken, int minutesTaken) {
		newIncident(userToken);
//		System.out.println(getID(userToken));
		addTimeTrack(userToken, getID(userToken), minutesTaken);
	}
	
	public static void newIncident(String userToken) {
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
					" <description>Test curl-ing new incidents</description>" + 
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
					+ " <name>Test</name>"
					+ " <priority>Medium</priority>"
					+ " <requester><email>MINHTA16@augustana.edu</email></requester>"
					+ " <category><name>Meetings  (ITS use only)</name></category>"
					+ " <subcategory>"
					+ "      <name>Training/Workshops</name>"
					+ " </subcategory>"
					+ " <description>Test curl-ing new incidents</description>"
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

	public static void addTimeTrack(String userToken, int incidentID, int time) {

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
					+ "<name>slow</name>"
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
	
	public static int getNumPages(String userToken, String dataType) {
		try {
 			String url = "https://api.samanage.com/" + dataType + ".xml?per_page=1";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setDoOutput(true);

			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
			conn.setRequestProperty("Accept", "application/vnd.samanage.v2.1+xml");
			//conn.setRequestProperty("Content-Type", "text/xml");

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
		
			int pages = Integer.parseInt(getString("total_entries", rootElement)) / 100 + 1;
			conn.disconnect();
			return pages;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	public static void getAllUsers(String userToken) {
//		int numPages = getNumPages(userToken, "users");
		int numPages = 1;
		Map<Integer, String> map = new HashMap<Integer, String>();
		for (int page = 1; page <= numPages; page++) {
			try {
	 			String url = "https://api.samanage.com/users.xml?email=minhta16@augustana.edu";

				URL obj = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
				conn.setDoOutput(true);

				conn.setRequestMethod("GET");
				conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
				conn.setRequestProperty("Accept", "application/vnd.samanage.v2.1+xml");
				//conn.setRequestProperty("Content-Type", "text/xml");

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
			
				NodeList listOfUsers = rootElement.getElementsByTagName("user");
				for (int i = 0; i < listOfUsers.getLength(); i++) {
					
					if (listOfUsers.item(i) instanceof Element)
				    {
				        Element user  = (Element) listOfUsers.item(i);
						int ID = Integer.parseInt(getString("id", user));
						String name = getString("name", user);
						map.put(ID, name);
						System.err.println(name);	
				    }
					
				}
				conn.disconnect();
				//return map; #purposely comment out
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.err.println(map);
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
