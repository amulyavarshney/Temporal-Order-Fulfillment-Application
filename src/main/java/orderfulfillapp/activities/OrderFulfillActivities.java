package orderfulfillapp.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import orderfulfillapp.model.Order;

/**
 * Activity interface for order fulfillment operations.
 * Corresponds to the TypeScript activities.ts file.
 */
@ActivityInterface
public interface OrderFulfillActivities {

    /**
     * Check if the order requires approval (over $10k).
     */
    @ActivityMethod
    boolean requireApproval(Order order);

    /**
     * Process payment for the order.
     */
    @ActivityMethod
    String processPayment(Order order) throws Exception;

    /**
     * Reserve inventory for the order items.
     */
    @ActivityMethod
    String reserveInventory(Order order) throws Exception;

    /**
     * Deliver the order to the customer.
     */
    @ActivityMethod
    String deliverOrder(Order order);
} 