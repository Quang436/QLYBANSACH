package BLL;

import DAO.AuthorDAO;
import Model.Author;
import java.util.List;

public class AuthorBLL {
    private AuthorDAO authorDAO;
    
    public AuthorBLL() {
        this.authorDAO = new AuthorDAO();
    }
    
    public List<Author> getAllAuthors() {
        return authorDAO.getAllAuthors();
    }
    
    public boolean addAuthor(Author author) {
        if (author.getName() == null || author.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên tác giả không được để trống");
        }
        
        return authorDAO.addAuthor(author);
    }
    
    public boolean updateAuthor(Author author) {
        if (author.getName() == null || author.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên tác giả không được để trống");
        }
        
        return authorDAO.updateAuthor(author);
    }
    
    public boolean deleteAuthor(int authorId) {
        if (authorId <= 0) {
            throw new IllegalArgumentException("ID tác giả không hợp lệ");
        }
        
        return authorDAO.deleteAuthor(authorId);
    }
}