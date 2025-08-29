package dao;

import adt.OrderedMap;
import entity.VisitHistory;
import entity.Patient;

import java.time.LocalDateTime;

public class VisitHistoryInitializer {
    public static OrderedMap<String, VisitHistory> initializeVisitHistories() {
        OrderedMap<String, VisitHistory> visitHistories = new OrderedMap<>();

        // Create sample patients for the visit histories
        Patient patient1 = new Patient("P001", "Alice", 19, "Female", "1234567890", "123 Main St", false);
        Patient patient2 = new Patient("P002", "Bob", 40, "Male", "0987654321", "456 Elm St", false);

        // Sample visit histories
        visitHistories.put("V001", new VisitHistory(
                "V001",
                patient1,
                LocalDateTime.now().minusDays(30),
                "Initial Registration",
                "COMPLETED"
        ));

        visitHistories.put("V002", new VisitHistory(
                "V002",
                patient1,
                LocalDateTime.now().minusDays(15),
                "Follow-up Check",
                "COMPLETED"
        ));

        visitHistories.put("V003", new VisitHistory(
                "V003",
                patient2,
                LocalDateTime.now().minusDays(20),
                "Initial Registration",
                "COMPLETED"
        ));

        visitHistories.put("V004", new VisitHistory(
                "V004",
                patient2,
                LocalDateTime.now().minusDays(5),
                "Routine Checkup",
                "COMPLETED"
        ));

        return visitHistories;
    }

    public static void main(String[] args) {
        OrderedMap<String, VisitHistory> visitHistories = initializeVisitHistories();
        // Display initialized visit histories
        System.out.println("=== INITIALIZED VISIT HISTORIES ===");
        for (int i = 0; i < visitHistories.size(); i++) {
            VisitHistory visit = visitHistories.get(i);
            System.out.println("Visit ID: " + visit.getVisitId() +
                    ", Patient: " + visit.getPatient().getName() +
                    ", Reason: " + visit.getVisitReason());
        }
    }
}