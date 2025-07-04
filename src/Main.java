import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

public class Main{
    private static final Scanner scanner = new Scanner(System.in);
    private static final Database database = Database.getInstance();
    static Customer currentCustomer;

    public static void main(String[] args){
        TreeMap<String, MenuItem> menuItems = database.getMenuItems();

        while (true){
            System.out.println("\n=== Welcome to Byte Me! ===");
            System.out.println("1. Login as Customer");
            System.out.println("2. Login as Admin");
            System.out.println("3. Signup");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            try{
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice){
                    case 1:
                        loginAsCustomer(menuItems);
                        break;
                    case 2:
                        loginAsAdmin();
                        break;
                    case 3:
                        signup();
                        break;
                    case 4:
                        System.out.println("Exiting. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }catch(InputMismatchException e){
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                scanner.nextLine();
            }
        }
    }

    private static void loginAsCustomer(TreeMap<String, MenuItem> menuItems){
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if(database.validateCustomerCredentials(username, password)){
            System.out.println("Welcome, " + username);

            currentCustomer = new Customer(username, menuItems, database);
            currentCustomer.displayMainMenu();
        }else{
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    private static void loginAsAdmin(){
        String adminUsername = "admin";
        String adminPassword = "admin123";

        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        if(adminUsername.equals(username) && adminPassword.equals(password)){
            System.out.println("Admin Main Menu Displayed.");
            Admin admin = new Admin(database);
            admin.displayAdminMenu();
        }else{
            System.out.println("Invalid admin username or password. Please try again.");
        }
    }

    private static void signup(){
        System.out.print("Enter a new username: ");
        String username = scanner.nextLine();
        System.out.print("Enter a new password: ");
        String password = scanner.nextLine();

        if(database.customerExists(username)){
            System.out.println("Username already exists. Please try a different one.");
        }else{
            database.addCustomerAccount(username, password);
            System.out.println("Signup successful! You can now login.");
        }
    }
}
