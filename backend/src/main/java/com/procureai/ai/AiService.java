package com.procureai.ai;

import com.procureai.domain.Supplier;
import com.procureai.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiService {
    private final SupplierRepository supplierRepository;

    public AiService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public Map<String, Object> processMessage(String message) {
        String msg = message.toLowerCase().trim();
        List<Supplier> all = supplierRepository.findAll();

        if (msg.contains("top") || msg.contains("highest") || msg.contains("biggest")) {
            List<Supplier> top = supplierRepository.findTopBySpend();
            List<Map<String, Object>> list = top.stream().limit(5).map(s -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("name", s.getName());
                m.put("spend", s.getAnnualSpend());
                m.put("category", s.getCategory());
                return m;
            }).collect(Collectors.toList());
            return buildResponse("Top Suppliers by Spend", list);
        }
        else if (msg.contains("risk") || msg.contains("risky")) {
            List<Map<String, Object>> list = all.stream()
                .filter(s -> s.getRiskScore() != null && s.getRiskScore() > 50)
                .map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", s.getName());
                    m.put("riskScore", s.getRiskScore());
                    m.put("category", s.getCategory());
                    return m;
                }).collect(Collectors.toList());
            String title = list.isEmpty() ? "No high-risk suppliers found" : "High-Risk Suppliers (" + list.size() + ")";
            return buildResponse(title, list);
        }
        else if (msg.contains("count") || msg.contains("how many") || msg.contains("total")) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("totalSuppliers", all.size());
            data.put("categories", all.stream().map(Supplier::getCategory).distinct().count());
            data.put("totalSpend", all.stream().mapToDouble(Supplier::getAnnualSpend).sum());
            return buildResponse("Database Summary", data);
        }
        else if (msg.contains("category") || msg.contains("group")) {
            Map<String, Long> byCat = all.stream()
                .collect(Collectors.groupingBy(Supplier::getCategory, Collectors.counting()));
            Map<String, Object> data = new LinkedHashMap<>(byCat);
            return buildResponse("Suppliers by Category", data);
        }
        else if (msg.contains("all") || msg.contains("list")) {
            List<Map<String, Object>> list = all.stream().map(s -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", s.getId());
                m.put("name", s.getName());
                m.put("category", s.getCategory());
                m.put("spend", s.getAnnualSpend());
                m.put("risk", s.getRiskScore());
                return m;
            }).collect(Collectors.toList());
            return buildResponse("All Suppliers (" + list.size() + ")", list);
        }
        else if (msg.contains("average") || msg.contains("avg")) {
            double avg = all.stream().mapToDouble(Supplier::getAnnualSpend).average().orElse(0);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("averageSpend", avg);
            data.put("formatted", String.format("$%,.2f", avg));
            return buildResponse("Average Supplier Spend", data);
        }
        else if (msg.contains("hello") || msg.contains("hi") || msg.contains("hey")) {
            return buildResponse("👋 Welcome to ProcureAI Copilot!", List.of(
                "I can help you with:",
                "• 'Show top suppliers' — see highest spend",
                "• 'Which suppliers are high risk?' — risk analysis",
                "• 'How many suppliers?' — database counts",
                "• 'List all suppliers' — full supplier list",
                "• 'Group by category' — category breakdown",
                "• 'What's the average spend?' — spend metrics"
            ));
        }
        else {
            String cat = msg.contains("tech") ? "Technology"
                       : msg.contains("manufacturing") ? "Manufacturing"
                       : msg.contains("health") ? "Healthcare"
                       : msg.contains("food") ? "Food & Beverage"
                       : msg.contains("construction") ? "Construction"
                       : null;

            if (cat != null) {
                List<Supplier> filtered = all.stream()
                    .filter(s -> s.getCategory().equalsIgnoreCase(cat))
                    .collect(Collectors.toList());
                List<Map<String, Object>> list = filtered.stream().map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", s.getName());
                    m.put("spend", s.getAnnualSpend());
                    m.put("risk", s.getRiskScore());
                    return m;
                }).collect(Collectors.toList());
                return buildResponse(cat + " Suppliers (" + list.size() + ")", list);
            }

            return buildResponse("I didn't understand that. Try one of these:", List.of(
                "'Show top suppliers'", "'Which suppliers are high risk?'",
                "'How many suppliers?'", "'List all suppliers'",
                "'Group by category'", "'Hello'"
            ));
        }
    }

    private Map<String, Object> buildResponse(String answer, Object data) {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("answer", answer);
        resp.put("data", data);
        resp.put("timestamp", new Date().toString());
        return resp;
    }
}
