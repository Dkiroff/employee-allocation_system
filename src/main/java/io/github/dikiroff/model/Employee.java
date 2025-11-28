package io.github.dikiroff.model;

public class Employee {

    private int id;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private double salary;
    private int departmentId;


    // Constructor for Reading from DB
    public Employee(int id, String firstName, String lastName, String jobTitle, double salary, int departmentId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.departmentId = departmentId;
    }

    // Constructor for Creating (No ID)
    public Employee(String firstName, String lastName, String jobTitle, double salary, int departmentId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.departmentId = departmentId;
    }

    // Getters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getJobTitle() { return jobTitle; }
    public double getSalary() { return salary; }
    public int getDepartmentId() { return departmentId; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public void setSalary(double salary) { this.salary = salary; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    @Override
    public String toString() {
        return String.format("ID %d: %s %s | Role: %s | Salary: $%.2f | Dept ID: %d",
                id, firstName, lastName, jobTitle, salary, departmentId);
    }
}