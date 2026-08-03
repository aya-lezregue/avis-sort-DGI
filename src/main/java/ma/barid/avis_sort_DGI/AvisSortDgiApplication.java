package ma.barid.avis_sort_DGI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AvisSortDgiApplication {
	public static void main(String[] args) {
		SpringApplication.run(AvisSortDgiApplication.class, args);
	}
}
