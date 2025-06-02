package DAO;

import Model.Author;
import Util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO {
    
    public List<Author> getAllAuthors() {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM AUTHORS ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Author author = new Author();
                author.setAuthorId(rs.getInt("author_id"));
                author.setImageUrl(rs.getString("image_url"));
                author.setName(rs.getString("name"));
                author.setBio(rs.getString("bio"));
                author.setBirthYear(rs.getInt("birth_year"));
                author.setCountry(rs.getString("country"));
                authors.add(author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }
    
    public boolean addAuthor(Author author) {
        String sql = "INSERT INTO AUTHORS (image_url, name, bio, birth_year, country) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, author.getImageUrl());
            stmt.setString(2, author.getName());
            stmt.setString(3, author.getBio());
            stmt.setInt(4, author.getBirthYear());
            stmt.setString(5, author.getCountry());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateAuthor(Author author) {
        String sql = "UPDATE AUTHORS SET image_url = ?, name = ?, bio = ?, birth_year = ?, country = ? WHERE author_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, author.getImageUrl());
            stmt.setString(2, author.getName());
            stmt.setString(3, author.getBio());
            stmt.setInt(4, author.getBirthYear());
            stmt.setString(5, author.getCountry());
            stmt.setInt(6, author.getAuthorId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteAuthor(int authorId) {
        String sql = "DELETE FROM AUTHORS WHERE author_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, authorId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}