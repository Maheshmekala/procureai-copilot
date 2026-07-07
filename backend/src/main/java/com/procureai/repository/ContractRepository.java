package com.procureai.repository;

import com.procureai.domain.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findBySupplierId(Long supplierId);
    List<Contract> findByStatusIgnoreCase(String status);

    @Query("SELECT c FROM Contract c WHERE c.endDate BETWEEN :now AND :thirtyDays")
    List<Contract> findExpiringSoon(LocalDate now, LocalDate thirtyDays);

    long countByStatusIgnoreCase(String status);
}
