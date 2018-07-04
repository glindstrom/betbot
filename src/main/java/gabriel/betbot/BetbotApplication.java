package gabriel.betbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BetbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(BetbotApplication.class, args);
    }       
}
