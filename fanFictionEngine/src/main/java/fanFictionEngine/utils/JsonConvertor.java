package fanFictionEngine.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import fanFictionEngine.database.DatabaseConnector;

public class JsonConvertor {

	public static void userFavNetworkData() {

		try {
			DatabaseConnector a = new DatabaseConnector();
			Connection con = a.connector();
			Statement stmt = con.createStatement();
			
			JSONObject json = new JSONObject();
			JSONArray nodes = new JSONArray();
			//JSONArray links = new JSONArray();
			

			// for all users - may be employee filters
			stmt.execute("select DISTINCT ff_id from fanfictiondrg201610.user_favorite_author_batch limit 10;");

			ResultSet rs = null;
			rs = stmt.getResultSet();
			while (rs.next()) {
				int id = rs.getInt("ff_id");
				
				//get fan fiction id
				String ff_user = String.format("select id from fanfictiondrg201610.user where ff_id=%d;", id);
				Statement stmt1 = con.createStatement();
				ResultSet result = stmt1.executeQuery(ff_user);
				int db_id = 0;
				try {
					while (result.next()) {
						db_id = result.getInt("id");
						System.out.println("db - "+db_id);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					continue;
				}
				if(db_id == 0){
					continue;
				}
				
				
				//get story data
				String ff_story = String.format("select * from fanfictiondrg201610.story where user_id=%d;", db_id);
				Statement stmt2 = con.createStatement();
				ResultSet result_story = stmt2.executeQuery(ff_story);
				
				HashMap<Integer, Integer> listLang = new HashMap<Integer, Integer>();
				
				try {
					while (result_story.next()) {
						int key = result_story.getInt("language_id");
						System.out.println(key);
						if(listLang.containsKey(key)){
							listLang.put(key, listLang.get(key)+1);							
						} else{
							listLang.put(key, 1);														
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
					continue;
				}
				System.out.println(listLang);

				//analyze 
				int langId = 0;
				Map.Entry<Integer, Integer> maxEntry = null;

				for (Map.Entry<Integer, Integer> entry : listLang.entrySet())
				{
				    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
				    {
				        maxEntry = entry;
				        langId = maxEntry.getKey();
				    }
				}

				
				//append json
				JSONObject nodeJson = new JSONObject();
				nodeJson.put("id", id);
				nodeJson.put("group", langId);
				nodes.put(nodeJson);
				System.out.println(id);
			}
			json.put("nodes", nodes);
			System.out.println(json);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

//	public static void main(String args[]) {
//		userFavNetworkData();
//	}
}
