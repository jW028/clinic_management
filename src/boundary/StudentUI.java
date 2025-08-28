package boundary;

import adt.CustomADT;
import control.ConsultationMaintenance;
import control.PatientMaintenance;
import control.PaymentMaintenance;
import control.TreatmentMaintenance;
import entity.Consultation;
import entity.Patient;
import entity.Payment;
import entity.Treatment;
import entity.VisitHistory;
import utility.IDGenerator;
import utility.InputHandler;

public class StudentUI {
    private final PatientMaintenance patientMaintenance;
    private final TreatmentMaintenance treatmentMaintenance;
    private final PaymentMaintenance paymentMaintenance;
    private final ConsultationMaintenance consultationMaintenance;
    private final ConsultationMaintenanceUI consultationMaintenanceUI;
    private Patient currentPatient;
    private boolean exit = false;

    public StudentUI(PatientMaintenance patientMaintenance) {
        this.patientMaintenance = patientMaintenance;
        this.treatmentMaintenance = new TreatmentMaintenance();
        this.paymentMaintenance = PaymentMaintenance.getInstance();
        this.consultationMaintenance = new ConsultationMaintenance();
        this.consultationMaintenanceUI = new ConsultationMaintenanceUI();
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n------ Patient Access ------");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Back");
            int choice = InputHandler.getInt(0, 2);
            switch (choice) {
                case 1 -> loginPatient();
                case 2 -> registerPatient();
                case 0 -> { return; }
            }
            if (currentPatient != null) {
                patientSessionMenu();
                currentPatient = null; // logout after session ends
            }
        }
    }

    private void loginPatient() {
        String id = InputHandler.getID("Enter Patient ID", "P", 4);
        Patient p = patientMaintenance.getPatientById(id);
        if (p == null) {
            System.out.println("Patient not found.");
        } else {
            currentPatient = p;
            System.out.println("Login successful. Welcome, " + p.getName() + ".");
        }
    }

    public void registerPatient() {
        System.out.println("\n=== REGISTER NEW PATIENT ===");

        String id = IDGenerator.generatePatientID();
        System.out.println("Generated Patient ID: " + id);

        String name = InputHandler.getString("Enter Name");
        int age = InputHandler.getInt("Enter Age", 1, 120);
        String gender = InputHandler.getGender("Select Gender");
        String contactNumber = InputHandler.getPhoneNumber("Enter Contact Number");
        String address = InputHandler.getString("Enter Address");
        boolean isEmergency = InputHandler.getYesNo("Is this an emergency case?");

        if (patientMaintenance.registerPatient(id, name, age, gender, contactNumber, address, isEmergency)) {
            System.out.println("✅ You registered successfully!");
            System.out.println("Patient ID: " + id);
        } else {
            System.out.println("❌ Registration failed - ID already exists");
        }
    }

    private void patientSessionMenu() {
        boolean back = false;
        while (!back && currentPatient != null) {
            System.out.println("\n====== Patient Dashboard ======");
            System.out.println("Logged in as: " + currentPatient.getPatientId() + " | " + currentPatient.getName());
            System.out.println("1. View details");
            System.out.println("2. Update details");
            System.out.println("3. Book appointment");
            System.out.println("4. View appointment status");
            System.out.println("5. View visit history");
            System.out.println("6. View & Pay Treatment Bills");
            System.out.println("0. Logout");
            int choice = InputHandler.getInt(0, 6);
            switch (choice) {
                case 1 -> viewDetails();
                case 2 -> updateDetails();
                case 3 -> makeAppointment();
                case 4 -> viewAppointmentStatus();
                case 5 -> viewVisitHistory();
                case 6 -> viewAndPayTreatmentBills();
                case 0 -> {
                    back = true;
                    System.out.println("Logged out.");
                }
            }
        }
    }

    private void viewDetails() {
        System.out.println("\n--- Patient Details ---");
        System.out.println(currentPatient);
        System.out.println("Emergency: " + (currentPatient.isEmergency() ? "YES" : "NO"));
    }

    private void updateDetails() {
        System.out.println("\n--- Update Patient Details ---");
        String name = InputHandler.getOptionalString("Name (leave blank keep)");
        String ageStr = InputHandler.getOptionalString("Age (leave blank keep)");
        String changeGender = InputHandler.getOptionalString("Change gender? (y to change)");
        String gender = currentPatient.getGender();
        if (changeGender.equalsIgnoreCase("y")) {
            gender = InputHandler.getGender("Select Gender");
        }
        String phone = InputHandler.getOptionalString("Contact Number (leave blank keep)");
        String address = InputHandler.getOptionalString("Address (leave blank keep)");
        String emerChange = InputHandler.getOptionalString("Change emergency flag? (y to change)");
        boolean emergency = currentPatient.isEmergency();
        if (emerChange.equalsIgnoreCase("y")) {
            emergency = InputHandler.getYesNo("Set emergency status now?");
        }

        String finalName = name.isBlank() ? currentPatient.getName() : name;
        int finalAge = ageStr.isBlank() ? currentPatient.getAge() :
                parsePositiveOrDefault(ageStr, currentPatient.getAge());

        boolean ok = patientMaintenance.updatePatient(
                currentPatient.getPatientId(),
                finalName,
                finalAge,
                gender,
                phone.isBlank() ? currentPatient.getContactNumber() : phone,
                address.isBlank() ? currentPatient.getAddress() : address,
                emergency
        );
        if (ok) {
            currentPatient = patientMaintenance.getPatientById(currentPatient.getPatientId());
            System.out.println("Details updated.");
        } else {
            System.out.println("Update failed.");
        }
    }

    private int parsePositiveOrDefault(String val, int def) {
        try {
            int v = Integer.parseInt(val);
            return v > 0 ? v : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private void makeAppointment() {
        System.out.println("\n--- Make Appointment ---");
        consultationMaintenanceUI.customerMakeAppointment(currentPatient.getPatientId());

//        System.out.println("\n--- Book Appointment (Queue Entry) ---");
//        if (patientMaintenance.isPatientInQueue(currentPatient.getPatientId())) {
//            System.out.println("Already in queue.");
//            return;
//        }
//
//        patientMaintenance.enqueuePatient(currentPatient.getPatientId());
//       if (patientMaintenance.isPatientInQueue(currentPatient.getPatientId())) {
//            System.out.println("Added to active queue.");
//        }  else {
//            System.out.println("Failed to enqueue.");
//        }
    }

    private void viewAppointmentStatus() {
        System.out.println("\n--- Appointment Status ---");
        String pid = currentPatient.getPatientId();
        if (patientMaintenance.isPatientInQueue(pid)) {
            int position = computeQueuePosition(pid);
            System.out.println("Status: In Queue. Position: " + position);
        } else {
            System.out.println("No active appointment (not queued).");
        }
    }

    private int computeQueuePosition(String pid) {
        int pos = 1;
        // Emergency queue first
        for (int i = 0; i < patientMaintenance.getEmergencyQueueSize(); i++) {
            Patient p = patientMaintenance.getEmergencyQueue().get(i);
            if (p.getPatientId().equals(pid)) return pos;
            pos++;
        }
        for (int i = 0; i < patientMaintenance.getNormalQueueSize(); i++) {
            Patient p = patientMaintenance.getNormalQueue().get(i);
            if (p.getPatientId().equals(pid)) return pos;
            pos++;
        }
        return -1;
    }

    private void viewVisitHistory() {
        System.out.println("\n--- Visit History ---");
        CustomADT<String, VisitHistory> visits = patientMaintenance.getPatientVisitHistory(currentPatient.getPatientId());
        if (visits.size() == 0) {
            System.out.println("No visit history.");
            return;
        }
        for (int i = 0; i < visits.size(); i++) {
            VisitHistory v = visits.get(i);
            System.out.printf("%d. %s | %s | %s | %s\n",
                    i + 1,
                    v.getVisitId(),
                    v.getVisitDate(),
                    v.getVisitReason(),
                    v.getStatus());
        }

        CustomADT<String, Consultation> consults = patientMaintenance.getConsultationsByPatient(currentPatient.getPatientId());
        if (consults.size() == 0) {
            System.out.println("No consultation history.");
        } else {
            System.out.println("\n--- Consultation History ---");
            for (int i = 0; i < consults.size(); i++) {
                Consultation c = consults.get(i);
                System.out.printf("%d. %s | Doctor: %s | Time: %s | Diagnosis: %s\n",
                        i + 1,
                        c.getConsultationId(),
                        c.getDoctor() != null ? c.getDoctor().getName() : "-",
                        c.getConsultationTime(),
                        c.getDiagnosis() != null ? c.getDiagnosis().getName() : "-"
                );
            }
        }
    }

    private void viewAndPayTreatmentBills() {
        System.out.println("\n=== Treatment Bills & Payment ===");
        
        // Get all treatments for the current patient
        CustomADT<String, Treatment> patientTreatments = treatmentMaintenance.getTreatmentsByPatient(currentPatient);
        
        if (patientTreatments.size() == 0) {
            System.out.println("No treatments found for your account.");
            return;
        }
        
        // Filter for completed treatments that need payment
        CustomADT<String, Treatment> unpaidTreatments = new CustomADT<>();
        for (Treatment treatment : patientTreatments.toArray(new Treatment[0])) {
            if ("COMPLETED".equals(treatment.getStatus())) {
                // Check if payment exists and is not completed
                String consultationId = treatment.getConsultationID();
                boolean needsPayment = true;
                
                // Check if there's already a completed payment
                Payment[] allPayments = paymentMaintenance.getAllPayments();
                for (Payment payment : allPayments) {
                    if (payment.getConsultationId().equals(consultationId) && 
                        "COMPLETED".equals(payment.getPaymentStatus())) {
                        needsPayment = false;
                        break;
                    }
                }
                
                if (needsPayment) {
                    unpaidTreatments.put(treatment.getTreatmentID(), treatment);
                }
            }
        }
        
        if (unpaidTreatments.size() == 0) {
            System.out.println("✅ All your treatments have been paid for!");
            return;
        }
        
        // Display unpaid treatments
        System.out.println("\n📄 Treatments requiring payment:");
        System.out.println("┌─────┬──────────────┬─────────────────┬──────────────┐");
        System.out.println("│ No. │ Treatment ID │ Date            │ Type         │");
        System.out.println("├─────┼──────────────┼─────────────────┼──────────────┤");
        
        Treatment[] unpaidArray = unpaidTreatments.toArray(new Treatment[0]);
        for (int i = 0; i < unpaidArray.length; i++) {
            Treatment t = unpaidArray[i];
            System.out.printf("│ %-3d │ %-12s │ %-15s │ %-12s │%n",
                i + 1,
                t.getTreatmentID(),
                t.getTreatmentDate().toLocalDate().toString(),
                t.getType());
        }
        System.out.println("└─────┴──────────────┴─────────────────┴──────────────┘");
        
        // Let user select treatment to pay for
        int choice = InputHandler.getInt("Select treatment to view bill (0 to go back)", 0, unpaidArray.length);
        if (choice == 0) return;
        
        Treatment selectedTreatment = unpaidArray[choice - 1];
        viewAndProcessPayment(selectedTreatment);
    }
    
    private void viewAndProcessPayment(Treatment treatment) {
        System.out.println("\n=== Treatment Bill Details ===");
        System.out.println("Treatment ID: " + treatment.getTreatmentID());
        System.out.println("Date: " + treatment.getTreatmentDate().toLocalDate());
        System.out.println("Type: " + treatment.getType());
        
        // Create or get payment for this treatment
        Payment payment = treatmentMaintenance.createSimplePayment(
            treatment.getConsultationID(), 
            treatment.getTreatmentID()
        );
        
        if (payment == null) {
            System.out.println("❌ Error: Could not generate payment details.");
            return;
        }
        
        // Display payment breakdown
        System.out.println("\n💰 Bill Breakdown:");
        System.out.println("┌─────────────────────────┬──────────────┐");
        System.out.println("│ Service                 │ Amount (RM)  │");
        System.out.println("├─────────────────────────┼──────────────┤");
        
        CustomADT<String, Double> breakdown = payment.getPaymentBreakdown();
        double total = 0.0;
        
        // Iterate through breakdown items
        String[] serviceTypes = {"Consultation Services", "Treatment Procedures", "Prescription Medicines"};
        for (String serviceType : serviceTypes) {
            Double amount = breakdown.get(serviceType);
            if (amount != null && amount > 0) {
                System.out.printf("│ %-23s │ %10.2f   │%n", serviceType, amount);
                total += amount;
            }
        }
        
        System.out.println("├─────────────────────────┼──────────────┤");
        System.out.printf("│ %-23s │ %10.2f   │%n", "TOTAL", total);
        System.out.println("└─────────────────────────┴──────────────┘");
        
        // Payment options
        System.out.println("\nPayment Options:");
        System.out.println("1. Pay Full Amount (RM " + String.format("%.2f", total) + ")");
        System.out.println("2. Pay Partial Amount");
        System.out.println("0. Back to Bills List");
        
        int choice = InputHandler.getInt("Select payment option", 0, 2);
        
        switch (choice) {
            case 1 -> processFullPayment(payment, total);
            case 2 -> processPartialPayment(payment, total);
            case 0 -> System.out.println("Returning to bills list...");
        }
    }
    
    private void processFullPayment(Payment payment, double totalAmount) {
        boolean confirm = InputHandler.getYesNo("Confirm payment of RM " + String.format("%.2f", totalAmount) + "?");
        
        if (confirm) {
            String result = treatmentMaintenance.processPayment(payment.getPaymentId(), totalAmount);
            System.out.println("\n✅ " + result);
            if (result.contains("COMPLETED")) {
                System.out.println("💳 Payment completed successfully!");
                System.out.println("📧 Receipt will be sent to your registered contact details.");
            }
        } else {
            System.out.println("Payment cancelled.");
        }
    }
    
    private void processPartialPayment(Payment payment, double totalAmount) {
        System.out.println("Total Amount Due: RM " + String.format("%.2f", totalAmount));
        double paidAmount = InputHandler.getDouble("Enter amount to pay (RM)", 0.01, totalAmount);
        
        boolean confirm = InputHandler.getYesNo("Confirm payment of RM " + String.format("%.2f", paidAmount) + "?");
        
        if (confirm) {
            String result = treatmentMaintenance.processPayment(payment.getPaymentId(), paidAmount);
            System.out.println("\n✅ " + result);
            
            if (result.contains("PARTIAL")) {
                double remaining = totalAmount - paidAmount;
                System.out.println("💰 Remaining balance: RM " + String.format("%.2f", remaining));
                System.out.println("📧 Partial payment receipt will be sent to your registered contact details.");
            } else if (result.contains("COMPLETED")) {
                System.out.println("💳 Payment completed successfully!");
                System.out.println("📧 Full payment receipt will be sent to your registered contact details.");
            }
        } else {
            System.out.println("Payment cancelled.");
        }
    }

    public static void main(String[] args) {
        PatientMaintenance pm = new PatientMaintenance();
        StudentUI ui = new StudentUI(pm);
        ui.displayMenu();
    }
}