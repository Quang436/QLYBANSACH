package DAO;

import Model.Order;
import Util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class OrderDAO {
    
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = """
            SELECT o.order_id, o.customer_id, o.order_date, o.total_amount, 
                   o.status, o.shipping_address, o.payment_method, c.name as customer_name
            FROM ORDERS o
            LEFT JOIN CUSTOMERS c ON o.customer_id = c.customer_id
            ORDER BY o.order_date DESC
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setShippingAddress(rs.getString("shipping_address"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setCustomerName(rs.getString("customer_name"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE ORDERS SET status = ? WHERE order_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getTotalOrders() {
        String sql = "SELECT COUNT(*) FROM ORDERS";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getTodayOrders() {
        String sql = "SELECT COUNT(*) FROM ORDERS WHERE CAST(order_date AS DATE) = CAST(GETDATE() AS DATE)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public BigDecimal getMonthRevenue(int month, int year) {
    BigDecimal total = BigDecimal.ZERO;
    String sql = "SELECT SUM(total_amount) FROM ORDERS WHERE MONTH(order_date) = ? AND YEAR(order_date) = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, month);
        stmt.setInt(2, year);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                total = rs.getBigDecimal(1);
                if (total == null) total = BigDecimal.ZERO;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return total;
}
}
