package BLL;

import DAO.CategoryDAO;
import Model.Category;
import java.util.List;

public class CategoryBLL {
    private CategoryDAO categoryDAO;
    private BookBLL bookBLL = new BookBLL();
    
    public CategoryBLL() {
        this.categoryDAO = new CategoryDAO();
    }
    
    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }
    
    public boolean addCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }
        
        return categoryDAO.addCategory(category);
    }
    
    public boolean updateCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }
        
        return categoryDAO.updateCategory(category);
    }
    
    public boolean deleteCategory(int categoryId) {
        if (categoryId <= 0) {
            throw new IllegalArgumentException("ID danh mục không hợp lệ");
        }
        
        return categoryDAO.deleteCategory(categoryId);
    }
    public int countBooksInCategory(int categoryId) {
    return (int) bookBLL.getAllBooks().stream()
        .filter(book -> book.getCategoryId() == categoryId)
        .count();
}   
}