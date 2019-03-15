package application.test;

import application.data.AppSession;
import application.data.SamanageRequests;

public class Test {
	private AppSession currentSession = AppSession.getSession();
	
	public static void main(String[] args) {
		SamanageRequests.newIncident("", "test", "test");
	}
	

}
