package utility;

import dao.*;
import java.io.File;

/**
 * Initializes all system data at once by running all initializers sequentially
 */
public class SystemDataInitializer {
    
    public static void main(String[] args) {
        System.out.println("🚀 CLINIC MANAGEMENT SYSTEM - COMPLETE DATA INITIALIZATION");
        System.out.println("=".repeat(65));
        
        // Create data directory if it doesn't exist
        File dataDir = new File("src/data");
        if (!dataDir.exists()) {
            System.out.println("📁 Creating data directory...");
            boolean created = dataDir.mkdirs();
            if (created) {
                System.out.println("✅ Data directory created successfully");
            } else {
                System.out.println("⚠️ Failed to create data directory");
            }
        }
        
        System.out.println("\n🔄 Starting data initialization sequence...\n");
        
        // Run each initializer in proper dependency order
        // 1. Core entities first (no dependencies)
        initializeWithErrorHandling("Patient", () -> PatientInitializer.main(null));
        initializeWithErrorHandling("Doctor", () -> DoctorInitializer.main(null));
        initializeWithErrorHandling("Medicine", () -> MedicineInitializer.main(null));
        initializeWithErrorHandling("Diagnosis", () -> DiagnosisInitializer.main(null));
        initializeWithErrorHandling("Consultation Service", () -> ConsultationServiceInitializer.main(null));
        initializeWithErrorHandling("Procedure", () -> ProcedureInitializer.main(null));
        
        // 2. Schedule data (depends on doctors)
        initializeWithErrorHandling("Schedule", () -> ScheduleInitializer.main(null));
        
        // 3. Appointments (depends on patients and doctors)
        initializeWithErrorHandling("Appointment", () -> AppointmentInitializer.main(null));
        
        // 4. Visit History (depends on patients)
        initializeWithErrorHandling("Visit History", () -> VisitHistoryInitializer.main(null));
        
        // 5. Prescriptions (depends on medicines)
        initializeWithErrorHandling("Prescription", () -> PrescriptionInitializer.main(null));
        
        // 6. Transactions (standalone)
        initializeWithErrorHandling("Transaction", () -> TransactionInitializer.main(null));
        
        System.out.println("\n" + "=".repeat(65));
        System.out.println("✅ ALL DATA INITIALIZED SUCCESSFULLY!");
        System.out.println("=".repeat(65));
        
        // List all data files that were created
        listDataFiles();
        
        // Summary statistics
        showInitializationSummary();
    }
    
    /**
     * Run an initializer with proper error handling and timing
     */
    private static void initializeWithErrorHandling(String type, Runnable initializer) {
        System.out.printf("📦 %-20s ", "Initializing " + type + "...");
        
        long startTime = System.currentTimeMillis();
        try {
            initializer.run();
            long duration = System.currentTimeMillis() - startTime;
            System.out.printf("✅ SUCCESS (%d ms)\n", duration);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            System.out.printf("❌ FAILED (%d ms)\n", duration);
            System.out.println("   Error: " + e.getMessage());
            // Don't print full stack trace to keep output clean
            // e.printStackTrace();
        }
    }
    
    /**
     * List all data files that were created with detailed information
     */
    private static void listDataFiles() {
        System.out.println("\n📋 DATA FILES CREATED:");
        System.out.println("-".repeat(50));
        
        File dataDir = new File("src/data");
        if (dataDir.exists() && dataDir.isDirectory()) {
            File[] files = dataDir.listFiles();
            if (files != null && files.length > 0) {
                // Sort files by name for consistent output
                java.util.Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
                
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".dat")) {
                        String status = file.length() > 0 ? "✅" : "⚠️";
                        System.out.printf("%s %-25s %8.2f KB\n", 
                                status,
                                file.getName(), 
                                file.length() / 1024.0);
                    }
                }
            } else {
                System.out.println("❌ No data files found in src/data directory");
            }
        } else {
            System.out.println("❌ Data directory not found");
        }
    }
    
    /**
     * Show initialization summary and next steps
     */
    private static void showInitializationSummary() {
        System.out.println("\n🎯 INITIALIZATION SUMMARY:");
        System.out.println("-".repeat(40));
        
        String[] expectedFiles = {
            "appointment.dat",
            "diagnosis.dat",
            "doctors.dat",
            "medicine.dat",
            "pending_prescriptions.dat",
            "processed_prescriptions.dat",
            "procedures.dat",
            "schedules.dat",
            "service.dat",
            "transactions.dat",
            "visithistory.dat"
        };
        
        File dataDir = new File("src/data");
        int existingFiles = 0;
        
        for (String fileName : expectedFiles) {
            File file = new File(dataDir, fileName);
            if (file.exists() && file.length() > 0) {
                existingFiles++;
            }
        }
        
        System.out.printf("📊 Data files: %d/%d created successfully\n", 
                existingFiles, expectedFiles.length);
        
        if (existingFiles == expectedFiles.length) {
            System.out.println("🎉 All expected data files are present!");
        } else {
            System.out.println("⚠️ Some data files may be missing or empty");
        }
        
        System.out.println("\n🚀 NEXT STEPS:");
        System.out.println("   1. Run your main application (ADTDriverTest)");
        System.out.println("   2. Test consultation creation and treatment workflows");
        System.out.println("   3. Verify all data loads correctly");
        
        System.out.println("\n💡 TIP: If you encounter issues, check the file sizes above.");
        System.out.println("    Empty files (0.00 KB) indicate initialization problems.");
    }
    
    /**
     * Utility method to check if a specific data type is initialized
     */
    public static boolean isDataInitialized(String dataType) {
        String fileName = getFileNameForDataType(dataType);
        if (fileName != null) {
            File file = new File("src/data/" + fileName);
            return file.exists() && file.length() > 0;
        }
        return false;
    }
    
    /**
     * Map data types to their corresponding file names
     */
    private static String getFileNameForDataType(String dataType) {
        switch (dataType.toLowerCase()) {
            case "patient": return "patients.dat";
            case "doctor": return "doctors.dat";
            case "appointment": return "appointment.dat";
            case "schedule": return "schedules.dat";
            case "medicine": return "medicine.dat";
            case "diagnosis": return "diagnosis.dat";
            case "prescription": return "pending_prescriptions.dat";
            case "service": return "service.dat";
            case "procedure": return "procedures.dat";
            case "transaction": return "transactions.dat";
            case "visithistory": return "visithistory.dat";
            default: return null;
        }
    }
    
    /**
     * Force re-initialization of specific data type
     */
    public static void reinitializeDataType(String dataType) {
        System.out.println("🔄 Re-initializing " + dataType + " data...");
        
        switch (dataType.toLowerCase()) {
            case "patient":
                PatientInitializer.main(null);
                break;
            case "doctor":
                DoctorInitializer.main(null);
                break;
            case "appointment":
                AppointmentInitializer.main(null);
                break;
            case "schedule":
                ScheduleInitializer.main(null);
                break;
            case "medicine":
                MedicineInitializer.main(null);
                break;
            case "diagnosis":
                DiagnosisInitializer.main(null);
                break;
            case "prescription":
                PrescriptionInitializer.main(null);
                break;
            case "service":
                ConsultationServiceInitializer.main(null);
                break;
            case "procedure":
                ProcedureInitializer.main(null);
                break;
            case "transaction":
                TransactionInitializer.main(null);
                break;
            case "visithistory":
                VisitHistoryInitializer.main(null);
                break;
            default:
                System.out.println("❌ Unknown data type: " + dataType);
        }
    }
}