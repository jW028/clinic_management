package utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Simple utility class for consistent date and time formatting in the clinic management system
 */
public class DateTimeFormatterUtil {
    
    // Core formats for small clinic use
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter APPOINTMENT_FORMAT = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy 'at' HH:mm");

    // Private constructor to prevent instantiation
    private DateTimeFormatterUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ===============================
    // ESSENTIAL FORMATTING METHODS
    // ===============================
    
    /**
     * Format LocalDateTime for general display (dd/MM/yyyy HH:mm)
     * Example: "28/07/2025 14:30"
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMAT) : "N/A";
    }
    
    /**
     * Format LocalDate for general display (dd/MM/yyyy)
     * Example: "28/07/2025"
     */
    public static String formatDateForDisplay(LocalDate date) {
        return date != null ? date.format(DATE_FORMAT) : "N/A";
    }
    
    /**
     * Format for appointment display (EEE, dd MMM yyyy 'at' HH:mm)
     * Example: "Mon, 28 Jul 2025 at 14:30"
     */
    public static String formatForAppointment(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(APPOINTMENT_FORMAT) : "N/A";
    }

    // ===============================
    // PARSING METHODS
    // ===============================
    
    /**
     * Parse display format string to LocalDateTime
     * Expected format: "dd/MM/yyyy HH:mm"
     */
    public static LocalDateTime parseDisplayFormat(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString, DATETIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected: dd/MM/yyyy HH:mm", e);
        }
    }
    
    /**
     * Parse date format string to LocalDate
     * Expected format: "dd/MM/yyyy"
     */
    public static LocalDate parseDateFormat(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected: dd/MM/yyyy", e);
        }
    }

    // ===============================
    // UTILITY METHODS
    // ===============================
    
    /**
     * Get current timestamp formatted for display
     * Example: "28/07/2025 14:30"
     */
    public static String getCurrentTimestamp() {
        return formatForDisplay(LocalDateTime.now());
    }
    
    /**
     * Format duration between two DateTimes for treatment records
     * Example: "2 hours 30 minutes"
     */
    public static String formatDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return "N/A";
        
        long minutes = java.time.Duration.between(start, end).toMinutes();
        if (minutes < 60) {
            return minutes + " minutes";
        } else {
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            if (remainingMinutes == 0) {
                return hours + " hours";
            } else {
                return hours + " hours " + remainingMinutes + " minutes";
            }
        }
    }
    
    /**
     * Check if a treatment/appointment is today
     */
    public static boolean isToday(LocalDateTime dateTime) {
        if (dateTime == null) return false;
        return dateTime.toLocalDate().equals(LocalDate.now());
    }
    
    /**
     * Check if a treatment/appointment is overdue
     */
    public static boolean isOverdue(LocalDateTime scheduledTime) {
        if (scheduledTime == null) return false;
        return LocalDateTime.now().isAfter(scheduledTime.plusHours(1));
    }
}