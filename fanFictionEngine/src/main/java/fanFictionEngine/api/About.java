package fanFictionEngine.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class About {
	Logger logger = LoggerFactory.getLogger(About.class);
	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public @ResponseBody
	String aboutMethod() {
		return "OK";
	}
}