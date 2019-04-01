package application.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class SamanageRequests {

	private static final String ACCEPT_VERSION = "application/vnd.samanage.v2.1+xml";
	// HTML METHOD:
	// GET
	public static TreeMap<String, Incident> getIncidents(String userToken, String userID) {
		TreeMap<String, Incident> incidentMap = new TreeMap<String, Incident>();
		boolean hasMore = true;
		int curPage = 1;
		while (hasMore) {
			try {
				// String url =
				// "https://api.samanage.com/incidents.xml?per_page=100&page=1&created%5B%5D=Select%20Date%20Range&created_custom_gte%5B%5D=27/03/2019&created_custom_lte%5B%5D=27/03/2019";
				/*
				 *  curl -H "X-Samanage-Authorization: Bearer TOKEN"
				 *  -H'Accept: application/vnd.samanage.v2.1+xml'
				 *  -X GET https://api.samanage.com/incidents.xml
				 */
			    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
			    Date date = new Date();  
			    String url = "https://api.samanage.com/incidents.xml"
			    		+ "?per_page=100&page=" + curPage
			    		+ "&created%5B%5D=Select%20Date%20Range"
			    		+ "&created_custom_gte%5B%5D=" + sdf.format(date)
			    		+ "&created_custom_lte%5B%5D=" + sdf.format(date)
			    		+ "&assigned_to%5B%5D=" + userID;

				URL obj = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
				conn.setDoOutput(true);

				conn.setRequestMethod("GET");
				conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
				conn.setRequestProperty("Accept", ACCEPT_VERSION);
				// conn.setRequestProperty("Content-Type", "text/xml");

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

				// got from
				// https://stackoverflow.com/questions/4076910/how-to-retrieve-element-value-of-xml-using-java
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(new InputSource(new StringReader(xml.toString())));
				Element rootElement = document.getDocumentElement();

				NodeList listOfIncidents = rootElement.getElementsByTagName("incident");
				if (listOfIncidents.getLength() != 100) {
					hasMore = false;
				}

				for (int i = 0; i < listOfIncidents.getLength(); i++) {

					if (listOfIncidents.item(i) instanceof Element) {
						Element incident = (Element) listOfIncidents.item(i);
						ArrayList<User> trackedUsers = new ArrayList<User>();

//						ArrayList<User> trackedUsers = getTrackedUsers(incident);
						
						String number = getString("number", incident);
						Incident newIncident = new Incident(number,
								getString("state", incident),
								getString("name", incident), 
								getString("priority", incident),
								getString("name",  (Element) incident.getElementsByTagName("category").item(0)),
								getString("name",  (Element) incident.getElementsByTagName("subcategory").item(0)),
								getString("email",  (Element) incident.getElementsByTagName("assignee").item(0)).toLowerCase(),
								getString("email",  (Element) incident.getElementsByTagName("requester").item(0)).toLowerCase(),
								getString("name",  (Element) incident.getElementsByTagName("site").item(0)),
								getString("name",  (Element) incident.getElementsByTagName("department").item(0)),
								getString("description", incident),
								trackedUsers);
						
						incidentMap.put(number, newIncident);
					}

				}
				conn.disconnect();
				curPage++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return incidentMap;
		// System.err.println(map);
	}
	// HTML METHOD:
	// POST

	public static void newIncidentWithTimeTrack(String userToken, String incidentName, String priority, String category,
			String subcategory, String description, String dueDate, String state, String assignee, String requester)
			throws IOException {
		SamanageRequests.newIncident(userToken, incidentName, priority, category, subcategory, description, dueDate,
				assignee, requester);
		String incidentID = SamanageRequests.getID(userToken);
		ArrayList<User> trackedUsers = AppSession.getSession().getTrackedUsers();
		for (User user : trackedUsers) {
			SamanageRequests.addTimeTrack(userToken, incidentID, user.getComment(), user.getID(), user.getTime());
		}
		SamanageRequests.updateState(userToken, incidentID, state);

		// clear the UI
		AppSession.getSession().clearTrackedUsers();
	}

	public static void newIncident(String userToken, String incidentName, String priority, String category,
			String subcategory, String description, String dueDate, String assignee, String requester)
			throws IOException {
		String url = "https://api.samanage.com/incidents.xml";

		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setDoOutput(true);

		conn.setRequestMethod("POST");
		conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
		conn.setRequestProperty("Accept", ACCEPT_VERSION);
		conn.setRequestProperty("Content-Type", "text/xml");

		String data1 = "<incident>" + " <name>Test</name>" + " <priority>Medium</priority>"
				+ " <requester><email>MINHTA16@augustana.edu</email></requester>"
				+ " <category><name>Meetings  (ITS use only)</name></category>" + " <subcategory>"
				+ "      <name>Training/Workshops</name>" + " </subcategory>" + " <cc type=\"array\">"
				+ "   <cc>MINHTA16@augustana.edu</cc>" + " </cc>" + " <description>" + description + "</description>"
				+ " <due_at>Mar 8, 2019</due_at>" + " <assignee><email>MINHTA16@augustana.edu</email></assignee>"
				+ " <incidents type=\"array\">" + "   <incident><number>1474</number></incident>"
				+ "   <incident><number>1475</number></incident>" + " </incidents>" + " <assets type=\"array\">"
				+ "   <asset><id>275498</id></asset>" + " </assets>" + " <problem><number>445</number></problem>"
				+ " <solutions type=\"array\">" + "   <solution><number>34</number></solution>" + " </solutions>"
				+ " <configuration_items type=\"array\">"
				+ "   <configuratioexpn_item><id>27234</id></configuration_item>" + " </configuration_items>"
				+ " <custom_fields_values>" + "   <custom_fields_value>" + "     <name>field name</name>"
				+ "     <value>content</value>" + "   </custom_fields_value>" + "   <custom_fields_value>"
				+ "     <name>field name</name>" + "     <value>content</value>" + "   </custom_fields_value>"
				+ " </custom_fields_values>" + "</incident>";

		String data = "<incident>";
		data += " <name>" + incidentName + "</name>";
		data += " <priority>" + priority + "</priority>";
		data += " <requester><email>" + requester + "</email></requester>";
		data += " <category><name>" + category + "</name></category>";
		if (!subcategory.equals("")) {
			data += " <subcategory>" + "      <name>" + subcategory + "</name>" + " </subcategory>";
		}
		data += " <description>" + description + "</description>";
		data += " <due_at>" + dueDate + "</due_at>";
		data += " <assignee><email>" + assignee + "</email></assignee>" + "</incident>";

		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(data);
		out.close();

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

		conn.disconnect();

	}
	
	

	public static void addTimeTrack(String userToken, String incidentID, String trackCmt, String creatorID,
			double time) {

		try {
			String url = "https://api.samanage.com/incidents/" + incidentID + "/time_tracks.xml";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setDoOutput(true);

			conn.setRequestMethod("POST");
			conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
			conn.setRequestProperty("Accept", ACCEPT_VERSION);
			conn.setRequestProperty("Content-Type", "text/xml");

			String data = "<time_track>" + "<name>" + trackCmt + "</name>";
			data += "<creator_id>" + creatorID + "</creator_id>";
			data += "<minutes_parsed>" + time + "</minutes_parsed>" + "</time_track>";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();

			new InputStreamReader(conn.getInputStream());
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// HTML METHOD:
	// GET

	public static User getUserByEmail(String userToken, String email) {
		try {
			String url = "https://api.samanage.com/users.xml?email=" + email;

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setDoOutput(true);

			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
			conn.setRequestProperty("Accept", ACCEPT_VERSION);
			// conn.setRequestProperty("Content-Type", "text/xml");

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

			// got from
			// https://stackoverflow.com/questions/4076910/how-to-retrieve-element-value-of-xml-using-java
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xml.toString())));
			Element rootElement = document.getDocumentElement();

			NodeList listOfUsers = rootElement.getElementsByTagName("user");
			if (listOfUsers.getLength() == 0) {
				return null;
			}

			User newUser = new User();
			for (int i = 0; i < listOfUsers.getLength(); i++) {
				if (listOfUsers.item(i) instanceof Element) {
					Element user = (Element) listOfUsers.item(i);
					String name = getString("name", user);
					String ID = getString("id", user);
					newUser.setName(name);
					newUser.setEmail(email);
					newUser.setDept(getString("name", (Element) user.getElementsByTagName("department").item(0)));
					newUser.setSite(getString("name", (Element) user.getElementsByTagName("site").item(0)));
					newUser.setID(ID);
				}

			}
			conn.disconnect();
			return newUser;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static TreeMap<String, User> getAllUsers(String userToken) {
		TreeMap<String, User> users = new TreeMap<String, User>();

		boolean hasMore = true;
		int curPage = 1;
		while (hasMore) {
			try {
				// String url =
				// "https://api.samanage.com/users.xml?email=minhta16@augustana.edu";
				String url = "https://api.samanage.com/users.xml?per_page=100&page=" + curPage;

				URL obj = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
				conn.setDoOutput(true);

				conn.setRequestMethod("GET");
				conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
				conn.setRequestProperty("Accept", ACCEPT_VERSION);
				// conn.setRequestProperty("Content-Type", "text/xml");

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

				// got from
				// https://stackoverflow.com/questions/4076910/how-to-retrieve-element-value-of-xml-using-java
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(new InputSource(new StringReader(xml.toString())));
				Element rootElement = document.getDocumentElement();

				NodeList listOfUsers = rootElement.getElementsByTagName("user");
				if (listOfUsers.getLength() != 100) {
					hasMore = false; // what if the number of user divides 100?
				}

				for (int i = 0; i < listOfUsers.getLength(); i++) {

					if (listOfUsers.item(i) instanceof Element) {
						Element user = (Element) listOfUsers.item(i);
						User newUser = new User();
						String name = getString("name", user);
						String ID = getString("id", user);
						String email = getString("email", user).toLowerCase();
						newUser.setName(name);
						newUser.setEmail(email);
						newUser.setDept(getString("name", (Element) user.getElementsByTagName("department").item(0)));
						newUser.setSite(getString("name", (Element) user.getElementsByTagName("site").item(0)));
						newUser.setID(ID);
						ArrayList<String> groupIdList = new ArrayList<String>();
						NodeList groupIds = user.getElementsByTagName("group_ids");
						for (int j = 0; j < groupIds.getLength(); j++) {
							if (groupIds.item(j) instanceof Element) {
								String groupID = getString("group_id", (Element) groupIds.item(j));
								groupIdList.add(groupID);
							}
						}
						newUser.setGroupID(groupIdList);
						users.put(email, newUser);
					}

				}
				conn.disconnect();
				curPage++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return users;
		// System.err.println(map);
	}

	public static TreeMap<String, ArrayList<String>> getCategories(String userToken) {
		/*
		 * curl -H "X-Samanage-Authorization: Bearer TOKEN" -H 'Accept:
		 * application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET
		 * https://api.samanage.com/categories.xml
		 */
		try {
			String url = "https://api.samanage.com/categories.xml";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setDoOutput(true);

			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
			conn.setRequestProperty("Accept", ACCEPT_VERSION);
			conn.setRequestProperty("Content-Type", "text/xml");

			Element rootElement = documentFromOutput(conn);
			NodeList categories = rootElement.getElementsByTagName("incident-type");
			if (categories.getLength() == 0) {
				return null;
			}

			TreeMap<String, ArrayList<String>> categoriesMap = new TreeMap<String, ArrayList<String>>();
			for (int i = 0; i < categories.getLength(); i++) {
				if (categories.item(i) instanceof Element) {
					Element category = (Element) categories.item(i);
					if (category.getElementsByTagName("parent_id")
							.item(category.getElementsByTagName("parent_id").getLength() - 1).hasAttributes()) {
						String name = getString("name", category);
						categoriesMap.put(name, new ArrayList<String>());
						NodeList subcat = category.getElementsByTagName("child");
						for (int j = 0; j < subcat.getLength(); j++) {
							categoriesMap.get(name).add(getString("name", (Element) subcat.item(j)));
						}
					}
				}

			}

			conn.disconnect();
			return categoriesMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<String> getDepartments(String userToken) {
		/*
		 * curl -H "X-Samanage-Authorization: Bearer TOKEN" -H 'Accept:
		 * application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET
		 * https://api.samanage.com/categories.xml
		 */
		boolean hasMore = true;
		int curPage = 1;
		ArrayList<String> deptList = new ArrayList<String>();
		while (hasMore) {
			try {
				String url = "https://api.samanage.com/departments.xml?per_page=100&page=" + curPage;

				URL obj = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
				conn.setDoOutput(true);

				conn.setRequestMethod("GET");
				conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
				conn.setRequestProperty("Accept", ACCEPT_VERSION);
				conn.setRequestProperty("Content-Type", "text/xml");

				Element rootElement = documentFromOutput(conn);
				NodeList depts = rootElement.getElementsByTagName("department");
				if (depts.getLength() != 100) {
					hasMore = false;
				}

				for (int i = 0; i < depts.getLength(); i++) {
					if (depts.item(i) instanceof Element) {
						Element dept = (Element) depts.item(i);
						String name = getString("name", dept);
						deptList.add(name);
					}

				}

				conn.disconnect();
				curPage++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deptList;
	}

	public static ArrayList<String> getSites(String userToken) {
		/*
		 * curl -H "X-Samanage-Authorization: Bearer TOKEN" -H 'Accept:
		 * application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET
		 * https://api.samanage.com/categories.xml
		 */
		boolean hasMore = true;
		int curPage = 1;
		ArrayList<String> siteList = new ArrayList<String>();
		while (hasMore) {
			try {
				String url = "https://api.samanage.com/sites.xml?per_page=100&page=" + curPage;

				URL obj = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
				conn.setDoOutput(true);

				conn.setRequestMethod("GET");
				conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
				conn.setRequestProperty("Accept", ACCEPT_VERSION);
				conn.setRequestProperty("Content-Type", "text/xml");

				Element rootElement = documentFromOutput(conn);
				NodeList sites = rootElement.getElementsByTagName("site");
				if (sites.getLength() != 100) {
					hasMore = false;
				}

				for (int i = 0; i < sites.getLength(); i++) {
					if (sites.item(i) instanceof Element) {
						Element site = (Element) sites.item(i);
						String name = getString("name", site);
						siteList.add(name);
					}

				}

				conn.disconnect();
				curPage++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return siteList;
	}

	public static String getID(String userToken) {
		try {
			String url = "https://api.samanage.com/incidents.xml?per_page=1";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setDoOutput(true);

			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
			conn.setRequestProperty("Accept", ACCEPT_VERSION);
			conn.setRequestProperty("Content-Type", "application/xml");

			Element rootElement = documentFromOutput(conn);

			String incidentID = getString("id", rootElement);
			conn.disconnect();
			return incidentID;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static int getTotalElements(String userToken, String type) {
		try {
			String url = "https://api.samanage.com/" + type + ".xml";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setDoOutput(true);

			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
			conn.setRequestProperty("Accept", ACCEPT_VERSION);
			conn.setRequestProperty("Content-Type", "application/xml");

			Element rootElement = documentFromOutput(conn);

			int totalEntries = 0;
			if (getString("total_entries", rootElement) == "") {
				Node childNode = rootElement.getFirstChild();
				while (childNode.getNextSibling() != null) {
					childNode = childNode.getNextSibling();
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) childNode;
						if (element.getElementsByTagName("parent_id")
								.item(element.getElementsByTagName("parent_id").getLength() - 1).hasAttributes()) {
							totalEntries++;
						}
					}
				}
			} else {
				totalEntries = Integer.parseInt(getString("total_entries", rootElement));
			}
			conn.disconnect();
			return totalEntries;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// HTML METHOD:
	// PUT

	public static void updateState(String userToken, String incidentID, String state) {
		try {
			String url = "https://api.samanage.com/incidents/" + incidentID + ".xml";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setDoOutput(true);

			conn.setRequestMethod("PUT");
			conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
			conn.setRequestProperty("Accept", ACCEPT_VERSION);
			conn.setRequestProperty("Content-Type", "text/xml");

			String data = "<incident>" + " <state>" + state + "</state>" + "</incident>";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();

			new InputStreamReader(conn.getInputStream());
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//OTHERS
	//
	
	// got from
	// https://stackoverflow.com/questions/4076910/how-to-retrieve-element-value-of-xml-using-java
	protected static String getString(String tagName, Element element) {
		return getString(tagName, element, 0);
	}
	
	protected static String getString(String tagName, Element element, int item) {
		NodeList list = element.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {
			NodeList subList = list.item(item).getChildNodes();

			if (subList != null && subList.getLength() > 0) {
				return subList.item(0).getNodeValue();
			}
		}

		return "";
	}
	
	private static ArrayList<User> getTrackedUsers(Element incident) {
		ArrayList<User> users = new ArrayList<User>();
		NodeList timeTracks = incident.getElementsByTagName("time_tracks");
		for (int i = 0; i < timeTracks.getLength(); i++) {
			if (timeTracks.item(i) instanceof Element) {
				Element site = (Element) timeTracks.item(i);
				String name = getString("name", site);
				users.add(new User());
			}

		}
		return null;
	}
	
	
	public static Element documentFromOutput(HttpURLConnection conn) {
		Element rootElement = null;
		try {
			BufferedReader br;
			if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			String output;
			StringBuffer xml = new StringBuffer();
			while ((output = br.readLine()) != null) {
				xml.append(output);
			}

			// got from
			// https://stackoverflow.com/questions/4076910/how-to-retrieve-element-value-of-xml-using-java
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xml.toString())));
			rootElement = document.getDocumentElement();

			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rootElement;
	}
}