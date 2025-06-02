package GUI;
import BLL.OrderBLL;
import Model.Order;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.border.Border;

public class OrderManagementPanel extends JPanel {
    private OrderBLL orderBLL = new OrderBLL();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cmbStatus;
    private NumberFormat currencyFormat;
    
    public OrderManagementPanel() {
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
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
        
        txtSearch = new JTextField("Mã đơn hàng hoặc tên khách hàng...");
        txtSearch.setPreferredSize(new Dimension(400, 40));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtSearch.setBackground(Color.WHITE);
        txtSearch.setForeground(new Color(108, 117, 125));
        
        // Status filter
        JLabel lblStatus = new JLabel("Trạng thái");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblStatus.setForeground(new Color(73, 80, 87));
        
        cmbStatus = new JComboBox<>(new String[]{"Tất cả", "Đã giao hàng", "Đang giao hàng", "Chờ xử lý"});
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
        
        searchRow.add(lblSearch);
        searchRow.add(txtSearch);
        searchRow.add(lblStatus);
        searchRow.add(cmbStatus);
        searchRow.add(btnSearch);
        
        searchPanel.add(searchRow, BorderLayout.CENTER);
        
        return searchPanel;
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
        columnModel.getColumn(5).setPreferredWidth(100); // Thao tác
        
        // Custom cell renderers
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionCellRenderer());
        
        // Double click event
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    showUpdateStatusDialog(table.getSelectedRow());
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
            if (status.equals("Đã giao hàng") || status.equals("Delivered")) {
                setBackground(new Color(212, 237, 218));
                setForeground(new Color(21, 87, 36));
                setText("Đã giao hàng");
            } else if (status.equals("Đang giao hàng") || status.equals("Processing") || status.equals("Shipped")) {
                setBackground(new Color(255, 243, 205));
                setForeground(new Color(102, 77, 3));
                setText("Đang giao hàng");
            } else if (status.equals("Chờ xử lý") || status.equals("Pending")) {
                setBackground(new Color(217, 237, 247));
                setForeground(new Color(12, 84, 96));
                setText("Chờ xử lý");
            } else {
                setBackground(new Color(253, 236, 234));
                setForeground(new Color(114, 28, 36));
                setText("Đã hủy");
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
            
            // Edit button  
            JButton btnEdit = new JButton("✏");
            btnEdit.setPreferredSize(new Dimension(32, 32));
            btnEdit.setBackground(new Color(255, 193, 7));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setBorder(BorderFactory.createEmptyBorder());
            btnEdit.setFocusPainted(false);
            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            
            // Delete button
            JButton btnDelete = new JButton("🗑");
            btnDelete.setPreferredSize(new Dimension(32, 32));
            btnDelete.setBackground(new Color(220, 53, 69));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setBorder(BorderFactory.createEmptyBorder());
            btnDelete.setFocusPainted(false);
            btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDelete.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            
            panel.add(btnView);
            panel.add(btnEdit);
            panel.add(btnDelete);
            
            return panel;
        }
    }
    
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