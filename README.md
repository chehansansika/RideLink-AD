# RideLink - Backend Microservices

RideLink is a distributed ride-sharing backend system developed using Spring Boot and MongoDB, structured into independent microservices.

## Project Structure

```text
RideLink/
├── account-service/          # Account & User management microservice
├── driver-vehicle-service/   # Driver & Vehicle management microservice
├── ride-management-service/  # Ride lifecycle & dispatch microservice
├── fare-payment-service/     # Fare calculation & payment processing microservice
├── .github/
│   └── workflows/            # GitHub Actions CI/CD workflows
├── postman/                  # Postman collections & environment files
├── docs/                     # Architecture & API documentation
├── README.md                 # Project overview and guidelines
└── .gitignore                # Global Git ignore rules
```

## Technology Stack

- **Framework**: Java + Spring Boot
- **Database**: MongoDB + Spring Data MongoDB
- **API**: RESTful APIs & Swagger / OpenAPI
- **Testing**: JUnit + Mockito
- **API Testing**: Postman
- **Version Control**: Git & GitHub
- **CI/CD**: GitHub Actions

## Branching Strategy

The repository follows a service-oriented branching model:

* `main` — Integrated production branch.
* `account-service` — Development branch for Account Service.
* `driver-vehicle-service` — Development branch for Driver & Vehicle Service.
* `ride-management-service` — Development branch for Ride Management Service.
* `fare-payment-service` — Development branch for Fare & Payment Service.
