# Dovewing Car Rentals

A Java-based car rental management application built with a focus on object-oriented design, encapsulation, business-rule enforcement, and maintainability.

## Overview

Dovewing Car Rentals manages a vehicle fleet, renters, and vehicle rental transactions.

The application supports different vehicle rental policies, renter categories, rental pricing, and deposit rules. It is designed to preserve valid business behaviour while preventing inconsistent rental states.

A central business rule governs the system: **a vehicle cannot have more than one active rental at a time.**

The project was rebuilt to address design and data-integrity weaknesses in the original implementation while making the domain model easier to maintain and extend.

## Features

- **Fleet management:** Maintain vehicles and their availability.
- **Rental management:** Rent and return vehicles while maintaining rental history.
- **Multiple vehicle policies:** Support economy and premium rental policies, with an extensible design for additional vehicle categories.
- **Renter categories:** Support casual and member renters.
- **Deposit rules:** Apply vehicle-specific deposits and waive deposits for members.
- **Rental validation:** Reject invalid rental durations, unavailable vehicles, and invalid return operations.
- **Rental history:** Retain completed rentals and their statuses.
- **Financial accuracy:** Use `BigDecimal` for monetary calculations.
- **State protection:** Encapsulate domain state and prevent uncontrolled external modification.
- **Business-rule enforcement:** Ensure each vehicle has at most one active rental.

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

**Encapsulation and state ownership**

The `Depot` owns the active vehicle-to-rental relationship through a private `Map<Vehicle, Rental>`. Vehicle availability is derived from this mapping rather than maintained as a separate mutable status.

**Composition and policy-based design**

A `RentalPolicy` interface separates vehicle-specific pricing and deposit rules from rental coordination. Different policy implementations can be introduced without adding vehicle-category conditionals to the depot.

**Domain integrity**

Rental operations validate their requirements before modifying domain state. A vehicle cannot be rented again until its current rental has been returned.

**Separation of responsibilities**

Domain operations are separated from console presentation, making the business logic easier to test independently.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 21 or later.
- Git.
- Internet access for the initial Maven Wrapper and dependency downloads.

You do not need to install Maven separately.

### Clone the Repository

```bash
git clone https://github.com/emmanuel-tormeti/java-basics-b-dovewing-car-rentals
cd dovewing-car-rentals
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

After building, run the application using:

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

The test suite covers:

- Rental and return operations.
- Vehicle availability and active rental constraints.
- Rental pricing and deposit calculations.
- Renter behaviour.
- Input validation.
- Rental history and status transitions.
- Domain state protection.
- Application behaviour.

The current test suite contains **72 automated tests**, with the latest successful local run reporting zero failures and errors.

Run the tests using:

**Windows PowerShell:**

```powershell
.\mvnw.cmd test
```

**Linux or macOS:**

```bash
./mvnw test
```

For a complete Maven verification:

```bash
./mvnw --batch-mode verify
```

On Windows, use `.\mvnw.cmd --batch-mode verify`.

## Development Workflow and Automation

The project includes local and remote quality checks to help prevent defective changes from being committed or merged.

### Maven Wrapper

The Maven Wrapper ensures that contributors use the Maven version configured by the project. It downloads the required Maven distribution when necessary.

### Pre-commit Hook

A local Git pre-commit hook automatically executes the Maven test suite before a commit proceeds.

To enable the hook after cloning the repository, run:

```bash
git config core.hooksPath .githooks
```

The hook runs the tests and blocks a normal commit if they fail.

### Continuous Integration

GitHub Actions is configured to execute the Maven verification process on:

- Pushes to the `main` branch.
- Pull requests targeting the `main` branch.

The workflow runs on GitHub-hosted Ubuntu with Java 21 and executes:

```bash
./mvnw --batch-mode verify
```

This provides an additional automated check in an environment independent of the developer's local machine.

## Project Documentation

Additional project documentation includes:

- **Code diagnosis:** Analysis of the original implementation, identified risks, proposed improvements, and behaviour-preservation requirements.
- **Original output:** `ORIGINAL_OUTPUT.txt`, preserving the baseline application output before the rebuild.
- **Design diagram:** `dovewing-class-design.png`.
- **Editable design diagram:** `dovewing-class-design.drawio`.

## Additional Engineering Feature: Automated Quality Assurance and CI

Beyond rebuilding the application's domain model, an additional engineering feature was introduced to improve development reliability and automate quality checks. This feature combines automated testing, a local Git pre-commit hook, and GitHub Actions continuous integration.

### Automated Testing

The original application contained business-rule and data-integrity weaknesses, particularly around vehicle availability and active rental records.

An automated test suite was introduced to verify the expected behaviour of the rebuilt domain model and protect its business rules against regressions.

The tests cover rental operations, return operations, pricing policies, validation, state management, and application behaviour.

The project currently contains **72 automated tests** using JUnit Jupiter.

### Pre-commit Hook

Running tests manually before every commit relies on the developer remembering to do so.

A local Git pre-commit hook was introduced to automate this check. It executes the Maven test suite before allowing a normal commit to proceed.

This helps prevent commits containing changes that break existing tests and encourages a consistent development workflow.

### Continuous Integration (CI)

Local testing alone does not guarantee that the project will build and pass tests in a different environment.

GitHub Actions was introduced to run the Maven verification process on a clean GitHub-hosted Ubuntu environment whenever changes are pushed to `main` or a pull request targets that branch.

The CI workflow provides an independent check that the project compiles, passes its automated tests, and completes Maven verification outside the developer's local Windows environment.

### How They Work Together

| Mechanism | When it runs | Purpose |
|---|---|---|
| Automated tests | When executed | Verify application behaviour |
| Pre-commit hook | Before a local commit | Catch regressions before committing |
| GitHub Actions CI | After a push or pull request | Validate changes in a separate environment |

Together, these checks establish multiple layers of feedback: automated tests verify correctness, the pre-commit hook encourages local development discipline, and CI independently validates changes in the remote development workflow.

---

**Dovewing Car Rentals**  
Built with Java 21 and Apache Maven.