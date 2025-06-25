# Inventory Management with Audit Logging

A simple Spring Boot 2 (Java 8) application demonstrating inventory management with comprehensive audit logging.

## Features

- **Spring Boot 2** with Java 8
- **REST API** for inventory operations
- **Audit Logging** for all operations using the v1 audit library
- **Simple Architecture**: Controller → Service → Model
- **In-Memory Storage** with sample inventory data

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
- `GET /api/inventory/health` - Service health check

### Inventory Operations
- `GET /api/inventory` - Get all inventory items
- `GET /api/inventory/{itemId}` - Get specific item
- `POST /api/inventory/add` - Add items to inventory (restock)
- `POST /api/inventory/remove` - Remove items from inventory (checkout)

## Sample Inventory

The application starts with sample inventory:
- **LAPTOP-001**: Dell XPS 13 (5 units, Electronics, Warehouse A)
- **MOUSE-001**: Wireless Mouse (20 units, Electronics, Warehouse B)
- **DESK-001**: Standing Desk (3 units, Furniture, Warehouse A)
- **CHAIR-001**: Ergonomic Chair (8 units, Furniture, Warehouse B)

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

### Get all inventory
```bash
curl http://localhost:8080/api/inventory
```

### Get specific item
```bash
curl http://localhost:8080/api/inventory/LAPTOP-001
```

### Add inventory (restock)
```bash
curl -X POST http://localhost:8080/api/inventory/add \
  -H "Content-Type: application/json" \
  -d '{
    "item_id": "LAPTOP-001",
    "quantity": 2,
    "user_id": "alice@company.com",
    "reason": "New shipment received"
  }'
```

### Remove inventory (checkout)
```bash
curl -X POST http://localhost:8080/api/inventory/remove \
  -H "Content-Type: application/json" \
  -d '{
    "item_id": "LAPTOP-001",
    "quantity": 1,
    "user_id": "bob@company.com",
    "reason": "New employee setup"
  }'
```

## Audit Logs

All operations are logged to `./inventory-audit-logs/audit.log` in JSON format. Each log entry includes:
- Timestamp
- User ID
- Action performed
- Resource accessed
- Result (success/failure)
- Additional details

## Prerequisites

- Java 8+
- Maven 3.6+
- Audit library installed in local Maven repository (`mvn install` from parent directory) 