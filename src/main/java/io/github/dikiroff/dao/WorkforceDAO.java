package io.github.dikiroff.dao;

import io.github.dikiroff.model.Department;
import io.github.dikiroff.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkforceDAO {

    private Connection connection;

    public WorkforceDAO() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/employee_allocation",
                    "root",
                    "password"
            );
            System.out.println("Connected to the database successfully!");
        } catch (SQLException e) {
            System.err.println("Connection to the database has failed!");
            e.printStackTrace();
        }
    }

    //region ------- 🏢 DEPARTMENT MANAGEMENT -------------------------------

    public List<Department> getAllDepartments() {
        ArrayList<Department> departments = new ArrayList<>();
        String query = "SELECT * FROM departments";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()){
            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String streetAddress = rs.getString("street_address");
                String city = rs.getString("city");
                String country = rs.getString("country");
                double annualBudget = rs.getDouble("annual_budget");

                Department department = new Department(id, name, streetAddress, city, country, annualBudget);
                departments.add(department);

            }
        }catch (SQLException e){
            System.err.println("Error fetching departments: " + e.getMessage());
            e.printStackTrace();
        }
        return departments;
    }

    public Department getDepartment(int id) {
        String query = "SELECT * FROM departments WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int deptId = rs.getInt("id");
                    String name = rs.getString("name");
                    String streetAddress = rs.getString("street_address");
                    String city = rs.getString("city");
                    String country = rs.getString("country");
                    double annualBudget = rs.getDouble("annual_budget");

                    return new Department(deptId, name, streetAddress, city, country, annualBudget);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addDepartment(Department dept) {
        String query = "INSERT INTO departments (name, street_address, city, country) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, dept.getName());
            pstmt.setString(2, dept.getStreetAddress());
            pstmt.setString(3, dept.getCity());
            pstmt.setString(4, dept.getCountry());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) dept.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding department: " + e.getMessage());
        }
        return false;
    }

    public double getDepartmentBudget(int deptId) {
        String query = "SELECT annual_budget FROM departments WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, deptId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("annual_budget");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching budget: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getMonthlySalaryTotal(int deptId) {
        String sql = "SELECT SUM(salary) FROM employees WHERE department_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getProjectedYearlyCost(int deptId) {
        return getMonthlySalaryTotal(deptId) * 12;
    }

    public boolean updateDepartment(Department dept) {
        String query = "UPDATE departments SET name = ?, street_address = ?, city = ?, country = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, dept.getName());
            pstmt.setString(2, dept.getStreetAddress());
            pstmt.setString(3, dept.getCity());
            pstmt.setString(4, dept.getCountry());
            pstmt.setInt(5, dept.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDepartment(int deptId) {
        String query = "DELETE FROM departments WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, deptId);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Department deleted successfully.");
                return true;
            } else {
                System.out.println("No department found with ID: " + deptId);
                return false;
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Cannot delete Department ID " + deptId);
            System.err.println("This department is not empty! It still has employees.");
            return false;
        } catch (SQLException e) {
            System.err.println("Error deleting department: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //endregion



    //region ------- 👨‍💻 EMPLOYEE MANAGEMENT -------------------------------

    public List<Employee> getAllEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()){
            while (rs.next()){
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String jobTitle = rs.getString("job_title");
                double salary = rs.getDouble("salary");
                int departmentId = rs.getInt("department_id");

                Employee employee = new Employee(id, firstName, lastName, jobTitle, salary, departmentId);
                employees.add(employee);

            }
        }catch (SQLException e){
            System.err.println("Error fetching employees: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }

    public Employee getEmployee(int id) {
        String query = "SELECT * FROM employees WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(query)){
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String jobTitle = rs.getString("job_title");
                    double salary = rs.getDouble("salary");
                    int departmentId = rs.getInt("department_id");

                    return new Employee(id, firstName, lastName, jobTitle, salary, departmentId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding employee: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean addEmployee(Employee employee) {
        String query = "INSERT INTO employees (first_name, last_name, job_title, salary, department_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getJobTitle());
            pstmt.setDouble(4, employee.getSalary());
            pstmt.setInt(5, employee.getDepartmentId());
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int newId = rs.getInt(1);
                        employee.setId(newId);
                        return true;
                    }
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("❌ FAILURE: Could not add employee.");
            System.err.println("Reason: Department with ID " + employee.getDepartmentId() + " does not exist!");
            return false;

        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public double getSalary(int id) {
        String query = "SELECT salary FROM employees WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)){
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()){
                if (rs.next()) {
                    return rs.getDouble("salary");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching budget: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean removeEmployee(int id) {
        String query = "DELETE FROM employees WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error removing employee: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateJobTitle(int id, String newTitle) {
        String query = "UPDATE employees SET job_title = ? WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(query)){
            pstmt.setString(1, newTitle);
            pstmt.setInt(2, id);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            } else {
                System.out.println("No employee found with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error updating job title: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSalary(int id, double newSalary) {
        String query = "UPDATE employees SET salary = ? WHERE id = ?";
        try(PreparedStatement pstmt = connection.prepareStatement(query)){
            pstmt.setDouble(1, newSalary);
            pstmt.setInt(2, id);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            } else {
                System.out.println("No employee found with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error updating salary: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean transferEmployee(int id, int newDepartmentId) {
        String query = "UPDATE employees SET department_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, newDepartmentId);
            pstmt.setInt(2, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            } else {
                System.out.println("No employee found with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error updating salary: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Employee> searchEmployees(String searchKey) {
        List<Employee> foundEmployees = new ArrayList<>();
        String query = "SELECT * FROM employees WHERE first_name LIKE ? OR last_name LIKE ? OR job_title LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchKey + "%";

            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Employee employee = new Employee(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("job_title"),
                            rs.getDouble("salary"),
                            rs.getInt("department_id")
                    );
                    foundEmployees.add(employee);
                }
            }
        } catch (SQLException e) {
            System.err.println("Search failed: " + e.getMessage());
            e.printStackTrace();
        }

        return foundEmployees;
    }
    //endregion
}
