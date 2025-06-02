package BLL;

import Model.Book;
import DAO.BookDAO;
import java.util.List;

public class BookBLL {
    private BookDAO bookDAO = new BookDAO();
    public BookBLL() {
        this.bookDAO = new BookDAO();
    }
    
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }
    public boolean addBook(Book book) {
        // Validate book data
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sách không được để trống");
        }
        if (book.getPrice() == null || book.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá sách phải lớn hơn 0");
        }
        if (book.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không được âm");
        }
        
        return bookDAO.addBook(book);
    }
    
    public boolean updateBook(Book book) {
        // Validate book data
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sách không được để trống");
        }
        if (book.getPrice() == null || book.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá sách phải lớn hơn 0");
        }
        if (book.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho không được âm");
        }
        
        return bookDAO.updateBook(book);
    }
    
    public boolean deleteBook(int bookId) {
        if (bookId <= 0) {
            throw new IllegalArgumentException("ID sách không hợp lệ");
        }
        
        return bookDAO.deleteBook(bookId);
    }
    
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        
        return bookDAO.searchBooks(keyword.trim());
    }
}