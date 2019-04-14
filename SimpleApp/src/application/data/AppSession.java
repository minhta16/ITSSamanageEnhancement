package application.data;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;

public class AppSession {
	private static final String DATA_LOCATION = "./resources/bin/data.json";
	private static AppSession session = new AppSession();

	private String userEmail;
	private String userToken;
	private String defaultDomain;
	private String defaultAssignee;
	private String defaultRequester;
	private User requesterInfo;
	private boolean defaultAutoUpdateCheckChoice;
	private boolean dtbUpdateCheckAskAgainCheckBox;

	private transient TreeMap<String, Incident> currentIncidents;
	private transient ArrayList<TimeTrack> timeTracks;
	private transient IncidentEditType editType;

	private TreeMap<String, Incident> templates;
	private transient ArrayList<TimeTrack> templateTimeTracks;

	private transient TreeMap<String, Group> groups;
	private transient TreeMap<String, Software> softwares;
	private transient TreeMap<String, ArrayList<String>> categories;
	private transient ArrayList<String> departments;

	private ArrayList<String> assigneeEmails;
	private ArrayList<String> states;
	private ArrayList<String> sites;
	private ArrayList<String> priorities;
	private TreeMap<String, User> users;

	private transient Map<String, Runnable> updateCheckboxList;
	private String dateOfLastSystemUpdate;

	private AppSession() {
		this("");
	}

	private AppSession(String userToken) {
		userEmail = "";
		this.userToken = userToken;
		defaultDomain = "";
		defaultAssignee = "";
		defaultRequester = "";
		defaultAutoUpdateCheckChoice = false;
		dtbUpdateCheckAskAgainCheckBox = false;
		requesterInfo = new User();
		currentIncidents = new TreeMap<String, Incident>();
		timeTracks = new ArrayList<TimeTrack>();
		templateTimeTracks = new ArrayList<TimeTrack>();
		users = new TreeMap<String, User>();
		states = new ArrayList<String>();
		categories = new TreeMap<String, ArrayList<String>>();
		priorities = new ArrayList<String>();
		assigneeEmails = new ArrayList<String>();
		departments = new ArrayList<String>();
		sites = new ArrayList<String>();
		groups = new TreeMap<String, Group>();
		softwares = new TreeMap<String, Software>();
		updateCheckboxList = new HashMap<String, Runnable>();
		//updateCheckboxList.put("All", () -> updateAll());
		dateOfLastSystemUpdate = "";
	}

	/**
	 * @return the templateTimeTracks
	 */
	public ArrayList<TimeTrack> getTemplateTimeTracks() {
		return templateTimeTracks;
	}

	/**
	 * @param templateTimeTracks the templateTimeTracks to set
	 */
	public void setTemplateTimeTracks(ArrayList<TimeTrack> templateTimeTracks) {
		this.templateTimeTracks = templateTimeTracks;
	}

	public void removeTemplateTimeTrackByEmail(String email) {
		for (int i = templateTimeTracks.size() - 1; i >= 0; i--) {
			if (templateTimeTracks.get(i).getEmail().equalsIgnoreCase(email)) {
				templateTimeTracks.remove(templateTimeTracks.get(i));
			}
		}
	}

	/**
	 * @return the templates
	 */
	public TreeMap<String, Incident> getTemplates() {
		return templates;
	}

	/**
	 * @param templates the templates to set
	 */
	public void setTemplates(TreeMap<String, Incident> templates) {
		this.templates = templates;
	}

	public static AppSession getSession() {
		return session;
	}

	public void addTrackedUser(TimeTrack track) throws JsonIOException, IOException {
		timeTracks.add(track);
	}

	public TreeMap<String, ArrayList<String>> getCategories() {
		return categories;
	}

	public void setCategories(TreeMap<String, ArrayList<String>> categories) {
		this.categories = categories;
	}

	public boolean containTimeTrack(TimeTrack track) {
		return timeTracks.contains(track);
	}

