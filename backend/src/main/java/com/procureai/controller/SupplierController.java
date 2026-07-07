package com.procureai.controller;

import com.procureai.domain.Supplier;
import com.procureai.repository.SupplierRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierRepository supplierRepository;

    public SupplierController(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @GetMapping
    public List<Supplier> getAll() { return supplierRepository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getById(@PathVariable Long id) {
        return ResponseEntity.of(supplierRepository.findById(id));
    }

    @PostMapping
    public Supplier create(@RequestBody Supplier s) { return supplierRepository.save(s); }

    @GetMapping("/top")
    public List<Supplier> getTop() { return supplierRepository.findTopBySpend(); }

    @GetMapping("/search")
    public List<Supplier> search(@RequestParam String q) {
        return supplierRepository.findByNameContainingIgnoreCase(q);
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        List<Supplier> all = supplierRepository.findAll();
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("total", all.size());
        s.put("totalSpend", all.stream().mapToDouble(Supplier::getAnnualSpend).sum());
        s.put("highRisk", all.stream().filter(x -> x.getRiskScore() != null && x.getRiskScore() >= 70).count());
        s.put("categories", supplierRepository.getCategorySummary().size());
        return s;
    }

    @PostMapping("/seed")
    public Map<String, Object> seed(@RequestParam(defaultValue = "50") int count) {
        Random r = new Random();
        String[] names = {"Acme", "Global", "TechVendor", "DataSync", "MedEquip",
            "BuildRite", "FreshSupply", "LogiTrans", "PrimeSource", "NovaTech",
            "Apex", "Vertex", "Pinnacle", "Summit", "Horizon"};
        String[] cats = {"Technology", "Manufacturing", "Healthcare",
            "Food & Beverage", "Construction", "Logistics", "Finance", "Energy"};

        for (int i = 0; i < count; i++) {
            Supplier s = new Supplier();
            s.setName(names[r.nextInt(names.length)] + " " + (r.nextInt(999) + 1));
            s.setCategory(cats[r.nextInt(cats.length)]);
            s.setAnnualSpend(Math.round((r.nextDouble() * 5000000 + 100000) * 100.0) / 100.0);
            s.setRiskScore(r.nextInt(100));
            s.setContactEmail("vendor" + i + "@company.com");
            supplierRepository.save(s);
        }
        return Map.of("message", count + " suppliers created", "total", supplierRepository.count());
    }
}
