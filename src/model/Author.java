package Model;

public class Author {
    private int authorId;
    private String imageUrl;
    private String name;
    private String bio;
    private int birthYear;
    private String country;

    // Constructor
    public Author() {}

    public Author(int authorId, String imageUrl, String name, String bio, int birthYear, String country) {
        this.authorId = authorId;
        this.imageUrl = imageUrl;
        this.name = name;
        this.bio = bio;
        this.birthYear = birthYear;
        this.country = country;
    }

    // Getters and Setters
    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public int getBirthYear() { return birthYear; }
    public void setBirthYear(int birthYear) { this.birthYear = birthYear; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}