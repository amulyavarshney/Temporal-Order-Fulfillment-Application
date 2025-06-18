package orderfulfillapp.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import orderfulfillapp.model.Order;

/**
 * Workflow interface for order fulfillment.
 * Corresponds to the TypeScript workflows.ts file.
 */
@WorkflowInterface
public interface OrderFulfillWorkflow {

    /**
     * Main workflow method that orchestrates the order fulfillment process.
     */
    @WorkflowMethod
    String fulfillOrder(Order order);
} 