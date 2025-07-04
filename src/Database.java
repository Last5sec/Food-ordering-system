import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Database {
    private static Database instance;
    private static final String USERS_FILE = "users.txt";
    private static final String ORDERS_DIR = "user_orders/";
    private Map<String, String> customerAccounts;
    private TreeMap<String, MenuItem> menuItems;
    private List<Order> processedOrders;
    private List<String> reviews;

    private Database() {
        customerAccounts = new HashMap<>();
        menuItems = new TreeMap<>();
        processedOrders = new ArrayList<>();
        reviews = new ArrayList<>();
        initializeMenuItems();

        try {
            Files.createDirectories(Paths.get(ORDERS_DIR));
        } catch (IOException e) {
            System.err.println("Error creating orders directory: " + e.getMessage());
        }

        loadUserAccounts();
        initializeMenuItems();
    }

    private void loadUserAccounts() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    customerAccounts.put(parts[0], parts[1]);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing user accounts found.");
        } catch (IOException e) {
            System.err.println("Error reading user accounts: " + e.getMessage());
        }
    }

    public void addCustomerAccount(String username, String password) {
        if (customerExists(username)) {
            System.out.println("Username already exists. Please try a different one.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            writer.write(username + ":" + password);
            writer.newLine();
            customerAccounts.put(username, password);
            System.out.println("Signup successful!");
        } catch (IOException e) {
            System.err.println("Error saving user account: " + e.getMessage());
        }
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public boolean validateCustomerCredentials(String username, String password) {
        return customerAccounts.containsKey(username) && customerAccounts.get(username).equals(password);
    }

    public boolean customerExists(String username) {
        return customerAccounts.containsKey(username);
    }

    public TreeMap<String, MenuItem> getMenuItems() {
        return menuItems;
    }

    public void addMenuItem(MenuItem item) {
        menuItems.put(item.getName(), item);
    }

    public void removeMenuItem(MenuItem item) {
        menuItems.remove(item.getName());
    }

    public List<Order> getProcessedOrders() {
        return processedOrders;
    }

    private void initializeMenuItems() {
        addMenuItem(new MenuItem("Burger", 100.0, "Fast Food"));
        addMenuItem(new MenuItem("Pizza", 200.0, "Fast Food"));
        addMenuItem(new MenuItem("Pasta", 150.0, "Italian"));
        addMenuItem(new MenuItem("Salad", 80.0, "Healthy"));
        addMenuItem(new MenuItem("Soda", 50.0, "Beverage"));
    }

    public void saveReview(String username, String comment, int rating) {
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5.");
            return;
        }
        String reviewEntry = username + ": " + comment + " (Rating: " + rating + ")";
        reviews.add(reviewEntry);
        System.out.println("Review saved: " + reviewEntry);
    }

    public List<String> getReviews() {
        return reviews;
    }

    public void saveOrder(Customer customer, Map<MenuItem, Integer> cart) {
        Order order = new Order(customer.getUsername(), customer.isVip());

        for (Map.Entry<MenuItem, Integer> entry : cart.entrySet()) {
            order.addItem(entry.getKey(), entry.getValue());
        }
        processedOrders.add(order);

        String filename = ORDERS_DIR + customer.getUsername() + "_orders.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(order.toString());
            writer.newLine();
            System.out.println("Order saved to file for " + customer.getUsername());
        } catch (IOException e) {
            System.err.println("Error saving order: " + e.getMessage());
        }
    }

    public List<Order> getUserOrderHistory(String username) {
        List<Order> userOrders = new ArrayList<>();
        String filename = ORDERS_DIR + username + "_orders.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            StringBuilder orderBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Customer Username:")) {
                    if (orderBuilder.length() > 0) {
                        userOrders.add(parseOrderFromString(orderBuilder.toString()));
                        orderBuilder = new StringBuilder();
                    }
                }
                orderBuilder.append(line).append("\n");
            }

            if (orderBuilder.length() > 0) {
                userOrders.add(parseOrderFromString(orderBuilder.toString()));
            }
        } catch (FileNotFoundException e) {
            System.out.println("No order history found for " + username);
        } catch (IOException e) {
            System.err.println("Error reading order history: " + e.getMessage());
        }

        return userOrders;
    }

    private Order parseOrderFromString(String orderString) {
        String[] lines = orderString.split("\n");
        String username = lines[0].split(": ")[1];
        boolean isVip = lines[1].contains("Yes");

        Order order = new Order(username, isVip);
        return order;
    }
}