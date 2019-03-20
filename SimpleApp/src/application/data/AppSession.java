package application.data;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;

public class AppSession {
	private final String DATA_LOCATION = "./resources/bin/data.json";
	private static AppSession session = new AppSession();
	
	private String userToken;
	private String defaultDomain;
	private String defaultAssignee;
	private String defaultRequester;
	private User requesterInfo;
	private transient ArrayList<User> trackedUsers;
	private ArrayList<String> assigneeEmails;
	private ArrayList<String> states;
	private TreeMap<String, ArrayList<String>> categories;
	private ArrayList<String> departments;
	private ArrayList<String> sites;
	private ArrayList<String> priorities;
	private TreeMap<String, User> users;
	
	private AppSession() {
		userToken = "";
		defaultDomain = "";
		defaultAssignee = "";
		defaultRequester = "";
		requesterInfo = new User();
		trackedUsers = new ArrayList<User>();
		users = new TreeMap<String, User>();
		states = new ArrayList<String>();
		categories = new TreeMap<String, ArrayList<String>>();
		priorities = new ArrayList<String>();
		assigneeEmails = new ArrayList<String>();
		departments = new ArrayList<String>();
		sites = new ArrayList<String>();
	}
	private AppSession(String userToken) {
		this.userToken = userToken;
		defaultDomain = "";
		defaultAssignee = "";
		defaultRequester = "";
		requesterInfo = new User();
		trackedUsers = new ArrayList<User>();
		users = new TreeMap<String, User>();
		states = new ArrayList<String>();
		categories = new TreeMap<String, ArrayList<String>>();
		priorities = new ArrayList<String>();
		assigneeEmails = new ArrayList<String>();
		departments = new ArrayList<String>();
		sites = new ArrayList<String>();
	}
	
	public static AppSession getSession() {
		return session;
	}
	
	public void addTrackedUser(User user) throws JsonIOException, IOException {
		trackedUsers.add(user);
		saveData();
	}
	
	public TreeMap<String, ArrayList<String>> getCategories() {
		return categories;
	}
	
	public void setCategories(TreeMap<String, ArrayList<String>> categories) {
		this.categories = categories;
	}
	
	public boolean containTrackedUser(User user) {
		return trackedUsers.contains(user);
	}
	
	public boolean containTrackedUser(String email) {
		for (User user: trackedUsers) {
			if (user.getEmail().equalsIgnoreCase(email)) {
				return true;
			}
		}
		return false;
	}
	
	public TreeMap<String, User> getUsers() {
		return users;
	}
	
	public Set<String> getSavedEmails() {
		return users.keySet();
	}
	
	public ArrayList<String> getAssigneeEmails() {
		return assigneeEmails;
	}

	public ArrayList<String> getStates() {
		return states;
	}

	public ArrayList<String> getPriorities() {
		return priorities;
	}
	
	public void removeTrackedUser(String email) {
		for (int i = trackedUsers.size() - 1; i >= 0; i--) {
			if (trackedUsers.get(i).getEmail().equalsIgnoreCase(email)) {
				trackedUsers.remove(trackedUsers.get(i));
			}
		}
	}
	
	public ArrayList<User> getTrackedUsers() {
		return trackedUsers;
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
	public void clearTrackedUsers() {
		trackedUsers.clear();
	}
	
	public void setDefaultAssignee(String assignee) {
		defaultAssignee = assignee;
	}
	
	public String getDefaultAssignee() {
		if (defaultAssignee.contains("@")) {
			if (defaultAssignee.split("@")[1].substring(1).equalsIgnoreCase(defaultDomain)) {
				return defaultAssignee.split("@")[0];
			}
		}
		return defaultAssignee;
	}
	
	public void setDefaultRequester(String requester) {
		if (users.containsKey(toCorrectDomain(requester))) {
			defaultRequester = requester;
			updateDefaultRequesterData();
		}
	}
	
	public String getDefaultRequester() {
		if (defaultRequester.contains("@")) {
			if (defaultRequester.split("@")[1].substring(1).equalsIgnoreCase(defaultDomain)) {
				return defaultRequester.split("@")[0];
			}
		}
		return defaultRequester;
	}

	/**
	 * @return the requesterInfo
	 */
	public User getRequesterInfo() {
		return requesterInfo;
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
	
	public void updateDefaultRequesterData() {
		requesterInfo = users.get(toCorrectDomain(defaultRequester));
	}
	
	public boolean isUpToDate() {
		if (SamanageRequests.getTotalElements(userToken, "users") != users.size()) {
			return false;
		} else if (SamanageRequests.getTotalElements(userToken, "departments") != departments.size()) {
			return false;
		} else if (SamanageRequests.getTotalElements(userToken, "sites") != sites.size()) {
			return false;
		} else if (SamanageRequests.getTotalElements(userToken, "categories") != categories.size()) {
			return false;
		} else {
			return true;
		}
//				&& SamanageRequests.getTotalElements(userToken, users);
	}

	public void updateUsers() {
		if (SamanageRequests.getTotalElements(userToken, "users") != users.size()) {
			users = SamanageRequests.getAllUsers(userToken);
		}
	}
	
	public void updateDepts() {
		if (SamanageRequests.getTotalElements(userToken, "departments") != departments.size()) {
			departments = SamanageRequests.getDepartments(userToken);
		}
	}
	
	public void updateSites() {
		if (SamanageRequests.getTotalElements(userToken, "sites") != sites.size()) {
	    	sites = SamanageRequests.getSites(userToken);
		}
	}
	
	public void updateCategories() {
		if (SamanageRequests.getTotalElements(userToken, "categories") != categories.size()) {
			categories = SamanageRequests.getCategories(userToken);
		}

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
