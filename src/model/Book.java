package Model;

import java.math.BigDecimal;
import java.util.Date;

public class Book {
    private int bookId;
    private String title;
    private int authorId;
    private int categoryId;
    private int publisherId;
    private String isbn;
    private BigDecimal price;
    private int stockQuantity;
    private Date publicationDate;
    private String description;
    private String imageUrl;
    
    // Additional fields for display
    private String authorName;
    private String categoryName;
    private String publisherName;

    public Book() {}

    public Book(int bookId, String title, int authorId, int categoryId, int publisherId, 
                String isbn, BigDecimal price, int stockQuantity, Date publicationDate, 
                String description, String imageUrl) {
        this.bookId = bookId;
        this.title = title;
        this.authorId = authorId;
        this.categoryId = categoryId;
        this.publisherId = publisherId;
        this.isbn = isbn;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.publicationDate = publicationDate;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }
    
    public int getPublisherId() { return publisherId; }
    public void setPublisherId(int publisherId) { this.publisherId = publisherId; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public Date getPublicationDate() { return publicationDate; }
    public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }
}