package GUI;

import BLL.OrderBLL;
import Model.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class OrderManagementPanel extends JPanel {
    private OrderBLL orderBLL = new OrderBLL();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;

    public OrderManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("🔄 Làm mới");
        topPanel.add(btnRefresh);

        // Table
        String[] cols = {"Mã đơn", "Khách hàng", "Ngày đặt", "Tổng tiền", "Trạng thái", "Thao tác"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(40);
        JScrollPane scroll = new JScrollPane(table);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Load data
        refreshData();

        btnRefresh.addActionListener(e -> refreshData());

        // Double click để cập nhật trạng thái
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    showUpdateStatusDialog(table.getSelectedRow());
                }
            }
        });
    }

    public void refreshData() {
        model.setRowCount(0);
        List<Order> orders = orderBLL.getAllOrders();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Order o : orders) {
            model.addRow(new Object[]{
                o.getOrderId(),
                o.getCustomerName(),
                o.getOrderDate() != null ? sdf.format(o.getOrderDate()) : "",
                o.getTotalAmount(),
                o.getStatus(),
                "👁 ✏️"
            });
        }
    }

    private void showUpdateStatusDialog(int row) {
        int orderId = (int) model.getValueAt(row, 0);
        String[] statuses = {"Pending", "Processing", "Shipped", "Delivered", "Cancelled"};
        JComboBox<String> cmbStatus = new JComboBox<>(statuses);
        cmbStatus.setSelectedItem(model.getValueAt(row, 4));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Cập nhật trạng thái đơn hàng:"));
        panel.add(cmbStatus);

        int result = JOptionPane.showConfirmDialog(this, panel, "Cập nhật trạng thái", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newStatus = (String) cmbStatus.getSelectedItem();
            try {
                orderBLL.updateOrderStatus(orderId, newStatus);
                refreshData();
                JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}