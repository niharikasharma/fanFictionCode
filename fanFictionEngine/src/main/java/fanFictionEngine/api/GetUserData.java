package fanFictionEngine.api;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GetUserData {
	Logger logger = LoggerFactory.getLogger(GetUserData.class);

	@RequestMapping(value = "/userData", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String getData(@RequestBody String json) {

		JSONObject request = new JSONObject();
		System.out.println(json);
		try {

			if (json == null || json.equals("")) {
				return request.put("Status", "No user requested").toString();
			} else {

				JSONObject object = new JSONObject(json);

				if (object.has("user1")) {
					
					if (object.has("user2") && object.has("user3") && object.has("user4") && object.has("user5")) {

						String[] users = { object.getString("user1"), object.getString("user2"),
								object.getString("user3"), object.getString("user4"), object.getString("user5") };
						request = fanFictionEngine.utils.GetUserData.userFavNetworkData(users);

					} else if (object.has("user2") && object.has("user3") && object.has("user4")) {

						String[] users = { object.getString("user1"), object.getString("user2"),
								object.getString("user3"), object.getString("user4") };
						request = fanFictionEngine.utils.GetUserData.userFavNetworkData(users);

					} else if (object.has("user2") && object.has("user3")) {

						String[] users = { object.getString("user1"), object.getString("user2"),
								object.getString("user3") };
						request = fanFictionEngine.utils.GetUserData.userFavNetworkData(users);

					} else if (object.has("user2")) {

						String[] users = { object.getString("user1"), object.getString("user2") };
						request = fanFictionEngine.utils.GetUserData.userFavNetworkData(users);

					} else {

						String[] users = { object.getString("user1") };
						request = fanFictionEngine.utils.GetUserData.userFavNetworkData(users);

					}

				} else {
					return request.put("Status", "No user requested").toString();
				}

			}
			return request.toString();
		} catch (Exception e) {
			logger.error("Error Description", e);
			return request.put("Status", "Failure").toString();
		}
	}

}
