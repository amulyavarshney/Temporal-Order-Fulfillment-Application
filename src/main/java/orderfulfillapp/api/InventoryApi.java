package orderfulfillapp.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import orderfulfillapp.Shared;
import orderfulfillapp.model.OrderItem;
import orderfulfillapp.model.StockItem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API class for inventory operations, similar to the TypeScript api.ts file.
 */
public class InventoryApi {
    private static final Logger logger = LoggerFactory.getLogger(InventoryApi.class);
    private static final String STOCK_DATABASE_PATH = Shared.STOCK_DATABASE_PATH;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Reserve inventory for the given order items.
     * This simulates the inventory reservation process.
     */
    public static void reserveInventory(List<OrderItem> orderItems) throws Exception {
        List<StockItem> stockDatabase = loadStockDatabase();

        for (OrderItem orderItem : orderItems) {
            String itemName = orderItem.getItemName();

            // // // SIMULATE BUG FIX FOR INVALID DATA BUG
            // // // Removes @@@ from the end of the item name if present
            // if (itemName.endsWith("@@@")) {
            //     itemName = itemName.substring(0, itemName.length() - 3);
            //     logger.info("BUG FIX: Removed @@@ from item name: {}", itemName);
            // }

            final String finalItemName = itemName;
            StockItem stockItem = stockDatabase.stream()
                    .filter(item -> item.getItemName().equals(finalItemName))
                    .findFirst()
                    .orElse(null);

            if (stockItem == null) {
                throw new RuntimeException("Couldn't find item in stock database: " + orderItem.getItemName());
            }

            logger.info("Reserving inventory for item: {}", orderItem.getItemName());
        }
        // simulating the reservation with log statements
    }

    /**
     * Load the stock database from the JSON file.
     */
    private static List<StockItem> loadStockDatabase() throws IOException {
        try {
            // Try to read from the file system first
            if (Files.exists(Paths.get(STOCK_DATABASE_PATH))) {
                byte[] jsonData = Files.readAllBytes(Paths.get(STOCK_DATABASE_PATH));
                return objectMapper.readValue(jsonData, new TypeReference<List<StockItem>>() {});
            }
            
            // Fall back to reading from classpath
            try (InputStream inputStream = InventoryApi.class.getClassLoader()
                    .getResourceAsStream(STOCK_DATABASE_PATH)) {
                if (inputStream == null) {
                    throw new IOException("Could not find stock database file: " + STOCK_DATABASE_PATH);
                }
                return objectMapper.readValue(inputStream, new TypeReference<List<StockItem>>() {});
            }
        } catch (IOException e) {
            logger.error("Failed to load stock database from {}", STOCK_DATABASE_PATH, e);
            throw e;
        }
    }
} 