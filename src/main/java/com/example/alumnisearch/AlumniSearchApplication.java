package com.example.alumnisearch;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Alumni Search API (PhantomBuster)",
                version = "1.0",
                description = "RESTful API to search LinkedIn alumni profiles using PhantomBuster and persist retrieved data into PostgreSQL/H2.",
                contact = @Contact(name = "Aditi Sharma", email = "aditisharma0032@gmail.com")
        )
)
public class AlumniSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlumniSearchApplication.class, args);
    }
}
