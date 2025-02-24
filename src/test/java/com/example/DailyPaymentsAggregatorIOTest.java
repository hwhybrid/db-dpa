package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class DailyPaymentsAggregatorIOTest {

    @Test
    void testConvertToEUR_ValidConversion() {
        // Arrange
        Map<String, Double> exchangeRates = new HashMap<>();
        exchangeRates.put("USD-EUR", 0.9);

        // Act
        double result = DailyPaymentsAggregatorIO.convertToEUR("USD", 100.0, exchangeRates);

        // Assert
        assertEquals(90.0, result, 0.001);
    }

    @Test
    void testConvertToEUR_NoExchangeRate() {
        Map<String, Double> exchangeRates = new HashMap<>();
        double result = DailyPaymentsAggregatorIO.convertToEUR("GBP", 100.0, exchangeRates);
        assertTrue(Double.isNaN(result));
    }

    @Test
    void testConvertToEUR_EURtoEUR() {
        Map<String, Double> exchangeRates = new HashMap<>();
        double result = DailyPaymentsAggregatorIO.convertToEUR("EUR", 100.0, exchangeRates);
        assertEquals(100.0, result);
    }
}
