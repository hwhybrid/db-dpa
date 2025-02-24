# Daily Payments Aggregator

## Overview
DailyPaymentsAggregator is a **Java application** that processes daily payment transactions and currency exchange rates to compute key financial aggregates.

### Features:
- Highest & Lowest EUR value transactions
- Outstanding amounts per company (EUR)
- Transaction volume (EUR)
- Outstanding amounts per currency (converted to EUR when possible)

### Technologies Used:
- **Java 21**
- **Maven** (for build & dependency management)
- **JUnit 5** (for unit testing)

---

## How to Run the Application

### **1. Prerequisites**
Before running the application, ensure you have:
-  Java 21+ installed (`java -version`)
-  Maven installed (`mvn -version`)

---

### **2️. Ensure Input Files Are Present**
**Before running the program, ensure the following files are in the root directory:*
- Payment transactions file:  
  `payments.txt ` 
- Currency exchange rates file: 
`currency_rates.txt`

### **3️. Build the Application**
To compile and package the project, navigate to the project folder and run:

`mvn clean package`

- This generates the runnable JAR file in the target/ folder.

### **4. Run the JAR**

After building, execute the program:

`java -jar target/DailyPaymentsAggregator-1.0-SNAPSHOT-jar-with-dependencies.jar` 

Ensure payments.txt and currency_rates.txt are in the same directory before running.

## How to Run the Unit Tests: ##
To verify the application with unit tests, run:

`mvn test`

## Key points to remember:
- If a currency exchange rate is missing, transactions in that currency are not converted.

- The program must be run from the same directory as payments.txt and currency_rates.txt.
  
- No additional dependencies are required beyond those in pom.xml.