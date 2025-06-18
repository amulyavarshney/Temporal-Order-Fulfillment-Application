# Temporal Order Fulfillment Application

A comprehensive Java application demonstrating **Temporal workflow orchestration** for e-commerce order fulfillment processes. This application showcases best practices for building resilient, scalable microservices using the Temporal workflow engine.

## 🏗️ Architecture Overview

This application implements a typical e-commerce order fulfillment workflow with the following components:

- **Temporal Workflows**: Orchestrate the order fulfillment process
- **Activities**: Handle individual business operations (payment, inventory, delivery)
- **Models**: Define data structures for orders, payments, and inventory
- **API Layer**: Interface with external services (payment gateways, inventory systems)

### Workflow Process

```
Order Received → Payment Processing → Inventory Reservation → Order Delivery → Completion
```

## 🚀 Quick Start

### Prerequisites

- **Java 21+** (configured for Java 24 in pom.xml)
- **Maven 3.6+**
- **Docker** (optional, for local Temporal server)

### 1. Clone and Build

```bash
git clone <repository-url>
cd Temporal-Order-Fulfillment-Application
make build
```

### 2. Start Temporal Server

**Option A: Using Docker (Recommended)**
```bash
make temporal-up
```

**Option B: Manual Installation**
```bash
# Install Temporal CLI
curl -sSf https://temporal.download/cli.sh | sh

# Start Temporal server
temporal server start-dev
```

### 3. Run the Application

**Terminal 1: Start the Worker**
```bash
make worker
```

**Terminal 2: Process Orders**
```bash
make run
```

## 📋 Available Commands

Run `make help` to see all available commands:

```bash
make help              # Show all available commands
make build             # Build the application
make test              # Run tests
make worker            # Start Temporal worker
make run               # Process sample orders
make demo              # Run demo with multiple orders
make temporal-up       # Start Temporal server
make temporal-down     # Stop Temporal server
```

## 🛠️ Development

### Project Structure

```
src/
├── main/java/orderfulfillapp/
│   ├── activities/              # Business logic activities
│   │   ├── OrderFulfillActivities.java
│   │   └── OrderFulfillActivitiesImpl.java
│   ├── api/                     # External API interfaces
│   │   └── InventoryApi.java
│   ├── exception/               # Custom exceptions
│   │   └── CreditCardExpiredException.java
│   ├── model/                   # Data models
│   │   ├── CreditCard.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Payment.java
│   │   └── StockItem.java
│   ├── starter/                 # Order generation utilities
│   │   └── OrderStarter.java
│   ├── workflows/               # Temporal workflows
│   │   ├── OrderFulfillWorkflow.java
│   │   └── OrderFulfillWorkflowImpl.java
│   ├── OrderFulfillApp.java     # Main application
│   ├── OrderFulfillWorker.java  # Temporal worker
│   └── Shared.java              # Shared constants
├── main/resources/
│   ├── data/                    # Test data
│   │   ├── stock_database.json
│   │   └── test_orders_short_valid.json
│   └── logback.xml              # Logging configuration
└── test/java/                   # Unit tests
    └── orderfulfillapp/
        └── OrderFulfillWorkflowTest.java
```

### Running Tests

```bash
# Run all tests
make test

# Run with Maven directly
mvn test

# Run specific test
mvn test -Dtest=OrderFulfillWorkflowTest
```

### Custom Order Processing

**Process Multiple Orders:**
```bash
make run-with-args ARGS="--numOrders 10 --invalidPercentage 20"
```

**Command Line Options:**
- `--numOrders, -n`: Number of orders to process (default: 1)
- `--invalidPercentage, -i`: Percentage of orders to make invalid (0-100, default: 0)
- `--help, -h`: Display help message

## 🏭 Business Logic

### Activities

1. **Payment Processing** (`processPayment`)
   - Validates credit card information
   - Handles expired cards with custom exceptions
   - Simulates payment gateway interaction

