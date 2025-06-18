# Temporal Order Fulfillment Application Makefile
# Author: Generated for Temporal Order Fulfillment Application
# Description: Build, test, and run the Temporal order fulfillment application

.PHONY: help build clean compile test package worker run install deps temporal-up temporal-down logs format

# Default target
help: ## Show this help message
	@echo "Temporal Order Fulfillment Application"
	@echo "====================================="
	@echo "Available targets:"
	@echo ""
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

# Build and compilation targets
deps: ## Install dependencies
	@echo "Installing dependencies..."
	@mvn dependency:resolve -q

compile: ## Compile the application
	@echo "Compiling application..."
	@mvn compile -q -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

build: compile ## Build the application (compile + resources)
	@echo "Building application..."
	@mvn compile resources:resources -q -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

test: ## Run tests
	@echo "Running tests..."
	@mvn test -Dorg.slf4j.simpleLogger.defaultLogLevel=info

package: ## Package the application into JAR
	@echo "Packaging application..."
	@mvn package -q -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

install: ## Install the package to local repository
	@echo "Installing to local repository..."
	@mvn clean install -q -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

clean: ## Clean build artifacts
	@echo "Cleaning build artifacts..."
	@mvn clean -q

# Application execution targets
worker: build ## Start the Temporal worker
	@echo "Starting Temporal worker..."
	@mvn exec:java -Dexec.mainClass="orderfulfillapp.OrderFulfillWorker" -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

run: build ## Run the order fulfillment application
	@echo "Running order fulfillment application..."
	@mvn exec:java -Dexec.mainClass="orderfulfillapp.OrderFulfillApp" -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

run-with-args: build ## Run with custom arguments (use ARGS="--numOrders 5 --invalidPercentage 10")
	@echo "Running with arguments: $(ARGS)"
	@mvn exec:java -Dexec.mainClass="orderfulfillapp.OrderFulfillApp" -Dexec.args="$(ARGS)" -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

# Temporal server management (requires Docker)
temporal-up: ## Start Temporal server (requires Docker)
	@echo "Starting Temporal server..."
	@if command -v docker >/dev/null 2>&1; then \
		docker run --rm -d -p 7233:7233 -p 8233:8233 --name temporal-server temporalio/auto-setup:latest; \
		echo "Temporal server started at http://localhost:8233"; \
		echo "gRPC endpoint: localhost:7233"; \
	else \
		echo "Docker not found. Please install Docker or start Temporal server manually."; \
		echo "See: https://docs.temporal.io/dev-guide/introduction#set-up-a-local-temporal-service"; \
	fi

temporal-down: ## Stop Temporal server
	@echo "Stopping Temporal server..."
	@if command -v docker >/dev/null 2>&1; then \
		docker stop temporal-server || true; \
	else \
		echo "Docker not found. Please stop Temporal server manually."; \
	fi

# Development and maintenance targets
format: ## Format Java code (requires google-java-format)
	@echo "Formatting Java code..."
	@if command -v google-java-format >/dev/null 2>&1; then \
		find src -name "*.java" -exec google-java-format --replace {} \; ; \
	else \
		echo "google-java-format not found. Skipping formatting."; \
		echo "Install: https://github.com/google/google-java-format"; \
	fi

logs: ## Show recent logs (if using file logging)
	@if [ -f "logs/application.log" ]; then \
		tail -f logs/application.log; \
	else \
		echo "No log file found. Logs are printed to console."; \
	fi

check-deps: ## Check for dependency updates
	@echo "Checking for dependency updates..."
	@mvn versions:display-dependency-updates

# Quality and security targets
security-check: ## Run security vulnerability check
	@echo "Running security vulnerability check..."
	@mvn org.owasp:dependency-check-maven:check

verify: test ## Run all verification (tests + checks)
	@echo "Running verification..."
	@mvn verify -Dorg.slf4j.simpleLogger.defaultLogLevel=info

# Quick start targets
quick-start: temporal-up worker ## Quick start: Start Temporal + Worker

demo: build ## Run a demo with sample orders
	@echo "Running demo with sample orders..."
	@mvn exec:java -Dexec.mainClass="orderfulfillapp.OrderFulfillApp" -Dexec.args="--numOrders 3 --invalidPercentage 0" -Dorg.slf4j.simpleLogger.defaultLogLevel=info

# Java version check
java-version: ## Check Java version
	@echo "Java version:"
	@java -version
	@echo ""
	@echo "Maven version:"
	@mvn -version
