package fanFictionEngine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;



public class StoryTime {

	public static void uploadFile() throws Exception {

		try {
			JSONParser parser = new JSONParser();
			System.out.println("-- start --");

			  JSONArray a = (JSONArray) parser.parse(new FileReader("src/main/resources/story.json"));			
			

			System.out.println("-- end --");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//
//	public static void main(String[] args) {
//		try {
//			uploadFile();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
