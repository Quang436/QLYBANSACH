package Model;

public class Publisher {
    private int publisherId;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String website;

    public Publisher() {}

    public Publisher(int publisherId, String name, String address, String phone, String email, String website) {
        this.publisherId = publisherId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.website = website;
    }

    // Getters and Setters
    public int getPublisherId() { return publisherId; }
    public void setPublisherId(int publisherId) { this.publisherId = publisherId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

}