2. **Inventory Reservation** (`reserveInventory`)
   - Checks stock availability
   - Reserves items for the order
   - Handles inventory service downtime

3. **Order Delivery** (`deliverOrder`)
   - Manages shipping logistics
   - Tracks delivery status

4. **Approval Check** (`requireApproval`)
   - Reviews high-value orders (>$10,000)
   - Implements approval workflows

### Error Handling

- **Retry Policies**: Automatic retries for transient failures
- **Custom Exceptions**: `CreditCardExpiredException` with no retry policy
- **Circuit Breakers**: Handles downstream service failures
- **Timeouts**: Configurable activity timeouts

### Data Models

**Order Structure:**
```json
{
  "items": [
    {
      "itemName": "Low Top Sneaker (Men)",
      "itemPrice": 67.00,
      "quantity": 1
    }
  ],
  "payment": {
    "creditCard": {
      "number": "1234 5678 1234 5678",
      "expiration": "12/25"
    }
  }
}
```

## 🔧 Configuration

### Environment Variables

- `TEMPORAL_ADDRESS`: Temporal server address (default: localhost:7233)
- `TEMPORAL_NAMESPACE`: Temporal namespace (default: default)

### Application Properties

Key configurations in `pom.xml`:
- Java version: 24
- Temporal SDK: 1.25.2
- Jackson: 2.18.2
- Logging: SLF4J + Logback

## 📊 Monitoring

### Temporal Web UI

Access the Temporal Web UI at: http://localhost:8233

Features:
- Workflow execution history
- Activity logs and metrics
- Error tracking and debugging
- Performance monitoring

### Application Logs

Logs are configured via `logback.xml` and output to console by default.

```bash
# View logs in real-time (if file logging is enabled)
make logs
```

## 🧪 Testing

The application includes comprehensive tests:

- **Unit Tests**: Individual component testing
- **Integration Tests**: Workflow execution testing
- **Mock Testing**: Isolated activity testing
- **Edge Cases**: Error conditions and boundary testing

### Test Coverage

- Order calculation and validation
- Payment processing (success/failure scenarios)
- Inventory management
- Credit card validation and masking
- High-value order approval logic

## 🚀 Production Deployment

### Prerequisites for Production

1. **Temporal Cloud** or self-hosted Temporal cluster
2. **Database**: PostgreSQL/MySQL for Temporal persistence
3. **Message Queue**: Kafka for high-throughput scenarios
4. **Monitoring**: Prometheus/Grafana for metrics

### Build for Production

```bash
# Package application
make package

# The JAR will be available in target/
java -jar target/temporal-order-fulfill-0.1.0.jar
```

### Docker Deployment

```dockerfile
FROM openjdk:24-jdk-slim
COPY target/temporal-order-fulfill-0.1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🔒 Security Considerations

- Credit card numbers are masked in logs
- Secure credential handling for payment gateways
- Network security for Temporal communication
- Input validation for all order data

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Run tests: `make test`
4. Format code: `make format`
5. Submit a pull request

## 📚 Additional Resources

- [Temporal Documentation](https://docs.temporal.io/)
- [Java SDK Guide](https://docs.temporal.io/dev-guide/java)
- [Workflow Patterns](https://docs.temporal.io/encyclopedia/workflow-patterns)
- [Best Practices](https://docs.temporal.io/dev-guide/best-practices)

## 📄 License

This project is licensed under the terms specified in the [LICENSE](LICENSE) file.

## 🐛 Troubleshooting

### Common Issues

**1. Temporal Server Not Running**
```bash
# Check if Temporal is running
docker ps | grep temporal
# or
curl -I http://localhost:8233
```

**2. Java Version Issues**
```bash
# Check Java version
make java-version
# Ensure Java 21+ is installed
```

**3. Maven Build Issues**
```bash
# Clean and rebuild
make clean
make build
```

**4. Port Conflicts**
```bash
# Check if ports 7233 or 8233 are in use
lsof -i :7233
lsof -i :8233
```

For additional support, please check the [issues](../../issues) section or create a new issue.
