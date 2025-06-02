package GUI;

import javax.swing.*;
import java.awt.*;

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

    public MainFrame() {
        setTitle("Quản Lý Bán Sách");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(33, 37, 41));
        sidebar.setPreferredSize(new Dimension(220, 0));

        JButton btnDashboard = createSidebarButton("Tổng quan", "🏠");
        JButton btnBooks = createSidebarButton("Quản lý sách", "📚");
        JButton btnAuthors = createSidebarButton("Tác giả", "✍️");
        JButton btnCategories = createSidebarButton("Danh mục", "📂");
        JButton btnPublishers = createSidebarButton("Nhà xuất bản", "🏢");
        JButton btnCustomers = createSidebarButton("Khách hàng", "👥");
        JButton btnOrders = createSidebarButton("Đơn hàng", "🛒");
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
        JLabel lblTitle = new JLabel("📚 Quản Lý Bán Sách");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
        JLabel userLabel = new JLabel("👤 Admin");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 20));
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