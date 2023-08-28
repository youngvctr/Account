package com.example.account.dto;


import com.example.account.domain.Transaction;
import com.example.account.domain.TransactionResult;
import com.example.account.type.TransactionResultType;
import com.example.account.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResultDto {
    private String accountNumber;
    private TransactionType transactionType;
    private TransactionResultType transactionResultType;
    private Long amount;
    private Long balanceSnapshot;
    private String transactionId;
    private String transactionResultId;
    private LocalDateTime transactedAt;

    public static TransactionResultDto fromEntity(TransactionResult transactionResult) {
        return TransactionResultDto.builder()
                .accountNumber(transactionResult.getAccount().getAccountNumber())
                .transactionType(transactionResult.getTransactionType())
                .transactionResultType(transactionResult.getTransactionResultType())
                .amount(transactionResult.getAmount())
                .balanceSnapshot(transactionResult.getBalanceSnapshot())
                .transactionId(transactionResult.getTransactionId())
                .transactionResultId(transactionResult.getTransactionResultId())
                .transactedAt(transactionResult.getTransactedAt())
                .build();
    }
}
