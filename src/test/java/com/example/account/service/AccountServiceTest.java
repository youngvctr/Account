package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.repository.AccountRepository;
import com.example.account.type.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;
//    @Autowired
//    private AccountService accountService;

//    @Test
//    @DisplayName("계좌 조회 성공")
//    void testMock() {
//        //given
//        given(accountRepository
//                .findById(anyLong()))
//                .willReturn(Optional.of(Account.builder()
//                        .accountStatus(AccountStatus.UNREGISTERED)
//                        .accountNumber("65789").build()));
//
//        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
//
//        //when
//        Account account = accountService.getAccount(4555L);
//
//        //then
//        verify(accountRepository, times(1)).findById(captor.capture());
//        verify(accountRepository, times(1)).save(any());
//
//        assertThat(4555L).isEqualTo(captor.getValue());
//        assertNotEquals(455151L, captor.getValue());
//        assertTrue(4555L == captor.getValue());
//        assertThat("65789").isEqualTo(account.getAccountNumber());
//        assertThat(AccountStatus.UNREGISTERED).isEqualTo(account.getAccountStatus());
//    }
//
//    @Test
//    @DisplayName("계좌 조회 실패- 음수 조회")
//    void testFailedToSearchAccount() {
//        //given
//        //when
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.getAccount(-10L));
//        //Account account = accountService.getAccount(-10L);
//
//        //then
//        assertThat("Minus").isEqualTo(exception.getMessage());
//    }
//
//    @BeforeEach
//    void init() {
//        Long userId = 0l;
//        Long initialBalance = 0l;
//        accountService.createAccount(userId, initialBalance);
//    }
//
//    @Test
//    void createAccount() {
//        Account account = accountService.getAccount(2L);
//        assertThat(account.getAccountNumber()).isEqualTo("40000");
//        assertThat(account.getAccountStatus()).isEqualTo(AccountStatus.IN_USE);
//    }
//
//    @Test
//    void getAccount() {
//        Account account = new Account();
//        account.setAccountNumber("40000");
//        account.setAccountStatus(AccountStatus.IN_USE);
//        assertThat(account.getId()).isEqualTo(0);
//    }

    @Test
    @DisplayName("계좌 생성")
    void createAccountSuccess() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Dodo").build();

//        given(accountUserRepository.findById(anyLong()))
//                .willReturn(Optional.of(AccountUser.builder()
//                                .name("Popo").build()));
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
//        given(accountRepository.findFirstByOrderByIdDesc())
//                .willReturn(Optional.of(Account.builder()
//                        .accountNumber("1000000012").build()));
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000015").build());
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        /*
         * ArgumentCaptor 를 사용하여 특정 메소드에 사용되는 아규먼트를  capture(저장)해놨다가 나중에
         * 다시 사용(getValue)할 수 있다. 아규먼트의 값에 대해서도 테스트를 진행하기 위해 사용한다.
         * */

        //when
        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("1000000000", captor.getValue().getAccountNumber());
    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 생성 실패")
    void createAccount_UserNotFound() {
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void successGetAccountsByUserId() {
        //given
        AccountUser coco = AccountUser.builder()
                .id(12L)
                .name("Codo").build();
        List<Account> accounts = Arrays.asList(
                Account.builder()
                        .accountUser(coco)
                        .accountNumber("1234567890")
                        .balance(1000L)
                        .build(),
                Account.builder()
                        .accountUser(coco)
                        .accountNumber("1111111111")
                        .balance(2000L)
                        .build(),
                Account.builder()
                        .accountUser(coco)
                        .accountNumber("2222222222")
                        .balance(3000L)
                        .build()
        );
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(coco));
        given(accountRepository.findByAccountUser(any()))
                .willReturn(accounts);
        //when
        List<AccountDto> accountDtos = accountService.getAccountsByUserId(1L);

        //then
        assertEquals(3, accountDtos.size());
        assertEquals("1234567890", accountDtos.get(0).getAccountNumber());
        assertEquals(1000, accountDtos.get(0).getBalance());
        assertEquals("1111111111", accountDtos.get(1).getAccountNumber());
        assertEquals(2000, accountDtos.get(1).getBalance());
        assertEquals("2222222222", accountDtos.get(2).getAccountNumber());
        assertEquals(3000, accountDtos.get(2).getBalance());
    }

    @Test
    @DisplayName("유저당 최대 계좌는 10개")
    void userAccount_maxAccountIs10() {
        //given
        AccountUser user = AccountUser.builder().id(15L).name("Aoao").build();
        given(accountUserRepository
                .findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository
                .countByAccountUser(any()))
                .willReturn(10);

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        //then
        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_10, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 삭제")
    void deleteAccountSuccess() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Coco").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .balance(0L)
                        .accountNumber("1000000012").build()));
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountDto accountDto = accountService.deleteAccount(12L, "1000000012");

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("1000000012", captor.getValue().getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());
    }

    @Test
    @DisplayName("해당 유저 없음 - 계좌 해지 실패")
    void deleteAccount_UserNotFound() {
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("해당 계좌 없음 - 계좌 해지 실패")
    void deleteAccount_AccountNotFound() {
        //given
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("Coco").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        //then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌 소유주 다름")
    void deleteAccountFailed_userUnMatch() {
        //given
        AccountUser coco = AccountUser.builder()
                .id(12L)
                .name("Coco").build();
        AccountUser dodo = AccountUser.builder()
                .id(13L)
                .name("Dodo").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(coco));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(dodo)
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        //then
        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH, exception.getErrorCode());

    }

    @Test
    @DisplayName("해지 계좌는 잔액이 없어야 한다.")
    void deleteAccountFailed_balanceNotEmpty() {
        //given
        AccountUser coco = AccountUser.builder()
                .id(12L)
                .name("Coco").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(coco));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(coco)
                        .balance(100L)
                        .accountNumber("1000000012").build()));

        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        //then
        assertEquals(ErrorCode.BALANCE_NOT_EMPTY, exception.getErrorCode());
    }

    @Test
    void failedToGetAccounts() {
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        //when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.getAccountsByUserId(1L));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }
}