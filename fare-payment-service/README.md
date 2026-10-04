# Fare & Payment Microservice - RideLink

The **Fare & Payment Service** is an independent Spring Boot backend microservice responsible for fare estimation, final trip fare calculation, simulated payment processing, transaction status management, and digital receipt generation for the RideLink ride-sharing platform.

---

## 1. Responsibilities

- **Fare Estimation**: Computes an upfront estimated fare based on ride distance and configurable tariffs.
- **Final Fare Calculation**: Calculates and persists the final fare upon trip completion.
- **Simulated Payment Recording**: Handles simulated transactions (`CASH`, `CARD`, `DIGITAL_WALLET`), tracks status (`PENDING`, `COMPLETED`, `FAILED`), and generates unique transaction references.
- **Payment Status Management**: Enforces validation preventing duplicate payments for completed rides and handles payment failure simulations.
- **Receipt Generation & Retrieval**: Automatically issues a receipt upon payment completion and allows querying by receipt ID, payment ID, or ride ID.
- **Loosely Coupled REST Communication**: Interacts with the Ride Management Service via a dedicated REST client.

---

## 2. Fare Calculation Formula

The fare calculation is deterministic, transparent, and fully configurable via `application.properties`:

$$\text{Total Fare} = \text{Base Fare} + (\text{Distance (km)} \times \text{Rate per KM}) + \text{Service Fee}$$

### Default Configuration Values
* **Base Fare (`fare.base-fare`)**: `100.00 LKR`
* **Rate per KM (`fare.rate-per-km`)**: `40.00 LKR`
* **Service Fee (`fare.service-fee`)**: `25.00 LKR`
* **Currency (`fare.currency`)**: `LKR`

#### Example:
For an **8.2 km** ride:
$$\text{Distance Charge} = 8.2 \times 40.00 = 328.00\text{ LKR}$$
$$\text{Total Fare} = 100.00 + 328.00 + 25.00 = 453.00\text{ LKR}$$

---

## 3. Technology Stack

- **Language & Runtime**: Java 21
- **Framework**: Spring Boot 3.3.4 (Spring Web, Spring Data MongoDB, Spring Validation)
- **Database**: MongoDB (Database: `ridelink_fare_payment_db`)
- **API Documentation**: OpenAPI 3.0 / Swagger UI (`springdoc-openapi-starter-webmvc-ui`)
- **Testing**: JUnit 5 + Mockito
- **API Testing**: Postman Collection v2.1

---

## 4. API Endpoints

### Fare Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/fares/estimate` | Estimate ride fare based on distance and locations |
| `POST` | `/api/fares/calculate` | Calculate and store final fare upon trip completion |
| `GET` | `/api/fares/ride/{rideId}` | Retrieve calculated fare by Ride ID |
| `GET` | `/api/fares/{fareId}` | Retrieve fare record by Fare ID |

### Payment Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/payments` | Record simulated payment (`CASH`, `CARD`, `DIGITAL_WALLET`) |
| `GET` | `/api/payments/{paymentId}` | Retrieve payment by Payment ID |
| `GET` | `/api/payments/ride/{rideId}` | Retrieve all payments for a Ride ID |

### Receipt Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/receipts/{receiptId}` | Retrieve receipt by Receipt ID |
| `GET` | `/api/receipts/payment/{paymentId}` | Retrieve receipt by Payment ID |
| `GET` | `/api/receipts/ride/{rideId}` | Retrieve receipt by Ride ID |

---

## 5. Swagger UI

When the service is running, interactive Swagger documentation is available at:
```
http://localhost:8084/swagger-ui.html
```
OpenAPI JSON specification:
```
http://localhost:8084/api-docs
```

---

## 6. How to Run Locally

### Prerequisites
- JDK 21 installed
- Maven 3.8+ installed
- MongoDB running on `localhost:27017` (or provide `MONGODB_URI` environment variable)

### Build
```bash
cd fare-payment-service
mvn clean package
```

### Run Unit Tests
```bash
mvn test
```

### Start the Service
```bash
mvn spring-boot:run
```
The service will start on port **`8084`**.

---

## 7. Postman Demonstration Flow

1. **Estimate Fare**: Send `POST /api/fares/estimate` with pickup, dropoff, and distance.
2. **Calculate Final Fare**: Send `POST /api/fares/calculate` with completed ride distance.
3. **Record Simulated Payment**: Send `POST /api/payments` with `rideId`, `passengerId`, `amount`, and `paymentMethod: "CARD"`.
4. **Retrieve Payment**: Send `GET /api/payments/{paymentId}` to inspect transaction reference and `COMPLETED` status.
5. **Retrieve Receipt**: Send `GET /api/receipts/payment/{paymentId}` to inspect issued receipt details.

---

## 8. Project Structure

```text
fare-payment-service/
├── pom.xml
├── README.md
├── postman/
│   └── RideLink_FarePayment_Service.postman_collection.json
└── src/
    ├── main/
    │   ├── java/com/ridelink/farepayment/
    │   │   ├── client/       # REST clients for inter-service communication
    │   │   ├── config/       # Application configuration (OpenAPI, properties)
    │   │   ├── controller/   # REST API Controllers (Fare, Payment, Receipt)
    │   │   ├── dto/          # Data Transfer Objects
    │   │   ├── exception/    # Global exception handling
    │   │   ├── model/        # Entities (Fare, Payment, Receipt)
    │   │   ├── repository/   # MongoDB repositories
    │   │   ├── service/      # Business logic services
    │   │   └── FarePaymentServiceApplication.java
    │   └── resources/
    │       └── application.properties
    └── test/                 # Unit and Integration tests
```
