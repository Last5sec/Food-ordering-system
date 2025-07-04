import java.util.HashMap;
import java.util.Map;

public class Order {
    private String customerUsername;
    private boolean isVip;
    private double totalAmount;
    private OrderStatus status;
    private Map<MenuItem, Integer> items;

    public Order(String customerUsername, boolean isVip) {
        this.customerUsername = customerUsername;
        this.isVip = isVip;
        this.items = new HashMap<>();
        this.status = OrderStatus.PENDING;
        this.totalAmount = 0.0;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("Customer Username: ").append(customerUsername).append("\n");
        sb.append("VIP: ").append(isVip ? "Yes" : "No").append("\n");
        sb.append("Total Amount: ₹").append(String.format("%.2f", totalAmount)).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Items:\n");

        if (items.isEmpty()) {
            sb.append("  No items in this order.\n");
        } else {
            items.forEach((menuItem, quantity) ->
                    sb.append("  - ").append(menuItem.getName())
                            .append(" (Quantity: ").append(quantity)
                            .append(", Price: ₹").append(String.format("%.2f", menuItem.getPrice()))
                            .append(")\n")
            );
        }
        return sb.toString();
    }

    public boolean isVip() {
        return isVip;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void addItem(MenuItem item, int quantity) {
        items.put(item, quantity);
        totalAmount += item.getPrice() * quantity;
    }

    public Iterable<Object> getItems() {
        return null;
    }
}
