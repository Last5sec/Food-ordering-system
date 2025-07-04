public class MenuItem {
    private String name;
    private double price;
    private String category;
    private boolean isAvailable;

    public MenuItem(String name, double price, String category, boolean isAvailable) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.isAvailable = isAvailable;
    }

    public MenuItem(String name, double price, String category) {
        this(name, price, category, true);
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public void setPrice(double price) { this.price = price; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }
}
