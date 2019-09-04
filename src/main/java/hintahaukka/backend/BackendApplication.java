package hintahaukka.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;

@Controller
@SpringBootApplication
public class BackendApplication {
    
    @GetMapping("/")
    String home() {
      return "Hello World!!";
    }
    
    public static void main(String[] args) {
	SpringApplication.run(BackendApplication.class, args);
    }

}
