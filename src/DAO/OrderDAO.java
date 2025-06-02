package DAO;

import Model.Order;
import Util.DatabaseConnection;
import static Util.DatabaseConnection.getConnection;
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
    
    public boolean updateOrderStatus(int orderId, String newStatus) throws SQLException {
         String sql = "UPDATE Orders SET Status = ? WHERE OrderId = ?";
         try (Connection conn = getConnection();
              PreparedStatement ps = conn.prepareStatement(sql)) {

             ps.setString(1, newStatus);
             ps.setInt(2, orderId);

             int rowsAffected = ps.executeUpdate();

             return rowsAffected > 0; // Trả về true nếu thành công
         } catch (SQLException e) {
             System.err.println("Lỗi SQL tại OrderDAO khi cập nhật trạng thái đơn hàng (" + orderId + "): " + e.getMessage());
             e.printStackTrace();
             throw e;
         }
     }
    public boolean deleteOrder(int orderId) throws SQLException {
        String sql = "DELETE FROM Orders WHERE OrderId = ?"; // Thay thế tên bảng và cột
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0; // Trả về true nếu có ít nhất 1 dòng bị xóa

//>>>>>>> Stashed changes
        } catch (SQLException e) {
            System.err.println("Lỗi SQL tại OrderDAO khi xóa đơn hàng (" + orderId + "): " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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
    public boolean updateOrder(Order order) throws SQLException {
        // Câu lệnh UPDATE (thay thế tên bảng và cột)
        String sql = "UPDATE Orders SET CustomerName = ?, OrderDate = ?, TotalAmount = ?, Status = ? WHERE OrderId = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, order.getCustomerName());
             ps.setDate(2, order.getOrderDate() != null ? new java.sql.Date(order.getOrderDate().getTime()) : null);
            ps.setBigDecimal(3, order.getTotalAmount());
            ps.setString(4, order.getStatus());
            ps.setInt(5, order.getOrderId()); // Điều kiện WHERE

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0; // Trả về true nếu có ít nhất 1 dòng được cập nhật

        } catch (SQLException e) {
            System.err.println("Lỗi SQL tại OrderDAO khi cập nhật đơn hàng (" + order.getOrderId() + "): " + e.getMessage());
            e.printStackTrace();
            throw e;
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
    public boolean addOrder(Order order) throws SQLException {
        // Câu lệnh INSERT (thay thế tên bảng và cột)
        String sql = "INSERT INTO Orders (CustomerName, OrderDate, TotalAmount, Status) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Thiết lập các tham số từ đối tượng Order
            ps.setString(1, order.getCustomerName());
            // Chuyển đổi java.util.Date sang java.sql.Date
            ps.setDate(2, order.getOrderDate() != null ? new java.sql.Date(order.getOrderDate().getTime()) : null);
            ps.setBigDecimal(3, order.getTotalAmount());
            ps.setString(4, order.getStatus());

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0; // Trả về true nếu có ít nhất 1 dòng được thêm

        } catch (SQLException e) {
            System.err.println("Lỗi SQL tại OrderDAO khi thêm đơn hàng: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
     public Order getOrderById(int orderId) throws SQLException {
        String sql = "SELECT OrderId, CustomerName, OrderDate, TotalAmount, Status FROM Orders WHERE OrderId = ?"; // Thay thế tên bảng và cột
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("OrderId"));
                    order.setCustomerName(rs.getString("CustomerName"));
                    order.setOrderDate(rs.getDate("OrderDate"));
                    order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                    order.setStatus(rs.getString("Status"));
                    return order; // Trả về đối tượng Order nếu tìm thấy
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi SQL tại OrderDAO khi lấy đơn hàng theo ID (" + orderId + "): " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return null; // Trả về null nếu không tìm thấy hoặc có lỗi
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
    
public List<Object[]> getBestSellingBooks(int limit) throws SQLException {
    List<Object[]> result = new ArrayList<>();
    String sql = "SELECT TOP (?) b.BookID, b.Title, SUM(oi.Quantity) as TotalSold, " +
                 "SUM(oi.Quantity * oi.Price) as TotalRevenue " +
                 "FROM OrderItems oi " +
                 "JOIN Books b ON oi.BookID = b.BookID " +
                 "GROUP BY b.BookID, b.Title " +
                 "ORDER BY TotalSold DESC";
                 
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, limit);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Object[] row = new Object[4];
            row[0] = rs.getInt("BookID");
            row[1] = rs.getString("Title");
            row[2] = rs.getInt("TotalSold");
            row[3] = rs.getBigDecimal("TotalRevenue");
            result.add(row);
        }
    }
    
    return result;
}
}