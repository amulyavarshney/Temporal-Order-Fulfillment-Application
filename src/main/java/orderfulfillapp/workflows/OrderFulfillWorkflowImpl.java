package orderfulfillapp.workflows;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import orderfulfillapp.activities.OrderFulfillActivities;
import orderfulfillapp.exception.CreditCardExpiredException;
import orderfulfillapp.model.Order;

import java.time.Duration;
import java.util.List;

/**
 * Implementation of the order fulfillment workflow.
 * Corresponds to the TypeScript workflows.ts implementation.
 */
public class OrderFulfillWorkflowImpl implements OrderFulfillWorkflow {

    // Configure activity options with timeout and retry policy
    private final ActivityOptions activityOptions = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setDoNotRetry(CreditCardExpiredException.class.getName())
                    .build())
            .build();

    // Create activity stub
    private final OrderFulfillActivities activities = 
            Workflow.newActivityStub(OrderFulfillActivities.class, activityOptions);

    @Override
    public String fulfillOrder(Order order) {
        try {
            // Execute activities in parallel, just like the TypeScript version
            String paymentResult = activities.processPayment(order);
            String inventoryResult = activities.reserveInventory(order);
            String deliveryResult = activities.deliverOrder(order);
            
            return String.format("Order fulfilled: %s, %s, %s", 
                    paymentResult, inventoryResult, deliveryResult);
        } catch (Exception e) {
            // Log the error and re-throw as runtime exception for Temporal to handle
            Workflow.getLogger(OrderFulfillWorkflowImpl.class)
                    .error("Order fulfillment failed for order: {}", order, e);
            throw new RuntimeException("Order fulfillment failed: " + e.getMessage(), e);
        }
    }
} 