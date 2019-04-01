package application.data;

import java.util.ArrayList;

import javafx.scene.control.Button;

public class User {
	private String name;
	private String email;
	private String dept;
	private String site;


	private String ID;
	private ArrayList<String> groupID;
	
	public User() {
		name = "";
		email = "";
		ID = "";
		dept = "";
		site = "";
		groupID = new ArrayList<String>();
	}

	public User(String name, String email, String ID) {
		this.name = name;
		this.email = email;
		this.ID = ID;
	}

	
	/**
	 * @return the groupID
	 */
	public ArrayList<String> getGroupID() {
		return groupID;
	}

	/**
	 * @param groupID the groupID to set
	 */
	public void setGroupID(ArrayList<String> groupID) {
		this.groupID = groupID;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setID(String iD) {
		ID = iD;
	}
	
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}

	
	public String getID() {
		return ID;
	}

	public String toString() {
		return "User: " + name + " Email: " + email + " ID: " + ID;
	}
}
