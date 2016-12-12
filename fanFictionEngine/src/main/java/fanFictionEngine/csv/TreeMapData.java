package fanFictionEngine.csv;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fanFictionEngine.database.DatabaseConnector;

public class TreeMapData {

	public static void getTreeMapData(long year) {

		try {

			// get country
			DatabaseConnector a = new DatabaseConnector();
			Connection con = a.connector();

			Statement userId = con.createStatement();
			String userIdQuery = String
					.format("select Distinct(country_name) from fanfictiondrg201610.user_profile_batch;");
			System.out.println(userIdQuery);
			userId.execute(userIdQuery);
			ResultSet userIdResultSet = userId.getResultSet();

			List<String> list = new ArrayList<String>();

			while (userIdResultSet.next()) {
				if (userIdResultSet.getString("country_name") != null
						&& !userIdResultSet.getString("country_name").equals("null")) {
					list.add(userIdResultSet.getString("country_name"));
				}
			}

			Iterator itr = list.iterator();

			int total = 0;
			while (itr.hasNext()) {
				Object country = itr.next();
				Statement userIDwithParamsStatement = con.createStatement();
				String userIDwithParams = String.format(
						"SELECT A.id as id FROM fanfictiondrg201610.user_profile_batch as A join fanfictiondrg201610.user as B ON A.ff_id = B.ff_id where A.country_name = '%s' AND A.join_date <= %d;",
						country, year);
				userIDwithParamsStatement.execute(userIDwithParams);
				ResultSet userIDwithParamsResult = userIDwithParamsStatement.getResultSet();

				int i = 0;
				Map<String, Integer> langCount = new HashMap<String, Integer>();
				while (userIDwithParamsResult.next()) {

					Statement langStatement = con.createStatement();
					String langParams = String.format(
							"SELECT B.name as lang, count(*) as num FROM fanfictiondrg201610.story as A JOIN fanfictiondrg201610.language as B on A.language_id = B.id where A.user_id = %d GROUP BY B.name ORDER BY num DESC;",
							userIDwithParamsResult.getInt("id"));
					langStatement.execute(langParams);
					ResultSet langResult = langStatement.getResultSet();

					while (langResult.next()) {
						String key = langResult.getString("lang");
						if (langCount.containsKey(key)) {
							langCount.put(key, langCount.get(key) + 1);
						} else {
							langCount.put(key, 1);
						}
						break;
					}
					i++;
					System.out.println("user - " + i + " - " + userIDwithParamsResult.getInt("id"));
				}
				total = total + i;
				System.out.print(i + " - " + country + " - " + year + "\n");
				System.out.print(langCount + "\n");
				int t = 0;
				for (String k : langCount.keySet()) {
					t = t + langCount.get(k);
				}
				System.out.println("Verify count  ----- " + t);

			}
			System.out.print("Total - " + total + "\n");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getData() {

		try {
			DatabaseConnector a = new DatabaseConnector();
			Connection con = a.connector();

			System.out.println("Preparing Data......");
			System.out.println(System.currentTimeMillis());
			Statement userId = con.createStatement();
			String userIdQuery = String.format(
					"SELECT A.country_name as country, D.name as language, count(distinct A.id) as number FROM fanfictiondrg201610.user_profile_batch as A join fanfictiondrg201610.user as B on A.ff_id = B.ff_id join fanfictiondrg201610.story as C on B.id = C.user_id join fanfictiondrg201610.language as D on C.language_id = D.id GROUP BY A.country_name, D.name;");
			System.out.println(userIdQuery);
			userId.execute(userIdQuery);
			ResultSet userIdResultSet = userId.getResultSet();

			String csvFile = "abc.csv";
			FileWriter writer = new FileWriter(csvFile);

			CSVUtils.writeLine(writer, Arrays.asList("id", "value"));
			CSVUtils.writeLine(writer, Arrays.asList("Users,"));

			System.out.println("SQL Data is Ready!!!!......");
			System.out.println(System.currentTimeMillis());

			Set<String> countryset = new HashSet<String>();

			while (userIdResultSet.next()) {
				if (userIdResultSet.getString("country") != null
						&& !userIdResultSet.getString("country").equals("null")) {
					String country = String.format("Users.%s", userIdResultSet.getString("country"));
					if (!countryset.contains(country)) {
						CSVUtils.writeLine(writer, Arrays.asList(String.format("%s,", country)));
					} else {
						String id = String.format("%s.%s", country, userIdResultSet.getString("language"));
						String value = String.format("%d", userIdResultSet.getInt("number"));
						CSVUtils.writeLine(writer, Arrays.asList(id, value));
					}
				} else {
					String country = "Users.Country Not Mentioned";
					if (!countryset.contains(country)) {
						CSVUtils.writeLine(writer, Arrays.asList(String.format("%s,", country)));
					} else {
						String id = String.format("%s.%s", country, userIdResultSet.getString("language"));
						String value = String.format("%d", userIdResultSet.getInt("number"));
						CSVUtils.writeLine(writer, Arrays.asList(id, value));
					}
				}
			}
			System.out.println("CSV is Ready!!!!......");
			System.out.println(System.currentTimeMillis());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {

		// String csvFile = "abc.csv";
		// FileWriter writer = new FileWriter(csvFile);
		//
		// CSVUtils.writeLine(writer, Arrays.asList("a", "b", "c", "d"));
		//
		// // custom separator + quote
		// CSVUtils.writeLine(writer, Arrays.asList("aaa", "bb,b", "cc,c"), ',',
		// '"');
		//
		// // custom separator + quote
		// CSVUtils.writeLine(writer, Arrays.asList("aaa", "bbb", "cc,c"), '|',
		// '\'');
		//
		// // double-quotes
		// CSVUtils.writeLine(writer, Arrays.asList("aaa", "bbb", "cc\"c"));
		//
		// writer.flush();
		// writer.close();

		getData();
	}
}
