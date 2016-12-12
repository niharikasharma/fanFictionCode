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

public class GetLevelUserData {

	public static JSONObject userFavNetworkData(String[] userNames) {

		JSONObject result = new JSONObject();

		try {
			DatabaseConnector a = new DatabaseConnector();
			Connection con = a.connector();

			String userName = "";
			int length = userNames.length;

			for (int i = 0; i < userNames.length; i++) {

				System.out.println(userNames[i]);
				userName = userName + "'" + userNames[i] + "'";
				if (i != length - 1) {
					userName = userName + ", ";
				}
			}
			System.out.println("usernames - " + userName);

			// get db id and ff id
			Statement userId = con.createStatement();
			String userIdQuery = String.format("select * from fanfictiondrg201610.user where name IN (%s);", userName);
			System.out.println(userIdQuery);
			userId.execute(userIdQuery);
			ResultSet userIdResultSet = userId.getResultSet();

			String id = "";
			String ff_id = "";
			Map<Integer, String> usersMap = new HashMap<Integer, String>();
			Map<Integer, String> ffUsersMap = new HashMap<Integer, String>();

			while (userIdResultSet.next()) {

				id = id + String.format("%d", userIdResultSet.getInt("id"));
				ff_id = ff_id + String.format("%d", userIdResultSet.getInt("ff_id"));
				usersMap.put(userIdResultSet.getInt("id"), userIdResultSet.getString("name"));
				ffUsersMap.put(userIdResultSet.getInt("ff_id"), userIdResultSet.getString("name"));
				if (!userIdResultSet.isLast()) {
					id = id + ", ";
					ff_id = ff_id + ", ";
				}

			}

			if (id.equals("") || ff_id.equals("")) {
				return result.put("Status", "No Such User");
			}

			System.out.println("ids - " + id);
			System.out.println("ff_ids - " + ff_id);
			System.out.println("ids - " + usersMap);
			System.out.println("ff_ids - " + ffUsersMap);

			// get associated users

			JSONArray nodes = new JSONArray();
			JSONArray links = new JSONArray();

			Statement associatedUser = con.createStatement();
			String associatedUserQuery = String
					.format("select * from fanfictiondrg201610.user_favorite_author_batch where ff_id IN (%s);", ff_id);
			System.out.println(associatedUserQuery);
			associatedUser.execute(associatedUserQuery);
			ResultSet associatedUserResultSet = associatedUser.getResultSet();

			while (associatedUserResultSet.next()) {

				JSONObject link = new JSONObject();

				link.put("source", ffUsersMap.get(associatedUserResultSet.getInt("ff_id")));
				link.put("target", associatedUserResultSet.getString("favorite_name"));
				links.put(link);
				if (!ffUsersMap.containsKey(associatedUserResultSet.getInt("favorite_ff_id"))) {
					ffUsersMap.put(associatedUserResultSet.getInt("favorite_ff_id"),
							associatedUserResultSet.getString("favorite_name"));
				}
			}
			
			result.put("links", links);
			System.out.println(result);
			String storyIds = "";
			for (Integer storyId : ffUsersMap.keySet()) {
				storyIds = storyIds + ", " + storyId;
			}
			storyIds = storyIds.substring(2, storyIds.length());

			Statement userIdforstory = con.createStatement();
			String userIdforstoryQuery = String.format("select * from fanfictiondrg201610.user where ff_id IN (%s);",
					storyIds);
			System.out.println(userIdforstoryQuery);
			userIdforstory.execute(userIdforstoryQuery);
			ResultSet userIdforstoryResultSet = userIdforstory.getResultSet();

			while (userIdforstoryResultSet.next()) {
				usersMap.put(userIdforstoryResultSet.getInt("id"), userIdforstoryResultSet.getString("name"));
			}

			// see if reader included or not
			for (Integer key : ffUsersMap.keySet()) {
				if (!usersMap.containsValue(ffUsersMap.get(key))) {
					usersMap.put(key + 1000000000, ffUsersMap.get(key));
					System.out.println(ffUsersMap.get(key));
				}
			}

			for (Integer storyIdno : usersMap.keySet()) {

				// get story data
				String ff_story = String.format("select * from fanfictiondrg201610.story where user_id IN (%s);",
						storyIdno);
				Statement stmt2 = con.createStatement();
				ResultSet result_story = stmt2.executeQuery(ff_story);

				HashMap<Integer, Integer> listLang = new HashMap<Integer, Integer>();
				HashMap<Integer, Integer> mapFandom = new HashMap<Integer, Integer>();
				HashMap<Integer, Integer> mapRating = new HashMap<Integer, Integer>();

				int storyCount = 0;
				int words = 0;
				int fav = 0;
				int review = 0;
				int follower = 0;
				if (result_story.next()) {
					try {
						do {

							// language
							int language = result_story.getInt("language_id");
							if (listLang.containsKey(language)) {
								listLang.put(language, listLang.get(language) + 1);
							} else {
								listLang.put(language, 1);
							}

							// fandom
							int fandom = result_story.getInt("fandom_id");
							if (mapFandom.containsKey(fandom)) {
								mapFandom.put(fandom, mapFandom.get(fandom) + 1);
							} else {
								mapFandom.put(fandom, 1);
							}

							// rating
							int rating = result_story.getInt("rating_id");
							// System.out.println(rating);
							if (mapRating.containsKey(rating)) {
								mapRating.put(rating, mapRating.get(rating) + 1);
							} else {
								mapRating.put(rating, 1);
							}

							// story
							storyCount++;

							// words
							words = words + result_story.getInt("words");

							// fav.
							fav = fav + result_story.getInt("favorites");

							// review
							review = review + result_story.getInt("reviews");

							// follower
							follower = follower + result_story.getInt("followers");

						} while (result_story.next());
					} catch (SQLException e) {
						e.printStackTrace();
					}
					// System.out.println(mapFandom);

					// analyze language
					int langId = 0;
					Map.Entry<Integer, Integer> maxEntry = null;

					for (Map.Entry<Integer, Integer> entry : listLang.entrySet()) {
						if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
							maxEntry = entry;
							langId = maxEntry.getKey();
						}
					}

					// analyze fandom
					int fandomId = 0;
					Map.Entry<Integer, Integer> maxEntryFamdom = null;

					for (Map.Entry<Integer, Integer> entry : mapFandom.entrySet()) {
						if (maxEntryFamdom == null || entry.getValue().compareTo(maxEntryFamdom.getValue()) > 0) {
							maxEntryFamdom = entry;
							fandomId = maxEntryFamdom.getKey();
						}
					}

					// analyse ratings
					int ratingId = 0;
					Map.Entry<Integer, Integer> maxEntryRating = null;

					for (Map.Entry<Integer, Integer> entry : mapRating.entrySet()) {
						if (maxEntryRating == null || entry.getValue().compareTo(maxEntryRating.getValue()) > 0) {
							maxEntryRating = entry;
							ratingId = maxEntryRating.getKey();
						}
					}

					// append json
					JSONObject nodeJson = new JSONObject();
					nodeJson.put("id", usersMap.get(storyIdno));
					nodeJson.put("lang", Language.values()[langId].toString());

					// get fandom
					String fandom = String.format("select * from fanfictiondrg201610.fandom where id = %d;", fandomId);
					Statement fstmt2 = con.createStatement();
					ResultSet result_fandom = fstmt2.executeQuery(fandom);
					String category = "unknown";
					String fandomName = "unknown";
					while (result_fandom.next()) {
						fandomName = result_fandom.getString("name");
						try {
							category = Category.values()[result_fandom.getInt("category_id")].toString();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					nodeJson.put("fandom", fandomName);
					nodeJson.put("category", category);
					nodeJson.put("rating", Rating.values()[ratingId].toString());
					nodeJson.put("ratingDescription", Rating.values()[ratingId].getDescription(ratingId));
					nodeJson.put("story", storyCount);
					nodeJson.put("type", "writer");
					nodeJson.put("avgWords", words / storyCount);
					nodeJson.put("ttlWords", words);
					nodeJson.put("avgFav", fav / storyCount);
					nodeJson.put("ttlFav", fav);
					nodeJson.put("avgReview", review / storyCount);
					nodeJson.put("ttlReview", review);
					nodeJson.put("avgFollowers", follower / storyCount);

					String storyRange = String.format("%d", storyCount);
					if (storyCount <= 10) {
						storyRange = "1-10";
					} else if (11 <= storyCount && storyCount <= 50) {
						storyRange = "11-50";
					} else if (51 <= storyCount && storyCount <= 100) {
						storyRange = "51-100";
					} else if (101 <= storyCount && storyCount <= 150) {
						storyRange = "101-150";
					} else if (151 <= storyCount && storyCount <= 250) {
						storyRange = "151-251";
					} else if (251 <= storyCount && storyCount <= 350) {
						storyRange = "251-350";
					} else if (351 <= storyCount) {
						storyRange = "350+";
					}

					nodeJson.put("storyRange", storyRange);

					nodes.put(nodeJson);
				}

				else {
					// append json
					JSONObject nodeJson = new JSONObject();
					nodeJson.put("id", usersMap.get(storyIdno));
					nodeJson.put("lang", "None");
					nodeJson.put("fandom", "None");
					nodeJson.put("category", "None");
					nodeJson.put("rating", "None");
					nodeJson.put("ratingDescription", "None");
					nodeJson.put("story", 0);
					nodeJson.put("storyRange", 0);
					nodeJson.put("type", "reader");
					nodeJson.put("avgWords", 0);
					nodeJson.put("ttlWords", 0);
					nodeJson.put("avgFav", 0);
					nodeJson.put("ttlFav", 0);
					nodeJson.put("avgReview", 0);
					nodeJson.put("ttlReview", 0);
					nodeJson.put("avgFollowers", 0);

					nodes.put(nodeJson);
					System.out.println("No data" + usersMap.get(storyIdno));
				}

			}
			result.put("nodes", nodes);
			System.out.println(result);
			return result;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result.put("Status", "Failure");
		}

	}

	public Object maxCalculator(HashMap<Integer, Integer> map) {
		Object id = null;
		Map.Entry<Integer, Integer> maxEntry = null;
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
				maxEntry = entry;
				id = maxEntry.getKey();
			}
		}
		return id;
	}

	// zoom in, filter, detail on demand

	public static void main(String args[]) {

		// Most Followers Naruto
		// 1. Lucillia
		// 2. Seto's Darkness
		// 3. ohwhatsherface
		// 4. N.V.9
		// 5. xXSakura-Hime-SamaXx
		// 6. spazzgirl
		
		// String[] user = { "Lucillia", "Seto''s Darkness", "ohwhatsherface", "N.V.9", "xXSakura-Hime-SamaXx", "spazzgirl" };

		// Most Followers Twilight
		// 1. LyricalKris
		// 2. ericastwilight
		// 3. airedalegirl1
		// 4. Dragons-Twilight1992
		// 5. Mrstrentreznor
		// 6. MeetTheMateContest
		
		//String[] user = { "LyricalKris", "ericastwilight", "airedalegirl1", "Dragons-Twilight1992", "Mrstrentreznor", "MeetTheMateContest" };

		// Most Followers Harry Potter
		// 1. Lomonaaeren
		// 2. RZZMG
		// 3. Teddylonglong
		// 4. NeonDomino
		// 5. articcat621
		// 6. DobbyRocksSocks
		// String[] user = { "Lomonaaeren", "RZZMG", "Teddylonglong", "NeonDomino", "articcat621", "DobbyRocksSocks" };


		//Followers:
		//Lucillia
		//DebsTheSlytherinSnapefan
		//LyricalKris
		//Tsume Yuki
		//robst
		// String[] user = { "Lucillia", "DebsTheSlytherinSnapefan", "LyricalKris", "Tsume Yuki", "robst" };

		// Favorites:
		// Lomonaaeren
		// Lucillia
		// robst
		// DebsTheSlytherinSnapefan
		// Cheryl Dyson
		//
		// Written Most stories total:
		// moonswirl
		// Kat Lee formerly Pirate Turner
		// reminiscent-afterthought
		// nfchan
		// Hawki
		// Marie S Zachary
		// Pricat
		// String[] user = { "moonswirl", "Kat Lee formerly Pirate Turner", "reminiscent-afterthought", "nfchan", "Hawki", "Marie S Zachary", "Pricat" };

		// Reviews:
		// LyricalKris
		// Nolebucgrl
		// Lomonaaeren
		// Rochelle Allison
		// Colubrina
		String[] user = { "sri ffn" };

			
		System.out.println(userFavNetworkData(user));
	}
}
