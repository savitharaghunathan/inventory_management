# Inventory Management Service

A Spring Boot application demonstrating enterprise audit logging with the v2 audit library. This service provides inventory management capabilities with comprehensive audit trails for all operations.

## Features

- RESTful API for inventory management 
- Enterprise-grade audit logging using v2 audit library
- Java 21 with Spring Boot 3
- TCP streaming to Logstash for audit events

## Quick Start

### 1. Start Logstash (Required)

The inventory service requires Logstash to be running for audit logging. Start Logstash first:

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

## API Testing

### Get All Inventory Items

```bash
curl -X GET "http://localhost:8080/api/inventory?userId=user123"
```

### Get Item by ID

```bash
curl -X GET "http://localhost:8080/api/inventory/1?userId=user123"
```

### Add New Item

```bash
curl -X POST "http://localhost:8080/api/inventory?userId=user123" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "quantity": 10,
    "price": 999.99
  }'
```

### Update Item

```bash
curl -X PUT "http://localhost:8080/api/inventory/1?userId=user123" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Laptop",
    "description": "Updated description",
    "quantity": 15,
    "price": 1099.99
  }'
```

### Delete Item

```bash
curl -X DELETE "http://localhost:8080/api/inventory/1?userId=user123"
```

### Search Items by Name

```bash
curl -X GET "http://localhost:8080/api/inventory/search?name=laptop&userId=user123"
```

## Sample Data

The application comes pre-loaded with sample inventory items:

- **ID 1:** Laptop - High-performance laptop ($999.99, 10 units)
- **ID 2:** Mouse - Wireless mouse ($29.99, 50 units)
- **ID 3:** Keyboard - Mechanical keyboard ($149.99, 25 units)
- **ID 4:** Monitor - 27" 4K monitor ($399.99, 15 units)
- **ID 5:** Headphones - Noise-canceling headphones ($199.99, 30 units)

## Configuration

The application uses environment variables for configuration:

- `AUDIT_LOGSTASH_HOST`: Logstash host (default: localhost)
- `AUDIT_LOGSTASH_PORT`: Logstash port (default: 5000)
- `AUDIT_APP_NAME`: Application name for audit events (default: inventory-management)

## Testing

Run the test suite:

```bash
mvn test
```

## Audit Logging

All inventory operations are automatically logged to Logstash with the following information:

- **Event Type:** Operation performed (CREATE, READ, UPDATE, DELETE, SEARCH)
- **User ID:** User performing the operation
- **Item Details:** Inventory item information
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
- Check that item IDs exist before updating/deleting
- Verify JSON payload format for POST/PUT requests
