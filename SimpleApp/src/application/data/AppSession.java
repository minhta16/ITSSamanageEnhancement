package application.data;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

	private TreeMap<String, Group> groups;
	private ArrayList<String> assigneeEmails;
	private ArrayList<String> states;
	private TreeMap<String, ArrayList<String>> categories;
	private ArrayList<String> departments;
	private ArrayList<String> sites;
	private ArrayList<String> priorities;
	private TreeMap<String, User> users;

	private Map<String, Runnable> checkFlags = new HashMap<String, Runnable>();
	
	
	
	
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
		users = new TreeMap<String, User>();
		states = new ArrayList<String>();
		categories = new TreeMap<String, ArrayList<String>>();
		priorities = new ArrayList<String>();
		assigneeEmails = new ArrayList<String>();
		departments = new ArrayList<String>();
		sites = new ArrayList<String>();
		groups = new TreeMap<String, Group>();
	}
	
	public static AppSession getSession() {
		return session;
	}
	
	public void addTrackedUser(TimeTrack track) throws JsonIOException, IOException {
		timeTracks.add(track);
		saveData();
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
		for (TimeTrack track: timeTracks) {
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
	
	public void loadData() throws IOException {
		FileReader fd = new FileReader(DATA_LOCATION);
		JsonReader reader = new JsonReader(fd);
		Gson gson = new Gson();
		session = gson.fromJson(reader, AppSession.class);
		System.err.println(userToken);
		fd.close();
	}
	
	public void saveData() throws JsonIOException, IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		FileWriter fw = new FileWriter(DATA_LOCATION);
		gson.toJson(session, fw);
		fw.close();
	}
	
	public boolean hasGroup(String groupName) {
		for (String group: groups.keySet()) {
			if (groups.get(group).getName().toLowerCase().equals(groupName.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<String> getGroupNames() {
		ArrayList<String> groupNames = new ArrayList<String>();
		for (String group: groups.keySet()) {
			groupNames.add(groups.get(group).getName());
		}
		return groupNames;
	}
	
	public String getGroupId(String groupName) {
		for (String group: groups.keySet()) {
			if (groups.get(group).getName().equals(groupName)) {
				return group;
			}
		}
		return "";
	}
	
	public void updateDefaultRequesterData() {
		requesterInfo = users.get(toCorrectDomain(defaultRequester));
	}
	
	public void updateTodayIncidents() {
		TreeMap<String, Incident> newIncidents = new TreeMap<String, Incident>();
		for (String groupID: users.get(toCorrectDomain(userEmail)).getGroupID()) {
			 newIncidents.putAll(SamanageRequests.getIncidents(userToken, groupID));
		}
		currentIncidents = newIncidents;
	}
	
	public String getUpdatePrompt() {
		String prompt = "";
		int dbUsers = SamanageRequests.getTotalElements(userToken, "users");
		int dbDepts = SamanageRequests.getTotalElements(userToken, "departments");
		int dbSites = SamanageRequests.getTotalElements(userToken, "sites");
		int dbCats = SamanageRequests.getTotalElements(userToken, "categories");
		int dbGroups = SamanageRequests.getTotalElements(userToken, "groups");
		
		if (dbUsers != users.size()) {
			prompt += "Local Users: " + users.size() + ", Database Users: " + dbUsers +"\n"; 
			String s1 = "Update Users";
			Runnable r1 = () -> updateUsers();
			checkFlags.put(s1,  r1);
		} if (dbDepts != departments.size()) {
			prompt += "Local Depts: " + departments.size() + ", Database Depts: " + dbDepts +"\n"; 
			String s2 = "Update Depts";
			Runnable r2 = () -> updateDepts();
			checkFlags.put(s2,  r2);
		} if (dbSites != sites.size()) {
			prompt += "Local Sites: " + sites.size() + ", Database Sites: " + dbSites +"\n"; 
			String s3 = "Update Sites";
			Runnable r3 = () -> updateSites();
			checkFlags.put(s3,  r3);
		} if (dbCats != categories.size()) {
			prompt += "Local Categories: " + categories.size() + ", Database Categories: " + dbCats +"\n"; 
			String s4 = "Update Categories";
			Runnable r4 = () -> updateCategories();
			checkFlags.put(s4,  r4);
		} if (dbGroups != groups.size()) {
			prompt += "Local Groups: " + groups.size() + ", Database Groups: " + dbGroups; 
			String s5 = "Update Groups";
			Runnable r5 = () -> updateGroups();
			checkFlags.put(s5,  r5);
		}
		return prompt;
//				&& SamanageRequests.getTotalElements(userToken, users);
	}
	
	public Map<String, Runnable> getCheckFlags() {
		return checkFlags;
	}

	public void updateUsers() {
		if (SamanageRequests.getTotalElements(userToken, "users") != users.size()) {
			System.err.println("Updating user...");
			users = SamanageRequests.getAllUsers(userToken);
		}
	}
	
	public void updateDepts() {
		if (SamanageRequests.getTotalElements(userToken, "departments") != departments.size()) {
			System.err.println("Updating depts...");
			departments = SamanageRequests.getDepartments(userToken);
		}
	}
	
	public void updateSites() {
		if (SamanageRequests.getTotalElements(userToken, "sites") != sites.size()) {
			System.err.println("Updating sites...");
	    	sites = SamanageRequests.getSites(userToken);
		}
	}
	
	public void updateCategories() {
		if (SamanageRequests.getTotalElements(userToken, "categories") != categories.size()) {
			System.err.println("Updating categories...");
			categories = SamanageRequests.getCategories(userToken);
		}

	}
	public void updateGroups() {
		if (SamanageRequests.getTotalElements(userToken, "groups") != groups.size()) {
			System.err.println("Updating group...");
			groups = SamanageRequests.getGroups(userToken);
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
		}
		else {
			return email;
		}
	}
}
