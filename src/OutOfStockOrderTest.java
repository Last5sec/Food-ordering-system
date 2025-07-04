import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.TreeMap;

public class OutOfStockOrderTest {
    private Customer customer;
    private Database database;
    private TreeMap<String, MenuItem> menuItems;

    @Before
    public void setUp() {
        // Reset the database to ensure a clean state
        database = Database.getInstance();
        menuItems = database.getMenuItems();
    }

    @Test
    public void testAddItemToCart() {
        customer = new Customer("testUser", menuItems, database);

        MenuItem burger = menuItems.get("Burger");
        assertNotNull("Burger should exist in menu", burger);
        customer.addToCart(burger, 1);

        assertEquals("Cart should contain the added item", 1, customer.getCart().size());
        assertTrue("Cart should contain the burger", customer.getCart().containsKey(burger));
    }

    @Test
    public void testUpdateCartQuantity() {
        customer = new Customer("testUser", menuItems, database);

        MenuItem burger = menuItems.get("Burger");
        assertNotNull("Burger should exist in menu", burger);

        customer.addToCart(burger, 1);

        customer.updateCartQuantity(burger, 3);
        assertEquals("Cart quantity should be updated", 3, customer.getCart().get(burger).intValue());
    }

    @Test
    public void testRemoveItemFromCart() {
        customer = new Customer("testUser", menuItems, database);

        MenuItem burger = menuItems.get("Burger");
        assertNotNull("Burger should exist in menu", burger);

        customer.addToCart(burger, 1);
        customer.removeFromCart(burger);
        assertTrue("Cart should be empty after removing the item", customer.getCart().isEmpty());
    }

    @Test
    public void testCartWithMultipleItems() {
        customer = new Customer("testUser", menuItems, database);

        MenuItem burger = menuItems.get("Burger");
        MenuItem pizza = menuItems.get("Pizza");

        assertNotNull("Burger should exist in menu", burger);
        assertNotNull("Pizza should exist in menu", pizza);

        customer.addToCart(burger, 1);
        customer.addToCart(pizza, 2);

        assertEquals("Cart should contain 2 unique items", 2, customer.getCart().size());
        assertEquals("Burger quantity should be 1", 1, customer.getCart().get(burger).intValue());
        assertEquals("Pizza quantity should be 2", 2, customer.getCart().get(pizza).intValue());
    }
}