import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderManagementGUI extends JFrame {
    private Database database;
    private JTable orderTable;
    private DefaultTableModel tableModel;

    public OrderManagementGUI(Database database) {
        this.database = database;
        initComponents();
    }

    private void initComponents() {
        setTitle("Byte Me! - Pending Orders");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create table model
        String[] columnNames = {"Username", "VIP", "Total Amount", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(tableModel);

        // Populate table
        populateOrderTable();

        // Add table to scrollpane
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        // Style table
        orderTable.setRowHeight(30);
        orderTable.setFont(new Font("Arial", Font.PLAIN, 14));
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));

        // Add refresh and details button
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh Orders");
        refreshButton.addActionListener(e -> populateOrderTable());

        JButton detailsButton = new JButton("View Order Details");
        detailsButton.addActionListener(e -> showOrderDetails());

        buttonPanel.add(refreshButton);
        buttonPanel.add(detailsButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateOrderTable() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Get processed orders from database
        List<Order> orders = database.getProcessedOrders();

        // Add orders to table
        for (Order order : orders) {
            Object[] row = {
                    order.getCustomerUsername(),
                    order.isVip() ? "Yes" : "No",
                    "₹" + String.format("%.2f", order.getTotalAmount()),
                    order.getStatus().toString()
            };
            tableModel.addRow(row);
        }
    }

    private void showOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an order to view details.",
                    "No Order Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) tableModel.getValueAt(selectedRow, 0);
        List<Order> userOrders = database.getUserOrderHistory(username);

        if (!userOrders.isEmpty()) {
            Order selectedOrder = userOrders.get(userOrders.size() - 1);
            JTextArea detailsArea = new JTextArea(selectedOrder.toString());
            detailsArea.setEditable(false);
            JScrollPane detailsPane = new JScrollPane(detailsArea);
            detailsPane.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(
                    this,
                    detailsPane,
                    "Order Details for " + username,
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public static void showPendingOrders(Database database) {
        SwingUtilities.invokeLater(() -> {
            OrderManagementGUI gui = new OrderManagementGUI(database);
            gui.setVisible(true);
        });
    }
}