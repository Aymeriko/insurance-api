package ch.insurance.api.repository;

import ch.insurance.api.domain.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Query("SELECT c FROM Contract c WHERE c.clientId = :clientId AND (c.endDate IS NULL OR c.endDate > :currentDate)")
    List<Contract> findActiveContractsByClientId(@Param("clientId") Long clientId, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT c FROM Contract c WHERE c.clientId = :clientId AND (c.endDate IS NULL OR c.endDate > :currentDate) AND c.lastModifiedDate >= :modifiedAfter")
    List<Contract> findActiveContractsByClientIdAndModifiedAfter(
        @Param("clientId") Long clientId,
        @Param("currentDate") LocalDate currentDate,
        @Param("modifiedAfter") LocalDateTime modifiedAfter
    );

    @Query("SELECT COALESCE(SUM(c.costAmount), 0) FROM Contract c WHERE c.clientId = :clientId AND (c.endDate IS NULL OR c.endDate > :currentDate)")
    BigDecimal sumActiveContractsCostByClientId(@Param("clientId") Long clientId, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT COUNT(c) FROM Contract c WHERE c.clientId = :clientId AND (c.endDate IS NULL OR c.endDate > :currentDate)")
    Long countActiveContractsByClientId(@Param("clientId") Long clientId, @Param("currentDate") LocalDate currentDate);

    List<Contract> findByClientId(Long clientId);
}
