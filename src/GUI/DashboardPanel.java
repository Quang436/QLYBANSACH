package GUI;

import BLL.BookBLL;
import bll.OrderBLL;
import bll.CustomerBLL;
import Model.Book;
import Model.Order;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

public class DashboardPanel extends JPanel {
  private BookBLL bookBLL;
    private OrderBLL orderBLL; // *** Biến instance OrderBLL ***
    private CustomerBLL customerBLL;
    
    private JLabel lblTotalBooks, lblTodayOrders, lblTotalCustomers, lblMonthRevenue;
    private DefaultTableModel orderModel, lowStockModel, bestSellModel;
    private JTable orderTable, lowStockTable;

    private DecimalFormat currencyFormat = new DecimalFormat("#,###.## ₫");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DashboardPanel() {
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 12));
           bookBLL = new BookBLL();
        orderBLL = new OrderBLL(); // *** Khởi tạo OrderBLL instance ***
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

        JLabel titleLabel = new JLabel("Tổng quan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 58, 64));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Card statistics với layout 1x4 ngang
        JPanel cardPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        cardPanel.setBackground(getBackground());
        cardPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Tạo các card và lấy reference JLabel
        lblTotalBooks = createStatCard(cardPanel, "TỔNG SỐ SÁCH", "0", new Color(74, 144, 226));
        lblTodayOrders = createStatCard(cardPanel, "ĐƠN HÀNG HÔM NAY", "0", new Color(40, 199, 111));
        lblTotalCustomers = createStatCard(cardPanel, "KHÁCH HÀNG", "0", new Color(23, 162, 184));
        lblMonthRevenue = createStatCard(cardPanel, "DOANH THU THÁNG", "0 ₫", new Color(255, 193, 7));

        topPanel.add(headerPanel);
        topPanel.add(cardPanel);

        // Main content area
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(getBackground());
        contentPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        // Đơn hàng gần đây
        String[] orderCols = {"Mã đơn", "Khách hàng", "Ngày đặt", "Tổng tiền", "Trạng thái", "Thao tác"};
        orderModel = new DefaultTableModel(orderCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        orderTable = new JTable(orderModel);
        styleTable(orderTable);
        setupOrderActionColumn(orderTable, 5);
        JScrollPane orderScroll = new JScrollPane(orderTable);
        JPanel orderPanel = createModernTablePanel("Đơn hàng gần đây", orderScroll, new Color(74, 144, 226));

        // Bottom panel với 2 bảng
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomPanel.setBackground(getBackground());
        bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        // Sắp hết hàng
        String[] lowStockCols = {"Mã sách", "Tên sách", "Số lượng", "Thao tác"};
        lowStockModel = new DefaultTableModel(lowStockCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        lowStockTable = new JTable(lowStockModel);
        styleTable(lowStockTable);
        setupLowStockActionColumn(lowStockTable, 3);
        JScrollPane lowStockScroll = new JScrollPane(lowStockTable);
        JPanel lowStockPanel = createModernTablePanel("Sắp hết hàng", lowStockScroll, new Color(220, 53, 69));

        // Sách bán chạy
        String[] bestSellCols = {"Mã sách", "Tên sách", "Đã bán", "Doanh thu"};
        bestSellModel = new DefaultTableModel(bestSellCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable bestSellTable = new JTable(bestSellModel);
        styleTable(bestSellTable);
        JScrollPane bestSellScroll = new JScrollPane(bestSellTable);
        JPanel bestSellPanel = createModernTablePanel("Sách bán chạy", bestSellScroll, new Color(40, 199, 111));

        bottomPanel.add(lowStockPanel);
        bottomPanel.add(bestSellPanel);

        // Add components
        contentPanel.add(orderPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(bottomPanel);

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Tự động refresh data sau khi tạo xong UI
        SwingUtilities.invokeLater(this::refreshData);
    }

    private JLabel createStatCard(JPanel parent, String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0, 0, 0, 50)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setPreferredSize(new Dimension(200, 120));
        card.setUI(new RoundedCornerUI(15));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(valueLabel);

        card.add(textPanel, BorderLayout.CENTER);
        parent.add(card);

        return valueLabel;
    }

    private static class RoundedCornerUI extends javax.swing.plaf.basic.BasicPanelUI {
        private int cornerRadius;

        public RoundedCornerUI(int cornerRadius) {
            this.cornerRadius = cornerRadius;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getBackground());
            g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, cornerRadius, cornerRadius);
            g2.dispose();
            super.paint(g, c);
        }
    }

    private JPanel createModernTablePanel(String title, JScrollPane table, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(accentColor);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        table.setBorder(null);
        table.getViewport().setBackground(Color.WHITE);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(table, BorderLayout.CENTER);

        return panel;
    }

    private void styleTable(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(73, 80, 87));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230)));
        header.setPreferredSize(new Dimension(0, 40));

        table.setBackground(Color.WHITE);
        table.setForeground(new Color(52, 58, 64));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(242, 244, 247));
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(new Color(52, 58, 64));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBorder(new EmptyBorder(8, 12, 8, 12));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void setupOrderActionColumn(JTable table, int columnIndex) {
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(new ButtonRenderer(true));
        table.getColumnModel().getColumn(columnIndex).setCellEditor(new ButtonEditor(new JCheckBox(), true));
    }

    private void setupLowStockActionColumn(JTable table, int columnIndex) {
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(new ButtonRenderer(false));
        table.getColumnModel().getColumn(columnIndex).setCellEditor(new ButtonEditor(new JCheckBox(), false));
    }

    class ButtonRenderer extends DefaultTableCellRenderer {
        private JPanel panel;
        private JButton viewButton;
        private JButton importButton;

        public ButtonRenderer(boolean isOrderTable) {
            panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setBackground(Color.WHITE);

            viewButton = new JButton("Xem");
            viewButton.setBackground(new Color(74, 144, 226));
            viewButton.setForeground(Color.WHITE);
            viewButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            viewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            importButton = new JButton("Nhập");
            importButton.setBackground(new Color(40, 199, 111));
            importButton.setForeground(Color.WHITE);
            importButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            importButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (isOrderTable) {
                panel.add(viewButton);
            } else {
                panel.add(viewButton);
                panel.add(importButton);
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }
            return panel;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton viewButton;
        private JButton importButton;
        private boolean isOrderTable;
        private int clickedRow;

        public ButtonEditor(JCheckBox checkBox, boolean isOrderTable) {
            super(checkBox);
            this.isOrderTable = isOrderTable;
            panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setBackground(Color.WHITE);

            viewButton = new JButton("Xem");
            viewButton.setBackground(new Color(74, 144, 226));
            viewButton.setForeground(Color.WHITE);
            viewButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            viewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            viewButton.addActionListener(e -> {
                if (clickedRow != -1) {
                    if (isOrderTable) {
                        int orderId = (int) orderModel.getValueAt(clickedRow, 0);
                        showOrderDetails(orderId);
                    } else {
                        int bookId = (int) lowStockModel.getValueAt(clickedRow, 0);
                        showBookDetails(bookId);
                    }
                }
                fireEditingStopped();
            });

            importButton = new JButton("Nhập");
            importButton.setBackground(new Color(40, 199, 111));
            importButton.setForeground(Color.WHITE);
            importButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            importButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            importButton.addActionListener(e -> {
                if (clickedRow != -1) {
                    int bookId = (int) lowStockModel.getValueAt(clickedRow, 0);
                    showImportForm(bookId);
                }
                fireEditingStopped();
            });

            if (isOrderTable) {
                panel.add(viewButton);
            } else {
                panel.add(viewButton);
                panel.add(importButton);
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            clickedRow = row; // Lưu chỉ mục hàng được nhấp
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private void showOrderDetails(int orderId) {
        try {
            Order order = orderBLL.getOrderById(orderId);
            if (order == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder details = new StringBuilder();
            details.append("Mã đơn: ").append(order.getOrderId()).append("\n")
                   .append("Khách hàng: ").append(order.getCustomerName()).append("\n")
                   .append("Ngày đặt: ").append(order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "").append("\n")
                   .append("Tổng tiền: ").append(currencyFormat.format(order.getTotalAmount())).append("\n")
                   .append("Trạng thái: ").append(order.getStatus()).append("\n")
                   .append("Danh sách sản phẩm:\n");

            List<Object[]> orderItems = orderBLL.getOrderItems(orderId);
            if (orderItems != null && !orderItems.isEmpty()) {
                for (Object[] item : orderItems) {
                    details.append("- ").append(item[0]).append(": ").append(item[1]).append(" x ").append(currencyFormat.format(item[2])).append("\n");
                }
            } else {
                details.append("Không có thông tin sản phẩm.\n");
            }

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết đơn hàng", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);

            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton printButton = new JButton("In đơn hàng");
            printButton.setBackground(new Color(52, 58, 64));
            printButton.setForeground(Color.WHITE);
            printButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(dialog, "Đang in đơn hàng " + orderId + "...", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            });
            buttonPanel.add(printButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin đơn hàng: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showBookDetails(int bookId) {
        Book book = bookBLL.getBookById(bookId);
        if (book == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Mã sách: ").append(book.getBookId()).append("\n")
               .append("Tên sách: ").append(book.getTitle()).append("\n")
               .append("Số lượng: ").append(book.getStockQuantity()).append("\n")
               .append("Giá: ").append(currencyFormat.format(book.getPrice())).append("\n")
               .append("Tác giả: ").append(book.getAuthorName() != null ? book.getAuthorName() : "Không có thông tin").append("\n")
               .append("Nhà xuất bản: ").append(book.getPublisherName() != null ? book.getPublisherName() : "Không có thông tin");

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết sách", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton historyButton = new JButton("Xem lịch sử nhập hàng");
        historyButton.setBackground(new Color(52, 58, 64));
        historyButton.setForeground(Color.WHITE);
        historyButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Lịch sử nhập hàng cho sách " + bookId + ":\n" +
                    "Ngày 03/06/2025: Nhập 50 đơn vị\n" +
                    "Ngày 01/06/2025: Nhập 30 đơn vị", "Lịch sử nhập hàng", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(historyButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showImportForm(int bookId) {
        Book book = bookBLL.getBookById(bookId);
        if (book == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nhập thêm sản phẩm", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel quantityLabel = new JLabel("Số lượng nhập thêm:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(quantityLabel, gbc);

        JTextField quantityField = new JTextField("0", 10);
        gbc.gridx = 1;
        gbc.gridy = 0;
        dialog.add(quantityField, gbc);

        JLabel priceLabel = new JLabel("Giá mới (nếu có):");
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(priceLabel, gbc);

        JTextField priceField = new JTextField(currencyFormat.format(book.getPrice()).replace(" ₫", ""), 10);
        gbc.gridx = 1;
        gbc.gridy = 1;
        dialog.add(priceField, gbc);

        JButton confirmButton = new JButton("Xác nhận");
        confirmButton.setBackground(new Color(40, 199, 111));
        confirmButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(confirmButton, gbc);

        confirmButton.addActionListener(e -> {
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int newStock = book.getStockQuantity() + quantity;
                if (newStock > 1000) {
                    JOptionPane.showMessageDialog(dialog, "Số lượng tồn kho không được vượt quá 1000!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal newPrice = book.getPrice();
                String priceInput = priceField.getText().trim();
                if (!priceInput.isEmpty()) {
                    newPrice = new BigDecimal(priceInput.replace(",", ""));
                    if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Giá phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                book.setStockQuantity(newStock);
                book.setPrice(newPrice);
                if (bookBLL.updateBook(book)) {
                    refreshData();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Nhập hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật số lượng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    public void refreshData() {
        try {
            System.out.println("Bắt đầu refresh data...");

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

            List<Book> allBooks = bookBLL.getAllBooks();
            int totalBooks = allBooks != null ? allBooks.size() : 0;
            lblTotalBooks.setText(String.valueOf(totalBooks));
            System.out.println("Tổng số sách: " + totalBooks);

            List<?> customers = customerBLL.getAllCustomers();
            int totalCustomers = customers != null ? customers.size() : 0;
            lblTotalCustomers.setText(String.valueOf(totalCustomers));
            System.out.println("Tổng số khách hàng: " + totalCustomers);

            int todayOrders = orderBLL.getTodayOrders();
            lblTodayOrders.setText(String.valueOf(todayOrders));
            System.out.println("Đơn hàng hôm nay: " + todayOrders);

            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            BigDecimal monthRevenue = orderBLL.getMonthRevenue(month, year);
            if (monthRevenue == null) monthRevenue = BigDecimal.ZERO;
            lblMonthRevenue.setText(currencyFormat.format(monthRevenue));
            System.out.println("Doanh thu tháng: " + monthRevenue);

            orderModel.setRowCount(0);
            List<Order> orders = orderBLL.getAllOrders();
            if (orders != null && !orders.isEmpty()) {
                int displayCount = Math.min(orders.size(), 10);
                for (int i = 0; i < displayCount; i++) {
                    Order o = orders.get(i);
                    orderModel.addRow(new Object[]{
                        o.getOrderId(),
                        o.getCustomerName(),
                        o.getOrderDate() != null ? dateFormat.format(o.getOrderDate()) : "",
                        currencyFormat.format(o.getTotalAmount()),
                        o.getStatus(),
                        "Xem"
                    });
                }
            } else {
                System.out.println("Không có đơn hàng để hiển thị!");
            }

            lowStockModel.setRowCount(0);
            if (allBooks != null && !allBooks.isEmpty()) {
                for (Book b : allBooks) {
                    if (b.getStockQuantity() <= 10) {
                        lowStockModel.addRow(new Object[]{
                            b.getBookId(),
                            b.getTitle(),
                            b.getStockQuantity(),
                            "Nhập"
                        });
                    }
                }
            } else {
                System.out.println("Không có sách để hiển thị trong lowStock!");
            }

            bestSellModel.setRowCount(0);
            List<Object[]> bestSellers = orderBLL.getBestSellingBooks(10);
            if (bestSellers != null && !bestSellers.isEmpty()) {
                for (Object[] bestSeller : bestSellers) {
                    bestSellModel.addRow(new Object[]{
                        bestSeller[0],
                        bestSeller[1],
                        bestSeller[2],
                        currencyFormat.format(bestSeller[3])
                    });
                }
            } else {
                System.out.println("Không có sách bán chạy để hiển thị!");
            }

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