package dao;

import adt.OrderedMap;
import entity.Procedure;

/**
 * Initialize sample procedure data for the clinic management system.
 * Procedures are treatment-based medical interventions (different from consultation services which are diagnostic).
 */
public class ProcedureInitializer {
    public static void main(String[] args) {
        OrderedMap<String, Procedure> procedureMap = new OrderedMap<>();

        // Treatment Procedures (different from diagnostic consultation services)
        
        // Surgical Procedures
        procedureMap.put("PROC001", new Procedure(
            "PROC001", "SUR001", "Minor Surgery", 
            "Minor surgical procedure for wound treatment or small tumor removal", 
            45, 500.00));
            
        procedureMap.put("PROC002", new Procedure(
            "PROC002", "SUR002", "Suture/Stitching", 
            "Suturing wounds or incisions with sterile technique", 
            20, 150.00));

        // Injection Procedures
        procedureMap.put("PROC003", new Procedure(
            "PROC003", "INJ001", "Intramuscular Injection", 
            "Administration of medication via intramuscular route", 
            5, 25.00));
            
        procedureMap.put("PROC004", new Procedure(
            "PROC004", "INJ002", "Intravenous Injection", 
            "Administration of medication or fluids via intravenous route", 
            10, 50.00));
            
        procedureMap.put("PROC005", new Procedure(
            "PROC005", "INJ003", "Vaccination", 
            "Administration of vaccines for disease prevention", 
            10, 75.00));

        // Therapeutic Procedures
        procedureMap.put("PROC006", new Procedure(
            "PROC006", "THP001", "Wound Dressing", 
            "Professional wound cleaning and dressing application", 
            15, 80.00));
            
        procedureMap.put("PROC007", new Procedure(
            "PROC007", "THP002", "Physical Therapy Session", 
            "Therapeutic physical rehabilitation and exercise", 
            60, 120.00));
            
        procedureMap.put("PROC008", new Procedure(
            "PROC008", "THP003", "Nebulizer Treatment", 
            "Respiratory therapy using nebulized medications", 
            20, 60.00));

        // Emergency Procedures
        procedureMap.put("PROC009", new Procedure(
            "PROC009", "EMG001", "Emergency Stabilization", 
            "Critical patient stabilization and emergency care", 
            30, 300.00));
            
        procedureMap.put("PROC010", new Procedure(
            "PROC010", "EMG002", "CPR Administration", 
            "Cardiopulmonary resuscitation for cardiac emergencies", 
            15, 200.00));

        // Specialist Procedures
        procedureMap.put("PROC011", new Procedure(
            "PROC011", "SPC001", "Catheter Insertion", 
            "Insertion of urinary or IV catheter", 
            25, 180.00));
            
        procedureMap.put("PROC012", new Procedure(
            "PROC012", "SPC002", "Endoscopy Procedure", 
            "Minimally invasive internal examination and treatment", 
            45, 800.00));

        // Outpatient Procedures
        procedureMap.put("PROC013", new Procedure(
            "PROC013", "OUT001", "Biopsy", 
            "Tissue sample collection for laboratory analysis", 
            30, 250.00));
            
        procedureMap.put("PROC014", new Procedure(
            "PROC014", "OUT002", "Cryotherapy", 
            "Treatment using controlled freezing for skin conditions", 
            15, 200.00));
            
        procedureMap.put("PROC015", new Procedure(
            "PROC015", "OUT003", "Laser Therapy", 
            "Medical laser treatment for various conditions", 
            20, 350.00));

        ProcedureDAO dao = new ProcedureDAO();
        boolean success = dao.saveToFile(procedureMap);
        
        if (success) {
            System.out.println("✅ Successfully initialized " + procedureMap.size() + " medical procedures");
            System.out.println("📋 Procedures include: Surgical, Injection, Therapeutic, Emergency, and Specialist treatments");
        } else {
            System.out.println("❌ Failed to initialize procedures");
        }
    }
}
