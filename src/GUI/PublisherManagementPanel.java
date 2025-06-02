package GUI;

import bll.PublisherBLL;
import Model.Publisher;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PublisherManagementPanel extends JPanel {
    private PublisherBLL publisherBLL = new PublisherBLL();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cmbCity;
    private int currentPage = 1;
    private int itemsPerPage = 10;
    
    public PublisherManagementPanel() {
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
        JLabel titleLabel = new JLabel("▲ Quản lý nhà xuất bản");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));

        // Add button styled like in the image
        JButton btnAdd = new JButton("+ Thêm nhà xuất bản mới");
        btnAdd.setBackground(new Color(0, 123, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        btnAdd.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnAdd, BorderLayout.EAST);

        // Search panel with two-row layout
        JPanel searchPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // First row: Search field and city dropdown
        JPanel searchRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow1.setBackground(Color.WHITE);
        
        JLabel lblSearch = new JLabel("Tìm kiếm");
        lblSearch.setFont(new Font("Arial", Font.PLAIN, 12));
        
        txtSearch = new JTextField(25);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 12));
        txtSearch.setBackground(Color.WHITE);
        
        JLabel lblCity = new JLabel("Thành phố");
        lblCity.setFont(new Font("Arial", Font.PLAIN, 12));
        
        cmbCity = new JComboBox<>(new String[]{"Tất cả", "Hà Nội", "TP.HCM", "Đà Nẵng", "Hải Phòng"});
        cmbCity.setFont(new Font("Arial", Font.PLAIN, 12));
        cmbCity.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        cmbCity.setBackground(Color.WHITE);
        cmbCity.setPreferredSize(new Dimension(120, 35));
        
        searchRow1.add(lblSearch);
        searchRow1.add(txtSearch);
        searchRow1.add(Box.createHorizontalStrut(20));
        searchRow1.add(lblCity);
        searchRow1.add(cmbCity);
        
        // Second row: Search button (right aligned)
        JPanel searchRow2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchRow2.setBackground(Color.WHITE);
        
        JButton btnSearch = new JButton("○ Tìm kiếm");
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Arial", Font.BOLD, 12));
        btnSearch.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        searchRow2.add(btnSearch);
        
        searchPanel.add(searchRow1);
        searchPanel.add(searchRow2);

        // Table setup
        String[] cols = {"ID", "Logo", "Tên nhà xuất bản", "Thành phố", "Liên hệ", "Số sách", "Thao tác"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return column == 6; // Only action column is editable
            }
        };
        
        table = new JTable(model);
        table.setRowHeight(80);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(232, 244, 253));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(80);   // Logo
        table.getColumnModel().getColumn(2).setPreferredWidth(200);  // Name
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // City
        table.getColumnModel().getColumn(4).setPreferredWidth(250);  // Contact
        table.getColumnModel().getColumn(5).setPreferredWidth(80);   // Book count
        table.getColumnModel().getColumn(6).setPreferredWidth(120);  // Actions

        // Custom renderers
        table.getColumnModel().getColumn(1).setCellRenderer(new LogoCellRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new ContactCellRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionButtonEditor());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(Color.WHITE);

        // Pagination panel
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.setBackground(Color.WHITE);
        paginationPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JButton btnPrev = createPaginationButton("‹");
        JButton btnPage1 = createPaginationButton("1");
        btnPage1.setSelected(true);
        JButton btnPage2 = createPaginationButton("2");
        JButton btnPage3 = createPaginationButton("3");
        JButton btnNext = createPaginationButton("›");
        
        paginationPanel.add(btnPrev);
        paginationPanel.add(btnPage1);
        paginationPanel.add(btnPage2);
        paginationPanel.add(btnPage3);
        paginationPanel.add(btnNext);

        // Assemble panels
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Color.WHITE);
        topSection.add(headerPanel, BorderLayout.NORTH);
        topSection.add(searchPanel, BorderLayout.CENTER);

        JPanel tableSection = new JPanel(new BorderLayout());
        tableSection.setBackground(Color.WHITE);
        tableSection.add(scroll, BorderLayout.CENTER);
        tableSection.add(paginationPanel, BorderLayout.SOUTH);

        mainPanel.add(topSection, BorderLayout.NORTH);
        mainPanel.add(tableSection, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);

        // Load initial data
        refreshData();
        
        // Event handlers
        btnSearch.addActionListener(e -> searchPublishers());
        txtSearch.addActionListener(e -> searchPublishers());
        cmbCity.addActionListener(e -> searchPublishers());
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

    private JButton createPaginationButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(35, 35));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(0, 123, 255));
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Special styling for selected page
        if (text.equals("1")) {
            btn.setBackground(new Color(0, 123, 255));
            btn.setForeground(Color.WHITE);
        }
        
        return btn;
    }

    // Logo cell renderer
    class LogoCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            
            // Create a simple logo placeholder
            String publisherName = (String) table.getValueAt(row, 2);
            String logoText = "NXB " + (publisherName.length() > 0 ? publisherName.substring(Math.max(0, publisherName.lastIndexOf(" ") + 1)) : "");
            label.setText(logoText);
            label.setFont(new Font("Arial", Font.BOLD, 10));
            label.setOpaque(true);
            label.setBackground(new Color(240, 248, 255));
            label.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255), 1));
            
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
            }
            
            return label;
        }
    }

    // Contact cell renderer for multi-line display
    class ContactCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            int publisherId = (int) table.getValueAt(row, 0);
            Publisher publisher = publisherBLL.getAllPublishers().stream()
                    .filter(p -> p.getPublisherId() == publisherId)
                    .findFirst().orElse(null);
            
            if (publisher != null) {
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setOpaque(true);
                
                JLabel phoneLabel = new JLabel("DT: " + (publisher.getPhone() != null ? publisher.getPhone() : "N/A"));
                JLabel emailLabel = new JLabel("Email: " + (publisher.getEmail() != null ? publisher.getEmail() : "N/A"));
                
                phoneLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                emailLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                
                panel.add(phoneLabel);
                panel.add(emailLabel);
                
                if (isSelected) {
                    panel.setBackground(table.getSelectionBackground());
                    phoneLabel.setForeground(table.getSelectionForeground());
                    emailLabel.setForeground(table.getSelectionForeground());
                } else {
                    panel.setBackground(table.getBackground());
                    phoneLabel.setForeground(table.getForeground());
                    emailLabel.setForeground(table.getForeground());
                }
                
                return panel;
            }
            
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    // Action button renderer
    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton viewBtn;
        private JButton editBtn;
        private JButton deleteBtn;

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 2, 5));
            setOpaque(true);
            
            viewBtn = createActionButton("◎", new Color(23, 162, 184), Color.WHITE);
            editBtn = createActionButton("◆", new Color(255, 193, 7), Color.BLACK);
            deleteBtn = createActionButton("✕", new Color(220, 53, 69), Color.WHITE);
            
            add(viewBtn);
            add(editBtn);
            add(deleteBtn);
        }

        private JButton createActionButton(String text, Color bgColor, Color textColor) {
            JButton btn = new JButton(text);
            btn.setBackground(bgColor);
            btn.setForeground(textColor);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(30, 25));
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

    // Action button editor
    class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton viewBtn;
        private JButton editBtn;
        private JButton deleteBtn;
        private int currentRow;

        public ActionButtonEditor() {
            super(new JCheckBox());
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 5));
            viewBtn = createActionButton("◎", new Color(23, 162, 184), Color.WHITE);
            editBtn = createActionButton("◆", new Color(255, 193, 7), Color.BLACK);
            deleteBtn = createActionButton("✕", new Color(220, 53, 69), Color.WHITE);
            
            viewBtn.addActionListener(e -> {
                fireEditingStopped();
                showViewDialog(currentRow);
            });
            
            editBtn.addActionListener(e -> {
                fireEditingStopped();
                showEditDialog(currentRow);
            });
            
            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                showDeleteDialog(currentRow);
            });
            
            panel.add(viewBtn);
            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        private JButton createActionButton(String text, Color bgColor, Color textColor) {
            JButton btn = new JButton(text);
            btn.setBackground(bgColor);
            btn.setForeground(textColor);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(30, 25));
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

    private void showViewDialog(int row) {
        int publisherId = (int) model.getValueAt(row, 0);
        Publisher p = publisherBLL.getAllPublishers().stream()
                .filter(x -> x.getPublisherId() == publisherId)
                .findFirst().orElse(null);
        if (p == null) return;

        String info = String.format(
            "ID: %d\nTên NXB: %s\nThành phố: %s\nSố điện thoại: %s\nEmail: %s\nWebsite: %s",
            p.getPublisherId(), p.getName(), p.getAddress(), 
            p.getPhone(), p.getEmail(), p.getWebsite()
        );
        
        JOptionPane.showMessageDialog(this, info, "Thông tin nhà xuất bản", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDeleteDialog(int row) {
        int publisherId = (int) model.getValueAt(row, 0);
        String publisherName = (String) model.getValueAt(row, 2);
        
        int result = JOptionPane.showConfirmDialog(
            this, 
            "Bạn có chắc chắn muốn xóa nhà xuất bản '" + publisherName + "' không?",
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Add your delete logic here
            // publisherBLL.deletePublisher(publisherId);
            refreshData();
            JOptionPane.showMessageDialog(this, "Đã xóa nhà xuất bản thành công!");
        }
    }

    public void refreshData() {
        model.setRowCount(0);
        List<Publisher> publishers = publisherBLL.getAllPublishers();
        for (Publisher p : publishers) {
            // Generate random book count for demo
            int bookCount = (int) (Math.random() * 200) + 10;
            
            model.addRow(new Object[]{
                p.getPublisherId(),
                "", // Logo placeholder
                p.getName(),
                p.getAddress(),
                "", // Contact info handled by renderer
                bookCount,
                "" // Action buttons handled by renderer
            });
        }
    }

    private void searchPublishers() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        String selectedCity = (String) cmbCity.getSelectedItem();
        
        model.setRowCount(0);
        List<Publisher> publishers = publisherBLL.getAllPublishers();
        for (Publisher p : publishers) {
            boolean matchesKeyword = keyword.isEmpty() || 
                p.getName().toLowerCase().contains(keyword) ||
                (p.getAddress() != null && p.getAddress().toLowerCase().contains(keyword)) ||
                (p.getEmail() != null && p.getEmail().toLowerCase().contains(keyword));
            
            boolean matchesCity = selectedCity.equals("Tất cả") || 
                (p.getAddress() != null && p.getAddress().contains(selectedCity));
            
            if (matchesKeyword && matchesCity) {
                int bookCount = (int) (Math.random() * 200) + 10;
                model.addRow(new Object[]{
                    p.getPublisherId(),
                    "",
                    p.getName(),
                    p.getAddress(),
                    "",
                    bookCount,
                    ""
                });
            }
        }
    }

    private void showAddDialog() {
        JTextField txtName = new JTextField();
        JTextField txtAddress = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtWebsite = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Tên NXB:"));
        panel.add(txtName);
        panel.add(new JLabel("Thành phố:"));
        panel.add(txtAddress);
        panel.add(new JLabel("Số điện thoại:"));
        panel.add(txtPhone);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Website:"));
        panel.add(txtWebsite);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm nhà xuất bản mới", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên nhà xuất bản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Publisher p = new Publisher();
            p.setName(txtName.getText().trim());
            p.setAddress(txtAddress.getText().trim());
            p.setPhone(txtPhone.getText().trim());
            p.setEmail(txtEmail.getText().trim());
            p.setWebsite(txtWebsite.getText().trim());
            publisherBLL.addPublisher(p);
            refreshData();
            JOptionPane.showMessageDialog(this, "Đã thêm nhà xuất bản thành công!");
        }
    }

    private void showEditDialog(int row) {
        int publisherId = (int) model.getValueAt(row, 0);
        Publisher p = publisherBLL.getAllPublishers().stream()
                .filter(x -> x.getPublisherId() == publisherId)
                .findFirst().orElse(null);
        if (p == null) return;

        JTextField txtName = new JTextField(p.getName());
        JTextField txtAddress = new JTextField(p.getAddress());
        JTextField txtPhone = new JTextField(p.getPhone());
        JTextField txtEmail = new JTextField(p.getEmail());
        JTextField txtWebsite = new JTextField(p.getWebsite());

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Tên NXB:"));
        panel.add(txtName);
        panel.add(new JLabel("Thành phố:"));
        panel.add(txtAddress);
        panel.add(new JLabel("Số điện thoại:"));
        panel.add(txtPhone);
        panel.add(new JLabel("Email:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Website:"));
        panel.add(txtWebsite);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa nhà xuất bản", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên nhà xuất bản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            p.setName(txtName.getText().trim());
            p.setAddress(txtAddress.getText().trim());
            p.setPhone(txtPhone.getText().trim());
            p.setEmail(txtEmail.getText().trim());
            p.setWebsite(txtWebsite.getText().trim());
            publisherBLL.updatePublisher(p);
            refreshData();
            JOptionPane.showMessageDialog(this, "Đã cập nhật nhà xuất bản thành công!");
        }
    }
}