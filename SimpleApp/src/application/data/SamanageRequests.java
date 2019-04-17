package application.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gson.JsonIOException;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;


public class SamanageRequests {

	private static final String ACCEPT_VERSION = "application/vnd.samanage.v2.1+xml";
	private static final int maxEachCall = 100;
	// HTML METHOD:
	// POST

	public static void newIncidentWithTimeTrack(String userToken, String incidentName, String priority, String category,
			String subcategory, String description, String dueDate, String state, String assignee, String requester,
			String dept, String site, String software, boolean notify) throws IOException {
		SamanageRequests.newIncident(userToken, incidentName, priority, category, subcategory, description, dueDate,
				assignee, requester, software);
		String incidentID = SamanageRequests.getID(userToken);
		ArrayList<TimeTrack> timeTracks = AppSession.getSession().getTimeTracks();
		for (TimeTrack track : timeTracks) {
			SamanageRequests.addTimeTrack(userToken, incidentID, track.getComment(), track.getUser().getID(),
					track.getTime());
		}
		SamanageRequests.updateStateAndDept(userToken, incidentID, state, dept, site, notify);

		// clear the UI
		AppSession.getSession().clearTrackedUsers();
	}

	public static void newIncident(String userToken, String incidentName, String priority, String category,
			String subcategory, String description, String dueDate, String assignee, String requester, String software)
			throws IOException {
		String url = "https://api.samanage.com/incidents.xml";

		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
		conn.setRequestProperty("Accept", ACCEPT_VERSION);
		conn.setRequestProperty("Content-Type", "text/xml");

		String data = "<incident>";
		data += " <name>" + incidentName + "</name>";
		data += " <priority>" + priority + "</priority>";
		data += " <requester><email>" + requester + "</email></requester>";
		data += " <category><name>" + category + "</name></category>";
		if (!subcategory.isEmpty()) {
			data += " <subcategory>";
			data += "<name>" + subcategory + "</name>";
			data += " </subcategory>";
		}
		data += " <description>" + description + "</description>";
		data += " <due_at>" + dueDate + "</due_at>";
		if (assignee.contains("@")) {
			data += " <assignee><email>" + assignee + "</email></assignee>";
		} else {
			data += " <assignee_id>" + assignee + "</assignee_id>";
		}
		if (software != null && !software.isEmpty()) {
			data += "<custom_fields_values>";
			data += "<custom_fields_value>";
			data += "<name>Software</name>";
			data += "<value>" + software + "</value>";
			data += "</custom_fields_value>";
			data += "</custom_fields_values>";

		}
		data += "</incident>";

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

	public static void addTimeTrack(String userToken, String incidentID, String trackCmt, String creatorID, double time)
			throws IOException {

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
		data += "<minutes_parsed>" + time + " m</minutes_parsed>" + "</time_track>";
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(data);
		out.close();

		new InputStreamReader(conn.getInputStream());
		conn.disconnect();

	}

	// HTML METHOD:
	// GET

	public static TreeMap<String, Incident> getIncidents(String userToken, LocalDate fromDate, LocalDate toDate)
			throws IOException, SAXException, ParserConfigurationException {
		TreeMap<String, Incident> incidentMap = new TreeMap<String, Incident>();
		boolean hasMore = true;
		int curPage = 1;
		while (hasMore) {
			// String url =
			// "https://api.samanage.com/incidents.xml?per_page=100&page=1&created%5B%5D=Select%20Date%20Range&created_custom_gte%5B%5D=27/03/2019&created_custom_lte%5B%5D=27/03/2019";
			/*
			 * curl -H "X-Samanage-Authorization: Bearer TOKEN" -H'Accept:
			 * application/vnd.samanage.v2.1+xml' -X GET
			 * https://api.samanage.com/incidents.xml
			 */
			String url = "https://api.samanage.com/incidents.xml" + "?per_page=100&page=" + curPage
					+ "&created%5B%5D=Select%20Date%20Range" + "&created_custom_gte%5B%5D="
					+ fromDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "&created_custom_lte%5B%5D="
					+ toDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			
			System.err.println(fromDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			System.err.println(toDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			
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
					String id = getString("id", incident);
					ArrayList<TimeTrack> trackedUsers = getTimeTracks(userToken, id);
					String ID = getString("id", incident);
					String number = getString("number", incident);
					String category = getString("name", (Element) incident.getElementsByTagName("category").item(0));
					String assigneeEmail = getString("email",
							(Element) incident.getElementsByTagName("assignee").item(0)).toLowerCase();
					String groupId = "";
					if (assigneeEmail.isEmpty()) {
						groupId = getString("id", (Element) incident.getElementsByTagName("assignee").item(0));
					} else {
						groupId = getString("group_id", (Element) incident.getElementsByTagName("assignee").item(0));
					}
					String software = "";
					if (category.equals("Software")) {
						for (int j = 0; j < incident.getElementsByTagName("custom_fields_value").getLength(); j++) {
							if (getString("name",
									(Element) incident.getElementsByTagName("custom_fields_value").item(j))
											.equals("Software")) {
								software = getString("value",
										(Element) incident.getElementsByTagName("custom_fields_value").item(j));
							}
						}
					}
					Incident newIncident = new Incident(ID, number, getString("state", incident),
							getString("name", incident), getString("priority", incident), category,
							getString("name", (Element) incident.getElementsByTagName("subcategory").item(0)),
							assigneeEmail,
							getString("email", (Element) incident.getElementsByTagName("requester").item(0))
									.toLowerCase(),
							getString("name",
									(Element) incident.getElementsByTagName("site")
											.item(incident.getElementsByTagName("site").getLength() - 1)),
							getString("name",
									(Element) incident.getElementsByTagName("department")
											.item(incident.getElementsByTagName("department").getLength() - 1)),
							getString("description", incident), toDate(getString("due_at", incident)), groupId,
							software, trackedUsers);

					incidentMap.put(number, newIncident);
				}

			}
			conn.disconnect();
			curPage++;
		}
		return incidentMap;
		// System.err.println(map);
	}

	public static User getUserByEmail(String userToken, String email)
			throws IOException, ParserConfigurationException, SAXException {
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
	}

	public static TreeMap<String, User> getAllUsers(String userToken) throws IOException {
		TreeMap<String, User> users = new TreeMap<String, User>();
		int totalUsers = getTotalElements(userToken, "users");
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
						Element groupIdsElement = (Element) user.getElementsByTagName("group_ids").item(0);
						NodeList groupIds = groupIdsElement.getElementsByTagName("group_id");
						for (int j = 0; j < groupIds.getLength(); j++) {
							if (groupIds.item(j) instanceof Element) {
								String groupID = groupIds.item(j).getTextContent();
								groupIdList.add(groupID);
							}
						}
						newUser.setGroupID(groupIdList);
						users.put(email, newUser);
					}
					System.out.print("Updating users: [" + (i + (curPage - 1) * 100) + "/" + totalUsers + "]\t\t\t\r");

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

	public static TreeMap<String, User> getAllUsersMultiThreads(String userToken) throws IOException {
		TreeMap<String, User> users = new TreeMap<String, User>();
		int totalUsers = getTotalElements(userToken, "users");
		int totalCalls = (int) totalUsers / 100 + 1;

		// TODO Auto-generated method stub

		ArrayList<String> doneThreads = new ArrayList<String>();

		for (int i = 1; i <= totalCalls; i++) {

			int current = i;

			Task<Parent> newThread = new Task<Parent>() {
				@Override
				public Parent call() throws JsonIOException, IOException {

					System.out.println("Thread " + current + " is running");
					// get users

						try {
							// String url =
							// "https://api.samanage.com/users.xml?email=minhta16@augustana.edu";
							String url = "https://api.samanage.com/users.xml?per_page=100&page=" + current;

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

							for (int i = 0; i < listOfUsers.getLength(); i++) {

								if (listOfUsers.item(i) instanceof Element) {
									Element user = (Element) listOfUsers.item(i);
									User newUser = new User();
									String name = getString("name", user);
									String ID = getString("id", user);
									String email = getString("email", user).toLowerCase();
									newUser.setName(name);
									newUser.setEmail(email);
									newUser.setDept(getString("name",
											(Element) user.getElementsByTagName("department").item(0)));
									newUser.setSite(
											getString("name", (Element) user.getElementsByTagName("site").item(0)));
									newUser.setID(ID);
									ArrayList<String> groupIdList = new ArrayList<String>();
									Element groupIdsElement = (Element) user.getElementsByTagName("group_ids").item(0);
									NodeList groupIds = groupIdsElement.getElementsByTagName("group_id");
									for (int j = 0; j < groupIds.getLength(); j++) {
										if (groupIds.item(j) instanceof Element) {
											String groupID = groupIds.item(j).getTextContent();
											groupIdList.add(groupID);
										}
									}
									newUser.setGroupID(groupIdList);
									users.put(email, newUser);
								}
								// System.out.print("Updating users with Thread: " + current + " ["
								// + (i + (curPage - 1) * 100) + "/" + totalUsers + "]\t\t\t\r");

							}
							conn.disconnect();
						} catch (Exception e) {
							e.printStackTrace();
						}
					return null;
				}
			};

			// method to set labeltext
			newThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent event) {
					System.err.println("DONE THREAD " + current);
					doneThreads.add("thread "+current);
					if (doneThreads.size() == totalCalls) {
						System.out.println("FINISH ALL");
					}

				}
			});
			Thread newUserThread = new Thread(newThread);
			newUserThread.start();
		}

		return users;

	}
	public static TreeMap<String, ArrayList<String>> getCategories(String userToken) throws IOException {
		/*
		 * curl -H "X-Samanage-Authorization: Bearer TOKEN" -H 'Accept:
		 * application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET
		 * https://api.samanage.com/categories.xml
		 */
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
				if (getString("parent_id", category).equals("")) {
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
	}

	public static ArrayList<String> getDepartments(String userToken) throws IOException {
		/*
		 * curl -H "X-Samanage-Authorization: Bearer TOKEN" -H 'Accept:
		 * application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET
		 * https://api.samanage.com/categories.xml
		 */
		boolean hasMore = true;
		int curPage = 1;
		ArrayList<String> deptList = new ArrayList<String>();
		while (hasMore) {
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
		}
		return deptList;

	}

	public static ArrayList<String> getSites(String userToken) throws IOException {
		/*
		 * curl -H "X-Samanage-Authorization: Bearer TOKEN" -H 'Accept:
		 * application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET
		 * https://api.samanage.com/categories.xml
		 */
		boolean hasMore = true;
		int curPage = 1;
		ArrayList<String> siteList = new ArrayList<String>();
		while (hasMore) {
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
		}
		return siteList;
	}

	// 408915 Software
	public static TreeMap<String, Software> getSoftwares(String userToken, String typeCode, String type)
			throws IOException {
		TreeMap<String, Software> softwareList = new TreeMap<String, Software>();
		// typeCode is for Category = Software, so that Software custom fields are there
		String url = "https://api.samanage.com/incidents.xml?per_page=1&page=1&category%5B%5D=" + typeCode;

		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setDoOutput(true);

		conn.setRequestMethod("GET");
		conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
		conn.setRequestProperty("Accept", ACCEPT_VERSION);
		conn.setRequestProperty("Content-Type", "text/xml");

		Element rootElement = documentFromOutput(conn);
		NodeList customFields = rootElement.getElementsByTagName("custom_fields_value");

		for (int i = 0; i < customFields.getLength(); i++) {
			if (customFields.item(i) instanceof Element) {
				Element customField = (Element) customFields.item(i);
				String name = getString("name", customField);
				if (name.equals(type)) {
					String[] softwares = getString("options", customField).split("<br>");
					for (String software : softwares) {
						softwareList.put(software, new Software(software));
					}
				}
			}

		}

		conn.disconnect();
		return softwareList;
	}

	public static TreeMap<String, Group> getGroups(String userToken) throws IOException {
		/*
		 * curl -H "X-Samanage-Authorization: Bearer TOKEN" -H 'Accept:
		 * application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET
		 * https://api.samanage.com/categories.xml
		 */
		String url = "https://api.samanage.com/groups.xml";

		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setDoOutput(true);

		conn.setRequestMethod("GET");
		conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
		conn.setRequestProperty("Accept", ACCEPT_VERSION);
		conn.setRequestProperty("Content-Type", "text/xml");

		Element rootElement = documentFromOutput(conn);
		NodeList groups = rootElement.getElementsByTagName("group");

		TreeMap<String, Group> groupsMap = new TreeMap<String, Group>();
		for (int i = 0; i < groups.getLength(); i++) {
			if (groups.item(i) instanceof Element) {
				Element group = (Element) groups.item(i);
				String name = getString("name", group);
				String groupId = getString("id", group);
				groupsMap.put(groupId, new Group(name, groupId));
			}

		}

		conn.disconnect();
		return groupsMap;
	}

	public static String getID(String userToken) throws IOException {
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
	}

	public static int getTotalElements(String userToken, String type) throws IOException {
		String url = "https://api.samanage.com/" + type + ".xml";

		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setDoOutput(true);

		conn.setRequestMethod("GET");
		conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
		conn.setRequestProperty("Accept", ACCEPT_VERSION);
//			conn.setRequestProperty("Content-Type", "text/xml");

		Element rootElement = documentFromOutput(conn);
		int totalEntries = 0;
		if (getString("total_entries", rootElement).equals("")) {
			NodeList nodes = rootElement.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node childNode = nodes.item(i);
				if (childNode instanceof Element) {
					totalEntries++;
				}
			}
		} else {
			totalEntries = Integer.parseInt(getString("total_entries", rootElement));
		}
		conn.disconnect();
		return totalEntries;
	}

	// HTML METHOD:
	// PUT

	public static void updateStateAndDept(String userToken, String incidentID, String state, String dept, String site,
			boolean notify) throws IOException {
		if (state == dept && dept == site) {
			return;
		} else if (state.isEmpty() && dept.isEmpty() && site.isEmpty()) {
			return;
		}
		if (state.equalsIgnoreCase("closed") && notify) {
			// to send out email notification
			updateStateAndDept(userToken, incidentID, "Resolved", dept, site, notify);
		}
		String url = "https://api.samanage.com/incidents/" + incidentID + ".xml";
		if (notify) {
			url += "?add_callbacks=true";
		}

		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setDoOutput(true);

		conn.setRequestMethod("PUT");
		conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
		conn.setRequestProperty("Accept", ACCEPT_VERSION);
		conn.setRequestProperty("Content-Type", "text/xml");

		String data = "<incident>";
		if (state != null && !state.isEmpty()) {
			data += " <state>" + state + "</state>";
		}
		if (site != null && !site.isEmpty()) {
			data += " <site><name>" + site + "</name></site>";
		}
		if (dept != null && !dept.isEmpty()) {
			data += " <department><name>" + dept + "</name></department>";
		}
		data += "</incident>";
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(data);
		out.close();

		new InputStreamReader(conn.getInputStream());
		conn.disconnect();
	}

	public static void updateIncidentWithTimeTrack(String userToken, String incidentName, String incidentID,
			String priority, String category, String subcategory, String description, String dueDate, String state,
			String assignee, String requester, String dept, String site, String software, boolean notify)
			throws IOException {
		SamanageRequests.updateIncident(userToken, incidentName, incidentID, priority, category, subcategory,
				description, dueDate, assignee, requester, software);
		// String incidentID = SamanageRequests.getID(userToken);
		ArrayList<TimeTrack> timeTracks = AppSession.getSession().getTimeTracks();
		for (TimeTrack track : timeTracks) {
			SamanageRequests.addTimeTrack(userToken, incidentID, track.getComment(), track.getUser().getID(),
					track.getTime());
		}
		SamanageRequests.updateStateAndDept(userToken, incidentID, state, dept, site, notify);

		// clear the UI
		AppSession.getSession().clearTrackedUsers();
	}

	public static void updateIncident(String userToken, String incidentName, String incidentID, String priority,
			String category, String subcategory, String description, String dueDate, String assignee, String requester,
			String software) throws IOException {
		String url = "https://api.samanage.com/incidents/" + incidentID + ".xml";

		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setDoOutput(true);

		conn.setRequestMethod("PUT");
		conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
		conn.setRequestProperty("Accept", ACCEPT_VERSION);
		conn.setRequestProperty("Content-Type", "text/xml");

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
		if (assignee.contains("@")) {
			data += " <assignee><email>" + assignee + "</email></assignee>";
		} else {
			data += " <assignee_id>" + assignee + "</assignee_id>";
		}
		if (!software.isEmpty()) {
			data += "<custom_fields_values>";
			data += "<custom_fields_value><name>Software</name>";
			data += "<value>" + software + "</value></custom_fields_value>";
			data += "</custom_fields_values>";
		}
		data += "</incident>";

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

	// OTHERS
	//

	protected static LocalDate toDate(String dateString) {
		if (dateString == "") {
			return null;
		} else {
			String date = dateString.split("T")[0];
			int yr = Integer.parseInt(date.split("-")[0]);
			int month = Integer.parseInt(date.split("-")[1]);
			int day = Integer.parseInt(date.split("-")[2]);
			return LocalDate.of(yr, month, day);
		}
	}

	// got from
	// https://stackoverflow.com/questions/4076910/how-to-retrieve-element-value-of-xml-using-java
	protected static String getString(String tagName, Element element) {
		return getString(tagName, element, 0);
	}

	protected static String getString(String tagName, Element element, int item) {
		NodeList list = element.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {
			NodeList subList = list.item(0).getChildNodes();

			if (subList != null && subList.getLength() > 0) {
				return subList.item(item).getNodeValue();
			}
		}

		return "";
	}

	private static ArrayList<TimeTrack> getTimeTracks(String userToken, String incidentId)
			throws IOException, SAXException, ParserConfigurationException {
		ArrayList<TimeTrack> timeTracks = new ArrayList<TimeTrack>();

		// String url =
		// "https://api.samanage.com/users.xml?email=minhta16@augustana.edu";
		String url = "https://api.samanage.com/incidents/" + incidentId + "/time_tracks.xml";

		URL obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		conn.setDoOutput(true);

		conn.setRequestMethod("GET");
		conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
		conn.setRequestProperty("Accept", ACCEPT_VERSION);
		conn.setRequestProperty("Content-Type", "text/xml");

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

		NodeList listOfTracks = rootElement.getElementsByTagName("time-track");

		for (int i = 0; i < listOfTracks.getLength(); i++) {

			if (listOfTracks.item(i) instanceof Element) {
				Element track = (Element) listOfTracks.item(i);
				TimeTrack newTrack = new TimeTrack(
						new User(getString("name", (Element) track.getElementsByTagName("creator").item(0)),
								getString("email", (Element) track.getElementsByTagName("creator").item(0))
										.toLowerCase(),
								getString("id", (Element) track.getElementsByTagName("creator").item(0))),
						Integer.parseInt(getString("minutes", track)), getString("name", track));
				timeTracks.add(newTrack);
			}

		}
		conn.disconnect();

		return timeTracks;
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