# Medical Device Inventory Management with Audit Logging

A Spring Boot 2 (Java 8) application demonstrating medical device inventory management with comprehensive audit logging.

## Features

- **Spring Boot 2** with Java 8
- **REST API** for medical device inventory operations
- **Audit Logging** for all operations using the v1 audit library
- **Simple Architecture**: Controller → Service → Model
- **In-Memory Storage** with sample medical device inventory data
- **Medical Device Specific Fields**: Manufacturer, model number, expiration date, status

## Architecture

```
Controller (REST API)
    ↓
Service (Business Logic + Audit Logging)
    ↓
Model (Data Objects)
```

## API Endpoints

### Health Check
- `GET /api/medical-devices/health` - Service health check

### Medical Device Operations
- `GET /api/medical-devices` - Get all medical devices
- `GET /api/medical-devices/{deviceId}` - Get specific medical device
- `POST /api/medical-devices/add` - Add medical devices to inventory (restock)
- `POST /api/medical-devices/remove` - Remove medical devices from inventory (checkout)

## Sample Medical Device Inventory

The application starts with sample medical device inventory:
- **VENT-001**: Ventilator (3 units, Respiratory, ICU Ward A, Philips V60)
- **MONITOR-001**: Patient Monitor (8 units, Monitoring, ER Department, GE Healthcare B650)
- **DEFIB-001**: Defibrillator (5 units, Emergency, Emergency Room, Zoll X Series)
- **PUMP-001**: Infusion Pump (12 units, Infusion, Med-Surg Unit, Baxter Sigma Spectrum)
- **XRAY-001**: X-Ray Machine (2 units, Imaging, Radiology, Siemens Ysio Max)

## Running the Application

```bash
# Build and run
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/audit-user-springboot-1.0.0.jar
```

The application will start on `http://localhost:8080`

## Example API Calls

### Get all medical devices
```bash
curl http://localhost:8080/api/medical-devices
```

### Get specific medical device
```bash
curl http://localhost:8080/api/medical-devices/VENT-001
```

### Add medical devices (restock)
```bash
curl -X POST http://localhost:8080/api/medical-devices/add \
  -H "Content-Type: application/json" \
  -d '{
    "device_id": "VENT-001",
    "quantity": 2,
    "user_id": "dr.smith@hospital.com",
    "reason": "New shipment received",
    "patient_id": null,
    "department": "ICU"
  }'
```

### Remove medical devices (checkout)
```bash
curl -X POST http://localhost:8080/api/medical-devices/remove \
  -H "Content-Type: application/json" \
  -d '{
    "device_id": "VENT-001",
    "quantity": 1,
    "user_id": "nurse.jones@hospital.com",
    "reason": "Patient care - Room 301",
    "patient_id": "PAT-12345",
    "department": "ICU"
  }'
```

## Audit Logs

All operations are logged to `./medical-device-audit-logs/audit.log` in JSON format. Each log entry includes:
- Timestamp
- User ID
- Action performed
- Resource accessed
- Result (success/failure)
- Additional details including patient ID and department

## Prerequisites

- Java 8+
- Maven 3.6+
- Audit library installed in local Maven repository (`mvn install` from parent directory) 