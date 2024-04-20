package com.lbu.lbufinance.controllers.impl;

import com.lbu.lbufinance.commons.auth.service.AuthService;
import com.lbu.lbufinance.commons.exceptions.LbuFinanceRuntimeException;
import com.lbu.lbufinance.controllers.AccountController;
import com.lbu.lbufinance.dtos.AccountDto;
import com.lbu.lbufinance.models.Account;
import com.lbu.lbufinance.services.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static com.lbu.lbufinance.commons.constants.ErrorConstants.INTERNAL_ERROR;

@Slf4j
@RestController
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    /**
     * Constructs an instance of AccountControllerImpl.
     *
     * @param accountService The service responsible for handling account-related operations.
     * @param authService    The service responsible for authentication.
     * @param modelMapper    The mapper used for object conversions.
     */
    AccountControllerImpl(AccountService accountService, AuthService authService, ModelMapper modelMapper) {
        this.accountService = accountService;
        this.authService = authService;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieves finance details for a user.
     *
     * @param token The authentication token.
     * @return ResponseEntity containing FinanceAccountDto if successful.
     */
    @Override
    public ResponseEntity<AccountDto> getFinanceDetailsForUser(String token) {
        String authUserHref = authService.validateAuthUserHref(token);
        log.info("getting finance account for {}", authUserHref);
        Account account = accountService.getAccountDetailsForAuthHref(authUserHref);
        try {
            return ResponseEntity.ok(modelMapper.map(account, AccountDto.class));
        } catch (Exception e) {
            log.error("Model conversion error", e);
            throw new LbuFinanceRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Creates a finance account.
     *
     * @param accountDto The DTO containing finance account details.
     * @param token      The authentication token.
     * @return ResponseEntity containing FinanceAccountDto if successful.
     */
    @Override
    public ResponseEntity<AccountDto> createFinanceAccount(AccountDto accountDto, String token) {
        validateFinanceRequest(accountDto);
        String authUserHref = authService.validateAuthUserHref(accountDto.getAuthUserHref(), token);
        log.info("creating finance account for {}", authUserHref);
        Account account;
        try {
            account = modelMapper.map(accountDto, Account.class);
        } catch (Exception e) {
            log.error("Model conversion error {}", accountDto);
            throw new LbuFinanceRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
        Account createdAccount = accountService.createFinanceAccount(account);
        try {
            return ResponseEntity.ok(modelMapper.map(createdAccount, AccountDto.class));
        } catch (Exception e) {
            log.error("Model conversion error {}", accountDto);
            throw new LbuFinanceRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

}
