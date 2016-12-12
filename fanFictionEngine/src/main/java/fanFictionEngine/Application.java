package fanFictionEngine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
/**
 * 
 * @author Niharika Sharma
 *
 */
public class Application {

	public static void main(String[] args) {
		System.out.println("Application Started");
		SpringApplication.run(Application.class, args);
	}
}