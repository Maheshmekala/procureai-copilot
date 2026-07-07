package com.procureai.repository;

import com.procureai.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByNameContainingIgnoreCase(String name);
    List<Supplier> findByCategoryIgnoreCase(String category);
    List<Supplier> findByRiskScoreGreaterThanEqual(Integer riskScore);

    @Query("SELECT s FROM Supplier s ORDER BY s.annualSpend DESC")
    List<Supplier> findTopBySpend();

    @Query("SELECT s.category, COUNT(s), SUM(s.annualSpend) FROM Supplier s GROUP BY s.category")
    List<Object[]> getCategorySummary();

    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.riskScore >= 70")
    long countHighRisk();
}
