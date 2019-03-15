package application.data;

public class User {
	private String name;
	private String email;
	private String ID;
	
	public User() {
		name = "";
		email = "";
		ID = "";
	}
	public User(String name, String email, String ID) {
		this.name = name;
		this.email = email;
		this.ID = ID;
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
