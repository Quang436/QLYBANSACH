package dao;

import Util.DatabaseConnection;
import Model.Customer;
import static Util.DatabaseConnection.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CustomerDAO {
    private static final Logger LOGGER = Logger.getLogger(CustomerDAO.class.getName());

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu");
            }

            String sql = "SELECT * FROM CUSTOMERS";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setName(rs.getString("name"));
                    customer.setEmail(rs.getString("email"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setAddress(rs.getString("address"));
                    customer.setRegistrationDate(rs.getDate("registration_date"));
                    customers.add(customer);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách khách hàng: " + e.getMessage(), e);
        }
        return customers;
    }

    public Customer getCustomerById(int customerId) {
        Customer customer = null;
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu");
            }

            String sql = "SELECT * FROM CUSTOMERS WHERE customer_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        customer = new Customer();
                        customer.setCustomerId(rs.getInt("customer_id"));
                        customer.setName(rs.getString("name"));
                        customer.setEmail(rs.getString("email"));
                        customer.setPhone(rs.getString("phone"));
                        customer.setAddress(rs.getString("address"));
                        customer.setRegistrationDate(rs.getDate("registration_date"));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy khách hàng theo ID: " + e.getMessage(), e);
        }
        return customer;
    }

    public boolean deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM Customer WHERE CustomerId = ?";
        int rowsAffected = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            rowsAffected = ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Lỗi SQL tại CustomerDAO khi xóa khách hàng (ID: " + customerId + "): " + e.getMessage());
            e.printStackTrace();
            throw e; // Ném lại exception để BLL có thể bắt và xử lý
        } catch (Exception e) {
             System.err.println("Lỗi không xác định tại CustomerDAO khi xóa khách hàng (ID: " + customerId + "): " + e.getMessage());
             e.printStackTrace();
             // Có thể ném Exception khác hoặc xử lý tại đây tùy cấu trúc project
             throw new SQLException("Lỗi khi xóa khách hàng: " + e.getMessage());
        }

        return rowsAffected > 0;
    }
    
    public boolean addCustomer(Customer customer) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu");
            }

            String sql = "INSERT INTO CUSTOMERS (name, email, phone, address, registration_date) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, customer.getName());
                ps.setString(2, customer.getEmail());
                ps.setString(3, customer.getPhone());
                ps.setString(4, customer.getAddress());
                ps.setTimestamp(5, new java.sql.Timestamp(customer.getRegistrationDate().getTime()));
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm khách hàng: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean updateCustomer(Customer customer) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu");
            }

            String sql = "UPDATE CUSTOMERS SET name = ?, email = ?, phone = ?, address = ? WHERE customer_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, customer.getName());
                ps.setString(2, customer.getEmail());
                ps.setString(3, customer.getPhone());
                ps.setString(4, customer.getAddress());
                ps.setInt(5, customer.getCustomerId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật khách hàng: " + e.getMessage(), e);
            return false;
        }
    }
}