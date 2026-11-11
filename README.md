# Dovewing Car Rentals

[![Dovewing CI](https://github.com/emmanuel-tormeti/java-basics-b-dovewing-car-rentals/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/emmanuel-tormeti/java-basics-b-dovewing-car-rentals/actions/workflows/ci.yml)

A Java-based car rental management application focused on object-oriented design, encapsulation, business rule enforcement, and maintainability.

## Overview

Dovewing Car Rentals is an application for managing a vehicle fleet, renters, and rental transactions.

It supports different vehicle rental policies, renter categories, pricing rules, and deposit calculations. The system is designed to preserve expected business behaviour while preventing inconsistent rental states.

A central business rule governs the application: **a vehicle cannot have more than one active rental at a time.**

The project was rebuilt to address design and data integrity weaknesses in the original implementation while improving the maintainability and extensibility of the domain model.

## Features

- **Fleet management:** Manage vehicles and track their availability.
- **Rental management:** Rent and return vehicles while maintaining rental history.
- **Multiple vehicle policies:** Support economy and premium rental policies with an extensible design for additional vehicle categories.
- **Renter categories:** Support casual and member renters.
- **Deposit rules:** Apply vehicle-specific deposits and waive deposits for members.
- **Rental validation:** Reject invalid rental durations, unavailable vehicles, and invalid return operations.
- **Rental history:** Retain completed rentals and their statuses.
- **Financial accuracy:** Use `BigDecimal` for monetary calculations.
- **State protection:** Encapsulate domain state and prevent uncontrolled external modification.
- **Business rule enforcement:** Ensure that each vehicle has at most one active rental.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Application development |
| Apache Maven 3.9.12 | Build and dependency management |
| JUnit Jupiter 6.1.2 | Automated testing |
| Maven Surefire 3.6.0 | Test execution |
| Git | Version control |
| GitHub Actions | Continuous integration |

The project includes the Maven Wrapper, so a separate Maven installation is not required.

## Design and Architecture

The application separates its domain model, business rules, and application-level responsibilities.

### Package Organisation

```text
dovewing/
├── app/
│   └── RentalTracker
│
└── domain/
    ├── model/
    ├── policy/
    └── validation/
```

- **`app`:** Application entry point and console presentation.
- **`domain.model`:** Core domain objects, including `Depot`, `Vehicle`, `Renter`, and `Rental`.
- **`domain.policy`:** Rental pricing and deposit policies.
- **`domain.validation`:** Validation responsibilities and shared checks.

### Key Design Decisions

**Encapsulation and State Ownership**

The `Depot` owns the active vehicle-to-rental relationship through a private `Map<Vehicle, Rental>`. Vehicle availability is derived from this mapping rather than maintained as a separate mutable status.

**Composition and Policy-Based Design**

A `RentalPolicy` interface separates vehicle-specific pricing and deposit rules from rental coordination. Different policy implementations can be introduced without adding vehicle category conditionals to the depot.

**Domain Integrity**

Rental operations validate their requirements before modifying domain state. A vehicle cannot be rented again until its current rental has been returned.

**Separation of Responsibilities**

Domain operations are separated from console presentation, making the business logic easier to test independently.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 21 or later.
- Git.
- Internet access for the initial Maven Wrapper and dependency downloads.

You do not need to install Maven separately.

### Clone the Repository

```bash
git clone https://github.com/emmanuel-tormeti/java-basics-b-dovewing-car-rentals.git
cd java-basics-b-dovewing-car-rentals
```

### Build the Project

On Windows PowerShell:

```powershell
.\mvnw.cmd clean package
```

On Linux or macOS:

```bash
./mvnw clean package
```

### Run the Application

After building, run the application using the following commands.

**Windows PowerShell:**

```powershell
java -cp target\dovewing-car-rentals.jar dovewing.app.RentalTracker
```

**Linux or macOS:**

```bash
java -cp target/dovewing-car-rentals.jar dovewing.app.RentalTracker
```

## Testing

The project uses JUnit Jupiter for automated unit and application-level testing.

The test suite currently contains **72 automated tests** across seven test classes:

- `RentalTrackerTest`
- `DepotTest`
- `RentalQuoteTest`
- `RentalTest`
- `RenterTest`
- `VehicleTest`
- `RentalPoliciesTest`

The tests cover:

- Rental and return operations.
- Vehicle availability and active rental constraints.
- Rental pricing and deposit calculations.
- Renter behaviour.
- Input validation.
- Rental history and status transitions.
- Domain state protection.
- Application behaviour.

The latest successful local test run reported zero failures and errors.

Run the tests using:

**Windows PowerShell:**

```powershell
.\mvnw.cmd test
```

**Linux or macOS:**

```bash
./mvnw test
```

For a complete Maven verification, including the build lifecycle checks, run:

```bash
./mvnw --batch-mode verify
```

On Windows PowerShell:

```powershell
.\mvnw.cmd --batch-mode verify
```

## Development Workflow and Automated Quality Assurance

Beyond rebuilding the application's domain model, an additional engineering improvement was introduced to strengthen development reliability and automate quality checks.

The original application contained business rule and data integrity weaknesses, particularly around vehicle availability and active rental records. Automated testing was introduced to verify the rebuilt domain model and protect its expected behaviour against regressions.

Three mechanisms work together to provide multiple levels of quality assurance.

### Maven Wrapper

The Maven Wrapper ensures that contributors use the Maven version configured by the project without requiring a separate Maven installation.

It automatically downloads the required Maven distribution when necessary.

### Pre-commit Hook

Running tests manually before every commit relies on the developer remembering to do so.

A local Git pre-commit hook was introduced to automate this check. It executes the Maven test suite before allowing a normal commit to proceed.

To enable the hook after cloning the repository, run:

```bash
git config core.hooksPath .githooks
```

The hook blocks a normal commit if the tests fail. It can be bypassed using Git's `--no-verify` option, so it serves as a development safeguard rather than a security boundary.

### Continuous Integration

Local testing alone does not guarantee that the project will build and pass tests in a different environment.

GitHub Actions was introduced to run the Maven verification process in a clean GitHub-hosted Ubuntu environment.

The workflow is triggered by:

- Pushes to the `main` branch.
- Pull requests targeting the `main` branch.

It uses Java 21 and executes:

```bash
./mvnw --batch-mode verify
```

This provides an independent check that the project compiles, passes its automated tests, and completes Maven verification outside the developer's local Windows environment.

### How the Quality Checks Work Together

| Mechanism | When it runs | Purpose |
|---|---|---|
| Automated tests | When executed | Verify application behaviour |
| Pre-commit hook | Before a local commit | Catch regressions before committing |
| GitHub Actions CI | After a push or pull request | Validate changes in a separate environment |

Together, these mechanisms establish multiple layers of feedback. Automated tests verify correctness, the pre-commit hook encourages local development discipline, and continuous integration independently validates changes in the remote development workflow.

## Project Documentation

Additional project documentation includes:

- **Code diagnosis:** [`DIAGNOSIS.md`](DIAGNOSIS.md), containing the analysis of the original implementation, identified risks, proposed improvements, and behaviour preservation requirements.
- **Original output:** [`ORIGINAL_OUTPUT.txt`](ORIGINAL_OUTPUT.txt), preserving the baseline application output before the rebuild.
- **Design diagram:** [`dovewing-class-design.png`](dovewing-class-design.png), providing a visual representation of the class design.
- **Editable design diagram:** [`dovewing-class-design.drawio`](dovewing-class-design.drawio), providing the editable source of the class diagram.

---

**Dovewing Car Rentals**

Built with Java 21 and Apache Maven.
