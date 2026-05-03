package com.ritfast.processor.repository;

import org.springframework.stereotype.Repository;
import com.ritfast.processor.entity.Dealer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long>, JpaSpecificationExecutor<Dealer> {
    List<Dealer> findByClientId(Long clientId);
}