package com.lbu.lbucourse.controllers.impl;

import com.lbu.lbucourse.commons.auth.services.AuthService;
import com.lbu.lbucourse.commons.exceptions.LBUFinanceRuntimeException;
import com.lbu.lbucourse.controllers.AccountController;
import com.lbu.lbucourse.dtos.FinanceAccountDto;
import com.lbu.lbucourse.models.Account;
import com.lbu.lbucourse.services.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static com.lbu.lbucourse.commons.constants.ErrorConstants.INTERNAL_ERROR;

@Slf4j
@RestController
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    AccountControllerImpl(AccountService accountService, AuthService authService, ModelMapper modelMapper) {
        this.accountService = accountService;
        this.authService = authService;
        this.modelMapper = modelMapper;
    }

    /**
     * Retrieves the finance account details for the authenticated user specified by the provided token. It validates
     * the user's authentication, retrieves the account details, and maps them to a FinanceAccountDto using ModelMapper.
     * If any exception occurs during the mapping process, it is caught and rethrown as an LBUFinanceRuntimeException.
     *
     * @param token The authentication token for the user.
     * @return ResponseEntity containing the FinanceAccountDto if successful.
     */
    @Override
    public ResponseEntity<FinanceAccountDto> getFinanceDetailsForUser(String token) {
        String authUserHref = authService.validateAuthUserHref(token);
        log.info("getting finance account for {}", authUserHref);
        Account account = accountService.getAccountDetailsForAuthHref(authUserHref);
        try {
            return ResponseEntity.ok(modelMapper.map(account, FinanceAccountDto.class));
        } catch (Exception e) {
            log.error("Model conversion error", e);
            throw new LBUFinanceRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

    /**
     * Creates a new finance account based on the provided FinanceAccountDto and the authentication token. It validates
     * the finance request, validates the user's authentication, maps the FinanceAccountDto to an Account using ModelMapper,
     * creates the finance account, and maps the created account to a FinanceAccountDto. If any exception occurs during the
     * process, it is caught and rethrown as an LBUFinanceRuntimeException.
     *
     * @param accountDto The FinanceAccountDto containing the details of the account to be created.
     * @param token      The authentication token for the user.
     * @return ResponseEntity containing the created FinanceAccountDto if successful.
     */
    @Override
    public ResponseEntity<FinanceAccountDto> createFinanceAccount(FinanceAccountDto accountDto, String token) {
        validateFinanceRequest(accountDto);
        String authUserHref = authService.validateAuthUserHref(accountDto.getAuthUserHref(), token);
        log.info("creating finance account for {}", authUserHref);
        Account account;
        try {
            account = modelMapper.map(accountDto, Account.class);
        } catch (Exception e) {
            log.error("Model conversion error {}", accountDto);
            throw new LBUFinanceRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
        Account createdAccount = accountService.createFinanceAccount(account);
        try {
            return ResponseEntity.ok(modelMapper.map(createdAccount, FinanceAccountDto.class));
        } catch (Exception e) {
            log.error("Model conversion error {}", accountDto);
            throw new LBUFinanceRuntimeException(INTERNAL_ERROR.getErrorMessage(), INTERNAL_ERROR.getErrorCode(), e);
        }
    }

}