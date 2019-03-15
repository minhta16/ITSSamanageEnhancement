package application.data;

import java.util.ArrayList;

public class AppSession {
	private static final AppSession session = new AppSession();
	
	private String userToken;
	private ArrayList<User> trackedUsers;
	
	private AppSession() {
		userToken = "";
		trackedUsers = new ArrayList<User>();
	}
	private AppSession(String userToken) {
		this.userToken = userToken;
		trackedUsers = new ArrayList<User>();
	}
	
	public static AppSession getSession() {
		return session;
	}
	
	public void addTrackedUser(User user) {
		trackedUsers.add(user);
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
	
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	
	public String getUserToken() {
		return userToken;
	}
}
