package com.procureai.service;

import com.procureai.domain.Supplier;
import com.procureai.domain.Contract;
import com.procureai.repository.SupplierRepository;
import com.procureai.repository.ContractRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {
    private final SupplierRepository supplierRepository;
    private final ContractRepository contractRepository;

    public DataSeeder(SupplierRepository supplierRepository, ContractRepository contractRepository) {
        this.supplierRepository = supplierRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    public void run(String... args) {
        if (supplierRepository.count() > 0) return;

        Random r = new Random(42);
        String[][] seedData = {
            {"Acme Corp", "Technology", "1250000", "30"},
            {"GlobalParts Inc", "Manufacturing", "890000", "65"},
            {"DataSync Solutions", "Technology", "2100000", "45"},
            {"FreshSupply Co", "Food & Beverage", "450000", "20"},
            {"BuildRite Materials", "Construction", "670000", "55"},
            {"TechVendor Pro", "Technology", "3200000", "75"},
            {"MedEquip Direct", "Healthcare", "1800000", "35"},
            {"LogiTrans Solutions", "Logistics", "920000", "50"},
            {"PrimeSource Industries", "Manufacturing", "2800000", "60"},
            {"NovaTech Systems", "Technology", "4500000", "25"},
            {"Apex Logistics", "Logistics", "1500000", "40"},
            {"Vertex Construction Co", "Construction", "980000", "70"},
            {"Pinnacle Healthcare", "Healthcare", "3100000", "15"},
            {"Summit Energy Group", "Energy", "5200000", "80"},
            {"Horizon Food Services", "Food & Beverage", "750000", "45"},
            {"BlueSky Technologies", "Technology", "1900000", "55"},
            {"IronClad Manufacturing", "Manufacturing", "2300000", "35"},
            {"GreenLeaf Energy", "Energy", "4100000", "20"},
            {"SafeHands Healthcare", "Healthcare", "1200000", "60"},
            {"QuickShip Logistics", "Logistics", "680000", "70"},
        };

        for (String[] row : seedData) {
            Supplier s = new Supplier(row[0], row[1], Double.parseDouble(row[2]));
            s.setRiskScore(Integer.parseInt(row[3]));
            s.setContactEmail(row[0].toLowerCase().replace(" ", "") + "@company.com");
            supplierRepository.save(s);
        }

        // Create some contracts
        String[][] contractData = {
            {"IT Infrastructure Agreement", "Acme Corp", "ACTIVE", "2026-01-15", "2027-01-14", "1200000"},
            {"Manufacturing Parts Supply", "GlobalParts Inc", "ACTIVE", "2026-03-01", "2026-12-31", "890000"},
            {"Cloud Services Contract", "DataSync Solutions", "ACTIVE", "2026-02-01", "2026-08-30", "2100000"},
            {"Fresh Produce Supply", "FreshSupply Co", "EXPIRING_SOON", "2025-07-01", "2026-07-15", "450000"},
            {"Construction Materials Deal", "BuildRite Materials", "ACTIVE", "2026-04-01", "2027-03-31", "670000"},
            {"Enterprise Software License", "TechVendor Pro", "ACTIVE", "2026-01-01", "2026-12-31", "3200000"},
            {"Medical Equipment Lease", "MedEquip Direct", "EXPIRING_SOON", "2025-08-01", "2026-07-20", "1800000"},
        };

        long supplierId = 1;
        for (String[] row : contractData) {
            Contract c = new Contract();
            c.setTitle(row[0]);
            c.setContent("This is a contract between the company and " + row[1] +
                ". Terms and conditions apply. Value: $" + row[5] +
                ". Start date: " + row[3] + ". End date: " + row[4] + ".");
            c.setStatus(row[2]);
            c.setStartDate(LocalDate.parse(row[3]));
            c.setEndDate(LocalDate.parse(row[4]));
            c.setValue(Double.parseDouble(row[5]));
            c.setSupplierId(supplierId);
            contractRepository.save(c);
            supplierId++;
        }

        System.out.println("✅ Seeded " + supplierRepository.count() + " suppliers and " +
            contractRepository.count() + " contracts");
    }
}
