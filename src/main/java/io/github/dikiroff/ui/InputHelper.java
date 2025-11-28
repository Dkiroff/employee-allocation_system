package io.github.dikiroff.ui;

import java.util.Scanner;

public class InputHelper {
    private static final Scanner sc = new Scanner(System.in);

    public static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid number. Please enter a valid integer: ");
            }
        }
    }

    public static double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid amount. Please enter a number: ");
            }
        }
    }

}
