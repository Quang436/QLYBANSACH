package dao;

import Util.DatabaseConnection;
import Model.Publisher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PublisherDAO {
    private static final Logger LOGGER = Logger.getLogger(PublisherDAO.class.getName());

    public List<Publisher> getAllPublishers() {
        List<Publisher> publishers = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu");
            }

            String sql = "SELECT * FROM PUBLISHERS";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Publisher publisher = new Publisher();
                    publisher.setPublisherId(rs.getInt("publisher_id"));
                    publisher.setName(rs.getString("name"));
                    publisher.setAddress(rs.getString("address"));
                    publisher.setPhone(rs.getString("phone"));
                    publisher.setEmail(rs.getString("email"));
                    publishers.add(publisher);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách nhà xuất bản: " + e.getMessage(), e);
        }
        return publishers;
    }

    public Publisher getPublisherById(int publisherId) {
        Publisher publisher = null;
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu");
            }

            String sql = "SELECT * FROM PUBLISHERS WHERE publisher_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, publisherId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        publisher = new Publisher();
                        publisher.setPublisherId(rs.getInt("publisher_id"));
                        publisher.setName(rs.getString("name"));
                        publisher.setAddress(rs.getString("address"));
                        publisher.setPhone(rs.getString("phone"));
                        publisher.setEmail(rs.getString("email"));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy nhà xuất bản theo ID: " + e.getMessage(), e);
        }
        return publisher;
    }

    public boolean addPublisher(Publisher publisher) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu");
            }

            String sql = "INSERT INTO PUBLISHERS (name, address, phone, email) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, publisher.getName());
                ps.setString(2, publisher.getAddress());
                ps.setString(3, publisher.getPhone());
                ps.setString(4, publisher.getEmail());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm nhà xuất bản: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean updatePublisher(Publisher publisher) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu");
            }

            String sql = "UPDATE PUBLISHERS SET name = ?, address = ?, phone = ?, email = ? WHERE publisher_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, publisher.getName());
                ps.setString(2, publisher.getAddress());
                ps.setString(3, publisher.getPhone());
                ps.setString(4, publisher.getEmail());
                ps.setInt(5, publisher.getPublisherId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật nhà xuất bản: " + e.getMessage(), e);
            return false;
        }
    }
}