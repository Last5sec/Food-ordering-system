import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.TreeMap;

public class MenuManagementGUI extends JFrame {
    private Database database;
    private JTable menuTable;
    private DefaultTableModel tableModel;

    public MenuManagementGUI(Database database) {
        this.database = database;
        initComponents();
    }

    private void initComponents() {
        setTitle("Byte Me! - Menu Browser");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create table model
        String[] columnNames = {"Name", "Price", "Category"};
        tableModel = new DefaultTableModel(columnNames, 0);
        menuTable = new JTable(tableModel);

        // Populate table
        populateMenuTable();

        // Add table to scrollpane
        JScrollPane scrollPane = new JScrollPane(menuTable);
        add(scrollPane, BorderLayout.CENTER);

        // Style table
        menuTable.setRowHeight(30);
        menuTable.setFont(new Font("Arial", Font.PLAIN, 14));
        menuTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));

        // Add refresh button
        JButton refreshButton = new JButton("Refresh Menu");
        refreshButton.addActionListener(e -> populateMenuTable());
        add(refreshButton, BorderLayout.SOUTH);
    }

    private void populateMenuTable() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Get menu items from database
        TreeMap<String, MenuItem> menuItems = database.getMenuItems();

        // Add items to table
        for (MenuItem item : menuItems.values()) {
            Object[] row = {
                    item.getName(),
                    "₹" + String.format("%.2f", item.getPrice()),
                    item.getCategory()
            };
            tableModel.addRow(row);
        }
    }

    public static void showMenuBrowser(Database database) {
        SwingUtilities.invokeLater(() -> {
            MenuManagementGUI gui = new MenuManagementGUI(database);
            gui.setVisible(true);
        });
    }
}