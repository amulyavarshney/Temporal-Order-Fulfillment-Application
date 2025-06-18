package orderfulfillapp.activities;

import orderfulfillapp.api.InventoryApi;
import orderfulfillapp.exception.CreditCardExpiredException;
import orderfulfillapp.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Implementation of order fulfillment activities.
 * Corresponds to the TypeScript activities.ts implementation.
 */
public class OrderFulfillActivitiesImpl implements OrderFulfillActivities {
    private static final Logger logger = LoggerFactory.getLogger(OrderFulfillActivitiesImpl.class);
    private final Random random = new Random();

    @Override
    public boolean requireApproval(Order order) {
        logger.info("Checking order requires approval (over $10k)");

        // Simulate approval logic
        double totalAmount = order.getTotalAmount();
        if (totalAmount > 10000) {
            logger.info("Order requires approval");
            return true;
        }

        simulateDelay(1000);
        return false;
    }

    @Override
    public String processPayment(Order order) throws CreditCardExpiredException {
        logger.info("Processing payment...");

        // Simulate payment processing logic
        if ("12/23".equals(order.getPayment().getCreditCard().getExpiration())) {
            throw new CreditCardExpiredException("Payment failed: Credit card expired");
        }

        simulateDelay(1000);
        return "Payment processed for " + order.getItems().size() + " items";
    }

    @Override
    public String reserveInventory(Order order) throws Exception {
        // // Simulate inventory service downtime
        // // The activity will sleep the first 3 times it is called
        // // And throw an error to simulate API call timeout
        // ActivityExecutionContext context = Activity.getExecutionContext();
        // int attempt = context.getInfo().getAttempt();
        // if (attempt <= 4) {
        //     logger.info("Inventory service down, attempt {}", attempt);
        //     Thread.sleep(10000);
        //     throw new RuntimeException("Inventory service down");
        // }

        // Simulate inventory reservation logic
        logger.info("Reserving inventory...");
        InventoryApi.reserveInventory(order.getItems());

        simulateDelay(1000);
        return "Inventory reserved for " + order.getItems().size() + " items";
    }

    @Override
    public String deliverOrder(Order order) {
        // Simulate order delivery logic
        logger.info("Delivering order...");

        simulateDelay(1000);
        return "Order delivered for " + order.getItems().size() + " items";
    }

    /**
     * Simulate delay with variance, matching the TypeScript implementation.
     */
    private void simulateDelay(int sleepMs) {
        // Take sleepMs as input and introduce variance of +/- 20%
        double variance = sleepMs * 0.2;
        int finalSleepMs = sleepMs + (int) (Math.floor(random.nextDouble() * 2 * variance) - variance);
        
        logger.info("Simulating delay of {}ms", finalSleepMs);
        
        try {
            Thread.sleep(finalSleepMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted", e);
        }
    }
} 