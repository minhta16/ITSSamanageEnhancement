package application.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;

public class AppSession {
	private static AppSession session = new AppSession();
	
	private String userToken;
	private transient ArrayList<User> trackedUsers;
	
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
	
	public void clearTrackedUsers() {
		trackedUsers.clear();
	}
	
	public void loadData(String fileName) throws FileNotFoundException {
		JsonReader reader = new JsonReader(new FileReader(fileName));
		Gson gson = new Gson();
		session = gson.fromJson(reader, AppSession.class);
	}
	
	public void saveData(String fileName) throws JsonIOException, IOException {
		Gson gson = new Gson();
		FileWriter fw = new FileWriter(fileName);
		gson.toJson(session, fw);
		fw.close();
	}
}
