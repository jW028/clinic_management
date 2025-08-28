package utility;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Comprehensive input handling utility for the clinic management system
 * Provides safe, validated input methods for all data types used in the system
 */
public class InputHandler {
    private static final Scanner scanner = new Scanner(System.in);
    
    // Private constructor to prevent instantiation
    private InputHandler() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    /**
     * Gets an integer input from the user within a specified range
     * 
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return A valid integer within the specified range
     */
    public static int getInt(int min, int max) {
        int choice = -1;
        while (choice < min || choice > max) {
            System.out.print("Select option (" + min + " - " + max + "): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < min || choice > max) {
                    System.out.println("Invalid choice. Please select a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return choice;
    }
    
    /**
     * Gets an integer input with a custom prompt
     * 
     * @param prompt The prompt message to display
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return A valid integer within the specified range
     */
    public static int getInt(String prompt, int min, int max) {
        int value = -1;
        while (value < min || value > max) {
            System.out.print(prompt + " (" + min + " - " + max + "): ");
            try {
                value = Integer.parseInt(scanner.nextLine());
                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return value;
    }
    
    /**
     * Gets a positive integer input
     * 
     * @param prompt The prompt message to display
     * @return A positive integer
     */
    public static int getPositiveInt(String prompt) {
        return getInt(prompt, 1, Integer.MAX_VALUE);
    }
    
    /**
     * Gets a double input within a specified range
     * 
     * @param prompt The prompt message to display
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return A valid double within the specified range
     */
    public static double getDouble(String prompt, double min, double max) {
        double value = -1;
        while (value < min || value > max) {
            System.out.print(prompt + " (" + min + " - " + max + "): ");
            try {
                value = Double.parseDouble(scanner.nextLine());
                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return value;
    }
    
    /**
     * Gets a positive double input (for prices, costs, etc.)
     * 
     * @param prompt The prompt message to display
     * @return A positive double value
     */
    public static double getPositiveDouble(String prompt) {
        return getDouble(prompt, 0.01, Double.MAX_VALUE);
    }
    
    /**
     * Gets a non-empty string input
     * 
     * @param prompt The prompt message to display
     * @return A non-empty string
     */
    public static String getString(String prompt) {
        String input = "";
        while (input.trim().isEmpty()) {
            System.out.print(prompt + ": ");
            input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            }
        }
        return input.trim();
    }
    
    /**
     * Gets a string input with length validation
     * 
     * @param prompt The prompt message to display
     * @param minLength The minimum allowed length
     * @param maxLength The maximum allowed length
     * @return A string within the specified length range
     */
    public static String getString(String prompt, int minLength, int maxLength) {
        String input = "";
        while (input.length() < minLength || input.length() > maxLength) {
            System.out.print(prompt + " (" + minLength + "-" + maxLength + " characters): ");
            input = scanner.nextLine().trim();
            if (input.length() < minLength || input.length() > maxLength) {
                System.out.println("Input must be between " + minLength + " and " + maxLength + " characters.");
            }
        }
        return input;
    }
    
    /**
     * Gets an optional string input (can be empty)
     * 
     * @param prompt The prompt message to display
     * @return A string that may be empty
     */
    public static String getOptionalString(String prompt) {
        System.out.print(prompt + " (optional): ");
        return scanner.nextLine().trim();
    }
    
    /**
     * Gets a yes/no confirmation from the user
     * 
     * @param prompt The prompt message to display
     * @return true for yes, false for no
     */
    public static boolean getYesNo(String prompt) {
        String input = "";
        while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n") &&
               !input.equalsIgnoreCase("yes") && !input.equalsIgnoreCase("no")) {
            System.out.print(prompt + " (y/n): ");
            input = scanner.nextLine().trim();
            if (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n") &&
                !input.equalsIgnoreCase("yes") && !input.equalsIgnoreCase("no")) {
                System.out.println("Please enter 'y' for yes or 'n' for no.");
            }
        }
        return input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes");
    }
    
    /**
     * Gets a choice from a list of options
     * 
     * @param prompt The prompt message to display
     * @param options Array of available options
     * @return The index of the selected option (0-based)
     */
    public static int getChoice(String prompt, String[] options) {
        System.out.println(prompt);
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        return getInt("Enter your choice", 1, options.length) - 1;
    }
    
    /**
     * Gets a valid ID input (alphanumeric, specific format)
     * 
     * @param prompt The prompt message to display
     * @param prefix The required prefix (e.g., "P" for patient ID)
     * @param length The total length of the ID
     * @return A valid ID string
     */
    public static String getID(String prompt, String prefix, int length) {
        String id = "";
        String pattern = "^" + prefix + "[0-9]{" + (length - prefix.length()) + "}$";
        
        while (!id.matches(pattern)) {
            System.out.print(prompt + " (format: " + prefix + "xxx...): ");
            id = scanner.nextLine().trim().toUpperCase();
            if (!id.matches(pattern)) {
                System.out.println("Invalid format. ID must start with '" + prefix + 
                                 "' followed by " + (length - prefix.length()) + " digits.");
            }
        }
        return id;
    }
    
    /**
     * Gets a valid date input
     * 
     * @param prompt The prompt message to display
     * @return A valid LocalDate
     */
    public static LocalDate getDate(String prompt) {
        LocalDate date = null;
        while (date == null) {
            System.out.print(prompt + " (dd/MM/yyyy): ");
            String input = scanner.nextLine().trim();
            try {
                date = DateTimeFormatterUtil.parseDateFormat(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date format. Please use dd/MM/yyyy (e.g., 28/07/2025).");
            }
        }
        return date;
    }
    
    /**
     * Gets a valid date and time input
     * 
     * @param prompt The prompt message to display
     * @return A valid LocalDateTime
     */
    public static LocalDateTime getDateTime(String prompt) {
        LocalDateTime dateTime = null;
        while (dateTime == null) {
            System.out.print(prompt + " (dd/MM/yyyy HH:mm): ");
            String input = scanner.nextLine().trim();
            try {
                dateTime = DateTimeFormatterUtil.parseDisplayFormat(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date/time format. Please use dd/MM/yyyy HH:mm (e.g., 28/07/2025 14:30).");
            }
        }
        return dateTime;
    }
    
    /**
     * Gets a future date input (for appointments, follow-ups)
     * 
     * @param prompt The prompt message to display
     * @return A valid future LocalDateTime
     */
    public static LocalDateTime getFutureDateTime(String prompt) {
        LocalDateTime dateTime = null;
        while (dateTime == null || dateTime.isBefore(LocalDateTime.now())) {
            dateTime = getDateTime(prompt);
            if (dateTime.isBefore(LocalDateTime.now())) {
                System.out.println("Date and time must be in the future.");
                dateTime = null;
            }
        }
        return dateTime;
    }
    
    /**
     * Gets a valid email address
     * 
     * @param prompt The prompt message to display
     * @return A valid email string
     */
    public static String getEmail(String prompt) {
        String email = "";
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        
        while (!email.matches(emailPattern)) {
            System.out.print(prompt + ": ");
            email = scanner.nextLine().trim().toLowerCase();
            if (!email.matches(emailPattern)) {
                System.out.println("Invalid email format. Please enter a valid email address.");
            }
        }
        return email;
    }
    
    /**
     * Gets a valid phone number
     * 
     * @param prompt The prompt message to display
     * @return A valid phone number string
     */
    public static String getPhoneNumber(String prompt) {
        String phone = "";
        // Matches formats: 0123456789, 012-3456789, +60123456789
        String phonePattern = "^(\\+?60)?[0-9]{2,3}-?[0-9]{7,8}$";
        
        while (!phone.matches(phonePattern)) {
            System.out.print(prompt + " (e.g., 012-3456789): ");
            phone = scanner.nextLine().trim();
            if (!phone.matches(phonePattern)) {
                System.out.println("Invalid phone number format. Please use format: 012-3456789");
            }
        }
        return phone;
    }
    
    /**
     * Gets gender input with validation
     * 
     * @param prompt The prompt message to display
     * @return "MALE" or "FEMALE"
     */
    public static String getGender(String prompt) {
        String[] genderOptions = {"Male", "Female"};
        int choice = getChoice(prompt, genderOptions);
        return genderOptions[choice].toUpperCase();
    }
    
    /**
     * Displays a menu and gets the user's choice
     * 
     * @param title The menu title
     * @param options Array of menu options
     * @return The selected option index (0-based)
     */
    public static int displayMenu(String title, String[] options) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(title.toUpperCase());
        System.out.println("=".repeat(50));
        
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        System.out.println("=".repeat(50));
        
        return getInt("Select an option", 1, options.length) - 1;
    }
    
    /**
     * Pauses execution and waits for user to press Enter
     * 
     * @param message The message to display
     */
    public static void pauseForUser(String message) {
        System.out.print(message + " Press Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Pauses execution with default message
     */
    public static void pauseForUser() {
        pauseForUser("");
    }
    
    /**
     * Clears the console screen (works on most terminals)
     */
    public static void clearScreen() {
        try {
            // Try to clear screen
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[2J\033[H");
            }
        } catch (IOException | InterruptedException e) {
            // If clearing fails, just print some newlines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    /**
     * Validates and gets medication dosage input
     * 
     * @param prompt The prompt message to display
     * @return A valid dosage string
     */
    public static String getDosage(String prompt) {
        String dosage = "";
        // Matches formats like: 250mg, 1 tablet, 10ml, 500 mg
        String dosagePattern = "^[0-9]+\\.?[0-9]*\\s*(mg|ml|tablet|tablets|capsule|capsules|g|kg)$";
        
        while (!dosage.toLowerCase().matches(dosagePattern)) {
            System.out.print(prompt + " (e.g., 250mg, 1 tablet): ");
            dosage = scanner.nextLine().trim();
            if (!dosage.toLowerCase().matches(dosagePattern)) {
                System.out.println("Invalid dosage format. Use formats like: 250mg, 1 tablet, 10ml");
            }
        }
        return dosage;
    }
    
    /**
     * Gets medication frequency input
     * 
     * @param prompt The prompt message to display
     * @return A valid frequency string
     */
    public static String getFrequency(String prompt) {
        String[] frequencyOptions = {
            "Once daily",
            "Twice daily", 
            "3 times daily",
            "4 times daily",
            "Every 6 hours",
            "Every 8 hours",
            "Every 12 hours",
            "As needed",
            "Before meals",
            "After meals"
        };
        
        int choice = getChoice(prompt, frequencyOptions);
        return frequencyOptions[choice];
    }
    public static boolean getBoolean(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt + " (true/false): ");
            input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true")) return true;
            if (input.equals("false")) return false;
            System.out.println("Invalid input. Please type 'true' or 'false'.");
        }
    }

    public static boolean isValidId(String id, String type) {
        String pattern = "";
        switch (type.toLowerCase()) {
            case "appointment":    pattern = "^A\\d{3}$"; break;
            case "consultation":   pattern = "^C\\d{3}$"; break;
            case "doctor":         pattern = "^DC\\d{3}$"; break;
            case "patient":        pattern = "^P\\d{3}$"; break;
            case "diagnosis":      pattern = "^D\\d{3}$"; break;
            case "service":        pattern = "^S\\d{3}$"; break;
            default: return false;
        }
        return id != null && id.matches(pattern);
    }
}
