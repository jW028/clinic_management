# Clinic Management S## 💾 Data Setup

### If Data Files Are Missing or Corrupted

If you encounter errors about missing data files or corrupted data, follow these steps:

1. **First, compile the project:**
```bash
javac -cp src --enable-preview --release 24 src/**/*.java
```

2. **Then run data initialization:**
```bash
java -cp src --enable-preview utility.SystemDataInitializer
```

This will create all necessary data files with sample data:
- Patient records
- Doctor information  
- Medicine inventory
- Procedures catalog
- And all other system dataensive Java-based clinic management system that handles patient registration, appointments, consultations, treatments, procedures and prescriptions.

## Quick Start

### Running the System
```bash
cd clinic_management
java -cp src --enable-preview ClinicManagement
```

This will start the main application with two user interfaces:
1. **Student Interface** - For patients to manage their appointments and treatments
2. **Admin Interface** - For administrators to manage the entire system

## 🔧 System Requirements

- **Java**: JDK 17 or higher with preview features enabled
- **IDE**: VS Code with Java extensions or any Java IDE

## Data Setup

### If Data Files Are Missing or Corrupted

If you encounter errors about missing data files or corrupted data, run the data initialization:

```bash
java -cp src --enable-preview --release 23 utility.SystemDataInitializer
```

This will create all necessary data files with sample data:
- Patient records
- Doctor information  
- Medicine inventory
- Procedures catalog
- And all other system data

### Data File Location
All data files are stored in `src/data/` directory and will be created automatically during initialization.

## 🛠️ Troubleshooting

### Common Issues

#### "No existing data file found" or "File corrupted" messages
**Solution:** Compile and run data initialization:
```bash
javac -cp src --enable-preview --release 24 src/**/*.java
java -cp src --enable-preview utility.SystemDataInitializer
```

#### Compilation errors
**Solution:** Ensure you're using JDK 17+ with preview features:
```bash
javac -cp src --enable-preview --release 24 src/**/*.java
```

#### Reset entire system
**Solution:** Delete all `.dat` files, recompile, and re-initialize:
```bash
rm -rf src/data/*.dat counter.dat
javac -cp src --enable-preview --release 24 src/**/*.java
java -cp src --enable-preview utility.SystemDataInitializer
```

---

**Note:** Always run from the `clinic_management` project root directory.

