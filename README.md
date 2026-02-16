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

### 1) Install PostgreSQL and ensure tools are available

Install PostgreSQL using your OS package manager, then confirm:

```bash
psql --version
createdb --version
```

### 2) Start PostgreSQL server

Start your local PostgreSQL service and ensure it is accepting connections on your configured host/port.

### 3) Create database and schema

```bash
createdb carRepair
psql carRepair < sql/schema.sql
```

### 4) Optional: load sample data (recommended for first run)

From the project root:

```bash
psql carRepair < sql/seed.sql
```

You can skip this step to start with an empty database.

### 5) Build

```bash
javac -cp .:postgresql-42.2.23.jar src/*.java -d bin
```

### 6) Configure app environment

```bash
export MECHANICSHOP_DB_NAME="carRepair"
export MECHANICSHOP_DB_PORT="5432"
export MECHANICSHOP_DB_USER="<your_postgres_role>"
export MECHANICSHOP_DB_PASSWORD="<your_postgres_password>"
```

### 7) Run

```bash
java -cp .:postgresql-42.2.23.jar:bin MechanicShop
```

## Startup Modes

- `Seeded mode` (recommended): run `psql carRepair < sql/seed.sql` and explore reports/ledger immediately.
- `Empty mode`: run only `sql/schema.sql`, then add data from the app menu (`AddCustomer`, `AddMechanic`, `AddCar`, `AddOwnership`).

## Legacy SQL Files

The original files are kept for reference (`sql/create.sql`, `sql/Mycreate.txt`), but the app is aligned with:

- `sql/schema.sql` for schema creation
- `sql/seed.sql` for sample data loading

## If `COPY` paths fail in legacy scripts

```bash
sed '/^COPY Customer/,$d' sql/create.sql | psql carRepair
```

## Troubleshooting

- `zsh: command not found: psql` or `createdb`:
  - PostgreSQL binaries are not on PATH. Add PostgreSQL `bin` directory to PATH.
- `FATAL: role "postgres" does not exist`:
  - Use an existing PostgreSQL role in `MECHANICSHOP_DB_USER`, or create the role first.
- `FATAL: database "carRepair" does not exist`:
  - Run `createdb carRepair`.
- `Connection to localhost:5432 refused`:
  - PostgreSQL service is not running or listening on that port. Start the service and verify host/port.
