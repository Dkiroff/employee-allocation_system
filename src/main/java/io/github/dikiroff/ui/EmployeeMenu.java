package io.github.dikiroff.ui;

import io.github.dikiroff.dao.WorkforceDAO;
import io.github.dikiroff.model.Department;
import io.github.dikiroff.model.Employee;

import java.util.List;
import java.util.Scanner;

public class EmployeeMenu {

    private final WorkforceDAO dao;
    private final Scanner sc;

    public EmployeeMenu(WorkforceDAO dao) {
        this.dao = dao;
        this.sc = new Scanner(System.in);
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- EMPLOYEE MANAGEMENT ---");
            System.out.println("1. List All Employees");
            System.out.println("2. Hire New Employee");
            System.out.println("3. Search Employee");
            System.out.println("4. Update Salary");
            System.out.println("5. Update Job Title");
            System.out.println("6. Transfer Department");
            System.out.println("7. Fire Employee");
            System.out.println("9. Back to Main Menu");
            System.out.print("Choose action: ");

            int choice = InputHelper.readInt();

            switch (choice) {
                case 1 -> printAllEmployees();
                case 2 -> hireNewEmployee();
                case 3 -> searchForEmployee();
                case 4 -> updateEmployeeSalary();
                case 5 -> updateEmployeeJobTitle();
                case 6 -> transferEmployeeToDept();
                case 7 -> fireEmployee();
                case 9 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void printAllEmployees() {
        List<Employee> employees = dao.getAllEmployees();
        System.out.println("\n--- EMPLOYEE LIST ---");
        for (Employee e : employees) {
            System.out.println(e);
        }
    }

    private void hireNewEmployee() {
        System.out.println("\n--- HIRE NEW EMPLOYEE ---");

        System.out.print("Enter First Name: ");
        String firstName = sc.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = sc.nextLine();

        System.out.print("Enter Job Title: ");
        String jobTitle = sc.nextLine();

        System.out.print("Enter Monthly Salary (EUR): ");
        double salary = InputHelper.readDouble();

        System.out.print("Enter Department ID: ");
        int deptId = InputHelper.readInt();

        double annualBudget = dao.getDepartmentBudget(deptId);
        double currentYearlyCost = dao.getProjectedYearlyCost(deptId);
        double newEmployeeCost = salary * 12;
        double projectedTotal = currentYearlyCost + newEmployeeCost;

        System.out.println("\n--- BUDGET ANALYSIS REPORT ---");
        System.out.printf("Department Budget:    %,12.2f EUR%n", annualBudget);
        System.out.printf("Current Allocation:   %,12.2f EUR%n", currentYearlyCost);
        System.out.printf("New Hire Impact:     +%,12.2f EUR/year%n", newEmployeeCost);
        System.out.println("----------------------------------------");
        System.out.printf("Projected Total:      %,12.2f EUR%n", projectedTotal);

        if (projectedTotal <= annualBudget) {
            Employee employee = new Employee(firstName, lastName, jobTitle, salary, deptId);
            if (dao.addEmployee(employee)) {
                System.out.println("SUCCESS: Budget Approved. Employee hired successfully.");
            }
        } else {
            double deficit = projectedTotal - annualBudget;
            System.out.println("\nFAILURE: Hiring Request Denied.");
            System.out.printf("Reason: Exceeds budget limit by %,.2f EUR%n", deficit);
        }
    }

    private void searchForEmployee() {
        System.out.println("\n--- SEARCH EMPLOYEE ---");
        System.out.print("Enter a Name or Job Title: ");
        String searchKey = sc.nextLine();
        List<Employee> foundEmployees = dao.searchEmployees(searchKey);

        if (foundEmployees.isEmpty()) {
            System.out.println("Found no employees matching '" + searchKey + "'.");
        } else if (foundEmployees.size() == 1) {
            System.out.println("Found 1 employee:");
            System.out.println(foundEmployees.get(0));
        } else {
            System.out.println("Found " + foundEmployees.size() + " employees:");
            for (Employee e : foundEmployees) {
                System.out.println(e);
            }
        }
    }

    private void updateEmployeeSalary() {
        System.out.println("\n--- UPDATE EMPLOYEE SALARY ---");
        System.out.print("Enter Employee ID: ");
        int id = InputHelper.readInt();

        Employee employee = dao.getEmployee(id);

        if (employee == null) {
            System.out.println("Error: Employee with ID " + id + " not found.");
            return;
        }

        System.out.println("-----------------------------------");
        System.out.printf("Employee:       %s %s%n", employee.getFirstName(), employee.getLastName());
        System.out.printf("Job Title:      %s%n", employee.getJobTitle());
        System.out.printf("Current Salary: %,.2f EUR / month%n", employee.getSalary());
        System.out.println("-----------------------------------");

        System.out.print("Enter NEW Monthly Salary: ");
        double newMonthlySalary = InputHelper.readDouble();

        int deptId = employee.getDepartmentId();
        double annualBudget = dao.getDepartmentBudget(deptId);
        double currentDeptYearlyCost = dao.getProjectedYearlyCost(deptId);

        double monthlyDiff = newMonthlySalary - employee.getSalary();
        double annualImpact = monthlyDiff * 12;
        double projectedTotal = currentDeptYearlyCost + annualImpact;

        System.out.println("\n--- FINANCIAL IMPACT ANALYSIS ---");
        System.out.printf("Department Budget:    %,12.2f EUR%n", annualBudget);
        System.out.printf("Current Allocation:   %,12.2f EUR%n", currentDeptYearlyCost);

        if (monthlyDiff > 0) {
            System.out.printf("Cost Increase:       +%,12.2f EUR/yr%n", annualImpact);
        } else {
            System.out.printf("Cost Savings:        -%,12.2f EUR/yr%n", Math.abs(annualImpact));
        }

        System.out.println("----------------------------------------");
        System.out.printf("Projected Total:      %,12.2f EUR%n", projectedTotal);

        if (monthlyDiff > 0 && projectedTotal > annualBudget) {
            double deficit = projectedTotal - annualBudget;
            System.out.println("\nREQUEST DENIED: Budget Constraint Violation.");
            System.out.printf("Reason: Raise exceeds annual budget by %,.2f EUR%n", deficit);
            return;
        }

        if (dao.updateSalary(id, newMonthlySalary)) {
            if (monthlyDiff > 0) {
                System.out.println("\nAPPROVED: Salary increase processed successfully.");
            } else if (monthlyDiff < 0) {
                System.out.println("\nCONFIRMED: Salary decrease processed successfully.");
            } else {
                System.out.println("\nNo Change: Salary remained the same.");
            }
        }
    }

    private void updateEmployeeJobTitle() {
        System.out.println("\n=== UPDATE JOB TITLE ===");
        System.out.print("Enter Employee ID: ");
        int id = InputHelper.readInt();

        Employee employee = dao.getEmployee(id);

        if (employee == null) {
            System.out.println("Error: Employee with ID " + id + " not found.");
            return;
        }

        System.out.println("-----------------------------------");
        System.out.printf("Employee:       %s %s%n", employee.getFirstName(), employee.getLastName());
        System.out.printf("Job Title:      %s%n", employee.getJobTitle());
        System.out.println("-----------------------------------");

        System.out.print("Enter the NEW Job Title: ");
        String newJobTitle = sc.nextLine();

        if (dao.updateJobTitle(id, newJobTitle)) {
            System.out.printf("CONFIRMED: Job Title has been updated: %s -> %s%n", employee.getJobTitle(), newJobTitle);
        } else {
            System.out.println("\nNo Change: Job Title remained the same.");
        }
    }

    private void transferEmployeeToDept() {
        System.out.println("\n--- TRANSFER EMPLOYEE RESOURCE ---");

        System.out.print("Enter Employee ID to re-assign: ");
        int employeeId = InputHelper.readInt();
        Employee employee = dao.getEmployee(employeeId);

        if (employee == null) {
            System.out.println("Error: Employee not found.");
            return;
        }

        Department oldDept = dao.getDepartment(employee.getDepartmentId());
        String oldDeptName = (oldDept != null) ? oldDept.getName() : "Unknown";

        System.out.println("-----------------------------------");
        System.out.printf("Employee:     %s %s  |  %s%n", employee.getFirstName(), employee.getLastName(), employee.getJobTitle());
        System.out.printf("Current Dept: [%d] %s%n", employee.getDepartmentId(), oldDeptName);
        System.out.println("-----------------------------------");

        System.out.println("Available Departments: ");
        List<Department> allDepts = dao.getAllDepartments();
        for (Department dept : allDepts) {
            if (dept.getId() != employee.getDepartmentId()) {
                System.out.printf("[%d] %s%n", dept.getId(), dept.getName());
            }
        }

        System.out.print("Enter TARGET Department ID: ");
        int newDeptId = InputHelper.readInt();

        Department newDept = dao.getDepartment(newDeptId);
        if (newDept == null) {
            System.out.println("Error: Target Department ID " + newDeptId + " does not exist.");
            return;
        }

        double employeeAnnualCost = employee.getSalary() * 12;
        double targetBudget = dao.getDepartmentBudget(newDeptId);
        double targetCurrentSpending = dao.getProjectedYearlyCost(newDeptId);
        double projectedTotal = targetCurrentSpending + employeeAnnualCost;

        System.out.println("\n--- TARGET DEPARTMENT ANALYSIS ---");
        System.out.printf("Target Dept:          %s%n", newDept.getName());
        System.out.printf("Available Budget:     %,12.2f EUR%n", targetBudget);
        System.out.printf("Current Spending:     %,12.2f EUR%n", targetCurrentSpending);
        System.out.printf("Transfer Impact:     +%,12.2f EUR/yr%n", employeeAnnualCost);
        System.out.println("----------------------------------------");
        System.out.printf("Projected Total:      %,12.2f EUR%n", projectedTotal);

        if (projectedTotal > targetBudget) {
            double deficit = projectedTotal - targetBudget;
            System.out.println("TRANSFER DENIED: Budget Constraint.");
            System.out.printf("Reason: Target department cannot afford this employee (Deficit: %,.2f EUR)%n", deficit);
            return;
        }

        System.out.println("\nSTATUS: Budget Validated. Transfer Authorized.");
        System.out.print("FINAL STEP: Type 'YES' to execute transfer: ");

        if (sc.nextLine().trim().toLowerCase().contains("yes")) {
            if (dao.transferEmployee(employeeId, newDeptId)) {
                System.out.println("SUCCESS: Resource reallocation complete.");
                System.out.printf("Log: %s -> %s%n", oldDeptName, newDept.getName());
            }
            return;
        }
        System.out.println("Action Cancelled. Employee remains in current department.");
    }

    private void fireEmployee() {
        System.out.println("\n--- FIRE EMPLOYEE / OFFBOARDING ---");

        System.out.print("Enter Employee ID to fire: ");
        int id = InputHelper.readInt();

        System.out.println("WARNING: This action acts immediately on the database.");
        System.out.print("Are you sure you want to FIRE this employee? (Type 'YES' to confirm): ");

        if (sc.nextLine().trim().toLowerCase().contains("yes")) {
            boolean isRemoved = dao.removeEmployee(id);

            if (isRemoved) {
                System.out.println("SUCCESS: Employee offboarded. Contract terminated.");
            } else {
                System.out.println("ERROR: Employee ID [" + id + "] not found.");
            }
        } else {
            System.out.println("Action Cancelled.");
        }
    }
}