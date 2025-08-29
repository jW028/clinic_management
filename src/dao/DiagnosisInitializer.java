package dao;

import adt.OrderedMap;
import entity.Diagnosis;
import utility.IDGenerator;

public class DiagnosisInitializer {

    DiagnosisDAO diagnosisDAO = new DiagnosisDAO();
    
    public void initializeDiagnoses() {
        System.out.println("🔧 Initializing diagnosis data...");
        
        // Create OrderedMap to store diagnoses
        OrderedMap<String, Diagnosis> diagnoses = new OrderedMap<>();
        
        // Create sample diagnoses
        createSampleDiagnoses(diagnoses);
        
        // Save to file using DAO
        diagnosisDAO.saveDiagnosis(diagnoses);
        
        System.out.println("✅ Diagnosis initialization completed!");
        System.out.println("📊 Total diagnoses created: " + diagnoses.size());
    }
    
    private void createSampleDiagnoses(OrderedMap<String, Diagnosis> diagnoses) {
        // Common medical diagnoses with varying severities
        
        // Respiratory conditions
        String diagnosisId1 = IDGenerator.generateDiagnosisID();
        Diagnosis pneumonia = new Diagnosis(diagnosisId1, "Pneumonia", "High");
        pneumonia.setDescription("Lung infection with inflammation");
        diagnoses.put(diagnosisId1, pneumonia);
        
        String diagnosisId2 = IDGenerator.generateDiagnosisID();
        Diagnosis asthma = new Diagnosis(diagnosisId2, "Asthma", "Medium");
        asthma.setDescription("Breathing difficulty, airway constriction");
        diagnoses.put(diagnosisId2, asthma);
        
        String diagnosisId3 = IDGenerator.generateDiagnosisID();
        Diagnosis bronchitis = new Diagnosis(diagnosisId3, "Bronchitis", "Low");
        bronchitis.setDescription("Bronchial tube inflammation");
        diagnoses.put(diagnosisId3, bronchitis);
        
        // Cardiovascular conditions
        String diagnosisId4 = IDGenerator.generateDiagnosisID();
        Diagnosis hypertension = new Diagnosis(diagnosisId4, "Hypertension", "Medium");
        hypertension.setDescription("High blood pressure");
        diagnoses.put(diagnosisId4, hypertension);
        
        String diagnosisId5 = IDGenerator.generateDiagnosisID();
        Diagnosis heartArrhythmia = new Diagnosis(diagnosisId5, "Atrial Fibrillation", "High");
        heartArrhythmia.setDescription("Irregular heart rhythm disorder");
        diagnoses.put(diagnosisId5, heartArrhythmia);
        
        // Digestive conditions
        String diagnosisId6 = IDGenerator.generateDiagnosisID();
        Diagnosis gastritis = new Diagnosis(diagnosisId6, "Gastritis", "Low");
        gastritis.setDescription("Stomach lining inflammation");
        diagnoses.put(diagnosisId6, gastritis);
        
        String diagnosisId7 = IDGenerator.generateDiagnosisID();
        Diagnosis ulcer = new Diagnosis(diagnosisId7, "Peptic Ulcer", "Medium");
        ulcer.setDescription("Stomach/duodenal sores");
        diagnoses.put(diagnosisId7, ulcer);
        
        // Infectious diseases
        String diagnosisId8 = IDGenerator.generateDiagnosisID();
        Diagnosis flu = new Diagnosis(diagnosisId8, "Influenza", "Low");
        flu.setDescription("Viral respiratory infection");
        diagnoses.put(diagnosisId8, flu);
        
        String diagnosisId9 = IDGenerator.generateDiagnosisID();
        Diagnosis covid = new Diagnosis(diagnosisId9, "COVID-19", "High");
        covid.setDescription("SARS-CoV-2 coronavirus disease");
        diagnoses.put(diagnosisId9, covid);
        
        // Musculoskeletal conditions
        String diagnosisId10 = IDGenerator.generateDiagnosisID();
        Diagnosis backPain = new Diagnosis(diagnosisId10, "Lower Back Pain", "Low");
        backPain.setDescription("Lower spine pain syndrome");
        diagnoses.put(diagnosisId10, backPain);
        
        String diagnosisId11 = IDGenerator.generateDiagnosisID();
        Diagnosis arthritis = new Diagnosis(diagnosisId11, "Rheumatoid Arthritis", "Medium");
        arthritis.setDescription("Joint inflammation disorder");
        diagnoses.put(diagnosisId11, arthritis);
        
        // Neurological conditions
        String diagnosisId12 = IDGenerator.generateDiagnosisID();
        Diagnosis migraine = new Diagnosis(diagnosisId12, "Migraine", "Medium");
        migraine.setDescription("Severe recurring headaches");
        diagnoses.put(diagnosisId12, migraine);
        
        String diagnosisId13 = IDGenerator.generateDiagnosisID();
        Diagnosis stroke = new Diagnosis(diagnosisId13, "Ischemic Stroke", "High");
        stroke.setDescription("Brain blood flow blockage");
        diagnoses.put(diagnosisId13, stroke);
        
        // Endocrine conditions
        String diagnosisId14 = IDGenerator.generateDiagnosisID();
        Diagnosis diabetes = new Diagnosis(diagnosisId14, "Type 2 Diabetes", "Medium");
        diabetes.setDescription("Blood sugar regulation disorder");
        diagnoses.put(diagnosisId14, diabetes);
        
        String diagnosisId15 = IDGenerator.generateDiagnosisID();
        Diagnosis thyroid = new Diagnosis(diagnosisId15, "Hypothyroidism", "Low");
        thyroid.setDescription("Low thyroid hormone production");
        diagnoses.put(diagnosisId15, thyroid);
        
        System.out.println("📋 Created " + diagnoses.size() + " sample diagnoses");
    }
    
    public static void main(String[] args) {
        DiagnosisInitializer diagnosisInitializer = new DiagnosisInitializer();
        diagnosisInitializer.initializeDiagnoses();

    }
}
