package bll;

import dao.CustomerDAO;
import Model.Customer;
import java.util.List;

public class CustomerBLL {
    private CustomerDAO customerDAO;

    public CustomerBLL() {
        this.customerDAO = new CustomerDAO();
    }

    
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    public Object getCustomerById(int customerId) {
        return customerDAO.getCustomerById(customerId);
    }

    public boolean addCustomer(Customer customer) {
        return customerDAO.addCustomer(customer);
    }

    public boolean updateCustomer(Customer customer) {
        return customerDAO.updateCustomer(customer);
    }
    public boolean deleteCustomer(int customerId) {
        // Bạn có thể thêm các kiểm tra logic nghiệp vụ tại đây nếu cần
        // Ví dụ: Kiểm tra ràng buộc khóa ngoại trước khi xóa

        try {
            // Gọi phương thức xóa từ lớp DAO
            return customerDAO.deleteCustomer(customerId);
        } catch (Exception e) {
            System.err.println("Lỗi tại CustomerBLL khi xóa khách hàng: " + e.getMessage());
            e.printStackTrace();
            // Xử lý lỗi (ví dụ: hiển thị thông báo lỗi cho người dùng)
            return false;
        }
    }
}