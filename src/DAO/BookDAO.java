package DAO;

import Model.Book;
import Util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = """
            SELECT b.book_id, b.title, b.author_id, b.category_id, b.publisher_id, 
                   b.ISBN, b.price, b.stock_quantity, b.publication_date, 
                   b.description, b.image_url,
                   a.name as author_name, c.name as category_name, p.name as publisher_name
            FROM BOOKS b
            LEFT JOIN AUTHORS a ON b.author_id = a.author_id
            LEFT JOIN CATEGORIES c ON b.category_id = c.category_id
            LEFT JOIN PUBLISHERS p ON b.publisher_id = p.publisher_id
            ORDER BY b.book_id
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthorId(rs.getInt("author_id"));
                book.setCategoryId(rs.getInt("category_id"));
                book.setPublisherId(rs.getInt("publisher_id"));
                book.setIsbn(rs.getString("ISBN"));
                book.setPrice(rs.getBigDecimal("price"));
                book.setStockQuantity(rs.getInt("stock_quantity"));
                book.setPublicationDate(rs.getDate("publication_date"));
                book.setDescription(rs.getString("description"));
                book.setImageUrl(rs.getString("image_url"));
                book.setAuthorName(rs.getString("author_name"));
                book.setCategoryName(rs.getString("category_name"));
                book.setPublisherName(rs.getString("publisher_name"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public boolean addBook(Book book) {
        String sql = """
            INSERT INTO BOOKS (title, author_id, category_id, publisher_id, ISBN, 
                              price, stock_quantity, publication_date, description, image_url)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setInt(2, book.getAuthorId());
            stmt.setInt(3, book.getCategoryId());
            stmt.setInt(4, book.getPublisherId());
            stmt.setString(5, book.getIsbn());
            stmt.setBigDecimal(6, book.getPrice());
            stmt.setInt(7, book.getStockQuantity());
            stmt.setDate(8, new java.sql.Date(book.getPublicationDate().getTime()));
            stmt.setString(9, book.getDescription());
            stmt.setString(10, book.getImageUrl());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateBook(Book book) {
        String sql = """
            UPDATE BOOKS SET title = ?, author_id = ?, category_id = ?, publisher_id = ?, 
                           ISBN = ?, price = ?, stock_quantity = ?, publication_date = ?, 
                           description = ?, image_url = ?
            WHERE book_id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setInt(2, book.getAuthorId());
            stmt.setInt(3, book.getCategoryId());
            stmt.setInt(4, book.getPublisherId());
            stmt.setString(5, book.getIsbn());
            stmt.setBigDecimal(6, book.getPrice());
            stmt.setInt(7, book.getStockQuantity());
            stmt.setDate(8, new java.sql.Date(book.getPublicationDate().getTime()));
            stmt.setString(9, book.getDescription());
            stmt.setString(10, book.getImageUrl());
            stmt.setInt(11, book.getBookId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int countBooksInCategory(int categoryId) {
    int count = 0;
    try (Connection conn = DatabaseConnection.getConnection()) {
        String sql = "SELECT COUNT(*) FROM Book WHERE categoryId = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, categoryId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return count;
}
    
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM BOOKS WHERE book_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = """
            SELECT b.book_id, b.title, b.author_id, b.category_id, b.publisher_id, 
                   b.ISBN, b.price, b.stock_quantity, b.publication_date, 
                   b.description, b.image_url,
                   a.name as author_name, c.name as category_name, p.name as publisher_name
            FROM BOOKS b
            LEFT JOIN AUTHORS a ON b.author_id = a.author_id
            LEFT JOIN CATEGORIES c ON b.category_id = c.category_id
            LEFT JOIN PUBLISHERS p ON b.publisher_id = p.publisher_id
            WHERE b.title LIKE ? OR a.name LIKE ? OR c.name LIKE ?
            ORDER BY b.book_id
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setBookId(rs.getInt("book_id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthorId(rs.getInt("author_id"));
                    book.setCategoryId(rs.getInt("category_id"));
                    book.setPublisherId(rs.getInt("publisher_id"));
                    book.setIsbn(rs.getString("ISBN"));
                    book.setPrice(rs.getBigDecimal("price"));
                    book.setStockQuantity(rs.getInt("stock_quantity"));
                    book.setPublicationDate(rs.getDate("publication_date"));
                    book.setDescription(rs.getString("description"));
                    book.setImageUrl(rs.getString("image_url"));
                    book.setAuthorName(rs.getString("author_name"));
                    book.setCategoryName(rs.getString("category_name"));
                    book.setPublisherName(rs.getString("publisher_name"));
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}