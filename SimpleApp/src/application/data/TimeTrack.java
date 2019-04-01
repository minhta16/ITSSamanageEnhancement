package application.data;

import javafx.scene.control.Button;

public class TimeTrack {
	private User user;
	private int time;
	private String comment;
	private transient Button removeBtn;
	
	public TimeTrack() {
		removeBtn = new Button("X");
	}
	
	public TimeTrack(User user, int time, String comment) {
		this.user = user;
		this.time = time;
		this.comment = comment;
		removeBtn = new Button("X");
	}
	
	public String getName() {
		return user.getName();
	}
	
	public String getEmail() {
		return user.getEmail();
	}
	/**
	 * @return the email
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param email the email to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	public Button getRemoveBtn() {
		return removeBtn;
	}
	public void setRemoveBtn(Button removeBtn) {
		this.removeBtn = removeBtn;
	}
	
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
}
