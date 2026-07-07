package com.procureai.ai;

import com.procureai.domain.Supplier;
import com.procureai.domain.Contract;
import com.procureai.repository.SupplierRepository;
import com.procureai.repository.ContractRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ToolExecutor {
    private final SupplierRepository supplierRepository;
    private final ContractRepository contractRepository;

    public ToolExecutor(SupplierRepository supplierRepository, ContractRepository contractRepository) {
        this.supplierRepository = supplierRepository;
        this.contractRepository = contractRepository;
    }

    public String execute(String toolName, String args) {
        return switch (toolName) {
            case "getTopSuppliers" -> getTopSuppliers();
            case "getSpendByCategory" -> getSpendByCategory();
            case "getTotalSpend" -> getTotalSpend();
            case "getHighRiskSuppliers" -> getHighRiskSuppliers();
            case "getSupplierCount" -> getSupplierCount();
            case "getExpiringContracts" -> getExpiringContracts();
            case "getContractSummary" -> getContractSummary();
            default -> "Unknown tool: " + toolName;
        };
    }

    public String getToolDefinitions() {
        return """
            AVAILABLE TOOLS:
            1. getTopSuppliers() - Returns top 10 suppliers by annual spend with name, category, spend
            2. getSpendByCategory() - Returns total spend grouped by category
            3. getTotalSpend() - Returns total spend across all suppliers
            4. getHighRiskSuppliers() - Returns suppliers with risk score >= 70
            5. getSupplierCount() - Returns total number of suppliers
            6. getExpiringContracts() - Returns contracts expiring within 30 days
            7. getContractSummary() - Returns summary of all contracts (total, active, expired)

            To call a tool, respond with exactly one TOOL_CALL per line. You can call multiple tools:
            TOOL_CALL: getTopSuppliers()
            TOOL_CALL: getSupplierCount()

            Each tool call must be on its own line starting with "TOOL_CALL:".
            After getting results, respond with your analysis.
            """;
    }

    private String getTopSuppliers() {
        List<Supplier> top = supplierRepository.findTopBySpend().stream().limit(10).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder("Top Suppliers by Spend:\n");
        for (int i = 0; i < top.size(); i++) {
            Supplier s = top.get(i);
            sb.append(String.format("%d. %s (%s) - $%,.2f - Risk: %d\n",
                i+1, s.getName(), s.getCategory(), s.getAnnualSpend(), s.getRiskScore()));
        }
        return sb.toString();
    }

    private String getSpendByCategory() {
        List<Object[]> summary = supplierRepository.getCategorySummary();
        StringBuilder sb = new StringBuilder("Spend by Category:\n");
        for (Object[] row : summary) {
            sb.append(String.format("- %s: %d suppliers, $%,.2f\n", row[0], row[1], row[2]));
        }
        return sb.toString();
    }

    private String getTotalSpend() {
        Double total = supplierRepository.findAll().stream()
            .mapToDouble(Supplier::getAnnualSpend).sum();
        long count = supplierRepository.count();
        return String.format("Total: $%,.2f across %d suppliers (Avg: $%,.2f)", total, count, total/count);
    }

    private String getHighRiskSuppliers() {
        List<Supplier> risky = supplierRepository.findByRiskScoreGreaterThanEqual(70);
        if (risky.isEmpty()) return "No high-risk suppliers found (risk >= 70)";
        StringBuilder sb = new StringBuilder("High-Risk Suppliers:\n");
        for (Supplier s : risky) {
            sb.append(String.format("- %s (%s): Risk %d, Spend $%,.2f\n",
                s.getName(), s.getCategory(), s.getRiskScore(), s.getAnnualSpend()));
        }
        return sb.toString();
    }

    private String getSupplierCount() {
        long total = supplierRepository.count();
        long highRisk = supplierRepository.countHighRisk();
        return String.format("Total: %d suppliers (High Risk: %d)", total, highRisk);
    }

    private String getExpiringContracts() {
        LocalDate now = LocalDate.now();
        List<Contract> expiring = contractRepository.findExpiringSoon(now, now.plusDays(30));
        if (expiring.isEmpty()) return "No contracts expiring within 30 days";
        StringBuilder sb = new StringBuilder("Contracts Expiring Soon:\n");
        for (Contract c : expiring) {
            sb.append(String.format("- %s: ends %s, value $%,.2f\n", c.getTitle(), c.getEndDate(), c.getValue()));
        }
        return sb.toString();
    }

    private String getContractSummary() {
        long total = contractRepository.count();
        long active = contractRepository.countByStatusIgnoreCase("ACTIVE");
        long expired = contractRepository.countByStatusIgnoreCase("EXPIRED");
        return String.format("Contracts: %d total (%d active, %d expired)", total, active, expired);
    }
}
