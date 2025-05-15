package demo.demo_ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoEcommerceApplication.class, args);
	}

}
