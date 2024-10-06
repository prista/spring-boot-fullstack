package com.amigoscode.customer;

import com.amigoscode.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        // Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );
        underTest.insertCustomer(customer);
        // When
        var actual = underTest.selectAllCustomers();
        // Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        var id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        Optional<Customer> actual = underTest.selectCustomerById(id);
        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        // Given
        var notExistedId = 0L;
        // When
        var actual = underTest.selectCustomerById(notExistedId);
        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void existsCustomerWithEmail() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var name = FAKER.name().fullName();
        var customer = new Customer(
                name,
                email,
                20
        );
        underTest.insertCustomer(customer);

        // When
        var actual = underTest.existsPersonWithEmail(email);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithEmailReturnsFalseWhenDoesNotExists() {
        // Given
        var notExistedEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        // When
        var actual = underTest.existsPersonWithEmail(notExistedEmail);
        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerWithId() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        var id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        var actual = underTest.existsPersonWithId(id);
        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithIdWillReturnFalseWhenIdNotPresent() {
        // Given
        var id = -1L;
        // When
        var actual = underTest.existsPersonWithId(id);
        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        var id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        underTest.deleteCustomerById(id);
        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomerName() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        var id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var nameUpdated = "foo";

        var update = new Customer();
        update.setId(id);
        update.setName(nameUpdated);

        // When
        underTest.updateCustomer(update);
        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(nameUpdated); // change
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerEmail() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        var id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        var emailUpdated = "foo@bar.com";
        var update = new Customer();
        update.setId(id);
        update.setEmail(emailUpdated);

        underTest.updateCustomer(update);
        // Then
        var actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(emailUpdated); // change
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerAge() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        var id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        var ageUpdated = 100;
        var update = new Customer();
        update.setId(id);
        update.setAge(ageUpdated);

        underTest.updateCustomer(update);
        // Then
        var actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getAge()).isEqualTo(ageUpdated); // change
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
        });
    }

    @Test
    void willUpdateAllPropertiesCustomer() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        var id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        var update = new Customer();
        update.setId(id);
        update.setAge(22);
        update.setEmail(UUID.randomUUID().toString());
        update.setName("foo");

        underTest.updateCustomer(update);
        // Then
        var actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValue(update);
    }

    @Test
    void willNotUpdateWhenNotingToUpdate() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        var id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        // When
        var update = new Customer();
        update.setId(id);

        underTest.updateCustomer(update);
        // Then
        var actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(email);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }
}