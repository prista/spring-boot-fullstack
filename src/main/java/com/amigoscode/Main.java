package com.amigoscode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping("/")
    public GreetResponse greet(@RequestParam(
            value = "name", required = false) String name) {
        String greetMessage = name == null || name.isBlank() ? "Hello" : "Hello " + name;
        final GreetResponse response = new GreetResponse(
                greetMessage,
                List.of("Java", "Golang", "Javascript"),
                new Person("Hello!", 28, 30_000));
        return response;
    }

    record GreetResponse(
            String greet,
            List<String> favProgrammingLanguages,
            Person person) {
    }

    record Person(String name, int age, double savings) {}
}
