package com.procureai.service;

import com.procureai.domain.Supplier;
import com.procureai.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> findAll() { return supplierRepository.findAll(); }

    public Supplier findById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + id));
    }

    public Supplier create(Supplier supplier) { return supplierRepository.save(supplier); }

    public Supplier update(Long id, Supplier updated) {
        Supplier existing = findById(id);
        existing.setName(updated.getName());
        existing.setCategory(updated.getCategory());
        existing.setAnnualSpend(updated.getAnnualSpend());
        existing.setContactEmail(updated.getContactEmail());
        existing.setRiskScore(updated.getRiskScore());
        return supplierRepository.save(existing);
    }

    public void delete(Long id) { supplierRepository.deleteById(id); }

    public List<Supplier> searchByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Supplier> getTopBySpend() { return supplierRepository.findTopBySpend(); }

    public int seedRandom(int count) {
        Random r = new Random();
        String[] names = {"Acme", "Global", "TechVendor", "DataSync", "MedEquip",
            "BuildRite", "FreshSupply", "LogiTrans", "PrimeSource", "NovaTech"};
        String[] categories = {"Technology", "Manufacturing", "Healthcare",
            "Food & Beverage", "Construction", "Logistics", "Finance", "Energy"};
        for (int i = 0; i < count; i++) {
            Supplier s = new Supplier();
            s.setName(names[r.nextInt(names.length)] + " " + (r.nextInt(99) + 1));
            s.setCategory(categories[r.nextInt(categories.length)]);
            s.setAnnualSpend(Math.round((r.nextDouble() * 5000000 + 100000) * 100.0) / 100.0);
            s.setRiskScore(r.nextInt(100));
            s.setContactEmail("vendor" + i + "@example.com");
            supplierRepository.save(s);
        }
        return count;
    }
}
