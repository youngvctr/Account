package com.example.account.repository;

import com.example.account.domain.TransactionResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionResultRepository extends JpaRepository<TransactionResult, Long> {
    Optional<Boolean> findByTransactionId(String transactionId);
}
