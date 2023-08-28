package com.example.account.dto;

import com.example.account.type.AccountStatus;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
/**
 * <a href="http://localhost:8080/h2-console">...</a>
 * Balance 가 0일 때만 삭제가 가능하다.
 * Balance 가 0이 아닌 경우에는 삭제되지 않는다.
 */
public class DeleteAccount {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {
        @NotNull
        @Min(1)
        private Long userId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long userId;
        private String accountNumber;

        @Enumerated(EnumType.STRING)
        private AccountStatus accountStatus;
        private LocalDateTime unRegisteredAt;

        public static Response from(AccountDto accountDto) {
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .accountStatus(AccountStatus.UNREGISTERED)
                    .unRegisteredAt(accountDto.getUnRegisteredAt())
                    .build();
        }
    }
}