	public boolean containTrackedUser(String email) {
		for (TimeTrack track : timeTracks) {
			if (track.getEmail().equalsIgnoreCase(email)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the currentIncidents
	 */
	public TreeMap<String, Incident> getCurrentIncidents() {
		return currentIncidents;
	}

	/**
	 * @param currentIncidents the currentIncidents to set
	 */
	public void setCurrentIncidents(TreeMap<String, Incident> currentIncidents) {
		this.currentIncidents = currentIncidents;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(TreeMap<String, User> users) {
		this.users = users;
	}

	public TreeMap<String, User> getUsers() {
		return users;
	}

	public Set<String> getSavedEmails() {
		return users.keySet();
	}

	public ArrayList<String> getAssignees() {
		ArrayList<String> assignees = new ArrayList<String>();
		assignees.addAll(getGroupNames());
		assignees.addAll(assigneeEmails);
		return assignees;
	}

	public ArrayList<String> getStates() {
		return states;
	}

	public ArrayList<String> getPriorities() {
		return priorities;
	}

	public void removeTimeTrackByEmail(String email) {
		for (int i = timeTracks.size() - 1; i >= 0; i--) {
			if (timeTracks.get(i).getEmail().equalsIgnoreCase(email)) {
				timeTracks.remove(timeTracks.get(i));
			}
		}
	}

//	public void removeTimeTrackByEmailOnServer(String email) {
//		SamanageRequests.add
//	}

	public ArrayList<TimeTrack> getTimeTracks() {
		return timeTracks;
	}

	public void setUserEmail(String email) {
		if (users.containsKey(toCorrectDomain(email))) {
			userEmail = toShortDomain(email);
		}
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getUserToken() {
		return userToken;
	}

	public String getDefaultDomain() {
		return defaultDomain;
	}

	public void setDefaultDomain(String domain) {
		defaultDomain = domain;
	}

	public boolean getDefaultAutoUpdateCheck() {
		return defaultAutoUpdateCheckChoice;
	}

	public void setDefaultAutoUpdateCheck(Boolean choice) {
		defaultAutoUpdateCheckChoice = choice;
	}

	public boolean getDtbUpdateCheckAskAgainCheckBox() {
		return dtbUpdateCheckAskAgainCheckBox;
	}

	public void setdtbUpdateCheckAskAgainCheckBox(Boolean choice) {
		dtbUpdateCheckAskAgainCheckBox = choice;
	}

	public ArrayList<String> getDepartments() {
		return departments;
	}

	public void setDepartments(ArrayList<String> departments) {
		this.departments = departments;
	}

	public ArrayList<String> getSites() {
		return sites;
	}

	public void setSites(ArrayList<String> sites) {
		this.sites = sites;
	}

	/**
	 * @return the editType
	 */
	public IncidentEditType getEditType() {
		return editType;
	}

	/**
	 * @param editType the editType to set
	 */
	public void setEditType(IncidentEditType editType) {
		this.editType = editType;
	}

	public void clearTrackedUsers() {
		timeTracks.clear();
	}

	public void setDefaultAssignee(String assignee) {
		if (users.containsKey(toCorrectDomain(assignee))) {
			defaultAssignee = toShortDomain(assignee);
		}
	}

	public String getDefaultAssignee() {
		return toShortDomain(defaultAssignee);
	}

	public void setDefaultRequester(String requester) {
		if (users.containsKey(toCorrectDomain(requester))) {
			defaultRequester = toShortDomain(requester);
			updateDefaultRequesterData();
		}
	}

	public String getDefaultRequester() {
		return toShortDomain(defaultRequester);
	}

	/**
	 * @return the requesterInfo
	 */
	public User getRequesterInfo() {
		return requesterInfo;
	}

	/**
	 * @return the requesterInfo
	 */
	public boolean hasUser(String email) {
		return users.containsKey(toCorrectDomain(email.toLowerCase()));
	}

	/**
	 * @return the requesterInfo
	 */
	public User getRequesterInfo(String email) {
		return users.get(toCorrectDomain(email.toLowerCase()));
	}

	/**
	 * @return the groups
	 */
	public TreeMap<String, Group> getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(TreeMap<String, Group> groups) {
		this.groups = groups;
	}
	
	public String getDateOfLastSystemUpdate() {
		return dateOfLastSystemUpdate;
	}
	
	public void setDateOfLastSystemUpdate(String date) {
		this.dateOfLastSystemUpdate = date;
	}

	public void loadData() throws IOException {
		FileReader fd = new FileReader(DATA_LOCATION);
		JsonReader reader = new JsonReader(fd);
		Gson gson = new Gson();
		session = gson.fromJson(reader, AppSession.class);
		fd.close();
	}

	public void saveData() throws JsonIOException, IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		FileWriter fw = new FileWriter(DATA_LOCATION);
		gson.toJson(session, fw);
		fw.close();
	}

	public boolean hasGroup(String groupName) {
		for (String group : groups.keySet()) {
			if (groups.get(group).getName().toLowerCase().equals(groupName.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<String> getGroupNames() {
		ArrayList<String> groupNames = new ArrayList<String>();
		for (String group : groups.keySet()) {
			groupNames.add(groups.get(group).getName());
		}
		return groupNames;
	}

	public TreeMap<String, Software> getSoftwares() {
		return softwares;
	}

	public String getGroupId(String groupName) {
		for (String group : groups.keySet()) {
			if (groups.get(group).getName().equals(groupName)) {
				return group;
			}
		}
		return "";
	}

	public void updateDefaultRequesterData() {
		requesterInfo = users.get(toCorrectDomain(defaultRequester));
	}

	public void updateListIncidents(LocalDate from, LocalDate to) {
		currentIncidents = SamanageRequests.getIncidents(userToken, from, to);
	}

	public int getTotalTime() {
		int totalTime = 0;
		for (String key : currentIncidents.keySet()) {
			Incident incident = currentIncidents.get(key);
			for (TimeTrack track : incident.getTimeTracks()) {
				totalTime += track.getTime();
			}

		}
		return totalTime;
	}

	public int getCatAndSubcatSize() {
		int size = categories.size();
		for (String key : categories.keySet()) {
			size += categories.get(key).size();
		}
		return size;
	}

	public String getUpdatePrompt() {
		String prompt = "";
		int dbUsers = SamanageRequests.getTotalElements(userToken, "users");
		int dbSites = SamanageRequests.getTotalElements(userToken, "sites");

		if (dbUsers != users.size()) {
			prompt += "Local Users: " + users.size() + ", Database Users: " + dbUsers + "\n";
			updateCheckboxList.put("Update Users", () -> updateUsers());
		}
		if (dbSites != sites.size()) {
			prompt += "Local Sites: " + sites.size() + ", Database Sites: " + dbSites + "\n";
			String s3 = "Update Sites";
			Runnable r3 = () -> updateSites();
			updateCheckboxList.put(s3, r3);
		}

		updateEasyStuff();
		return prompt;
//				&& SamanageRequests.getTotalElements(userToken, users);
	}

	public Map<String, Runnable> updateCheckboxList() {
		return updateCheckboxList;
	}

	public void updateUsersMultiThreads(int thread) {
		System.err.println("Loading users multithread...");
		users = SamanageRequests.getAllUsersMultiThreads(userToken, thread);
	}
	
	public void updateUsers() {
		System.err.println("Loading users...");
		users = SamanageRequests.getAllUsers(userToken);
	}

	public void updateSites() {
		System.err.println("Loading sites...");
		sites = SamanageRequests.getSites(userToken);
	}

	public void updateDepts() {
		System.err.println("Loading departments...");
		departments = SamanageRequests.getDepartments(userToken);
	}
	
	public void updateCategories() {
		System.err.println("Loading categories...");
		categories = SamanageRequests.getCategories(userToken);

	}

	public void updateGroups() {
		System.err.println("Loading groups...");
		groups = SamanageRequests.getGroups(userToken);

	}

	public void updateSoftwares() {
		System.err.println("Loading softwares...");
		softwares = SamanageRequests.getSoftwares(userToken, "408915", "Software");

	}

	public void updateEasyStuff() {
		updateDepts();
		updateCategories();
		updateGroups();
		updateSoftwares();
	}
	
	public void updateAll() {
		updateUsers();
		updateDepts();
		updateSites();
		updateCategories();
		updateGroups();
		updateSoftwares();
	}

	public void addTemplate(String id, Incident template) {
		templates.put(id, template);
		try {
			saveData();
		} catch (JsonIOException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		templateTimeTracks.clear();
	}

	public void removeTemplate(String id) {
		templates.remove(id);
		try {
			saveData();
		} catch (JsonIOException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String toShortDomain(String email) {
		if (email.contains("@")) {
			if (email.split("@")[1].equalsIgnoreCase(defaultDomain)) {
				return email.split("@")[0];
			}
		}
		return email;
	}

	private String toCorrectDomain(String email) {
		if (!email.contains("@") && !email.trim().equals("")) {
			return email + "@" + defaultDomain;
		} else {
			return email;
		}
	}
}
