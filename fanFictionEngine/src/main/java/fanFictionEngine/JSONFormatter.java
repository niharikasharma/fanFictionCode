package fanFictionEngine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONFormatter {

	@SuppressWarnings({ "unchecked" })
	public static Object uploadFile() throws Exception {
		JSONParser parser = new JSONParser();

		try {
			Object obj = parser.parse(new FileReader("src/main/resources/iso_639-2.json"));

			JSONObject newJson = new JSONObject();
			JSONArray newJsonArray = new JSONArray();

			JSONObject jsonObject = (JSONObject) obj;

			Object countryJSON = parser.parse(new FileReader("src/main/resources/getCountry.json"));

			JSONObject jsonObjectCountry = (JSONObject) countryJSON;
			JSONObject countries = (JSONObject) jsonObjectCountry.get("countries");

			JSONArray country = (JSONArray) countries.get("country");
			Iterator<JSONObject> iterator = country.iterator();
			while (iterator.hasNext()) {
				JSONObject country_meta = (JSONObject) iterator.next();
				String lang = (String) country_meta.get("languages");
				String continent = (String) country_meta.get("continent");
				String countryName = (String) country_meta.get("countryName");

				String[] arrayLang = lang.split(",");
				for (int i = 0; i < arrayLang.length; i++) {
					String languageValue = "";
					if (jsonObject.containsKey(arrayLang[i])) {
						JSONObject language = (JSONObject) jsonObject.get(arrayLang[i]);
						JSONArray lang_int = (JSONArray) language.get("int");
						languageValue = (String) lang_int.get(0);
					} else {
						System.out.println("language not found - " + arrayLang[i]);
						continue;
					}
					JSONObject level1 = new JSONObject();
					level1.put("continent", continent);
					level1.put("languageCode", arrayLang[i]);
					level1.put("countryName", countryName);
					level1.put("language", languageValue);
					newJsonArray.add(level1);
				}
			}

			System.out.println(newJson);
			return newJson.put("country", newJsonArray);


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

//	public static void main(String[] args) {
//		try {
//			uploadFile();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
