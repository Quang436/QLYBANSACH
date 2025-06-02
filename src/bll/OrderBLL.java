package bll;

import DAO.OrderDAO;
import Model.Order;
import java.util.List;
import java.math.BigDecimal;
import java.sql.SQLException;

public class OrderBLL {
    private OrderDAO orderDAO;
    
    public OrderBLL() {
        this.orderDAO = new OrderDAO();
    }

    /**
     * Lấy đơn hàng theo ID từ cơ sở dữ liệu.
     * @param orderId ID của đơn hàng cần lấy.
     * @return Đối tượng Order tương ứng, hoặc null nếu không tìm thấy.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình thao tác SQL.
     */
    public Order getOrderById(int orderId) throws SQLException {
        if (orderId <= 0) {
            throw new IllegalArgumentException("ID đơn hàng không hợp lệ");
        }
        return orderDAO.getOrderById(orderId);
    }

    public List<Object[]> getOrderItems(int orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("ID đơn hàng không hợp lệ");
        }
        return orderDAO.getOrderItems(orderId);
    }

    public BigDecimal getMonthRevenue(int month, int year) {
        try {
            BigDecimal revenue = orderDAO.getMonthRevenue(month, year);
            return revenue != null ? revenue : BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Lỗi tại OrderBLL khi lấy doanh thu tháng: " + e.getMessage());
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    public List<Object[]> getBestSellingBooks(int limit) {
    try {
        return orderDAO.getBestSellingBooks(limit);
    } catch (Exception e) {
        System.err.println("Lỗi tại OrderBLL khi lấy danh sách sách bán chạy: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
    public List<Order> getAllOrders() {
        try {
            return orderDAO.getAllOrders();
        } catch (Exception e) {
            System.err.println("Lỗi tại OrderBLL khi lấy tất cả đơn hàng: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        if (orderId <= 0) {
            throw new IllegalArgumentException("ID đơn hàng không hợp lệ");
        }
        
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái đơn hàng không được để trống");
        }
        
        // Validate status values
        String[] validStatuses = {"Pending", "Processing", "Shipped", "Delivered", "Cancelled"};
        boolean isValidStatus = false;
        String trimmedStatus = status.trim();
        for (String validStatus : validStatuses) {
            if (validStatus.equalsIgnoreCase(trimmedStatus)) {
                isValidStatus = true;
                status = validStatus;
                break;
            }
        }
        
        if (!isValidStatus) {
            throw new IllegalArgumentException("Trạng thái đơn hàng không hợp lệ: " + trimmedStatus);
        }
        
        return orderDAO.updateOrderStatus(orderId, status);
    }
    public int getTodayOrders() {
        try {
            return orderDAO.getTodayOrders();
        } catch (Exception e) {
            System.err.println("Lỗi tại OrderBLL khi lấy số đơn hàng hôm nay: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    public int getTotalOrders() {
        try {
            return orderDAO.getTotalOrders();
        } catch (Exception e) {
            System.err.println("Lỗi tại OrderBLL khi lấy tổng số đơn hàng: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    public boolean updateOrder(Order order) {
        if (order == null || order.getOrderId() <= 0 || order.getCustomerName() == null || order.getCustomerName().trim().isEmpty() || order.getTotalAmount() == null) {
            System.err.println("Order object is null, missing ID, or missing required fields for updating.");
            return false;
        }
        try {
            return orderDAO.updateOrder(order);
        } catch (Exception e) {
            System.err.println("Lỗi tại OrderBLL khi cập nhật đơn hàng (" + order.getOrderId() + "): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean addOrder(Order order) {
        if (order == null || order.getCustomerName() == null || order.getCustomerName().trim().isEmpty() || order.getTotalAmount() == null) {
            System.err.println("Order object is null or missing required fields for adding.");
            return false;
        }
        try {
            return orderDAO.addOrder(order);
        } catch (Exception e) {
            System.err.println("Lỗi tại OrderBLL khi thêm đơn hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteOrder(int orderId) {
        if (orderId <= 0) {
            System.err.println("Invalid Order ID for deletion: " + orderId);
            return false;
        }
        try {
            return orderDAO.deleteOrder(orderId);
        } catch (Exception e) {
            System.err.println("Lỗi tại OrderBLL khi xóa đơn hàng (" + orderId + "): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

