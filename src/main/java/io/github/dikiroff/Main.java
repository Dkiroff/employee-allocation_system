package io.github.dikiroff;

import io.github.dikiroff.dao.WorkforceDAO;
import io.github.dikiroff.ui.DepartmentMenu;
import io.github.dikiroff.ui.EmployeeMenu;
import io.github.dikiroff.ui.InputHelper;

public class Main {

    public static void main(String[] args) {
        WorkforceDAO dao = new WorkforceDAO();
        EmployeeMenu employeeMenu = new EmployeeMenu(dao);
        DepartmentMenu departmentMenu = new DepartmentMenu(dao);

        System.out.println("=== EMPLOYEE ALLOCATION SYSTEM ===");

        boolean running = true;
        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Employee Management");
            System.out.println("2. Department Management");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = InputHelper.readInt();

            switch (choice) {
                case 1 -> employeeMenu.show();
                case 2 -> departmentMenu.show();
                case 3 -> {
                    System.out.println("Exiting system... Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }
}