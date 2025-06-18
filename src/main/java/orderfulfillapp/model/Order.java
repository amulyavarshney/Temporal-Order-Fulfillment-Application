package orderfulfillapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents an order containing items and payment information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    @JsonProperty("items")
    private List<OrderItem> items;

    @JsonProperty("payment")
    private Payment payment;

    // Default constructor for Jackson
    public Order() {
    }

    public Order(List<OrderItem> items, Payment payment) {
        this.items = items;
        this.payment = payment;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    /**
     * Calculate the total amount from items.
     */
    public double getTotalAmount() {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(item -> item.getItemPrice() * item.getQuantity())
                .sum();
    }

    @Override
    public String toString() {
        return "Order{" +
                "items=" + items +
                ", payment=" + payment +
                ", totalAmount=" + getTotalAmount() +
                '}';
    }
} 