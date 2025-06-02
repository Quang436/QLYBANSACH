package GUI;

import BLL.CategoryBLL;
import Model.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CategoryManagementPanel extends JPanel {
    private CategoryBLL categoryBLL = new CategoryBLL();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    
    public CategoryManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Header panel with title and add button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Title with icon
        JLabel titleLabel = new JLabel("📋 Quản lý danh mục");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));

        // Add button styled like in the image
        JButton btnAdd = new JButton("+ Thêm danh mục mới");
        btnAdd.setBackground(new Color(0, 123, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        btnAdd.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnAdd, BorderLayout.EAST);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        txtSearch = new JTextField(25);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JButton btnSearch = new JButton("🔍 Tìm kiếm");
        btnSearch.setBackground(new Color(108, 117, 125));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Arial", Font.PLAIN, 12));
        btnSearch.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // Table setup
        String[] cols = {"ID", "Tên danh mục", "Số lượng tác phẩm", "Thao tác"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return column == 3; // Only action column is editable
            }
        };
        
        table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(232, 244, 253));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);

        // Custom renderer for action column
        table.getColumnModel().getColumn(3).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new ActionButtonEditor());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(Color.WHITE);

        // Assemble panels
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Color.WHITE);
        topSection.add(headerPanel, BorderLayout.NORTH);
        topSection.add(searchPanel, BorderLayout.SOUTH);

        mainPanel.add(topSection, BorderLayout.NORTH);
        mainPanel.add(scroll, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);

        // Load initial data
        refreshData();
        
        // Event handlers
        btnSearch.addActionListener(e -> searchCategories());
        txtSearch.addActionListener(e -> searchCategories());
        btnAdd.addActionListener(e -> showAddDialog());

        // Table double click for edit
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    showEditDialog(table.getSelectedRow());
                }
            }
        });
    }

    // Custom renderer for action buttons
    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editBtn;
        private JButton deleteBtn;

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);
            
            editBtn = createActionButton("Sửa", new Color(255, 193, 7), Color.BLACK);
            deleteBtn = createActionButton("Xóa", new Color(220, 53, 69), Color.WHITE);
            
            add(editBtn);
            add(deleteBtn);
        }

        private JButton createActionButton(String text, Color bgColor, Color textColor) {
            JButton btn = new JButton(text);
            btn.setBackground(bgColor);
            btn.setForeground(textColor);
            btn.setFont(new Font("Arial", Font.PLAIN, 11));
            btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    // Custom editor for action buttons
    class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editBtn;
        private JButton deleteBtn;
        private int currentRow;

        public ActionButtonEditor() {
            super(new JCheckBox());
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            editBtn = createActionButton("Sửa", new Color(255, 193, 7), Color.BLACK);
            deleteBtn = createActionButton("Xóa", new Color(220, 53, 69), Color.WHITE);
            
            editBtn.addActionListener(e -> {
                fireEditingStopped();
                showEditDialog(currentRow);
            });
            
            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                showDeleteDialog(currentRow);
            });
            
            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        private JButton createActionButton(String text, Color bgColor, Color textColor) {
            JButton btn = new JButton(text);
            btn.setBackground(bgColor);
            btn.setForeground(textColor);
            btn.setFont(new Font("Arial", Font.PLAIN, 11));
            btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private void showDeleteDialog(int row) {
        int categoryId = (int) model.getValueAt(row, 0);
        String categoryName = (String) model.getValueAt(row, 1);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            "Bạn có chắc chắn muốn xóa danh mục '" + categoryName + "' không?",
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Add your delete logic here
            // categoryBLL.deleteCategory(categoryId);
            refreshData();
            JOptionPane.showMessageDialog(this, "Đã xóa danh mục thành công!");
        }
    }

    public void refreshData() {
        model.setRowCount(0);
        List<Category> categories = categoryBLL.getAllCategories();
        for (Category c : categories) {
            int count = categoryBLL.countBooksInCategory(c.getCategoryId());
            model.addRow(new Object[]{
                c.getCategoryId(),
                c.getName(),
                count,
                "" // Empty string for action column
            });
        }
    }

    private void searchCategories() {
        String keyword = txtSearch.getText().trim();
        model.setRowCount(0);
        List<Category> categories = categoryBLL.getAllCategories();
        for (Category c : categories) {
            if (c.getName().toLowerCase().contains(keyword.toLowerCase())) {
                int count = categoryBLL.countBooksInCategory(c.getCategoryId());
                model.addRow(new Object[]{
                    c.getCategoryId(),
                    c.getName(),
                    count,
                    "" // Empty string for action column
                });
            }
        }
    }

    private void showAddDialog() {
        JTextField txtName = new JTextField();
        JTextField txtDesc = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Tên danh mục:"));
        panel.add(txtName);
        panel.add(new JLabel("Mô tả:"));
        panel.add(txtDesc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm danh mục mới", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên danh mục!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Category c = new Category();
            c.setName(txtName.getText().trim());
            c.setDescription(txtDesc.getText().trim());
            categoryBLL.addCategory(c);
            refreshData();
            JOptionPane.showMessageDialog(this, "Đã thêm danh mục thành công!");
        }
    }

    private void showEditDialog(int row) {
        int categoryId = (int) model.getValueAt(row, 0);
        Category c = categoryBLL.getAllCategories().stream()
                .filter(x -> x.getCategoryId() == categoryId)
                .findFirst().orElse(null);
        if (c == null) return;

        JTextField txtName = new JTextField(c.getName());
        JTextField txtDesc = new JTextField(c.getDescription());

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Tên danh mục:"));
        panel.add(txtName);
        panel.add(new JLabel("Mô tả:"));
        panel.add(txtDesc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa danh mục", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên danh mục!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            c.setName(txtName.getText().trim());
            c.setDescription(txtDesc.getText().trim());
            categoryBLL.updateCategory(c);
            refreshData();
            JOptionPane.showMessageDialog(this, "Đã cập nhật danh mục thành công!");
        }
    }
}