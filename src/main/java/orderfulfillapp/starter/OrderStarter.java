package orderfulfillapp.starter;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import orderfulfillapp.model.CreditCard;
import orderfulfillapp.model.Order;
import orderfulfillapp.model.OrderItem;
import orderfulfillapp.model.Payment;
import orderfulfillapp.model.StockItem;
import orderfulfillapp.workflows.OrderFulfillWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for creating and running workflow executions.
 * Corresponds to the TypeScript starter.ts file.
 */
public class OrderStarter {
    private static final Logger logger = LoggerFactory.getLogger(OrderStarter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random random = new Random();
    
    /**
     * Get default sample orders.
     */
    public static List<Order> getDefaultOrders() {
        List<OrderItem> items = List.of(
            new OrderItem("Cloudmonster Running Shoe (Men)", 126.99, 1),
            new OrderItem("2002R Sneaker (Men)", 63.00, 2)
        );
        
        CreditCard creditCard = new CreditCard("5678 1234 5678 1234", "12/24");
        Payment payment = new Payment(creditCard);
        Order order = new Order(items, payment);
        
        return List.of(order);
    }
    
    /**
     * Generate multiple orders with optional invalid percentage.
     */
    public static List<Order> generateOrders(int count, int invalidPercentage) {
        try {
            List<StockItem> stockData = loadStockDatabase();
            List<Order> orders = new ArrayList<>();
            int numInvalidOrders = (invalidPercentage * count) / 100;
            
            for (int i = 0; i < count; i++) {
                int numItems = getRandomInt(1, 3);
                List<OrderItem> items = new ArrayList<>();
                
                for (int j = 0; j < numItems; j++) {
                    int itemIndex = getRandomInt(0, stockData.size() - 1);
                    StockItem stockItem = stockData.get(itemIndex);
                    OrderItem orderItem = new OrderItem(
                        stockItem.getItemName(), 
                        stockItem.getItemPrice(), 
                        getRandomInt(1, 3)
                    );
                    items.add(orderItem);
                }
                
                CreditCard creditCard = new CreditCard("1234 5678 1234 5678", "12/25");
                Payment payment = new Payment(creditCard);
                Order order = new Order(items, payment);
                
                // Make some orders invalid
                if (i < numInvalidOrders) {
                    makeOrderInvalid(order);
                }
                
                orders.add(order);
            }
            
            return orders;
        } catch (IOException e) {
            logger.error("Failed to generate orders", e);
            return getDefaultOrders();
        }
    }
    
    /**
     * Run multiple workflows concurrently.
     */
    public static void runWorkflows(WorkflowClient client, String taskQueue, List<Order> orders) {
        List<CompletableFuture<String>> futures = new ArrayList<>();
        
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            String workflowId = String.format("order-fulfill-%d-%d", i, Instant.now().toEpochMilli());
            
            WorkflowOptions options = WorkflowOptions.newBuilder()
                    .setTaskQueue(taskQueue)
                    .setWorkflowId(workflowId)
                    .build();
            
            OrderFulfillWorkflow workflow = client.newWorkflowStub(OrderFulfillWorkflow.class, options);
            
            // Execute workflow asynchronously
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return workflow.fulfillOrder(order);
                } catch (Exception e) {
                    logger.error("Workflow {} failed", workflowId, e);
                    throw new RuntimeException(e);
                }
            });
            
            futures.add(future);
        }
        
        // Wait for all workflows to complete and log results
        for (int i = 0; i < futures.size(); i++) {
            try {
                String result = futures.get(i).get(30, TimeUnit.SECONDS);
                logger.info("Workflow {} succeeded with result: {}", i + 1, result);
            } catch (Exception e) {
                logger.error("Workflow {} failed with reason: {}", i + 1, e.getMessage());
            }
        }
    }
    
    /**
     * Make an order invalid by adding @@@ to item name.
     */
    private static void makeOrderInvalid(Order order) {
        if (!order.getItems().isEmpty()) {
            OrderItem firstItem = order.getItems().get(0);
            firstItem.setItemName(firstItem.getItemName() + "@@@");
        }
    }
    
    /**
     * Get random integer in range [min, max].
     */
    private static int getRandomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
    
    /**
     * Load stock database from JSON file.
     */
    private static List<StockItem> loadStockDatabase() throws IOException {
        String stockDatabasePath = "resources/data/stock_database.json";
        
        try {
            // Try to read from the file system first
            if (Files.exists(Paths.get(stockDatabasePath))) {
                byte[] jsonData = Files.readAllBytes(Paths.get(stockDatabasePath));
                return objectMapper.readValue(jsonData, new TypeReference<List<StockItem>>() {});
            }
            
            // Fall back to reading from classpath
            try (InputStream inputStream = OrderStarter.class.getClassLoader()
                    .getResourceAsStream(stockDatabasePath)) {
                if (inputStream == null) {
                    throw new IOException("Could not find stock database file: " + stockDatabasePath);
                }
                return objectMapper.readValue(inputStream, new TypeReference<List<StockItem>>() {});
            }
        } catch (IOException e) {
            logger.error("Failed to load stock database from {}", stockDatabasePath, e);
            throw e;
        }
    }
} 