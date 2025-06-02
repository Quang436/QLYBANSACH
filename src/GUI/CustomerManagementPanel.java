package GUI;

import bll.CustomerBLL;
import Model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CustomerManagementPanel extends JPanel {
    private CustomerBLL customerBLL = new CustomerBLL();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cityComboBox;

    public CustomerManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Create top panel exactly as shown in image
        JPanel topPanel = createTopPanel();
        
        // Create table with action buttons
        createTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Load data
        refreshData();
        setupEvents();
    }

    private JPanel createTopPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Left panel with search controls - horizontal layout like in image
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(Color.WHITE);

        // Search label and textfield
        JLabel searchLabel = new JLabel("Tìm kiếm");
        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(200, 30));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // City label and combobox
        JLabel cityLabel = new JLabel("Thành phố");
        cityComboBox = new JComboBox<>(new String[]{"Tất cả", "TP.HCM", "Hà Nội", "Đà Nẵng", "Cao Thắng"});
        cityComboBox.setPreferredSize(new Dimension(120, 30));

        // Search button
        JButton btnSearch = new JButton("🔍 Tìm kiếm");
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setPreferredSize(new Dimension(120, 30));
        btnSearch.setBorder(BorderFactory.createEmptyBorder());
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        leftPanel.add(searchLabel);
        leftPanel.add(txtSearch);
        leftPanel.add(cityLabel);
        leftPanel.add(cityComboBox);
        leftPanel.add(btnSearch);

        // Right panel with add button
        JButton btnAdd = new JButton("+ Thêm khách hàng mới");
        btnAdd.setBackground(new Color(0, 123, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(180, 30));
        btnAdd.setBorder(BorderFactory.createEmptyBorder());
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(btnAdd, BorderLayout.EAST);

        return mainPanel;
    }

    private void createTable() {
        String[] cols = {"ID", "Tên khách hàng", "Email", "Số điện thoại", "Thành phố", "Thao tác"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return column == 5; // Only action column is editable
            }
        };
        
        table = new JTable(model);
        table.setRowHeight(50);
        table.setShowGrid(true);
        table.setGridColor(new Color(233, 236, 239));
        table.setSelectionBackground(new Color(245, 248, 255));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setBackground(Color.WHITE);
        
        // Header styling to match image
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(233, 236, 239)));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Set column widths to match the image proportions
        table.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(180);  // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(200);  // Email
        table.getColumnModel().getColumn(3).setPreferredWidth(130);  // Phone
        table.getColumnModel().getColumn(4).setPreferredWidth(200);  // City
        table.getColumnModel().getColumn(5).setPreferredWidth(150);  // Actions

        // Custom renderer and editor for action column
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionButtonEditor());
        
        // Center align ID column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
    }

    private void setupEvents() {
        // Get components from top panel
        JPanel topPanel = (JPanel) getComponent(0);
        JPanel leftPanel = (JPanel) ((BorderLayout) topPanel.getLayout()).getLayoutComponent(BorderLayout.WEST);
        JButton addBtn = (JButton) ((BorderLayout) topPanel.getLayout()).getLayoutComponent(BorderLayout.EAST);
        JButton searchBtn = (JButton) leftPanel.getComponent(4);

        // Search events
        searchBtn.addActionListener(e -> searchCustomers());
        txtSearch.addActionListener(e -> searchCustomers());
        cityComboBox.addActionListener(e -> searchCustomers());

        // Add button event
        addBtn.addActionListener(e -> showAddDialog());

        // Table double click for edit (excluding action column)
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int col = table.columnAtPoint(evt.getPoint());
                    if (col != 5) { // Not action column
                        showEditDialog(table.getSelectedRow());
                    }
                }
            }
        });
    }

    public void refreshData() {
        model.setRowCount(0);
        List<Customer> customers = customerBLL.getAllCustomers();
        for (Customer c : customers) {
            model.addRow(new Object[]{
                c.getCustomerId(),
                c.getName(),
                c.getEmail(),
                c.getPhone(),
                c.getAddress(),
                "actions" // Placeholder for action buttons
            });
        }
    }

    private void searchCustomers() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        String selectedCity = (String) cityComboBox.getSelectedItem();
        
        model.setRowCount(0);
        List<Customer> customers = customerBLL.getAllCustomers();
        for (Customer c : customers) {
            boolean matchesKeyword = keyword.isEmpty() || 
                c.getName().toLowerCase().contains(keyword) ||
                c.getEmail().toLowerCase().contains(keyword) ||
                c.getAddress().toLowerCase().contains(keyword);
                
            boolean matchesCity = "Tất cả".equals(selectedCity) || 
                c.getAddress().contains(selectedCity);
                
            if (matchesKeyword && matchesCity) {
                model.addRow(new Object[]{
                    c.getCustomerId(),
                    c.getName(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getAddress(),
                    "actions"
                });
            }
        }
    }

    private void showAddDialog() {
        JTextField txtName = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtAddress = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(new JLabel("Tên khách hàng:"));
        panel.add(txtName);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Số điện thoại:"));
        panel.add(txtPhone);
        panel.add(new JLabel("Địa chỉ:"));
        panel.add(txtAddress);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm khách hàng mới", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Customer c = new Customer();
            c.setName(txtName.getText());
            c.setEmail(txtEmail.getText());
            c.setPhone(txtPhone.getText());
            c.setAddress(txtAddress.getText());
            customerBLL.addCustomer(c);
            refreshData();
        }
    }

    private void showEditDialog(int row) {
        int customerId = (int) model.getValueAt(row, 0);
        Customer c = customerBLL.getAllCustomers().stream()
            .filter(x -> x.getCustomerId() == customerId)
            .findFirst().orElse(null);
        if (c == null) return;

        JTextField txtName = new JTextField(c.getName());
        JTextField txtEmail = new JTextField(c.getEmail());
        JTextField txtPhone = new JTextField(c.getPhone());
        JTextField txtAddress = new JTextField(c.getAddress());

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(new JLabel("Tên khách hàng:"));
        panel.add(txtName);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Số điện thoại:"));
        panel.add(txtPhone);
        panel.add(new JLabel("Địa chỉ:"));
        panel.add(txtAddress);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa khách hàng", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            c.setName(txtName.getText());
            c.setEmail(txtEmail.getText());
            c.setPhone(txtPhone.getText());
            c.setAddress(txtAddress.getText());
            customerBLL.updateCustomer(c);
            refreshData();
        }
    }

    private void deleteCustomer(int row) {
        int customerId = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa khách hàng này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            customerBLL.deleteCustomer(customerId);
            refreshData();
        }
    }

    // Custom renderer for action buttons - exactly like in the image
    class ActionButtonRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 8));
            panel.setOpaque(true);
            
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }

            // Create three circular buttons exactly like in image
            JButton viewBtn = createCircularButton("👁", new Color(23, 162, 184));
            JButton editBtn = createCircularButton("✏", new Color(255, 193, 7));
            JButton deleteBtn = createCircularButton("🗑", new Color(220, 53, 69));

            panel.add(viewBtn);
            panel.add(editBtn);
            panel.add(deleteBtn);

            return panel;
        }

        private JButton createCircularButton(String icon, Color bgColor) {
            JButton btn = new JButton(icon);
            btn.setPreferredSize(new Dimension(32, 32));
            btn.setBackground(bgColor);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            
            // Make button circular
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            
            return btn;
        }
    }

    // Custom editor for action buttons
    class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private int currentRow;

        public ActionButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 8));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            panel.removeAll();
            panel.setBackground(table.getSelectionBackground());

            // View button
            JButton viewBtn = createCircularButton("👁", new Color(23, 162, 184));
            viewBtn.addActionListener(e -> {
                JOptionPane.showMessageDialog(CustomerManagementPanel.this, 
                    "Xem chi tiết khách hàng ID: " + model.getValueAt(currentRow, 0));
                stopCellEditing();
            });

            // Edit button
            JButton editBtn = createCircularButton("✏", new Color(255, 193, 7));
            editBtn.addActionListener(e -> {
                showEditDialog(currentRow);
                stopCellEditing();
            });

            // Delete button
            JButton deleteBtn = createCircularButton("🗑", new Color(220, 53, 69));
            deleteBtn.addActionListener(e -> {
                deleteCustomer(currentRow);
                stopCellEditing();
            });

            panel.add(viewBtn);
            panel.add(editBtn);
            panel.add(deleteBtn);

            return panel;
        }

        private JButton createCircularButton(String icon, Color bgColor) {
            JButton btn = new JButton(icon);
            btn.setPreferredSize(new Dimension(32, 32));
            btn.setBackground(bgColor);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder());
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            
            // Make button circular
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            
            return btn;
        }

        @Override
        public Object getCellEditorValue() {
            return "actions";
        }
    }
}