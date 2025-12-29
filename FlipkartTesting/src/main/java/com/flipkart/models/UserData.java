package com.flipkart.models;

/**
 * POJO model for User data including authentication details.
 * Used in login, registration, and checkout tests.
 */
public class UserData {
    
    private String phoneNumber;
    private String email;
    private String password;
    private String name;
    
    // Default constructor
    public UserData() {}
    
    // Parameterized constructor
    public UserData(String phoneNumber, String email) {
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
    
    // Builder pattern for fluent API
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "UserData{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
    
    // Builder class
    public static class Builder {
        private final UserData userData = new UserData();
        
        public Builder phoneNumber(String phoneNumber) {
            userData.setPhoneNumber(phoneNumber);
            return this;
        }
        
        public Builder email(String email) {
            userData.setEmail(email);
            return this;
        }
        
        public Builder password(String password) {
            userData.setPassword(password);
            return this;
        }
        
        public Builder name(String name) {
            userData.setName(name);
            return this;
        }
        
        public UserData build() {
            return userData;
        }
    }
}
