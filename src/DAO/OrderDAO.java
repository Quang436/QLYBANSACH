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
    
    public Order getOrderById(int orderId) {
        Order order = null;
        String sql = """
            SELECT o.order_id, o.customer_id, o.order_date, o.total_amount, 
                   o.status, o.shipping_address, o.payment_method, c.name as customer_name
            FROM ORDERS o
            LEFT JOIN CUSTOMERS c ON o.customer_id = c.customer_id
            WHERE o.order_id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setCustomerId(rs.getInt("customer_id"));
                    order.setOrderDate(rs.getTimestamp("order_date"));
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));
                    order.setStatus(rs.getString("status"));
                    order.setShippingAddress(rs.getString("shipping_address"));
                    order.setPaymentMethod(rs.getString("payment_method"));
                    order.setCustomerName(rs.getString("customer_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }
    
    public List<Object[]> getOrderItems(int orderId) {
        List<Object[]> orderItems = new ArrayList<>();
        String sql = """
            SELECT b.title, od.quantity, od.unit_price
            FROM ORDER_DETAILS od
            JOIN BOOKS b ON od.book_id = b.book_id
            WHERE od.order_id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] item = new Object[]{
                        rs.getString("title"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("unit_price")
                    };
                    orderItems.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
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
    
    public List<Object[]> getBestSellingBooks(int limit) {
        List<Object[]> bestSellers = new ArrayList<>();
        String sql = """
            SELECT TOP (?)
                b.book_id,
                b.title,
                SUM(od.quantity) as total_quantity,
                SUM(od.quantity * od.unit_price) as total_revenue
            FROM ORDER_DETAILS od
            JOIN BOOKS b ON od.book_id = b.book_id
            JOIN ORDERS o ON od.order_id = o.order_id
            GROUP BY b.book_id, b.title
            ORDER BY total_quantity DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getLong("total_quantity"),
                        rs.getBigDecimal("total_revenue")
                    };
                    bestSellers.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in getBestSellingBooks: " + e.getMessage());
            e.printStackTrace();
        }
        return bestSellers;
    }
}