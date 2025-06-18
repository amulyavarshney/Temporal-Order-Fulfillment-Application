package orderfulfillapp;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;

import orderfulfillapp.model.Order;
import orderfulfillapp.starter.OrderStarter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Main client application for order fulfillment.
 * Schedule a Workflow connecting with either mTLS or API key authentication.
 * Configuration is provided via environment variables.
 * 
 * For mTLS: Requires clientCertPath and clientKeyPath
 * For API key: Requires clientApiKey
 * Note that serverNameOverride and serverRootCACertificate are optional.
 */
public class OrderFulfillApp {
    private static final Logger logger = LoggerFactory.getLogger(OrderFulfillApp.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        
        // Parse command line arguments
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        
        try {
            CommandLine cmd = parser.parse(options, args);
            
            if (cmd.hasOption("h")) {
                printHelp(options);
                return;
            }
            
            int numOrders = Integer.parseInt(cmd.getOptionValue("n", "1"));
            int invalidPercentage = Integer.parseInt(cmd.getOptionValue("i", "0"));
            
            // Create a workflow service stub
            WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
            
            // Create a workflow service client
            WorkflowClient client = WorkflowClient.newInstance(service);
            
            // Load and process orders
            List<Order> orders;
            if (numOrders == 1 && invalidPercentage == 0) {
                // Load test orders from JSON file
                orders = loadTestOrders();
                if (orders.isEmpty()) {
                    logger.info("No test orders found, using default orders");
                    orders = OrderStarter.getDefaultOrders();
                }
            } else {
                // Generate orders with specified parameters
                orders = OrderStarter.generateOrders(numOrders, invalidPercentage);
            }
            
            logger.info("Processing {} orders", orders.size());
            
            // Execute the workflows
            OrderStarter.runWorkflows(client, Shared.ORDER_FULFILL_TASK_QUEUE, orders);
            
            logger.info("All workflows completed");
            
        } catch (ParseException e) {
            logger.error("Error parsing command line arguments: {}", e.getMessage());
            printHelp(options);
            System.exit(1);
        }
    }
    
    private static Options createOptions() {
        Options options = new Options();
        
        options.addOption(Option.builder("n")
                .longOpt("numOrders")
                .hasArg()
                .desc("Number of orders to process (default: 1)")
                .build());
                
        options.addOption(Option.builder("i")
                .longOpt("invalidPercentage")
                .hasArg()
                .desc("Percentage of orders to make invalid 0-100 (default: 0)")
                .build());
                
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Display this help message")
                .build());
                
        return options;
    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("OrderFulfillApp", options);
    }
    
    /**
     * Load test orders from the JSON file.
     */
    private static List<Order> loadTestOrders() {
        String testOrdersPath = "data/test_orders_short_valid.json";
        
        try (InputStream inputStream = OrderFulfillApp.class.getClassLoader()
                .getResourceAsStream(testOrdersPath)) {
            
            if (inputStream == null) {
                logger.warn("Could not find test orders file: {}", testOrdersPath);
                return List.of();
            }
            
            return objectMapper.readValue(inputStream, new TypeReference<List<Order>>() {});
            
        } catch (IOException e) {
            logger.error("Failed to load test orders from {}", testOrdersPath, e);
            return List.of();
        }
    }
} 