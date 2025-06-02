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
        setBackground(new Color(245, 247, 250)); // Màu nền xám nhạt

        // ===== TOP AREA: Title and Controls =====
        JPanel topAreaPanel = new JPanel();
        topAreaPanel.setLayout(new BoxLayout(topAreaPanel, BoxLayout.Y_AXIS));
        topAreaPanel.setBackground(getBackground());
        topAreaPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(getBackground());
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left side - Icon and Title
        JPanel titleLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleLeftPanel.setBackground(getBackground());
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        JLabel titleLabel = new JLabel(" Quản lý tác giả");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLeftPanel.add(iconLabel);
        titleLeftPanel.add(titleLabel);

        // Right side - Add button
        JPanel titleRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        titleRightPanel.setBackground(getBackground());
        btnAdd = createStyledButton("+ Thêm tác giả mới", new Color(0, 123, 255));
        titleRightPanel.add(btnAdd);

        titlePanel.add(titleLeftPanel, BorderLayout.WEST);
        titlePanel.add(titleRightPanel, BorderLayout.EAST);

        // Search and Filter Panel with modern card design
        JPanel searchCardPanel = new JPanel();
        searchCardPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        searchCardPanel.setBackground(Color.WHITE);
        searchCardPanel.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(12, new Color(0, 0, 0, 0)), // Transparent border for rounded effect
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Add shadow effect
        searchCardPanel.setBorder(BorderFactory.createCompoundBorder(
            createShadowBorder(),
            BorderFactory.createCompoundBorder(
                createRoundedBorder(12, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            )
        ));

        // Search field with modern styling
        JPanel searchFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchFieldPanel.setBackground(Color.WHITE);
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(new Color(73, 80, 87));
        
        txtSearch = new JTextField("Tên tác giả...");
        txtSearch.setPreferredSize(new Dimension(250, 40));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(8, new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Add placeholder behavior
        addPlaceholderBehavior(txtSearch, "Tên tác giả...");
        
        searchFieldPanel.add(searchLabel);
        searchFieldPanel.add(txtSearch);

        // Country dropdown with modern styling
        JPanel countryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        countryPanel.setBackground(Color.WHITE);
        JLabel countryLabel = new JLabel("Quốc gia:");
        countryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countryLabel.setForeground(new Color(73, 80, 87));
        
        String[] countries = {"-- Tất cả --", "Việt Nam", "Anh", "Mỹ", "Brazil", "Nhật Bản"};
        cmbCountry = new JComboBox<>(countries);
        cmbCountry.setPreferredSize(new Dimension(180, 40));
        cmbCountry.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbCountry.setBorder(createRoundedBorder(8, new Color(206, 212, 218)));
        cmbCountry.setBackground(Color.WHITE);
        
        countryPanel.add(countryLabel);
        countryPanel.add(cmbCountry);

        // Search button
        btnSearch = createStyledButton("🔍 Tìm kiếm", new Color(0, 123, 255));

        searchCardPanel.add(searchFieldPanel);
        searchCardPanel.add(countryPanel);
        searchCardPanel.add(btnSearch);

        // Add components to top area
        topAreaPanel.add(titlePanel);
        topAreaPanel.add(Box.createVerticalStrut(20));
        topAreaPanel.add(searchCardPanel);

        // ===== TABLE AREA with Card Design =====
        JPanel tableCardPanel = new JPanel(new BorderLayout());
        tableCardPanel.setBackground(Color.WHITE);
        tableCardPanel.setBorder(BorderFactory.createCompoundBorder(
            createShadowBorder(),
            BorderFactory.createCompoundBorder(
                createRoundedBorder(12, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
            )
        ));

        createTable();
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBackground(Color.WHITE);

        tableCardPanel.add(scroll, BorderLayout.CENTER);

        // ===== ADD COMPONENTS TO MAIN PANEL =====
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(getBackground());
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        mainContentPanel.add(tableCardPanel, BorderLayout.CENTER);

        add(topAreaPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);

        // Load data
        refreshData();

        // Event listeners
        btnSearch.addActionListener(e -> searchAuthors());
        txtSearch.addActionListener(e -> searchAuthors());
        cmbCountry.addActionListener(e -> searchAuthors());
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

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add rounded corners
        button.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(8, bgColor),
            BorderFactory.createEmptyBorder(12, 24, 12, 24)
        ));
        
        return button;
    }

    private javax.swing.border.Border createRoundedBorder(int radius, Color color) {
        return new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
                g2.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(2, 2, 2, 2);
            }
        };
    }

    private javax.swing.border.Border createShadowBorder() {
        return new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                for (int i = 0; i < 4; i++) {
                    g2.setColor(new Color(0, 0, 0, 5 - i));
                    g2.drawRoundRect(x + i, y + i, width - 2*i - 1, height - 2*i - 1, 12, 12);
                }
                g2.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(4, 4, 4, 4);
            }
        };
    }

    private void addPlaceholderBehavior(JTextField textField, String placeholder) {
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
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
        table.setRowHeight(90);
        table.setGridColor(new Color(248, 249, 250));
        table.setSelectionBackground(new Color(232, 245, 255));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        
        // Header styling
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(233, 236, 239)));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(350);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(180);
        
        table.getColumn("Thao tác").setCellRenderer(new ActionButtonRenderer());
    }

    // Custom renderer for action buttons with modern design
    class ActionButtonRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
            panel.setOpaque(true);
            
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }
            
            // View button (cyan/teal)
            JButton btnView = createActionButton("👁", new Color(23, 162, 184), "Xem");
            
            // Edit button (orange)
            JButton btnEdit = createActionButton("✏", new Color(255, 193, 7), "Sửa");
            
            // Delete button (red)
            JButton btnDelete = createActionButton("🗑", new Color(220, 53, 69), "Xóa");
            
            panel.add(btnView);
            panel.add(btnEdit);
            panel.add(btnDelete);
            
            return panel;
        }
        
        private JButton createActionButton(String icon, Color bgColor, String tooltip) {
            JButton button = new JButton(icon);
            button.setBackground(bgColor);
            button.setForeground(Color.WHITE);
            button.setPreferredSize(new Dimension(35, 35));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            button.setToolTipText(tooltip);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Make button circular
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            button.setOpaque(true);
            
            return button;
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
                        Image scaledImage = originalIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
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
        if (keyword.equals("Tên tác giả...")) keyword = "";
        
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
                            Image scaledImage = originalIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
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