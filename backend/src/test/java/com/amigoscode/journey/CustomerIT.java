package com.amigoscode.journey;

import com.amigoscode.customer.Customer;
import com.amigoscode.customer.CustomerRegistrationRequest;
import com.amigoscode.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIT {

    @Autowired
    private WebTestClient webTestClient;

    private static final Random RANDOM = new Random();
    private static final String CUSTOMER_URI = "/api/v1/customers";

    @Test
    void canRegisterACustomer() {
        // create registration request
        var faker = new Faker();
        var fakerName = faker.name();
        var name = fakerName.fullName();
        var email = fakerName.lastName() + UUID.randomUUID() + "@amigoscode.com";
        var age = RANDOM.nextInt(1, 100);
        var request = new CustomerRegistrationRequest(
                name, email, age
        );
        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // get all customers

        var allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();
        // make sure that customer is present
        var expectedCustomer = new Customer(
                name, email, age
        );

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        var id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        expectedCustomer.setId(id);
        // get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        // create registration request
        var faker = new Faker();
        var fakerName = faker.name();
        var name = fakerName.fullName();
        var email = fakerName.lastName() + UUID.randomUUID() + "@amigoscode.com";
        var age = RANDOM.nextInt(1, 100);
        var request = new CustomerRegistrationRequest(
                name, email, age
        );
        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // get all customers

        var allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();


        var id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // delete customer
        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
        // get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomerName() {
        // create registration request
        var faker = new Faker();
        var fakerName = faker.name();
        var name = fakerName.fullName();
        var email = fakerName.lastName() + UUID.randomUUID() + "@amigoscode.com";
        var age = RANDOM.nextInt(1, 100);
        var request = new CustomerRegistrationRequest(
                name, email, age
        );
        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // get all customers

        var allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();


        var id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // update customer all properties
        var newName = "nameUPD";
        var updRequest = new CustomerUpdateRequest(
                newName, null, null
        );
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get updated customer by id
        var updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON) // клиент ожидает ответ в формате JSON.
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();
        var expectedCustomer = new Customer(
                id, newName, email, age
        );

        assertThat(updatedCustomer).isEqualTo(expectedCustomer);
    }
}
