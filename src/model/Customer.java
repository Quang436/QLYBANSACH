package Model;

import java.util.Date;

public class Customer {
    private int customerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Date registrationDate;
    private String status;

    public Customer() {}

    public Customer(int customerId, String name, String email, String phone, 
                   String address, Date registrationDate, String status) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    // Getters and Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}