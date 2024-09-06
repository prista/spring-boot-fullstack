package com.amigoscode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping("/")
    public GreetResponse greet() {
        return new GreetResponse("Hello!");
    }

    record GreetResponse(String greet) {
    }

//    class GreetResponse {
//        private final String greet;
//        public GreetResponse(String greet) {
//            this.greet = greet;
//        }
//        public String getGreet() {
//            return greet;
//        }
//        @Override
//        public String toString() {
//            return "GreetResponse{" +
//                    "greet='" + greet + '\'' +
//                    '}';
//        }
//        @Override
//        public boolean equals(final Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            final GreetResponse that = (GreetResponse) o;
//            return Objects.equals(greet, that.greet);
//        }
//        @Override
//        public int hashCode() {
//            return Objects.hashCode(greet);
//        }
//    }
}
