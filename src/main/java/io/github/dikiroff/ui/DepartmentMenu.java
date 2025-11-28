package io.github.dikiroff.ui;

import io.github.dikiroff.dao.WorkforceDAO;
import io.github.dikiroff.model.Department;

import java.util.List;
import java.util.Scanner;

public class DepartmentMenu {

    private final WorkforceDAO dao;
    private final Scanner sc;

    public DepartmentMenu(WorkforceDAO dao) {
        this.dao = dao;
        this.sc = new Scanner(System.in);
    }

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- DEPARTMENT MANAGEMENT ---");
            System.out.println("1. List All Departments");
            System.out.println("2. Create New Department");
            System.out.println("3. Remove Department");
            System.out.println("4. Modify Department");
            System.out.println("9. Back to Main Menu");
            System.out.print("Choose action: ");

            int choice = InputHelper.readInt();

            switch (choice) {
                case 1 -> printAllDepartments();
                case 2 -> createNewDepartment();
                case 3 -> removeDepartment();
                case 4 -> modifyDepartment();
                case 9 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void printAllDepartments() {
        List<Department> departments = dao.getAllDepartments();
        System.out.println("\n--- DEPARTMENT LIST ---");
        for (Department d : departments) {
            System.out.println(d);
        }
    }

    private void createNewDepartment() {
        System.out.println("\n--- ADD NEW DEPARTMENT ---");
        System.out.print("Enter Department Name (e.g., HR, Sales): ");
        String name = sc.nextLine();

        System.out.print("Enter Street Address: ");
        String address = sc.nextLine();

        System.out.print("Enter City: ");
        String city = sc.nextLine();

        System.out.print("Enter Country: ");
        String country = sc.nextLine();

        System.out.print("Enter Annual Budget (EUR): ");
        double annualBudget = InputHelper.readDouble();

        Department dept = new Department(name, address, city, country, annualBudget);

        if (dao.addDepartment(dept)) {
            System.out.println("SUCCESS: Department '" + name + "' created with ID: " + dept.getId());
        } else {
            System.out.println("FAILURE: Could not create department.");
        }
    }

    private void removeDepartment() {
        System.out.println("\n--- REMOVE DEPARTMENT ---");
        System.out.print("Enter Department ID to remove: ");
        int id = InputHelper.readInt();

        Department dept = dao.getDepartment(id);
        if (dept == null) {
            System.out.println("Error: Department ID " + id + " not found.");
            return;
        }

        System.out.println("-----------------------------------");
        System.out.printf("Department: [%d] %s%n", dept.getId(), dept.getName());
        System.out.printf("Location:   %s, %s%n", dept.getCity(), dept.getCountry());
        System.out.println("-----------------------------------");

        System.out.println("WARNING: This action will permanently DELETE this record from the database.");
        System.out.println("This action is irreversible. Data cannot be recovered.");
        System.out.print("Type 'YES' to confirm deletion: ");

        if (sc.nextLine().trim().toLowerCase().contains("yes")) {
            if (dao.deleteDepartment(id)) {
                System.out.println("SUCCESS: Department deleted.");
            }
        } else {
            System.out.println("Action Cancelled.");
        }
    }

    private void modifyDepartment() {
        System.out.println("\n--- MODIFY DEPARTMENT ---");
        System.out.print("Enter Department ID: ");
        int id = InputHelper.readInt();

        Department dept = dao.getDepartment(id);
        if (dept == null) {
            System.out.println("Error: ID not found.");
            return;
        }

        System.out.println("--- Current Details (Press Enter to keep current value) ---");

        System.out.printf("Name [%s]: ", dept.getName());
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) dept.setName(name);

        System.out.printf("Address [%s]: ", dept.getStreetAddress());
        String address = sc.nextLine().trim();
        if (!address.isEmpty()) dept.setStreetAddress(address);

        System.out.printf("City [%s]: ", dept.getCity());
        String city = sc.nextLine().trim();
        if (!city.isEmpty()) dept.setCity(city);

        System.out.printf("Country [%s]: ", dept.getCountry());
        String country = sc.nextLine().trim();
        if (!country.isEmpty()) dept.setCountry(country);

        // Bonus: Update Budget if needed
        System.out.printf("Budget [%.2f]: ", dept.getAnnualBudget());
        String budgetInput = sc.nextLine().trim();
        if (!budgetInput.isEmpty()) {
            try {
                dept.setAnnualBudget(Double.parseDouble(budgetInput));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Budget unchanged.");
            }
        }

        if (dao.updateDepartment(dept)) {
            System.out.println("SUCCESS: Department updated.");
            System.out.println("New Details: " + dept);
        } else {
            System.out.println("FAILURE: Update failed.");
        }
    }
}