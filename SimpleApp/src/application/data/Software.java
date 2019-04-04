package application.data;

public class Software {
	private String name;
	private String id;
	private String vendor;
	
	public Software() {
		
	}
	
	public Software (String name) {
		this(name, "", "");
	}
	public Software(String name, String id, String vendor) {
		this.name = name;
		this.id = id;
		this.vendor = vendor;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the vendor
	 */
	public String getVendor() {
		return vendor;
	}

	/**
	 * @param vendor the vendor to set
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	
	
}
