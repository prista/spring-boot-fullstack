package com.amigoscode.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        // Given
        var customerRowMapper = new CustomerRowMapper();
        var resultSet = mock(ResultSet.class);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("Alex");
        when(resultSet.getString("email")).thenReturn("alex@gmail.com");
        when(resultSet.getInt("age")).thenReturn(19);
        // When
        var actual = customerRowMapper.mapRow(resultSet, 1);
        // Then
        var expected = new Customer(
                1L, "Alex", "alex@gmail.com", 19
        );
        assertThat(actual).isEqualTo(expected);
    }
}