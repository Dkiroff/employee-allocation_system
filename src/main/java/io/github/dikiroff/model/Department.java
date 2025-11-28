package io.github.dikiroff.model;

public class Department {
    private int id;
    private String name;
    private String streetAddress;
    private String city;
    private String country;
    double annualBudget;


    // Constructor for Reading from DB
    public Department(int id, String name, String streetAddress, String city, String country, double annualBudget) {
        this.id = id;
        this.name = name;
        this.streetAddress = streetAddress;
        this.city = city;
        this.country = country;
        this.annualBudget = annualBudget;
    }

    // Constructor for Creating (No ID)
    public Department(String name, String streetAddress, String city, String country, double annualBudget) {
        this.name = name;
        this.streetAddress = streetAddress;
        this.city = city;
        this.country = country;
        this.annualBudget = annualBudget;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getStreetAddress() { return streetAddress; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public double getAnnualBudget() { return annualBudget; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    public void setCity(String city) { this.city = city; }
    public void setCountry(String country) { this.country = country; }
    public void setAnnualBudget(double annualBudget) { this.annualBudget = annualBudget; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s, %s)", id, name, city, country);
    }
}