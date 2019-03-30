package application.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import javafx.scene.control.Button;

public class Incident {
	private String ID;
	private String number;
	private String state;
	private String title;
	private String priority;
	private String cat;
	private String subcat;
	private String assignee;
	private String requester;
	private String site;
	private String dept;
	private String desc;
	private LocalDate dueOn;

	private ArrayList<TimeTrack> trackedUsers;
	private int trackedUsersNum;
	private transient Button editBtn;
	
	public Incident() {
		ID = "";
		number = "";
		state = "";
		title = "";
		priority = "";
		cat = "";
		subcat = "";
		assignee = "";
		requester = "";
		site = "";
		dept = "";
		desc = "";
		trackedUsers = new ArrayList<TimeTrack>();
		setTrackedUsersNum(0);
		editBtn = new Button("Edit");
	}
	public Incident(String ID, String number, String state, String title, String priority, String cat
			, String subcat, String assignee, String requester, String site, String dept
			, String desc, LocalDate dueOn, ArrayList<TimeTrack> trackedUsers) {
		this.ID = ID;
		this.number = number;
		this.state = state;
		this.title = title;
		this.priority = priority;
		this.cat = cat;
		this.subcat = subcat;
		this.assignee = assignee;
		this.requester = requester;
		this.site = site;
		this.dept = dept;
		this.desc = desc;
		this.dueOn = dueOn;
		this.trackedUsers = trackedUsers;
		setTrackedUsersNum(trackedUsers.size());
		editBtn = new Button("Edit");
	}
	
	public String getID() {
		return ID;
	}
	
	/**
	 * @return the dueOn
	 */
	public LocalDate getDueOn() {
		return dueOn;
	}
	/**
	 * @param dueOn the dueOn to set
	 */
	public void setDueOn(LocalDate dueOn) {
		this.dueOn = dueOn;
	}
	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}
	/**
	 * @return the cat
	 */
	public String getCategory() {
		return cat;
	}
	/**
	 * @param cat the cat to set
	 */
	public void setCat(String cat) {
		this.cat = cat;
	}
	/**
	 * @return the subcat
	 */
	public String getSubcategory() {
		return subcat;
	}
	/**
	 * @param subcat the subcat to set
	 */
	public void setSubcat(String subcat) {
		this.subcat = subcat;
	}
	/**
	 * @return the assignee
	 */
	public String getAssignee() {
		return assignee;
	}
	/**
	 * @param assignee the assignee to set
	 */
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	/**
	 * @return the requester
	 */
	public String getRequester() {
		return requester;
	}
	/**
	 * @param requester the requester to set
	 */
	public void setRequester(String requester) {
		this.requester = requester;
	}
	/**
	 * @return the site
	 */
	public String getSite() {
		return site;
	}
	/**
	 * @param site the site to set
	 */
	public void setSite(String site) {
		this.site = site;
	}
	/**
	 * @return the dept
	 */
	public String getDept() {
		return dept;
	}
	/**
	 * @param dept the dept to set
	 */
	public void setDept(String dept) {
		this.dept = dept;
	}
	/**
	 * @return the trackedUsers
	 */
	public ArrayList<TimeTrack> getTrackedUsers() {
		return trackedUsers;
	}
	/**
	 * @param trackedUsers the trackedUsers to set
	 */
	public void setTrackedUsers(ArrayList<TimeTrack> trackedUsers) {
		this.trackedUsers = trackedUsers;
		updateTrackedUsersNum();
	}

	/**
	 * @return the desc
	 */
	public String getDescription() {
		return desc;
	}
	/**
	 * @return the cat
	 */
	public String getCat() {
		return cat;
	}
	/**
	 * @return the subcat
	 */
	public String getSubcat() {
		return subcat;
	}
	/**
	 * @return the editBtn
	 */
	public Button getEditBtn() {
		return editBtn;
	}
	
	public void setEditBtn(Button editBtn) {
		this.editBtn = editBtn;
	}
	public int getTrackedUsersNum() {
		return trackedUsersNum;
	}
	public void setTrackedUsersNum(int trackedUsersNum) {
		this.trackedUsersNum = trackedUsersNum;
	}
	
	public void updateTrackedUsersNum() {
		trackedUsersNum = trackedUsers.size();
	}
}
