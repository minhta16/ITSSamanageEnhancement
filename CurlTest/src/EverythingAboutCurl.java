import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStreamWriter;

public class EverythingAboutCurl {
	public static void main(String[] args) {
		String userToken = "TUlOSFRBMTZAYXVndXN0YW5hLmVkdQ==:eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxNzUzMzI2LCJnZW5lcmF0ZWRfYXQiOiIyMDE5LTAzLTA2IDE5OjEzOjE5In0.DxvUav8KRxixHbAAMZw5n6Kq19mzOJCc58h2cd1uViFqELmhZ2aj7shKuqR-K6Z58K6BsCLdmP4-XpETCtksfg";
		newIncident(userToken);
//		addTimeTrack(userToken, 35961711, 30);
	}

	public static void newIncident(String userToken) {
		try {
			String url = "https://api.samanage.com/incidents.xml";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setDoOutput(true);

			conn.setRequestMethod("POST");
			conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
			conn.setRequestProperty("Accept", "application/vnd.samanage.v2.1+xml");
			conn.setRequestProperty("Content-Type", "text/xml");

			String data = "<incident>"
						+ " <name>Test</name>" + " <priority>Medium</priority>"
						+ " <requester><email>MINHTA16@augustana.edu</email></requester>"
						+ " <category><name>Meetings  (ITS use only)</name></category>"
						+ " <subcategory>"
						+ "      <name>Training/Workshops</name>" + " </subcategory>"
						+ " <description>Test curl-ing new incidents</description>"
						+ " <assignee><email>MINHTA16@augustana.edu</email></assignee>"
						+ "</incident>";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();

			new InputStreamReader(conn.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addTimeTrack(String userToken, int incidentID, int time) {

		try {
			String url = "https://api.samanage.com/incidents/" + incidentID + "/time_tracks.xml";

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setDoOutput(true);

			conn.setRequestMethod("POST");
			conn.setRequestProperty("X-Samanage-Authorization", "Bearer " + userToken);
			conn.setRequestProperty("Accept", "application/vnd.samanage.v2.1+xml");
			conn.setRequestProperty("Content-Type", "text/xml");

			String data = 	"<time_track>" + 
								"<name>slow</name>" + 
								"<creator_id>1753326</creator_id>" + 
								"<minutes_parsed>" + time + "</minutes_parsed>" + 
							"</time_track>";
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();

			new InputStreamReader(conn.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
