package application.test;

import application.data.SamanageRequests;

public class Test {
	public static void main(String[] args) {
		String userToken = "TUlOSFRBMTZAYXVndXN0YW5hLmVkdQ\u003d\u003d:eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxNzUzMzI2LCJnZW5lcmF0ZWRfYXQiOiIyMDE5LTAzLTA2IDE5OjEzOjE5In0.DxvUav8KRxixHbAAMZw5n6Kq19mzOJCc58h2cd1uViFqELmhZ2aj7shKuqR-K6Z58K6BsCLdmP4-XpETCtksfg";
		System.err.println(SamanageRequests.getCategories(userToken).keySet());
	}
	

}
