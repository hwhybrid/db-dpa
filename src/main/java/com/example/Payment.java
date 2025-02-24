package com.example;

import java.time.LocalDateTime;

public class Payment {

	LocalDateTime transactionDateTime;
	String companyName;
	String transactionCurrency;
	double transactionAmount;

	
	public Payment(LocalDateTime transactionDateTime, String companyName, String transactionCurrency,
			double transactionAmount) {
		this.transactionDateTime = transactionDateTime;
		this.companyName = companyName;
		this.transactionCurrency = transactionCurrency;
		this.transactionAmount = transactionAmount;
	}

}
