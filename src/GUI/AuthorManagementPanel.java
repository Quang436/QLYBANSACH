package GUI;

import BLL.AuthorBLL;
import Model.Author;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class AuthorManagementPanel extends JPanel {
    private AuthorBLL authorBLL = new AuthorBLL();
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cmbCountry;

    public AuthorManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header panel with title and add button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel titleLabel = new JLabel(" Quản lý tác giả");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        
        // Add button (top right)
        JButton btnAdd = new JButton("+ Thêm tác giả mới");
        btnAdd.setBackground(new Color(0, 123, 255));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setPreferredSize(new Dimension(150, 35));
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(btnAdd, BorderLayout.EAST);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        
        // Search field
        JLabel lblSearch = new JLabel("Tìm kiếm");
        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(200, 30));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Country dropdown
        JLabel lblCountry = new JLabel("Quốc gia");
        String[] countries = {"-- Tất cả --", "Việt Nam", "Anh", "Mỹ", "Brazil", "Nhật Bản"};
        cmbCountry = new JComboBox<>(countries);
        cmbCountry.setPreferredSize(new Dimension(150, 30));
        
        // Search button
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setPreferredSize(new Dimension(120, 30));
        
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(lblCountry);
        searchPanel.add(cmbCountry);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(btnSearch);

        // Top container
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(Color.WHITE);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(searchPanel, BorderLayout.CENTER);
        topContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Table
        String[] cols = {"ID", "Ảnh", "Tên tác giả", "Quốc gia", "Tiểu sử", "Số tác phẩm", "Thao tác"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(80);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(240, 248, 255));
        
        // Custom cell renderer for action buttons
        table.getColumn("Thao tác").setCellRenderer(new ActionButtonRenderer());
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // Ảnh
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Tên
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Quốc gia
        table.getColumnModel().getColumn(4).setPreferredWidth(300); // Tiểu sử
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Số tác phẩm
        table.getColumnModel().getColumn(6).setPreferredWidth(160); // Thao tác
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        add(topContainer, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Load data
        refreshData();

        // Search events
        btnSearch.addActionListener(e -> searchAuthors());
        txtSearch.addActionListener(e -> searchAuthors());
        cmbCountry.addActionListener(e -> searchAuthors());

        // Add event
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
    class ActionButtonRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setOpaque(true);
            
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }
            
            // View button (cyan)
            JButton btnView = new JButton("View");
            btnView.setBackground(new Color(0, 188, 212));
            btnView.setForeground(Color.WHITE);
            btnView.setPreferredSize(new Dimension(50, 25));
            btnView.setBorderPainted(false);
            btnView.setFocusPainted(false);
            btnView.setFont(new Font("Arial", Font.PLAIN, 10));
            
            // Edit button (orange)
            JButton btnEdit = new JButton("Edit");
            btnEdit.setBackground(new Color(255, 152, 0));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setPreferredSize(new Dimension(50, 25));
            btnEdit.setBorderPainted(false);
            btnEdit.setFocusPainted(false);
            btnEdit.setFont(new Font("Arial", Font.PLAIN, 10));
            
            // Delete button (red)
            JButton btnDelete = new JButton("Del");
            btnDelete.setBackground(new Color(244, 67, 54));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setPreferredSize(new Dimension(50, 25));
            btnDelete.setBorderPainted(false);
            btnDelete.setFocusPainted(false);
            btnDelete.setFont(new Font("Arial", Font.PLAIN, 10));
            
            panel.add(btnView);
            panel.add(btnEdit);
            panel.add(btnDelete);
            
            return panel;
        }
    }

    public void refreshData() {
        model.setRowCount(0);
        List<Author> authors = authorBLL.getAllAuthors();
        for (Author a : authors) {
            // Simulate work count (you can replace with actual logic)
            int workCount = (int)(Math.random() * 3) + 1;
            
            model.addRow(new Object[]{
                a.getAuthorId(),
                a.getImageUrl() == null ? "" : new ImageIcon(a.getImageUrl()),
                a.getName(),
                a.getCountry(),
                a.getBio(),
                workCount,
                "" // Action buttons will be rendered by custom renderer
            });
        }
    }

    private void searchAuthors() {
        String keyword = txtSearch.getText().trim();
        String selectedCountry = (String) cmbCountry.getSelectedItem();
        
        model.setRowCount(0);
        List<Author> authors = authorBLL.getAllAuthors();
        
        for (Author a : authors) {
            boolean matchesKeyword = keyword.isEmpty() || 
                a.getName().toLowerCase().contains(keyword.toLowerCase());
            
            boolean matchesCountry = selectedCountry.equals("-- Tất cả --") || 
                a.getCountry().equals(selectedCountry);
            
            if (matchesKeyword && matchesCountry) {
                int workCount = (int)(Math.random() * 3) + 1;
                
                model.addRow(new Object[]{
                    a.getAuthorId(),
                    a.getImageUrl() == null ? "" : new ImageIcon(a.getImageUrl()),
                    a.getName(),
                    a.getCountry(),
                    a.getBio(),
                    workCount,
                    ""
                });
            }
        }
    }

    private void showAddDialog() {
        JTextField txtName = new JTextField();
        JTextField txtCountry = new JTextField();
        JTextField txtImage = new JTextField();
        JTextArea txtBio = new JTextArea(3, 20);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Tên tác giả:"));
        panel.add(txtName);
        panel.add(new JLabel("Quốc gia:"));
        panel.add(txtCountry);
        panel.add(new JLabel("Ảnh (URL):"));
        panel.add(txtImage);
        panel.add(new JLabel("Tiểu sử:"));
        panel.add(new JScrollPane(txtBio));

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm tác giả mới", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION) {
            Author a = new Author();
            a.setName(txtName.getText());
            a.setCountry(txtCountry.getText());
            a.setImageUrl(txtImage.getText());
            a.setBio(txtBio.getText());
            authorBLL.addAuthor(a);
            refreshData();
        }
    }

    private void showEditDialog(int row) {
        int authorId = (int) model.getValueAt(row, 0);
        Author a = authorBLL.getAllAuthors().stream()
            .filter(x -> x.getAuthorId() == authorId)
            .findFirst().orElse(null);
        if (a == null) return;

        JTextField txtName = new JTextField(a.getName());
        JTextField txtCountry = new JTextField(a.getCountry());
        JTextField txtImage = new JTextField(a.getImageUrl());
        JTextArea txtBio = new JTextArea(a.getBio(), 3, 20);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Tên tác giả:"));
        panel.add(txtName);
        panel.add(new JLabel("Quốc gia:"));
        panel.add(txtCountry);
        panel.add(new JLabel("Ảnh (URL):"));
        panel.add(txtImage);
        panel.add(new JLabel("Tiểu sử:"));
        panel.add(new JScrollPane(txtBio));

        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa tác giả", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION) {
            a.setName(txtName.getText());
            a.setCountry(txtCountry.getText());
            a.setImageUrl(txtImage.getText());
            a.setBio(txtBio.getText());
            authorBLL.updateAuthor(a);
            refreshData();
        }
    }
}