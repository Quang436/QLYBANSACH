package GUI;

import BLL.BookBLL;
import BLL.OrderBLL;
import bll.CustomerBLL;
import Model.Book;
import Model.Order;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class DashboardPanel extends JPanel {
    private BookBLL bookBLL;
    private OrderBLL orderBLL;
    private CustomerBLL customerBLL;

    private JLabel lblTotalBooks, lblTodayOrders, lblTotalCustomers, lblMonthRevenue;
    private DefaultTableModel orderModel, lowStockModel, bestSellModel;

    private DecimalFormat currencyFormat = new DecimalFormat("#,###.## ₫");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DashboardPanel() {
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));
         UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 12));
        bookBLL = new BookBLL();
        orderBLL = new OrderBLL();
        customerBLL = new CustomerBLL();

        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        // Top panel chứa header và cards
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(getBackground());

        // Header với title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(getBackground());
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel titleLabel = new JLabel("📊 Tổng quan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 58, 64));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Card statistics với layout 1x4 ngang
        JPanel cardPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        cardPanel.setBackground(getBackground());
        cardPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        // Tạo các card và lấy reference JLabel - FIXED
        lblTotalBooks = createStatCard(cardPanel, "TỔNG SỐ SÁCH", "0", new Color(74, 144, 226), "📚");
        lblTodayOrders = createStatCard(cardPanel, "ĐƠN HÀNG HÔM NAY", "0", new Color(40, 199, 111), "🛒");
        lblTotalCustomers = createStatCard(cardPanel, "KHÁCH HÀNG", "0", new Color(23, 162, 184), "👥");
        lblMonthRevenue = createStatCard(cardPanel, "DOANH THU THÁNG", "0 ₫", new Color(255, 193, 7), "💰");

        topPanel.add(headerPanel);
        topPanel.add(cardPanel);

        // Main content area
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(getBackground());
        contentPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        // Đơn hàng gần đây
        String[] orderCols = {"Mã đơn", "Khách hàng", "Ngày đặt", "Tổng tiền", "Trạng thái", "Thao tác"};
        orderModel = new DefaultTableModel(orderCols, 0);
        JTable orderTable = new JTable(orderModel);
        styleTable(orderTable);
        JScrollPane orderScroll = new JScrollPane(orderTable);
        JPanel orderPanel = createModernTablePanel("📋 Đơn hàng gần đây", orderScroll, new Color(74, 144, 226));

        // Bottom panel với 2 bảng
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setBackground(getBackground());
        bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        // Sắp hết hàng
        String[] lowStockCols = {"Mã sách", "Tên sách", "Số lượng", "Thao tác"};
        lowStockModel = new DefaultTableModel(lowStockCols, 0);
        JTable lowStockTable = new JTable(lowStockModel);
        styleTable(lowStockTable);
        JScrollPane lowStockScroll = new JScrollPane(lowStockTable);
        JPanel lowStockPanel = createModernTablePanel("⚠️ Sắp hết hàng", lowStockScroll, new Color(220, 53, 69));

        // Sách bán chạy
        String[] bestSellCols = {"Mã sách", "Tên sách", "Đã bán", "Doanh thu"};
        bestSellModel = new DefaultTableModel(bestSellCols, 0);
        JTable bestSellTable = new JTable(bestSellModel);
        styleTable(bestSellTable);
        JScrollPane bestSellScroll = new JScrollPane(bestSellTable);
        JPanel bestSellPanel = createModernTablePanel("📈 Sách bán chạy", bestSellScroll, new Color(40, 199, 111));

        bottomPanel.add(lowStockPanel);
        bottomPanel.add(bestSellPanel);

        // Add components
        contentPanel.add(orderPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(bottomPanel);

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Tự động refresh data sau khi tạo xong UI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                refreshData();
            }
        });
    }

    // FIXED: Method mới trả về JLabel và thêm card vào panel
    private JLabel createStatCard(JPanel parent, String title, String value, Color color, String icon) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 0),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(200, 100));

        // Icon panel
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        iconPanel.setOpaque(false);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setForeground(new Color(255, 255, 255, 180));
        iconPanel.add(iconLabel);

        // Text panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(valueLabel);

        card.add(iconPanel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        // Thêm card vào parent panel
        parent.add(card);

        // Trả về valueLabel để có thể cập nhật sau
        return valueLabel;
    }

    private JPanel createModernTablePanel(String title, JScrollPane table, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(accentColor);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Table styling
        table.setBorder(null);
        table.getViewport().setBackground(Color.WHITE);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(table, BorderLayout.CENTER);

        return panel;
    }

    private void styleTable(JTable table) {
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(73, 80, 87));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230)));
        header.setPreferredSize(new Dimension(0, 40));

        // Table styling
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(52, 58, 64));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(242, 244, 247));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(new Color(52, 58, 64));

        // Cell renderer for better appearance
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBorder(new EmptyBorder(8, 12, 8, 12));

        // Apply renderer to specific columns if needed
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public void refreshData() {
        try {
            System.out.println("Bắt đầu refresh data...");
            
            // Kiểm tra null pointer - IMPROVED ERROR HANDLING
            if (lblTotalBooks == null) {
                System.out.println("lblTotalBooks bị null!");
                return;
            }
            if (lblTodayOrders == null) {
                System.out.println("lblTodayOrders bị null!");
                return;
            }
            if (lblTotalCustomers == null) {
                System.out.println("lblTotalCustomers bị null!");
                return;
            }
            if (lblMonthRevenue == null) {
                System.out.println("lblMonthRevenue bị null!");
                return;
            }

            // Tổng số sách
            List<Book> allBooks = bookBLL.getAllBooks();
            int totalBooks = allBooks != null ? allBooks.size() : 0;
            lblTotalBooks.setText(String.valueOf(totalBooks));
            System.out.println("Tổng số sách: " + totalBooks);

            // Tổng số khách hàng  
            List<?> customers = customerBLL.getAllCustomers();
            int totalCustomers = customers != null ? customers.size() : 0;
            lblTotalCustomers.setText(String.valueOf(totalCustomers));
            System.out.println("Tổng số khách hàng: " + totalCustomers);

            // Đơn hàng hôm nay
            int todayOrders = orderBLL.getTodayOrders();
            lblTodayOrders.setText(String.valueOf(todayOrders));
            System.out.println("Đơn hàng hôm nay: " + todayOrders);

            // Doanh thu tháng
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            BigDecimal monthRevenue = orderBLL.getMonthRevenue(month, year);
            if (monthRevenue == null) monthRevenue = BigDecimal.ZERO;
            lblMonthRevenue.setText(currencyFormat.format(monthRevenue));
            System.out.println("Doanh thu tháng: " + monthRevenue);

            // Đơn hàng gần đây
            orderModel.setRowCount(0);
            List<Order> orders = orderBLL.getAllOrders();
            if (orders != null) {
                // Giới hạn chỉ hiển thị 10 đơn hàng gần nhất
                int displayCount = Math.min(orders.size(), 10);
                for (int i = 0; i < displayCount; i++) {
                    Order o = orders.get(i);
                    orderModel.addRow(new Object[]{
                        o.getOrderId(),
                        o.getCustomerName(),
                        o.getOrderDate() != null ? dateFormat.format(o.getOrderDate()) : "",
                        currencyFormat.format(o.getTotalAmount()),
                        o.getStatus(),
                        "👁"
                    });
                }
            }

            // Sách sắp hết hàng
            lowStockModel.setRowCount(0);
            if (allBooks != null) {
                for (Book b : allBooks) {
                    if (b.getStockQuantity() <= 10) {
                        lowStockModel.addRow(new Object[]{
                            b.getBookId(),
                            b.getTitle(),
                            b.getStockQuantity(),
                            "📝 Nhập thêm"
                        });
                    }
                }
            }
            // Sách bán chạy (giả lập data nếu chưa có method)
            bestSellModel.setRowCount(0);
            // TODO: Implement getBestSellingBooks() trong OrderBLL
            // Tạm thời để trống hoặc thêm sample data để test
            
            // Force repaint
            SwingUtilities.invokeLater(() -> {
                this.revalidate();
                this.repaint();
            });
            
            System.out.println("Refresh data hoàn thành!");
            
        } catch (Exception e) {
            System.err.println("Lỗi trong refreshData: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}