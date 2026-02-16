# Mechanic Shop Database Platform

A modular Java + PostgreSQL platform for managing customer accounts, vehicle ownership, service request lifecycle, and billing transactions.

## Overview

This project provides a console-based operations system for a mechanic shop. It supports customer and vehicle onboarding, ownership management, service request processing, billing workflows, account tracking, and reporting.

## Architecture

### Layers

- `MechanicShop` (`src/MechanicShop.java`): CLI entrypoint and orchestration layer.
- `AppConfig`, `InputReader` (`src/AppConfig.java`, `src/InputReader.java`): runtime configuration and robust user input.
- `AccountTrackingService`, `TransactionProcessingService`, `ReportService`: business application services.
- `AccountRepository`, `TransactionRepository` + SQL implementations: persistence abstraction.
- Domain models (`CustomerAccount`, `TransactionRecord`, request input models): typed business objects.
- Billing strategy module (`BillingStrategy`, `StandardBillingStrategy`, `ExperienceDiscountBillingStrategy`, `BillingStrategyFactory`).

### Design Patterns

- `Repository Pattern`: Decouples business logic from SQL implementation.
- `Service Layer Pattern`: Encapsulates account and transaction workflows.
- `Strategy + Factory`: Pluggable billing policy selection based on mechanic experience.

## Core Features

- Customer, mechanic, car, and ownership registration.
- Service request open/close transaction lifecycle.
- Account summary view:
  - Open request count per customer.
  - Lifetime billed amount per customer.
- Transaction ledger view:
  - Request-open and request-close events in chronological order.
- Reporting queries for billing and service analytics.

## Data Integrity and Validation

- Enforces existence checks before transaction operations:
  - Customer must exist.
  - Car must exist.
  - Customer must own car before opening request.
  - Service request and mechanic must exist before closing.
  - Service request cannot be closed twice.
- Validates positive IDs, odometer, and bill values.
- Uses prepared statements for operational writes and key reads.

## Setup

### 1) Compile

```bash
javac -cp .:postgresql-42.2.23.jar src/*.java -d bin
```

### 2) Configure DB Credentials

Use environment variables (recommended):

```bash
export MECHANICSHOP_DB_NAME=carRepair
export MECHANICSHOP_DB_PORT=5432
export MECHANICSHOP_DB_USER=postgres
export MECHANICSHOP_DB_PASSWORD=your_password_here
```

Or pass values as CLI args:

```bash
java -cp .:postgresql-42.2.23.jar:bin MechanicShop \
  --db-name carRepair --db-port 5432 --db-user postgres --db-password your_password_here
```

### 3) Run

```bash
java -cp .:postgresql-42.2.23.jar:bin MechanicShop
```
