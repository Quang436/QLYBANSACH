package BLL;

import DAO.OrderDAO;
import Model.Order;
import java.util.List;
import java.math.BigDecimal;

public class OrderBLL {
    private OrderDAO orderDAO;
    
    public OrderBLL() {
        this.orderDAO = new OrderDAO();
    }

    public Order getOrderById(int orderId) {
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
        return orderDAO.getMonthRevenue(month, year);
    }

    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }
    
    public boolean updateOrderStatus(int orderId, String status) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("ID đơn hàng không hợp lệ");
        }
        
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái đơn hàng không được để trống");
        }
        
        // Validate status values
        String[] validStatuses = {"Pending", "Processing", "Shipped", "Delivered", "Cancelled"};
        boolean isValidStatus = false;
        for (String validStatus : validStatuses) {
            if (validStatus.equalsIgnoreCase(status.trim())) {
                isValidStatus = true;
                break;
            }
        }
        
        if (!isValidStatus) {
            throw new IllegalArgumentException("Trạng thái đơn hàng không hợp lệ");
        }
        
        return orderDAO.updateOrderStatus(orderId, status.trim());
    }
    
    public int getTotalOrders() {
        return orderDAO.getTotalOrders();
    }
    
    public int getTodayOrders() {
        return orderDAO.getTodayOrders();
    }

    public List<Object[]> getBestSellingBooks(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Giới hạn phải lớn hơn 0");
        }
        return orderDAO.getBestSellingBooks(limit);
    }

}