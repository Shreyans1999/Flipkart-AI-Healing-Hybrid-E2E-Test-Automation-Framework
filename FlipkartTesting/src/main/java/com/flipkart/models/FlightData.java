package com.flipkart.models;

import java.time.LocalDate;

/**
 * POJO model for Flight booking data.
 * Used in flight search and booking tests.
 */
public class FlightData {
    
    private String departureCity;
    private String arrivalCity;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private int adults;
    private int children;
    private int infants;
    private String travelClass; // Economy, Business, First
    private boolean isRoundTrip;
    
    // Default constructor
    public FlightData() {
        this.adults = 1;
        this.children = 0;
        this.infants = 0;
        this.travelClass = "Economy";
        this.isRoundTrip = false;
        this.departureDate = LocalDate.now();
    }
    
    // Parameterized constructor
    public FlightData(String departureCity, String arrivalCity) {
        this();
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters and Setters
    public String getDepartureCity() {
        return departureCity;
    }
    
    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }
    
    public String getArrivalCity() {
        return arrivalCity;
    }
    
    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }
    
    public LocalDate getDepartureDate() {
        return departureDate;
    }
    
    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public int getAdults() {
        return adults;
    }
    
    public void setAdults(int adults) {
        this.adults = adults;
    }
    
    public int getChildren() {
        return children;
    }
    
    public void setChildren(int children) {
        this.children = children;
    }
    
    public int getInfants() {
        return infants;
    }
    
    public void setInfants(int infants) {
        this.infants = infants;
    }
    
    public String getTravelClass() {
        return travelClass;
    }
    
    public void setTravelClass(String travelClass) {
        this.travelClass = travelClass;
    }
    
    public boolean isRoundTrip() {
        return isRoundTrip;
    }
    
    public void setRoundTrip(boolean roundTrip) {
        isRoundTrip = roundTrip;
    }
    
    public int getTotalTravellers() {
        return adults + children + infants;
    }
    
    @Override
    public String toString() {
        return "FlightData{" +
                "from='" + departureCity + '\'' +
                ", to='" + arrivalCity + '\'' +
                ", date=" + departureDate +
                ", travellers=" + getTotalTravellers() +
                ", class='" + travelClass + '\'' +
                '}';
    }
    
    // Builder class
    public static class Builder {
        private final FlightData flightData = new FlightData();
        
        public Builder from(String departureCity) {
            flightData.setDepartureCity(departureCity);
            return this;
        }
        
        public Builder to(String arrivalCity) {
            flightData.setArrivalCity(arrivalCity);
            return this;
        }
        
        public Builder departureDate(LocalDate date) {
            flightData.setDepartureDate(date);
            return this;
        }
        
        public Builder returnDate(LocalDate date) {
            flightData.setReturnDate(date);
            flightData.setRoundTrip(true);
            return this;
        }
        
        public Builder adults(int count) {
            flightData.setAdults(count);
            return this;
        }
        
        public Builder children(int count) {
            flightData.setChildren(count);
            return this;
        }
        
        public Builder infants(int count) {
            flightData.setInfants(count);
            return this;
        }
        
        public Builder travelClass(String travelClass) {
            flightData.setTravelClass(travelClass);
            return this;
        }
        
        public Builder roundTrip(boolean isRoundTrip) {
            flightData.setRoundTrip(isRoundTrip);
            return this;
        }
        
        public FlightData build() {
            return flightData;
        }
    }
}
