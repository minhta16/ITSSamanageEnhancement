package application.data;

import java.util.ArrayList;

import javafx.scene.control.Button;

public class User {
	private String name;
	private String email;
	private int time;
	private String comment;
	private String dept;
	private String site;
	private transient Button removeBtn; //?


	private String ID;
	private ArrayList<String> groupID;
	
	public User() {
		name = "";
		email = "";
		ID = "";
		time = 0;
		comment = "";
		dept = "";
		site = "";
		groupID = new ArrayList<String>();
		removeBtn = new Button("X");
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

	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getID() {
		return ID;
	}

	public Button getRemoveBtn() {
		return removeBtn;
	}
	public void setRemoveBtn(Button removeBtn) {
		this.removeBtn = removeBtn;
	}
	public String toString() {
		return "User: " + name + " Email: " + email + " ID: " + ID;
	}
}
