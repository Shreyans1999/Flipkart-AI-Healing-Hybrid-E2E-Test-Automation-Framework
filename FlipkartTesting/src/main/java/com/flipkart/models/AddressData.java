package com.flipkart.models;

/**
 * POJO model for Address data.
 * Used in checkout and delivery tests.
 */
public class AddressData {
    
    private String name;
    private String phoneNumber;
    private String pincode;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String city;
    private String state;
    private String addressType; // Home, Work
    
    // Default constructor
    public AddressData() {
        this.addressType = "Home";
    }
    
    // Parameterized constructor
    public AddressData(String pincode, String city) {
        this();
        this.pincode = pincode;
        this.city = city;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getPincode() {
        return pincode;
    }
    
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
    
    public String getAddressLine1() {
        return addressLine1;
    }
    
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }
    
    public String getAddressLine2() {
        return addressLine2;
    }
    
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
    
    public String getLandmark() {
        return landmark;
    }
    
    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getAddressType() {
        return addressType;
    }
    
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
    
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine1 != null) sb.append(addressLine1);
        if (addressLine2 != null) sb.append(", ").append(addressLine2);
        if (landmark != null) sb.append(", Near ").append(landmark);
        if (city != null) sb.append(", ").append(city);
        if (pincode != null) sb.append(" - ").append(pincode);
        if (state != null) sb.append(", ").append(state);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "AddressData{" +
                "pincode='" + pincode + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", type='" + addressType + '\'' +
                '}';
    }
    
    // Builder class
    public static class Builder {
        private final AddressData addressData = new AddressData();
        
        public Builder name(String name) {
            addressData.setName(name);
            return this;
        }
        
        public Builder phoneNumber(String phoneNumber) {
            addressData.setPhoneNumber(phoneNumber);
            return this;
        }
        
        public Builder pincode(String pincode) {
            addressData.setPincode(pincode);
            return this;
        }
        
        public Builder addressLine1(String addressLine1) {
            addressData.setAddressLine1(addressLine1);
            return this;
        }
        
        public Builder addressLine2(String addressLine2) {
            addressData.setAddressLine2(addressLine2);
            return this;
        }
        
        public Builder landmark(String landmark) {
            addressData.setLandmark(landmark);
            return this;
        }
        
        public Builder city(String city) {
            addressData.setCity(city);
            return this;
        }
        
        public Builder state(String state) {
            addressData.setState(state);
            return this;
        }
        
        public Builder addressType(String addressType) {
            addressData.setAddressType(addressType);
            return this;
        }
        
        public AddressData build() {
            return addressData;
        }
    }
}
