package orderfulfillapp;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import orderfulfillapp.activities.OrderFulfillActivitiesImpl;
import orderfulfillapp.workflows.OrderFulfillWorkflowImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Worker application for order fulfillment.
 * Run a Worker with either mTLS or API key authentication.
 * Configuration is provided via environment variables.
 * 
 * For mTLS: Requires clientCertPath and clientKeyPath
 * For API key: Requires clientApiKey
 * Note that serverNameOverride and serverRootCACertificate are optional.
 */
public class OrderFulfillWorker {
    private static final Logger logger = LoggerFactory.getLogger(OrderFulfillWorker.class);

    public static void main(String[] args) {

        // Create a workflow service stub
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

        // Create a workflow service client which can be start, signal, query, and cancel workflow executions. 
        WorkflowClient client = WorkflowClient.newInstance(service);

        // Create a workflow worker factory. It is used to create workers that poll specific task queues for workflows and activities to execute.
        WorkerFactory factory = WorkerFactory.newInstance(client);

        // Create a workflow worker that polls the OrderFulfillTaskQueue for workflows and activities to execute.
        Worker worker = factory.newWorker(Shared.ORDER_FULFILL_TASK_QUEUE);

        // Register workflow and activities
        worker.registerWorkflowImplementationTypes(OrderFulfillWorkflowImpl.class);
        worker.registerActivitiesImplementations(new OrderFulfillActivitiesImpl());

        // Start the worker
        factory.start();
    }
} 