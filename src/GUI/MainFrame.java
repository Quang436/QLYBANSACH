package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel contentPanel = new JPanel(cardLayout);

    // Các panel quản lý
    private DashboardPanel dashboardPanel;
    private BookManagementPanel bookPanel;
    private AuthorManagementPanel authorPanel;
    private CategoryManagementPanel categoryPanel;
    private PublisherManagementPanel publisherPanel;
    private CustomerManagementPanel customerPanel;
    private OrderManagementPanel orderPanel;
    
    // Dropdown menu components
    private JPopupMenu adminDropdown;
    private JLabel userLabel;

    public MainFrame() {
        setTitle("Quản Lý Bán Sách");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);

        // Tạo Admin Dropdown Menu
        createAdminDropdown();

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(33, 37, 41));
        sidebar.setPreferredSize(new Dimension(220, 0));

        JButton btnDashboard = createSidebarButton("Tổng quan", "■");
        JButton btnBooks = createSidebarButton("Quản lý sách", "≡");
        JButton btnAuthors = createSidebarButton("Tác giả", "◊");
        JButton btnCategories = createSidebarButton("Danh mục", "※");
        JButton btnPublishers = createSidebarButton("Nhà xuất bản", "▲");
        JButton btnCustomers = createSidebarButton("Khách hàng", "◉");
        JButton btnOrders = createSidebarButton("Đơn hàng", "◎");
        
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(btnDashboard);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnBooks);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnAuthors);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnCategories);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnPublishers);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnCustomers);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnOrders);
        sidebar.add(Box.createVerticalGlue());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 37, 41));
        header.setPreferredSize(new Dimension(0, 60));
        
        JLabel lblTitle = new JLabel("≡ Quản Lý Bán Sách");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        
        // User label với dropdown
        userLabel = new JLabel("◉ Admin ▼");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 20));
        userLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Thêm mouse listener cho user label
        userLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showAdminDropdown(e);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                userLabel.setForeground(new Color(173, 216, 230)); // Light blue
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                userLabel.setForeground(Color.WHITE);
            }
        });
        
        header.add(lblTitle, BorderLayout.WEST);
        header.add(userLabel, BorderLayout.EAST);

        // Content panels
        dashboardPanel = new DashboardPanel();
        bookPanel = new BookManagementPanel();
        authorPanel = new AuthorManagementPanel();
        categoryPanel = new CategoryManagementPanel();
        publisherPanel = new PublisherManagementPanel();
        customerPanel = new CustomerManagementPanel();
        orderPanel = new OrderManagementPanel();

        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(bookPanel, "books");
        contentPanel.add(authorPanel, "authors");
        contentPanel.add(categoryPanel, "categories");
        contentPanel.add(publisherPanel, "publishers");
        contentPanel.add(customerPanel, "customers");
        contentPanel.add(orderPanel, "orders");

        // Layout
        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Sự kiện chuyển panel
        btnDashboard.addActionListener(e -> {
            cardLayout.show(contentPanel, "dashboard");
            dashboardPanel.refreshData();
        });
        btnBooks.addActionListener(e -> {
            cardLayout.show(contentPanel, "books");
            bookPanel.refreshData();
        });
        btnAuthors.addActionListener(e -> {
            cardLayout.show(contentPanel, "authors");
            authorPanel.refreshData();
        });
        btnCategories.addActionListener(e -> {
            cardLayout.show(contentPanel, "categories");
            categoryPanel.refreshData();
        });
        btnPublishers.addActionListener(e -> {
            cardLayout.show(contentPanel, "publishers");
            publisherPanel.refreshData();
        });
        btnCustomers.addActionListener(e -> {
            cardLayout.show(contentPanel, "customers");
            customerPanel.refreshData();
        });
        btnOrders.addActionListener(e -> {
            cardLayout.show(contentPanel, "orders");
            orderPanel.refreshData();
        });

        cardLayout.show(contentPanel, "dashboard");
        setVisible(true);
    }

    private void createAdminDropdown() {
        adminDropdown = new JPopupMenu();
        adminDropdown.setBackground(Color.WHITE);
        adminDropdown.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Tài khoản
        JMenuItem accountItem = createDropdownItem("👤 Tài khoản", new Color(0, 123, 255));
        accountItem.addActionListener(e -> showAccountDialog());
        
        // Cài đặt
        JMenuItem settingsItem = createDropdownItem("⚙ Cài đặt", Color.DARK_GRAY);
        settingsItem.addActionListener(e -> showSettingsDialog());
        
        // Đăng xuất
        JMenuItem logoutItem = createDropdownItem("🚪 Đăng xuất", Color.DARK_GRAY);
        logoutItem.addActionListener(e -> logout());

        adminDropdown.add(accountItem);
        adminDropdown.addSeparator();
        adminDropdown.add(settingsItem);
        adminDropdown.addSeparator();
        adminDropdown.add(logoutItem);
    }

    private JMenuItem createDropdownItem(String text, Color textColor) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Arial", Font.PLAIN, 14));
        item.setForeground(textColor);
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        item.setPreferredSize(new Dimension(150, 35));
        
        // Hover effect
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(248, 249, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(Color.WHITE);
            }
        });
        
        return item;
    }

    private void showAdminDropdown(MouseEvent e) {
        adminDropdown.show(userLabel, 0, userLabel.getHeight());
    }

    private void showAccountDialog() {
        JDialog accountDialog = new JDialog(this, "Thông tin tài khoản", true);
        accountDialog.setSize(400, 300);
        accountDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Tên đăng nhập
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        JTextField usernameField = new JTextField("admin", 15);
        usernameField.setEditable(false);
        panel.add(usernameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(new JTextField("admin@bookstore.com", 15), gbc);
        
        // Vai trò
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        JTextField roleField = new JTextField("Quản trị viên", 15);
        roleField.setEditable(false);
        panel.add(roleField, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("Lưu");
        JButton cancelBtn = new JButton("Hủy");
        
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(accountDialog, "Thông tin đã được cập nhật!");
            accountDialog.dispose();
        });
        
        cancelBtn.addActionListener(e -> accountDialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, gbc);
        
        accountDialog.add(panel);
        accountDialog.setVisible(true);
    }

    private void showSettingsDialog() {
        JDialog settingsDialog = new JDialog(this, "Cài đặt hệ thống", true);
        settingsDialog.setSize(450, 400);
        settingsDialog.setLocationRelativeTo(this);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab Giao diện
        JPanel uiPanel = new JPanel(new GridBagLayout());
        uiPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        uiPanel.add(new JLabel("Chủ đề:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> themeCombo = new JComboBox<>(new String[]{"Sáng", "Tối", "Tự động"});
        uiPanel.add(themeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        uiPanel.add(new JLabel("Ngôn ngữ:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> langCombo = new JComboBox<>(new String[]{"Tiếng Việt", "English"});
        uiPanel.add(langCombo, gbc);
        
        tabbedPane.addTab("Giao diện", uiPanel);
        
        // Tab Hệ thống
        JPanel systemPanel = new JPanel(new GridBagLayout());
        systemPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        systemPanel.add(new JCheckBox("Tự động sao lưu dữ liệu"), gbc);
        gbc.gridy = 1;
        systemPanel.add(new JCheckBox("Gửi thông báo qua email"), gbc);
        gbc.gridy = 2;
        systemPanel.add(new JCheckBox("Ghi log hoạt động"), gbc);
        
        tabbedPane.addTab("Hệ thống", systemPanel);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("Lưu cài đặt");
        JButton cancelBtn = new JButton("Hủy");
        
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(settingsDialog, "Cài đặt đã được lưu!");
            settingsDialog.dispose();
        });
        
        cancelBtn.addActionListener(e -> settingsDialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        settingsDialog.setLayout(new BorderLayout());
        settingsDialog.add(tabbedPane, BorderLayout.CENTER);
        settingsDialog.add(buttonPanel, BorderLayout.SOUTH);
        settingsDialog.setVisible(true);
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            // Đóng cửa sổ hiện tại và mở lại màn hình đăng nhập
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                // Có thể tạo một LoginFrame mới ở đây
                JOptionPane.showMessageDialog(null, "Đã đăng xuất thành công!");
                System.exit(0); // Tạm thời thoát ứng dụng
            });
        }
    }

    private JButton createSidebarButton(String text, String icon) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(33, 37, 41));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 58, 64));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(33, 37, 41));
            }
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}