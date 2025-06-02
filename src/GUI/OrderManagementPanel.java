package GUI;
import bll.OrderBLL;
import Model.Order;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Date;
import java.util.Locale;
import javax.swing.border.Border;
import java.sql.SQLException;

public class OrderManagementPanel extends JPanel {
    private OrderBLL orderBLL = new OrderBLL();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cmbStatus;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;
    private String searchPlaceholder = "Mã đơn hàng hoặc tên khách hàng...";
    
    public OrderManagementPanel() {
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        
        // Search Panel
        JPanel searchPanel = createSearchPanel();
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.SOUTH);
        
        // Load initial data
        refreshData();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel("🛒");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        JLabel titleLabel = new JLabel("Quản lý đơn hàng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));
        
        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        
        // Add new order button
        JButton btnAddOrder = new JButton("+ Thêm đơn hàng mới");
        btnAddOrder.setBackground(new Color(0, 123, 255));
        btnAddOrder.setForeground(Color.WHITE);
        btnAddOrder.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddOrder.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAddOrder.setFocusPainted(false);
        btnAddOrder.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add action listener for add button
        btnAddOrder.addActionListener(e -> showAddOrderDialog());
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnAddOrder, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchRow.setBackground(Color.WHITE);
        
        // Search field
        JLabel lblSearch = new JLabel("Tìm kiếm");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSearch.setForeground(new Color(73, 80, 87));
        
        txtSearch = new JTextField(searchPlaceholder);
        txtSearch.setPreferredSize(new Dimension(400, 40));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtSearch.setBackground(Color.WHITE);
        txtSearch.setForeground(new Color(108, 117, 125));
        
        // Add placeholder behavior
        setupPlaceholder(txtSearch);
        
        // Status filter
        JLabel lblStatus = new JLabel("Trạng thái");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblStatus.setForeground(new Color(73, 80, 87));
        
        cmbStatus = new JComboBox<>(new String[]{"Tất cả", "Chờ xử lý", "Đang xử lý", "Đang giao hàng", "Đã giao hàng", "Đã hủy"});
        cmbStatus.setPreferredSize(new Dimension(200, 40));
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbStatus.setBackground(Color.WHITE);
        cmbStatus.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        
        // Search button
        JButton btnSearch = new JButton("🔍 Tìm kiếm");
        btnSearch.setPreferredSize(new Dimension(150, 40));
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSearch.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add search functionality
        btnSearch.addActionListener(e -> performSearch());
        
        // Reset button
        JButton btnReset = new JButton("🔄 Làm mới");
        btnReset.setPreferredSize(new Dimension(120, 40));
        btnReset.setBackground(new Color(108, 117, 125));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReset.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnReset.setFocusPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnReset.addActionListener(e -> resetSearch());
        
        searchRow.add(lblSearch);
        searchRow.add(txtSearch);
        searchRow.add(lblStatus);
        searchRow.add(cmbStatus);
        searchRow.add(btnSearch);
        searchRow.add(btnReset);
        
        searchPanel.add(searchRow, BorderLayout.CENTER);
        
        return searchPanel;
    }
    
