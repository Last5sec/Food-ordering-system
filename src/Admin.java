import java.util.*;

enum OrderStatus {
    PREPARING, OUT_FOR_DELIVERY, COMPLETED, PENDING, DENIED, REFUNDED
}

interface AdminInterface {
    void addMenuItem(String name, double price, String category);
    void updateMenuItem(String name, double price);
    void removeMenuItem(String name);
    void viewOrders();
    void processRefund(String orderId);
    void generateDailyReport();
    void viewCustomerReviews();
}

public class Admin implements AdminInterface {
    private Database database;
    private Scanner scanner;

    public Admin(Database database) {
        this.database = database;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void addMenuItem(String name, double price, String category) {
        if (database.getMenuItems().containsKey(name)) {
            System.out.println("Item already exists in the menu.");
            return;
        }
        MenuItem item = new MenuItem(name, price, category);
        database.addMenuItem(item);
        System.out.println("Added " + name + " to the menu.");
    }

    @Override
    public void updateMenuItem(String name, double price) {
        for (MenuItem item : database.getMenuItems().values()) {
            if (item.getName().equalsIgnoreCase(name)) {
                item.setPrice(price);
                System.out.println("Updated " + item.getName() + " in the menu.");
                return;
            }
        }
        System.out.println("Item not found.");
    }

    @Override
    public void removeMenuItem(String name) {
        for (MenuItem item : database.getMenuItems().values()) {
            if (item.getName().equalsIgnoreCase(name)) {
                database.removeMenuItem(item);
                System.out.println("Removed " + item.getName() + " from the menu.");
                return;
            }
        }
        System.out.println("Item not found in the menu.");
    }

    public void viewMenu() {
        System.out.println("\n--- Menu Items ---");
        TreeMap<String, MenuItem> menuItems = database.getMenuItems();
        if (menuItems.isEmpty()) {
            System.out.println("The menu is empty.");
        } else {
            for (MenuItem item : menuItems.values()) {
                System.out.println(item.getName() + " - ₹" + item.getPrice() + " - " + item.getCategory());
            }
        }
    }

    @Override
    public void viewOrders() {
        System.out.println("All Orders:");
        List<Order> allOrders = database.getProcessedOrders();
        if (allOrders.isEmpty()) {
            System.out.println("No orders available.");
        } else {
            for (Order order : allOrders) {
                String vipStatus = order.isVip() ? " (VIP)" : "";
                System.out.println("Customer Username: " + order.getCustomerUsername() + vipStatus);
                System.out.println("Total Amount: ₹" + order.getTotalAmount());
                System.out.println("Status: " + order.getStatus());
                System.out.println("---");
            }
        }
    }

    @Override
    public void processRefund(String username) {
        for (Order order : database.getProcessedOrders()) {
            if (order.getCustomerUsername().equals(username) &&
                    order.getStatus() == OrderStatus.DENIED) {
                System.out.println("Refund processed for Customer: " + username);
                order.setStatus(OrderStatus.REFUNDED);
                return;
            }
        }
        System.out.println("No eligible orders found for the customer or the order is not denied.");
    }

    @Override
    public void generateDailyReport() {
        List<Order> completedOrders = new ArrayList<>();
        for (Order order : database.getProcessedOrders()) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                completedOrders.add(order);
            }
        }
        double totalRevenue = 0;
        for (Order order : completedOrders) {
            totalRevenue += order.getTotalAmount();
        }
        String report = "Daily Report - Total Revenue: ₹" + totalRevenue;
        System.out.println(report);
    }

    @Override
    public void viewCustomerReviews() {
        List<String> reviews = database.getReviews();
        System.out.println("\n--- Customer Reviews ---");
        if (reviews.isEmpty()) {
            System.out.println("No reviews submitted yet.");
        } else {
            for (String review : reviews) {
                System.out.println(review);
            }
        }
    }

    public void displayAdminMenu() {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. Add Menu Item");
            System.out.println("2. Update Menu Item");
            System.out.println("3. Remove Menu Item");
            System.out.println("4. View Menu (GUI)");
            System.out.println("5. View Orders (GUI)");
            System.out.println("6. Process Refund");
            System.out.println("7. Generate Daily Report");
            System.out.println("8. View Customer Reviews");
            System.out.println("9. Update Order Status");
            System.out.println("10. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    handleAddMenuItem();
                    break;
                case 2:
                    handleUpdateMenuItem();
                    break;
                case 3:
                    handleRemoveMenuItem();
                    break;
                case 4:
                    viewMenu();
                    MenuManagementGUI.showMenuBrowser(database);
                    break;
                case 5:
                    viewOrders();
                    OrderManagementGUI.showPendingOrders(database);
                    break;
                case 6:
                    handleProcessRefund();
                    break;
                case 7:
                    generateDailyReport();
                    break;
                case 8:
                    viewCustomerReviews();
                    break;
                case 9:
                    handleUpdateOrderStatus();
                    break;
                case 10:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleAddMenuItem() {
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter item price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter item category: ");
        String category = scanner.nextLine();
        addMenuItem(name, price, category);
    }

    private void handleUpdateMenuItem() {
        System.out.print("Enter item name to update: ");
        String name = scanner.nextLine();
        System.out.print("Enter new price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        updateMenuItem(name, price);
    }

    private void handleRemoveMenuItem() {
        System.out.print("Enter item name to remove: ");
        String name = scanner.nextLine();
        removeMenuItem(name);
    }

    private void handleProcessRefund() {
        System.out.print("Enter username for refund: ");
        String orderId = scanner.nextLine();
        processRefund(orderId);
    }

    public void updateOrderStatus(String username, String newStatus) {
        if (!newStatus.equalsIgnoreCase("COMPLETED") && !newStatus.equalsIgnoreCase("DENIED")) {
            System.out.println("Please choose 'COMPLETED' or 'DENIED'");
            return;
        }
        for (Order order : database.getProcessedOrders()) {
            if (order.getCustomerUsername().equals(username) &&
                    order.getStatus() == OrderStatus.PENDING) {
                OrderStatus status = OrderStatus.valueOf(newStatus.toUpperCase());
                order.setStatus(status);
                System.out.println("Order status updated to " + status + " for Customer: " + username);
                return;
            }
        }
        System.out.println("No PENDING orders found for Customer: " + username);
    }

    private void handleUpdateOrderStatus() {
        System.out.print("Enter customer username to update order status: ");
        String username = scanner.nextLine();
        System.out.print("Enter new status (COMPLETED or DENIED): ");
        String newStatus = scanner.nextLine();
        updateOrderStatus(username, newStatus);
    }
}
