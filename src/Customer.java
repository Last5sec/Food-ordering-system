import java.util.*;

public class Customer{
    private String username;
    private boolean isVip;
    private TreeMap<MenuItem, Integer> cart;
    private Scanner scanner;
    private static final double VIP_COST = 100.0;
    private TreeMap<String, MenuItem> menuItems;
    private Database database;

    public Customer(String username, TreeMap<String, MenuItem> menuItems, Database database){
        this.username = username;
        this.isVip = false;
        this.cart = new TreeMap<>((a, b) -> a.getName().compareTo(b.getName()));
        this.scanner = new Scanner(System.in);
        this.menuItems = menuItems;
        this.database = database;
    }

    public void displayMainMenu(){
        while (true){
            System.out.println("\nCustomer Menu");
            System.out.println("1. View Menu");
            System.out.println("2. Search Menu");
            System.out.println("3. Sort Menu by Price");
            System.out.println("4. Cart Operations");
            System.out.println("5. Place Order");
            System.out.println("6. Submit Review");
            System.out.println("7. Upgrade to VIP");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try{
                choice = Integer.parseInt(scanner.nextLine());
            }catch(NumberFormatException e){
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch(choice){
                case 1:
                    viewMenu();
                    break;
                case 2:
                    handleSearch();
                    break;
                case 3:
                    sortAndDisplayMenuByPrice();
                    break;
                case 4:
                    handleCartOperations();
                    break;
                case 5:
                    placeOrder();
                    break;
                case 6:
                    handleReview();
                    break;
                case 7:
                    upgradeToVip();
                    break;
                case 8:
                    System.out.println("Exiting. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleSearch(){
        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine().trim().toLowerCase();

        List<MenuItem> results = new ArrayList<>();
        for (MenuItem item : menuItems.values()){
            if (item.getName().toLowerCase().contains(searchTerm)){
                results.add(item);
            }
        }

        if(results.isEmpty()){
            System.out.println("No items found for the search term: " + searchTerm);
        }else{
            System.out.println("\n--- Search Results ---");
            for (MenuItem item : results) {
                System.out.println(item.getName() + " - ₹" + item.getPrice() +
                        " - " + item.getCategory());
            }
        }
    }

    private void handleCartOperations() {
        while (true) {
            System.out.println("\nCart Operations:");
            System.out.println("1. Add Item to Cart");
            System.out.println("2. Update Item Quantity in Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Remove Item from Cart");
            System.out.println("5. Go Back");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    handleAddItemToCart();
                    break;
                case 2:
                    handleUpdateItemQuantity();
                    break;
                case 3:
                    displayCart();
                    break;
                case 4:
                    handleRemoveItemFromCart();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleAddItemToCart() {
        System.out.print("Enter the item name: ");
        String itemName = scanner.nextLine().trim();
        MenuItem item = null;
        for (MenuItem menuItem : menuItems.values()) {
            if (menuItem.getName().equalsIgnoreCase(itemName)) {
                item = menuItem;
                break;
            }
        }

        if (item == null) {
            System.out.println("Item not found or not available.");
            return;
        }

        System.out.print("Enter the quantity: ");
        String input = scanner.nextLine();

        int quantity;
        try {
            quantity = Integer.parseInt(input);
            if (quantity <= 0) {
                System.out.println("Quantity must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity. Please enter a number.");
            return;
        }
        addToCart(item, quantity);
    }

    private void handleUpdateItemQuantity() {
        System.out.print("Enter the item name: ");
        String itemName = scanner.nextLine().trim();
        MenuItem itemToUpdate = null;
        for (MenuItem cartItem : cart.keySet()) {
            if (cartItem.getName().equalsIgnoreCase(itemName)) {
                itemToUpdate = cartItem;
                break;
            }
        }
        if (itemToUpdate == null) {
            System.out.println("Item not found in cart.");
            return;
        }
        System.out.print("Enter the new quantity: ");
        String input = scanner.nextLine();

        int quantity;
        try {
            quantity = Integer.parseInt(input);
            if (quantity < 0) {
                System.out.println("Quantity cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity. Please enter a number.");
            return;
        }
        updateCartQuantity(itemToUpdate, quantity);
    }

    private void handleRemoveItemFromCart() {
        System.out.print("Enter the item name to remove: ");
        String itemName = scanner.nextLine().trim();

        for (MenuItem item : cart.keySet()) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                removeFromCart(item);
                return;
            }
        }
        System.out.println("Item not found in cart.");
    }

    private void handleReview() {
        System.out.print("Enter your review comment: ");
        String comment = scanner.nextLine().trim();

        System.out.print("Enter your rating (1 to 5): ");
        int rating;
        try {
            rating = Integer.parseInt(scanner.nextLine());
            if (rating < 1 || rating > 5) {
                System.out.println("Invalid rating. Please enter a rating between 1 and 5.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid rating. Please enter a number between 1 and 5.");
            return;
        }
        submitReview(comment, rating);
    }

    public boolean isVip() {
        return isVip;
    }

    public void upgradeToVip() {
        if (!isVip) {
            isVip = true;
            System.out.println("Upgraded to VIP status for ₹" + VIP_COST);
        } else {
            System.out.println("You are already a VIP member.");
        }
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

    public void addToCart(MenuItem item, int quantity) {
        cart.put(item, quantity);
    }

    public void updateCartQuantity(MenuItem item, int newQuantity) {
        if (newQuantity <= 0) {
            cart.remove(item);
            System.out.println("Removed " + item.getName() + " from cart");
        } else {
            cart.put(item, newQuantity);
            System.out.println("Updated quantity of " + item.getName() + " to " + newQuantity);
        }
    }

    public void removeFromCart(MenuItem item) {
        cart.remove(item);
        System.out.println("Removed " + item.getName() + " from cart");
    }

    public void displayCart() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
        } else {
            System.out.println("\n--- Cart Items ---");
            for (Map.Entry<MenuItem, Integer> entry : cart.entrySet()) {
                MenuItem item = entry.getKey();
                int quantity = entry.getValue();
                System.out.println(item.getName() + " - ₹" + item.getPrice() + " x " + quantity);
            }
        }
    }

    public void placeOrder() {
        Map<MenuItem, Integer> cart = getCart();

        if (cart.isEmpty()) {
            System.out.println("Your cart is empty. Please add items before placing an order.");
            return;
        }

        database.saveOrder(Main.currentCustomer, cart);
    }

    public Map<MenuItem, Integer> getCart() {
        return cart;
    }


    public void submitReview(String comment, int rating) {
        database.saveReview(username, comment, rating);
        System.out.println("Your review has been submitted.");
    }

    public void sortAndDisplayMenuByPrice() {
        System.out.println("\n--- Menu Sorted by Price ---");
        menuItems.values().stream()
                .sorted(Comparator.comparingDouble(MenuItem::getPrice))
                .forEach(item -> System.out.println(item.getName() + " - ₹" + item.getPrice() + " - " + item.getCategory()));
    }

    public String getUsername() {
        return username;
    }
}