    private void setupPlaceholder(JTextField textField) {
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(searchPlaceholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(searchPlaceholder);
                    textField.setForeground(new Color(108, 117, 125));
                }
            }
        });
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(248, 249, 250));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Table container with shadow
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                new ShadowBorder()
            ),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Table setup
        String[] columns = {"ID", "Khách hàng", "Ngày đặt", "Tổng tiền", "Trạng thái", "Thao tác"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(model);
        customizeTable();
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        // Pagination panel
        JPanel paginationPanel = createPaginationPanel();
        tableContainer.add(paginationPanel, BorderLayout.SOUTH);
        
        tablePanel.add(tableContainer, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private void customizeTable() {
        // Table appearance
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(233, 236, 239));
        table.setSelectionBackground(new Color(230, 247, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Header styling
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(new Color(73, 80, 87));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(233, 236, 239)));
        table.getTableHeader().setReorderingAllowed(false);
        
        // Column widths
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // ID
        columnModel.getColumn(1).setPreferredWidth(150); // Khách hàng
        columnModel.getColumn(2).setPreferredWidth(100); // Ngày đặt
        columnModel.getColumn(3).setPreferredWidth(120); // Tổng tiền
        columnModel.getColumn(4).setPreferredWidth(120); // Trạng thái
        columnModel.getColumn(5).setPreferredWidth(150); // Thao tác
        
        // Custom cell renderers
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionCellRenderer());
        
        // Double click event
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    showOrderDetails(table.getSelectedRow());
                }
            }
        });
    }
    
    private JPanel createPaginationPanel() {
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.setBackground(Color.WHITE);
        paginationPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Pagination buttons
        JButton btnPrev = createPaginationButton("‹");
        JButton btnPage1 = createPaginationButton("1");
        JButton btnPage2 = createPaginationButton("2");
        JButton btnPage3 = createPaginationButton("3");
        JButton btnNext = createPaginationButton("›");
        
        // Set active page
        btnPage1.setBackground(new Color(0, 123, 255));
        btnPage1.setForeground(Color.WHITE);
        
        paginationPanel.add(btnPrev);
        paginationPanel.add(btnPage1);
        paginationPanel.add(btnPage2);
        paginationPanel.add(btnPage3);
        paginationPanel.add(btnNext);
        
        return paginationPanel;
    }
    
    private JButton createPaginationButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(35, 35));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(0, 123, 255));
        button.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            setHorizontalAlignment(CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            
            String status = value.toString();
            switch (status) {
                case "Đã giao hàng":
                case "Delivered":
                    setBackground(new Color(212, 237, 218));
                    setForeground(new Color(21, 87, 36));
                    setText("Đã giao hàng");
                    break;
                case "Đang giao hàng":
                case "Shipped":
                    setBackground(new Color(255, 243, 205));
                    setForeground(new Color(102, 77, 3));
                    setText("Đang giao hàng");
                    break;
                case "Đang xử lý":
                case "Processing":
                    setBackground(new Color(217, 237, 247));
                    setForeground(new Color(12, 84, 96));
                    setText("Đang xử lý");
                    break;
                case "Chờ xử lý":
                case "Pending":
                    setBackground(new Color(255, 248, 220));
                    setForeground(new Color(133, 100, 4));
                    setText("Chờ xử lý");
                    break;
                default:
                    setBackground(new Color(253, 236, 234));
                    setForeground(new Color(114, 28, 36));
                    setText("Đã hủy");
                    break;
            }
            
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            setOpaque(true);
            
            return this;
        }
    }
    
    // Custom cell renderer for action column
    private class ActionCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 8));
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            
            // View button
            JButton btnView = new JButton("👁");
            btnView.setPreferredSize(new Dimension(32, 32));
            btnView.setBackground(new Color(23, 162, 184));
            btnView.setForeground(Color.WHITE);
            btnView.setBorder(BorderFactory.createEmptyBorder());
            btnView.setFocusPainted(false);
            btnView.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnView.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            btnView.addActionListener(e -> showOrderDetails(row));
            
            // Edit button  
            JButton btnEdit = new JButton("✏");
            btnEdit.setPreferredSize(new Dimension(32, 32));
            btnEdit.setBackground(new Color(255, 193, 7));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setBorder(BorderFactory.createEmptyBorder());
            btnEdit.setFocusPainted(false);
            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            btnEdit.addActionListener(e -> showEditOrderDialog(row));
            
            // Delete button
            JButton btnDelete = new JButton("🗑");
            btnDelete.setPreferredSize(new Dimension(32, 32));
            btnDelete.setBackground(new Color(220, 53, 69));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setBorder(BorderFactory.createEmptyBorder());
            btnDelete.setFocusPainted(false);
            btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDelete.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            btnDelete.addActionListener(e -> deleteOrder(row));
            
            panel.add(btnView);
            panel.add(btnEdit);
            panel.add(btnDelete);
            
            return panel;
        }
    }
    
    // Search functionality
    private void performSearch() {
        String searchText = txtSearch.getText().equals(searchPlaceholder) ? "" : txtSearch.getText().trim();
        String selectedStatus = (String) cmbStatus.getSelectedItem();
        
        model.setRowCount(0);
        List<Order> orders = orderBLL.getAllOrders();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Order o : orders) {
            boolean matchesSearch = searchText.isEmpty() || 
                                  String.valueOf(o.getOrderId()).contains(searchText) ||
                                  (o.getCustomerName() != null && o.getCustomerName().toLowerCase().contains(searchText.toLowerCase()));
            
            boolean matchesStatus = selectedStatus.equals("Tất cả") || 
                                  statusMatches(o.getStatus(), selectedStatus);
            
            if (matchesSearch && matchesStatus) {
                String formattedAmount = currencyFormat.format(o.getTotalAmount()) + " đ";
                model.addRow(new Object[]{
                    o.getOrderId(),
                    o.getCustomerName(),
                    o.getOrderDate() != null ? sdf.format(o.getOrderDate()) : "",
                    formattedAmount,
                    o.getStatus(),
                    ""
                });
            }
        }
    }
    
    private boolean statusMatches(String orderStatus, String filterStatus) {
        switch (filterStatus) {
            case "Chờ xử lý":
                return "Pending".equals(orderStatus) || "Chờ xử lý".equals(orderStatus);
            case "Đang xử lý":
                return "Processing".equals(orderStatus) || "Đang xử lý".equals(orderStatus);
            case "Đang giao hàng":
                return "Shipped".equals(orderStatus) || "Đang giao hàng".equals(orderStatus);
            case "Đã giao hàng":
                return "Delivered".equals(orderStatus) || "Đã giao hàng".equals(orderStatus);
            case "Đã hủy":
                return "Cancelled".equals(orderStatus) || "Đã hủy".equals(orderStatus);
            default:
                return true;
        }
    }
    
    private void resetSearch() {
        txtSearch.setText(searchPlaceholder);
        txtSearch.setForeground(new Color(108, 117, 125));
        cmbStatus.setSelectedIndex(0);
        refreshData();
    }
    
    // Add new order dialog
    private void showAddOrderDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm đơn hàng mới", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Customer name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tên khách hàng:"), gbc);
        gbc.gridx = 1;
        JTextField txtCustomerName = new JTextField(20);
        panel.add(txtCustomerName, gbc);
        
        // Total amount
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tổng tiền:"), gbc);
        gbc.gridx = 1;
        JTextField txtTotalAmount = new JTextField(20);
        panel.add(txtTotalAmount, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbOrderStatus = new JComboBox<>(new String[]{"Pending", "Processing", "Shipped", "Delivered"});
        panel.add(cmbOrderStatus, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton btnSave = new JButton("Lưu");
        btnSave.setBackground(new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            try {
                String customerName = txtCustomerName.getText().trim();
                String totalAmountStr = txtTotalAmount.getText().trim();
                String status = (String) cmbOrderStatus.getSelectedItem();
                
                if (customerName.isEmpty() || totalAmountStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double totalAmount = Double.parseDouble(totalAmountStr);
                
                Order newOrder = new Order();
                newOrder.setCustomerName(customerName);
                newOrder.setTotalAmount(BigDecimal.valueOf(totalAmount)); // Chuyển double sang BigDecimal
                newOrder.setStatus(status);
                newOrder.setOrderDate(new Date());
                
                orderBLL.addOrder(newOrder);
                refreshData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Thêm đơn hàng thành công!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Tổng tiền phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    // Edit order dialog
    private void showEditOrderDialog(int row) {
        try {
            int orderId = (int) model.getValueAt(row, 0);
            Order order = orderBLL.getOrderById(orderId);
            
            if (order == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa đơn hàng", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Customer name
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Tên khách hàng:"), gbc);
            gbc.gridx = 1;
            JTextField txtCustomerName = new JTextField(order.getCustomerName(), 20);
            panel.add(txtCustomerName, gbc);
            
            // Total amount
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Tổng tiền:"), gbc);
            gbc.gridx = 1;
            JTextField txtTotalAmount = new JTextField(String.valueOf(order.getTotalAmount()), 20);
            panel.add(txtTotalAmount, gbc);
            
            // Status
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Trạng thái:"), gbc);
            gbc.gridx = 1;
            JComboBox<String> cmbOrderStatus = new JComboBox<>(new String[]{"Pending", "Processing", "Shipped", "Delivered", "Cancelled"});
            cmbOrderStatus.setSelectedItem(order.getStatus());
            panel.add(cmbOrderStatus, gbc);
            
            // Buttons
            gbc.gridx = 0; gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JPanel buttonPanel = new JPanel(new FlowLayout());
            
            JButton btnSave = new JButton("Lưu");
            btnSave.setBackground(new Color(40, 167, 69));
            btnSave.setForeground(Color.WHITE);
            btnSave.addActionListener(e -> {
                try {
                    String customerName = txtCustomerName.getText().trim();
                    String totalAmountStr = txtTotalAmount.getText().trim();
                    String status = (String) cmbOrderStatus.getSelectedItem();
                    
                    if (customerName.isEmpty() || totalAmountStr.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    double totalAmount = Double.parseDouble(totalAmountStr);
                    
                    order.setCustomerName(customerName);
                    order.setTotalAmount(BigDecimal.valueOf(totalAmount));
                    order.setStatus(status);
                    
                    orderBLL.updateOrder(order);
                    refreshData();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Cập nhật đơn hàng thành công!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Tổng tiền phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            JButton btnCancel = new JButton("Hủy");
            btnCancel.setBackground(new Color(108, 117, 125));
            btnCancel.setForeground(Color.WHITE);
            btnCancel.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(btnSave);
            buttonPanel.add(btnCancel);
            panel.add(buttonPanel, gbc);
            
            dialog.add(panel);
            dialog.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin đơn hàng: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Delete order
    private void deleteOrder(int row) {
        int orderId = (int) model.getValueAt(row, 0);
        String customerName = (String) model.getValueAt(row, 1);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa đơn hàng #" + orderId + " của khách hàng " + customerName + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                orderBLL.deleteOrder(orderId);
                refreshData();
                JOptionPane.showMessageDialog(this, "Xóa đơn hàng thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa đơn hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Show order details
    private void showOrderDetails(int orderId) {
    try {
        Order order = orderBLL.getOrderById(orderId);
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Mã đơn: ").append(order.getOrderId()).append("\n")
               .append("Khách hàng: ").append(order.getCustomerName()).append("\n")
               .append("Ngày đặt: ").append(order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "").append("\n")
               .append("Tổng tiền: ").append(currencyFormat.format(order.getTotalAmount())).append("\n")
               .append("Trạng thái: ").append(order.getStatus()).append("\n")
               .append("Danh sách sản phẩm:\n");

        List<Object[]> orderItems = orderBLL.getOrderItems(orderId);
        if (orderItems != null && !orderItems.isEmpty()) {
            for (Object[] item : orderItems) {
                details.append("- ").append(item[0]).append(": ").append(item[1]).append(" x ").append(currencyFormat.format(item[2])).append("\n");
            }
        } else {
            details.append("Không có thông tin sản phẩm.\n");
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết đơn hàng", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printButton = new JButton("In đơn hàng");
        printButton.setBackground(new Color(52, 58, 64));
        printButton.setForeground(Color.WHITE);
        printButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Đang in đơn hàng " + orderId + "...", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(printButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin đơn hàng: " + e.getMessage(), 
                                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
    
    // Helper method to get Vietnamese status text
    private String getVietnameseStatus(String status) {
        switch (status) {
            case "Pending": return "Chờ xử lý";
            case "Processing": return "Đang xử lý";
            case "Shipped": return "Đang giao hàng";
            case "Delivered": return "Đã giao hàng";
            case "Cancelled": return "Đã hủy";
            default: return status;
        }
    }
    
    // Helper method to get status color
    private Color getStatusColor(String status) {
        switch (status) {
            case "Delivered":
            case "Đã giao hàng":
                return new Color(21, 87, 36);
            case "Shipped":
            case "Đang giao hàng":
                return new Color(102, 77, 3);
            case "Processing":
            case "Đang xử lý":
                return new Color(12, 84, 96);
            case "Pending":
            case "Chờ xử lý":
                return new Color(133, 100, 4);
            default:
                return new Color(114, 28, 36);
        }
    }
    
    // Refresh data method
    public void refreshData() {
        model.setRowCount(0);
        List<Order> orders = orderBLL.getAllOrders();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Order o : orders) {
            String formattedAmount = currencyFormat.format(o.getTotalAmount()) + " đ";
            model.addRow(new Object[]{
                o.getOrderId(),
                o.getCustomerName(),
                o.getOrderDate() != null ? sdf.format(o.getOrderDate()) : "",
                formattedAmount,
                o.getStatus(),
                "" // Actions will be rendered by custom renderer
            });
        }
    }
    
    // Custom shadow border class
    private static class ShadowBorder implements Border {
        private final Color shadowColor = new Color(0, 0, 0, 50);
        private final int shadowSize = 3;
        private final int shadowOffset = 2;
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw shadow
            g2d.setColor(shadowColor);
            g2d.fillRoundRect(x + shadowOffset, y + shadowOffset, 
                            width - shadowOffset, height - shadowOffset, 6, 6);
            
            // Draw main border
            g2d.setColor(new Color(233, 236, 239));
            g2d.drawRoundRect(x, y, width - shadowOffset - 1, height - shadowOffset - 1, 6, 6);
            
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, shadowSize + shadowOffset, shadowSize + shadowOffset);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}