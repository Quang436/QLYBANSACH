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
    private JButton btnSearch;
    private JButton btnAdd;

    public AuthorManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250)); // Màu nền nhẹ nhàng hơn

        // ===== TOP AREA: Title, Search, Filter, Add Button =====
        JPanel topAreaPanel = new JPanel();
        topAreaPanel.setLayout(new BoxLayout(topAreaPanel, BoxLayout.Y_AXIS)); // Sử dụng BoxLayout theo chiều dọc
        topAreaPanel.setBackground(getBackground());
        topAreaPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Padding quanh khu vực trên

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // FlowLayout căn trái, không khoảng cách
        titlePanel.setBackground(getBackground());
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Font và kích thước lớn hơn
        JLabel titleLabel = new JLabel(" Quản lý tác giả");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Font và kích thước lớn hơn
        titleLabel.setForeground(new Color(52, 58, 64)); // Màu chữ đậm
        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn panel sang trái

        // Search and Add Button Panel
        JPanel searchAddPanel = new JPanel(new BorderLayout(20, 0)); // BorderLayout với khoảng cách ngang giữa search và add
        searchAddPanel.setBackground(getBackground());
        searchAddPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn panel sang trái

        // Search and Filter Panel (Left side of searchAddPanel)
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // FlowLayout căn trái
        searchFilterPanel.setBackground(getBackground());

        // Search field
        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(200, 35)); // Chiều cao lớn hơn
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)), // Màu border nhạt hơn
            BorderFactory.createEmptyBorder(8, 12, 8, 12) // Padding bên trong
        ));
        searchFilterPanel.add(new JLabel("Tìm kiếm:")); // Thêm label rõ ràng
        searchFilterPanel.add(txtSearch);

        // Country dropdown
        String[] countries = {"-- Tất cả --", "Việt Nam", "Anh", "Mỹ", "Brazil", "Nhật Bản"}; // Dữ liệu mẫu
        cmbCountry = new JComboBox<>(countries);
        cmbCountry.setPreferredSize(new Dimension(150, 35)); // Chiều cao lớn hơn
        cmbCountry.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchFilterPanel.add(new JLabel("Quốc gia:")); // Thêm label rõ ràng
        searchFilterPanel.add(cmbCountry);

        // Search button
        btnSearch = new JButton("🔍 Tìm kiếm");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Font đậm
        btnSearch.setBackground(new Color(0, 123, 255)); // Màu xanh dương
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false); // Bỏ border mặc định
        btnSearch.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18)); // Padding cho button
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchFilterPanel.add(btnSearch);

        // Add button (Right side of searchAddPanel)
        btnAdd = new JButton("+ Thêm tác giả mới");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Font đậm
        btnAdd.setBackground(new Color(0, 123, 255)); // Màu xanh dương
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false); // Bỏ border mặc định
        btnAdd.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18)); // Padding cho button
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // Panel chứa nút add, căn phải
        addButtonPanel.setBackground(getBackground());
        addButtonPanel.add(btnAdd);

        searchAddPanel.add(searchFilterPanel, BorderLayout.CENTER);
        searchAddPanel.add(addButtonPanel, BorderLayout.EAST);


        // Add Title and Search/Add panels to the topAreaPanel
        topAreaPanel.add(titlePanel);
        topAreaPanel.add(Box.createVerticalStrut(10)); // Khoảng cách giữa title và search/add
        topAreaPanel.add(searchAddPanel);

        // ===== TABLE AREA =====
        createTable(); // Phương thức tạo và cấu hình JTable

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230))); // Border cho scroll pane
        scroll.getViewport().setBackground(Color.WHITE); // Màu nền scroll pane

        // ===== ADD COMPONENTS TO MAIN PANEL =====
        add(topAreaPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Load data
        refreshData();

        // --- THÊM CÁC EVENT LISTENERS TRỞ LẠI ĐÂY ---
        // Search events
        btnSearch.addActionListener(e -> searchAuthors());
        txtSearch.addActionListener(e -> searchAuthors());
        cmbCountry.addActionListener(e -> searchAuthors());

        // Add button event
        btnAdd.addActionListener(e -> showAddDialog());

        // Table double click for edit
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    // Kiểm tra xem có click vào cột Thao tác không (nếu bạn có Editor cho cột này)
                     // int col = table.columnAtPoint(evt.getPoint());
                     // if (col != table.getColumn("Thao tác").getModelIndex()) {
                         showEditDialog(table.getSelectedRow()); // Giả sử double click bất kỳ ô nào (trừ cột action nếu có editor) mở dialog sửa
                     // }
                }
            }
        });
        // --- KẾT THÚC THÊM CÁC EVENT LISTENERS ---
    }

    private void createTable() {
        String[] cols = {"ID", "Ảnh", "Tên tác giả", "Quốc gia", "Tiểu sử", "Số tác phẩm", "Thao tác"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
                    return ImageIcon.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        table = new JTable(model);
        table.setRowHeight(80);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(240, 248, 255));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(300);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(160);
        
        table.getColumn("Thao tác").setCellRenderer(new ActionButtonRenderer());
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // Load data
        refreshData();
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
            int workCount = (int)(Math.random() * 3) + 1;

            ImageIcon authorImage = null;
            if (a.getImageUrl() != null && !a.getImageUrl().isEmpty()) {
                try {
                    ImageIcon originalIcon = new ImageIcon(new java.net.URL(a.getImageUrl()));
                    if (originalIcon.getIconWidth() > 0) {
                        Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80 * originalIcon.getIconHeight() / originalIcon.getIconWidth(), Image.SCALE_SMOOTH);
                        authorImage = new ImageIcon(scaledImage);
                    }
                } catch (Exception e) {
                    System.err.println("Error loading image for author " + a.getName() + ": " + e.getMessage());
                    authorImage = null;
                }
            }

            model.addRow(new Object[]{
                a.getAuthorId(),
                authorImage,
                a.getName(),
                a.getCountry(),
                a.getBio(),
                workCount,
                ""
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

                ImageIcon authorImage = null;
                if (a.getImageUrl() != null && !a.getImageUrl().isEmpty()) {
                    try {
                        ImageIcon originalIcon = new ImageIcon(new java.net.URL(a.getImageUrl()));
                        if (originalIcon.getIconWidth() > 0) {
                            Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80 * originalIcon.getIconHeight() / originalIcon.getIconWidth(), Image.SCALE_SMOOTH);
                            authorImage = new ImageIcon(scaledImage);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading image for author " + a.getName() + ": " + e.getMessage());
                        authorImage = null;
                    }
                }

                model.addRow(new Object[]{
                    a.getAuthorId(),
                    authorImage,
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