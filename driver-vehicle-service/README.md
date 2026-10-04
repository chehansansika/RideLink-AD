# RideLink - Driver & Vehicle Service (Member 2)

## 📌 Service Overview
The **Driver & Vehicle Service** is a core backend microservice for the RideLink platform, developed as part of **Member 2** responsibilities. It is responsible for managing driver operational profiles, vehicle records, operational availability states, simulated driver geographical locations, service areas, and providing discovery endpoints for eligible drivers during ride matching.

- **Assigned Member:** `IT24100110-Perera W.S.D.`
- **Git Branch:** `Driver-&-Vehicle-Service`
- **Application Name:** `driver-vehicle-service`
- **Port:** `8082`
- **Database:** MongoDB (`ridelink_driver_db`)
- **Package Base:** `com.ridelink.driver`

---

## 🛠️ Technology Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3.2.5
- **Persistence:** Spring Data MongoDB
- **Database:** MongoDB
- **API Documentation:** SpringDoc OpenAPI 2.5.0 (Swagger UI)
- **Validation:** Hibernate Validator (Jakarta Validation)
- **Testing:** JUnit 5, Mockito
- **Build Tool:** Maven

---

## 🏗️ Architecture & Project Structure
```
driver-vehicle-service/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/ridelink/driver/
    │   │   ├── DriverVehicleServiceApplication.java
    │   │   ├── config/
    │   │   │   ├── MongoConfig.java
    │   │   │   └── OpenApiConfig.java
    │   │   ├── controller/
    │   │   │   ├── DriverController.java
    │   │   │   └── VehicleController.java
    │   │   ├── dto/
    │   │   │   ├── CreateDriverRequest.java
    │   │   │   ├── UpdateDriverRequest.java
    │   │   │   ├── DriverResponse.java
    │   │   │   ├── CreateVehicleRequest.java
    │   │   │   ├── UpdateVehicleRequest.java
    │   │   │   ├── VehicleResponse.java
    │   │   │   ├── UpdateAvailabilityRequest.java
    │   │   │   ├── UpdateLocationRequest.java
    │   │   │   ├── UpdateServiceAreaRequest.java
    │   │   │   └── EligibleDriverResponse.java
    │   │   ├── exception/
    │   │   │   ├── DriverNotFoundException.java
    │   │   │   ├── VehicleNotFoundException.java
    │   │   │   ├── DuplicateDriverException.java
    │   │   │   ├── DuplicateVehicleException.java
    │   │   │   └── GlobalExceptionHandler.java
    │   │   ├── model/
    │   │   │   ├── Driver.java
    │   │   │   ├── Vehicle.java
    │   │   │   ├── DriverAvailability.java
    │   │   │   ├── VehicleStatus.java
    │   │   │   └── Location.java
    │   │   ├── repository/
    │   │   │   ├── DriverRepository.java
    │   │   │   └── VehicleRepository.java
    │   │   └── service/
    │   │       ├── DriverService.java
    │   │       └── VehicleService.java
    │   └── resources/
    │       └── application.yml
    └── test/
        ├── java/com/ridelink/driver/service/
        │   ├── DriverServiceTest.java
        │   └── VehicleServiceTest.java
        └── resources/
            └── application.yml
```

---

## 🚀 Key Features & Responsibilities

1. **Driver Operational Profiles**
   - Create, read, update, and soft-manage driver details (license number, contact info, service area).
   - Validations for phone formats and unique driving license constraint.
   
2. **Vehicle Management**
   - Register vehicles linked to a driver (make, model, license plate/registration number, capacity, vehicle type).
   - Vehicle lifecycle states: `ACTIVE`, `INACTIVE`.
   - Ensure license plates are unique across the system.

3. **Driver Availability Management**
   - Statuses: `AVAILABLE`, `BUSY`, `OFFLINE`.
   - Business Rule: A driver can only transition to `AVAILABLE` if they have at least one active vehicle registered.

4. **Simulated Driver Location Tracking**
   - Update and retrieve simulated geographic coordinates (`latitude`, `longitude`).
   - Latitude validated within `[-90, 90]`, Longitude within `[-180, 180]`.

5. **Eligible Driver Discovery (Ride Matching Integration)**
   - Query available drivers matching criteria (Status = `AVAILABLE`, has active vehicle, matching optional `serviceArea`).
   - Returns enriched driver and active vehicle information ready for the Ride Management Service.

---

## 📡 REST API Endpoints

### 👤 Driver Endpoints (`/api/drivers`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/drivers` | Register a new driver profile |
| `GET` | `/api/drivers/{id}` | Get driver by ID |
| `GET` | `/api/drivers` | Get all drivers |
| `PUT` | `/api/drivers/{id}` | Update driver details |
| `DELETE` | `/api/drivers/{id}` | Delete a driver profile |
| `PATCH` | `/api/drivers/{id}/availability` | Update driver availability status |
| `PATCH` | `/api/drivers/{id}/location` | Update driver current location coordinates |
| `GET` | `/api/drivers/{id}/location` | Get driver current location coordinates |
| `PATCH` | `/api/drivers/{id}/service-area` | Update driver primary service area |
| `GET` | `/api/drivers/eligible` | Find eligible available drivers for ride assignment |

### 🚗 Vehicle Endpoints (`/api/vehicles` & `/api/drivers/{driverId}/vehicles`)
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/vehicles` | Register a vehicle for a driver |
| `GET` | `/api/vehicles/{id}` | Get vehicle by ID |
| `GET` | `/api/drivers/{driverId}/vehicles` | List all vehicles owned by a driver |
| `PUT` | `/api/vehicles/{id}` | Update vehicle details |
| `PATCH` | `/api/vehicles/{id}/activate` | Activate vehicle status |
| `PATCH` | `/api/vehicles/{id}/deactivate` | Deactivate vehicle status |

---

## ⚙️ Configuration & Environment Variables

The service can be configured via `src/main/resources/application.yml` or overridden via environment variables:

| Environment Variable | Default Value | Description |
|---|---|---|
| `PORT` | `8082` | HTTP Server port |
| `MONGODB_URI` | `mongodb+srv://admin:admin123@dbcluster.eutnxe9.mongodb.net/ridelink_driver_db?retryWrites=true&w=majority&appName=dbCluster` | MongoDB connection string (Atlas or local) |

---

## 🧪 How to Run and Test

### Prerequisites
- JDK 21+
- Maven 3.8+ (or use the included `mvnw.cmd` / `mvnw` wrapper)
- MongoDB instance (MongoDB Atlas cluster or local `localhost:27017`)

### Running the Application
```bash
cd driver-vehicle-service
# Using Maven wrapper (recommended)
.\mvnw.cmd spring-boot:run     # On Windows PowerShell / CMD
./mvnw spring-boot:run        # On Linux / macOS / Git Bash

# Or using globally installed Maven
mvn spring-boot:run
```

### Running Unit Tests
```bash
cd driver-vehicle-service
.\mvnw.cmd clean test         # On Windows
./mvnw clean test             # On Linux / macOS / Git Bash
```

### Interactive API Documentation (Swagger / OpenAPI)
Once the service is started, visit:
- **Swagger UI:** [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- **OpenAPI JSON Spec:** [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs)

---

## 📮 Postman Collection
A complete Postman test suite covering positive, negative, validation, and ride matching scenarios is available at:
`postman/driver-vehicle-service.postman_collection.json`

Import this collection into Postman and use the collection variable `baseUrl = http://localhost:8082`.
