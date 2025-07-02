# Medical Device Inventory Management Service

A Spring Boot application demonstrating enterprise audit logging with the v2 audit library. This service provides medical device inventory management capabilities with comprehensive audit trails for all operations.

## Features

- RESTful API for medical device inventory management 
- Enterprise-grade audit logging using v2 audit library
- Java 21 with Spring Boot 3
- TCP streaming to Logstash for audit events
- Medical device specific fields: manufacturer, model number, expiration date, status

## Quick Start

### 1. Start Logstash (Required)

The medical device inventory service requires Logstash to be running for audit logging. Start Logstash first:

```bash
# Using Podman (recommended)
podman run -d --name logstash -p 5000:5000 \
  -v $(pwd)/logstash.conf:/usr/share/logstash/pipeline/logstash.conf \
  -v $(pwd)/logstash.yml:/usr/share/logstash/config/logstash.yml \
  docker.elastic.co/logstash/logstash:8.11.0

# Using Docker (alternative)
docker run -d --name logstash -p 5000:5000 \
  -v $(pwd)/logstash.conf:/usr/share/logstash/pipeline/logstash.conf \
  -v $(pwd)/logstash.yml:/usr/share/logstash/config/logstash.yml \
  docker.elastic.co/logstash/logstash:8.11.0

# Or using a local Logstash installation
# Ensure Logstash is configured to listen on port 5000 for TCP input
```

**Note:** The application will fail to start if Logstash is not available. This is intentional for production environments where audit logging is critical.

### 2. Build the Application

```bash
mvn clean package
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on http://localhost:8080

## API endpoints

### Medical Device Operations
- `GET /api/medical-devices` - Get all medical devices
- `GET /api/medical-devices/{deviceId}` - Get specific medical device
- `POST /api/medical-devices/add` - Add medical devices to inventory (restock)
- `POST /api/medical-devices/remove` - Remove medical devices from inventory (checkout)

### Get All Medical Devices

```bash
curl -X GET "http://localhost:8080/api/medical-devices?userId=user123"
```

### Get Medical Device by ID

```bash
curl -X GET "http://localhost:8080/api/medical-devices/VENT-001?userId=user123"
```

### Add Medical Devices (Restock)

```bash
curl -X POST "http://localhost:8080/api/medical-devices/add" \
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

### Remove Medical Devices (Checkout)

```bash
curl -X POST "http://localhost:8080/api/medical-devices/remove" \
  -H "Content-Type: application/json" \
  -d '{
    "device_id": "VENT-001",
    "quantity": 1,
    "user_id": "nurse.jones@hospital.com",
    "reason": "Device retired",
    "patient_id": null,
    "department": "ICU"
  }'
```

## Sample Medical Device Data

The application comes pre-loaded with sample medical device inventory:

- **VENT-001:** Ventilator - Philips V60 (3 units, Respiratory, ICU Ward A)
- **MONITOR-001:** Patient Monitor - GE Healthcare B650 (8 units, Monitoring, ER Department)
- **DEFIB-001:** Defibrillator - Zoll X Series (5 units, Emergency, Emergency Room)
- **PUMP-001:** Infusion Pump - Baxter Sigma Spectrum (12 units, Infusion, Med-Surg Unit)
- **XRAY-001:** X-Ray Machine - Siemens Ysio Max (2 units, Imaging, Radiology)

## Configuration

The application uses environment variables for configuration:

- `AUDIT_STREAM_HOST`: Logstash host (default: localhost)
- `AUDIT_STREAM_PORT`: Logstash port (default: 5000)
- `AUDIT_STREAM_PROTOCOL`: Stream protocol (default: tcp)

## Testing

Run the test suite:

```bash
mvn test
```

## Audit Logging

All medical device inventory operations are automatically logged to Logstash with the following information:

- **Event Type:** Operation performed (MEDICAL_DEVICE_ADD, MEDICAL_DEVICE_REMOVE, MEDICAL_DEVICE_VIEW, etc.)
- **User ID:** User performing the operation
- **Device Details:** Medical device information including patient ID and department
- **Timestamp:** When the operation occurred
- **Result:** Success/failure status

Audit events are streamed asynchronously to Logstash via TCP connection.

## Migration from v1

This application demonstrates the v2 audit library patterns. See `MIGRATION_GUIDE.md` for details on migrating from v1 to v2.

## Troubleshooting

### Application Won't Start

- Ensure Logstash is running and accessible on port 5000
- Check that the audit library is installed in your local Maven repository
- Verify Java 21 is installed and being used

### No Audit Events in Logstash

- Check Logstash logs: `podman logs logstash` or `docker logs logstash`
- Verify the TCP connection is established
- Check firewall settings for port 5000

### API Errors

- Ensure you're providing a `userId` parameter in all requests
- Check that device IDs exist before updating/deleting
- Verify JSON payload format for POST requests
