package com.example;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DailyPaymentsAggregatorIO {

	private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	// Implement main method, relevant variables for source data files, and name the necessary methods 
	// for reading the files (to be implemented later as helper functions)
	public static void main(String[] args) {
		String paymentsFile = "payments.txt";
		String currencyRatesFile = "currency_rates.txt";

		// Possible refactoring to use JAR will require to read files from resources directory instead of direct path
		// InputStream paymentsStream = DailyPaymentsAggregatorIO.class.getClassLoader().getResourceAsStream("payments.txt");
		// InputStream currencyRatesStream = DailyPaymentsAggregatorIO.class.getClassLoader().getResourceAsStream("currency_rates.txt");
		
		// A list of payment objects aggregated from the payments data file using the readPayments method.
		List<Payment> paymentsList = readPayments(paymentsFile);
		// A mapping of the exchange rates from the currency rates data file using the readExchangeRates method
		Map<String, Double> exchangeRatesMap = readCurrencyExchangeRates(currencyRatesFile);

		aggregateDailyPayments(paymentsList, exchangeRatesMap);

	}

	//  Implement an individual standard Java IO implementation with BufferedReader for each files (the payments and exchange rates file). 
	// Implement readPayments method
	private static List<Payment> readPayments(String fileName) {
		List<Payment> payments = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			// Using the Buffered Reader to read the payments file line by line and then split each into sections using a ';'. 
			String line;
		
			while ((line = br.readLine()) != null) {
				// Organize into sections using the split method.
				String[] sections = line.split(";");

				if (sections.length == 4) {
					LocalDateTime transactionDateTime = LocalDateTime.parse(sections[0], DATE_TIME_FORMAT);
					String companyName = sections[1];
					String transactionCurrency = sections[2];
					double transactionAmount = Double.parseDouble(sections[3]);
					// Create new payment object with constructor and relevant attributes
					payments.add(new Payment(transactionDateTime, companyName, transactionCurrency, transactionAmount));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return payments;

	}

// Implement the readCurrencyExchangeRates method (Read from an input file of dummy data)
	private static Map<String, Double> readCurrencyExchangeRates(String fileName) {

		Map<String, Double> ratesMap = new HashMap<String, Double>();

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
		// Same process of reading the file line by line and delimiting the sections with a ';'
		String line;
		
		while ((line = br.readLine()) != null) {
			String[] sections = line.split(";");

			if (sections.length == 4) {
				String sourceCurrency = sections[1];
				String targetCurrency = sections[2];
				// Standardize the key syntax for the exchange currencies:
				String exchangeCurrenciesKey = sourceCurrency + "-" + targetCurrency;
				Double exchangeRate = Double.parseDouble(sections[3]);

				// Populate/update the rates map with the formatted currencies key and the value for the rate for the pair: 
				ratesMap.put(exchangeCurrenciesKey, exchangeRate);
			}
		} 

	} catch (IOException e) {
		e.printStackTrace();
} 

return ratesMap;

}

// Implement the actual aggregator method here:
// This covers quite a few key insights/requirements as articulated in the problem statement (document). 
// Highest EUR value (for a single payment)
// Lowest EUR value (for a single payment)
// Outstanding amounts per company in EUR
// Transaction volume in EUR
// Outstanding Amounts per currency

// ***Note: If EUR conversion rate is not available, this must result in N.A. for respective metrics. 
// 

	private static void aggregateDailyPayments(List<Payment> paymentsList, Map<String, Double> exchangeRatesMap) {
		// Set up variables for the required values included in the aggregation 

		double highestValueEUR = Double.NEGATIVE_INFINITY;
		double lowestValueEUR = Double.POSITIVE_INFINITY;
		double transactionVolume = 0;  

		Map<String, Double> outstandingPerCompany = new HashMap<>();
		Map<String, Double> outstandingPerCurrency = new HashMap<>();

		// Now iterate through the paymentsList and assemble the data needed for the daily aggregation.

		for (Payment payment : paymentsList) {
			// convert it to EUR if necessary
			double valueInEUR = convertToEUR(payment.transactionCurrency, payment.transactionAmount, exchangeRatesMap);

			// Check to ensure the value in EUR is real. Otherwise handle for this scenario.
			// Essentially, handle missing conversion rates
			if (Double.isNaN(valueInEUR)) {
				System.out.printf("Warning: No exchange rate available for %s to EUR. Transactions in %s will not be converted.%n",
				payment.transactionCurrency, payment.transactionCurrency);
				continue;
			}
	
			highestValueEUR = Math.max(highestValueEUR, valueInEUR);
			lowestValueEUR = Math.min(lowestValueEUR, valueInEUR);
			transactionVolume += Math.abs(valueInEUR); 
	
		// Update the maps for outstanding balance per company and outstanding balance per currency by using the getOrDefault map method:
			outstandingPerCompany.put(payment.companyName, 
				outstandingPerCompany.getOrDefault(payment.companyName, 0.0) + valueInEUR);
	
			outstandingPerCurrency.put(payment.transactionCurrency, 
				outstandingPerCurrency.getOrDefault(payment.transactionCurrency, 0.0) + payment.transactionAmount);
		} 

		System.out.println("\n========= Daily Payment Aggregation Results =========");
		// System.out.println("Highest EUR value: " + highestValueEUR);
		// System.out.println("Lowest EUR value: " + lowestValueEUR);
	// Printing reformatted to 2 decimal places for readability:
		System.out.printf("Highest EUR value: %.2f EUR%n", highestValueEUR);
		System.out.printf("Lowest EUR value: %.2f EUR%n", lowestValueEUR);
		System.out.println("\n=============================================");
		
		
		// Print Outstanding Amounts per Company
    System.out.println("\n Outstanding amounts per company:");
    if (outstandingPerCompany.isEmpty()) {
        System.out.println("No company transactions available!");
    } else {
        outstandingPerCompany.forEach((companyName, outstandingAmount) -> 
            System.out.printf("   - %s: %.2f EUR%n", companyName, outstandingAmount));
    }


		System.out.println("\n=============================================");
		// System.out.println("Transaction volume in EUR: " + transactionVolume);
		System.out.printf("Transaction volume in EUR: %.2f EUR%n", transactionVolume);

		// Print Outstanding Amounts per Currency
		System.out.println("\n Outstanding amounts per currency:");
		if (outstandingPerCurrency.isEmpty()) {
			System.out.println("No outstanding amounts found!");
		} else {
			outstandingPerCurrency.forEach((currency, outstandingAmount) -> {
				if (currency.equals("EUR")) {
					// EUR should be printed normally without conversion
					System.out.printf("   - %s: %.2f%n", currency, outstandingAmount);
				} else {
					// Check if a conversion exists
					boolean conversionExists = exchangeRatesMap.containsKey(currency + "-EUR");
					String eurEquivalent = conversionExists ? String.format("(%.2f EUR)", convertToEUR(currency, outstandingAmount, exchangeRatesMap)) : "";
		
					System.out.printf("   - %s: %.2f %s%n", currency, outstandingAmount, eurEquivalent);
				}
			});
		}
		System.out.println("\n=====================================================");
	}


	// Helper function to convert to EUR:

	public static double convertToEUR(String currency, Double amount, Map<String, Double>exchangeRatesMap) {
		if (currency.equals("EUR")) {
			return amount;
		}

		String currencyPairKey = currency + "-EUR";
		// Ternary operator to return conversion value according to exchange rate if currency pair key exists in the map.
		return exchangeRatesMap.containsKey(currencyPairKey)? amount * exchangeRatesMap.get(currencyPairKey) : Double.NaN;
	}

}
