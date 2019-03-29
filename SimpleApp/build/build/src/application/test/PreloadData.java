package application.test;

import java.io.IOException;

import application.data.AppSession;
import application.data.SamanageRequests;

public class PreloadData {
	public static void main(String[] args) throws IOException {
		String userToken = "TUlOSFRBMTZAYXVndXN0YW5hLmVkdQ\u003d\u003d:eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxNzUzMzI2LCJnZW5lcmF0ZWRfYXQiOiIyMDE5LTAzLTA2IDE5OjEzOjE5In0.DxvUav8KRxixHbAAMZw5n6Kq19mzOJCc58h2cd1uViFqELmhZ2aj7shKuqR-K6Z58K6BsCLdmP4-XpETCtksfg";
		
		AppSession.getSession().loadData();
		AppSession.getSession().setUsers(SamanageRequests.getAllUsers(userToken));
//		AppSession.getSession().setDepartments(SamanageRequests.getDepartments(userToken));
//		AppSession.getSession().setSites(SamanageRequests.getSites(userToken));
		AppSession.getSession().saveData();
	}
	

}
