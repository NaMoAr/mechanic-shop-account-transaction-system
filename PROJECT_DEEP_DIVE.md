# Mechanic Shop Database Platform: Project Deep Dive

## Elevator Pitch

A modular Java/JDBC mechanic-shop application for account tracking and transaction processing on PostgreSQL. The system applies object-oriented design principles and established patterns (Repository, Service Layer, Strategy, Factory) to improve maintainability, data integrity, and extensibility.

## Problem Context

The original system bundled UI flow, validation, SQL construction, and business rules in a single class. This led to:

- Tight coupling between domain logic and persistence.
- High change risk for even small feature updates.
- Limited integrity checks around transaction lifecycle events.
- Security risk from string-concatenated SQL in write flows.

## What I Built

### 1) Layered Modular Architecture

- `MechanicShop`: thin orchestration and menu controller.
- `AppConfig` + `InputReader`: runtime config and controlled input boundaries.
- `TransactionProcessingService`, `AccountTrackingService`, `ReportService`: business logic services.
- `TransactionRepository`, `AccountRepository` + SQL implementations: persistence abstraction.
- Domain models (`CustomerAccount`, `TransactionRecord`, request DTOs): clear data contracts.

### 2) Account Tracking Module

- Customer account summary with:
  - Open service request count.
  - Total billed amount.
- Transaction ledger with chronological lifecycle events:
  - `REQUEST_OPENED`
  - `REQUEST_CLOSED`

### 3) Transaction Processing Module

- Open request workflow with validation:
  - Customer exists.
  - Car exists.
  - Customer owns the car.
  - Odometer and IDs are positive.
- Close request workflow with validation:
  - Request exists and is not already closed.
  - Mechanic exists.
  - Base bill and IDs are positive.
- Billing policy abstraction via Strategy + Factory:
  - Standard billing.
  - Experience-based discount strategy.

### 4) Data Safety and Security Improvements

- Replaced write-path raw SQL concatenation with parameterized `PreparedStatement` queries.
- Removed hardcoded credentials by introducing environment-driven DB config.
- Added operation-level error handling to keep the app running after invalid actions.

## Key Design Decisions

- Chose repository interfaces to isolate SQL from business logic and make persistence swappable.
- Chose service layer boundaries around domain workflows (account tracking vs transaction processing).
- Used strategy/factory for billing so policy changes do not ripple through orchestration code.
- Added ownership as a first-class menu operation to align runtime workflows with schema constraints.

## Tech Stack

- Java (OOP, layered architecture)
- JDBC
- PostgreSQL
- SQL

## How To Run

1. Compile:

```bash
javac -cp .:postgresql-42.2.23.jar src/*.java -d bin
```

2. Configure:

```bash
export MECHANICSHOP_DB_NAME=carRepair
export MECHANICSHOP_DB_PORT=5432
export MECHANICSHOP_DB_USER=postgres
export MECHANICSHOP_DB_PASSWORD=your_password_here
```

3. Execute:

```bash
java -cp .:postgresql-42.2.23.jar:bin MechanicShop
```

## Files To Reference

- `/Users/narani/Downloads/Mechanics_Shop_Database-main/src/MechanicShop.java`
- `/Users/narani/Downloads/Mechanics_Shop_Database-main/src/TransactionProcessingService.java`
- `/Users/narani/Downloads/Mechanics_Shop_Database-main/src/AccountTrackingService.java`
- `/Users/narani/Downloads/Mechanics_Shop_Database-main/src/SqlTransactionRepository.java`
- `/Users/narani/Downloads/Mechanics_Shop_Database-main/src/SqlAccountRepository.java`
- `/Users/narani/Downloads/Mechanics_Shop_Database-main/src/BillingStrategy.java`
- `/Users/narani/Downloads/Mechanics_Shop_Database-main/src/BillingStrategyFactory.java`
- `/Users/narani/Downloads/Mechanics_Shop_Database-main/src/AppConfig.java`
