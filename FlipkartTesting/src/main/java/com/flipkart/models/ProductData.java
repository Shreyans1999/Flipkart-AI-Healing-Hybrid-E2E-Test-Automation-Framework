package com.flipkart.models;

/**
 * POJO model for Product data.
 * Used in product search, filter, and cart tests.
 */
public class ProductData {
    
    private String name;
    private String category;
    private String brand;
    private Double minPrice;
    private Double maxPrice;
    private String productUrl;
    private Integer quantity;
    
    // Default constructor
    public ProductData() {
        this.quantity = 1;
    }
    
    // Parameterized constructor
    public ProductData(String name, String category) {
        this.name = name;
        this.category = category;
        this.quantity = 1;
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
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public Double getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }
    
    public Double getMaxPrice() {
        return maxPrice;
    }
    
    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }
    
    public String getProductUrl() {
        return productUrl;
    }
    
    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "ProductData{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", priceRange=" + minPrice + "-" + maxPrice +
                '}';
    }
    
    // Builder class
    public static class Builder {
        private final ProductData productData = new ProductData();
        
        public Builder name(String name) {
            productData.setName(name);
            return this;
        }
        
        public Builder category(String category) {
            productData.setCategory(category);
            return this;
        }
        
        public Builder brand(String brand) {
            productData.setBrand(brand);
            return this;
        }
        
        public Builder priceRange(Double min, Double max) {
            productData.setMinPrice(min);
            productData.setMaxPrice(max);
            return this;
        }
        
        public Builder productUrl(String url) {
            productData.setProductUrl(url);
            return this;
        }
        
        public Builder quantity(Integer quantity) {
            productData.setQuantity(quantity);
            return this;
        }
        
        public ProductData build() {
            return productData;
        }
    }
}
