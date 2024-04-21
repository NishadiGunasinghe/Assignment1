package com.lbu.lbuauth.services.impl;

import com.lbu.lbuauth.commons.exceptions.LBUAuthRuntimeException;
import com.lbu.lbuauth.dtos.JWTTokenDto;
import com.lbu.lbuauth.models.AccountActivationDetails;
import com.lbu.lbuauth.models.User;
import com.lbu.lbuauth.models.enums.RoleType;
import com.lbu.lbuauth.repositories.AccountActivationDetailsRepository;
import com.lbu.lbuauth.repositories.UserRepository;
import com.lbu.lbuauth.services.EmailService;
import com.lbu.lbuauth.services.JwtService;
import com.lbu.lbuauth.services.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.lbu.lbuauth.commons.constants.ErrorConstants.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AccountActivationDetailsRepository activationDetailsRepository;
    @Value("${custom.properties.account.activation.resend.hours}")
    private Long resendLimit;

    public UserServiceImpl(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            JwtService jwtService,
            EmailService emailService,
            AccountActivationDetailsRepository activationDetailsRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.activationDetailsRepository = activationDetailsRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public User createUser(User user) {
        //TODO validate content before saving
        log.info("creating a new user [{}]", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleType(RoleType.USER);
        user.setEnabled(Boolean.FALSE);
        user.setAccountNonExpired(Boolean.FALSE);
        user.setAccountNonLocked(Boolean.FALSE);
        user.setCredentialsNonExpired(Boolean.FALSE);
        User savedUser = userRepository.save(user);
        log.info("created a new user [{}]", user.getUsername());
        emailService.sendOrResendActivationLink(savedUser);
        log.info("successfully send an email to user [{}]", user.getUsername());
        return savedUser;
    }

    @Override
    public JWTTokenDto generateLoginToken(User user, boolean isPasswordCheck) {
        User selectedUser = userRepository.findByUsername(user.getUsername());
        log.info("found a user for the given username [{}]", user.getUsername());
        if (Objects.isNull(selectedUser)) {
            log.error(ACCOUNT_NOT_AVAILABLE_USERNAME_ERROR.getErrorMessage());
            throw new LBUAuthRuntimeException(ACCOUNT_NOT_AVAILABLE_USERNAME_ERROR.getErrorMessage(), ACCOUNT_NOT_AVAILABLE_USERNAME_ERROR.getErrorCode());
        }
        if (!selectedUser.getEnabled()) {
            log.error(ACCOUNT_NOT_ENABLE_ERROR.getErrorMessage());
            throw new LBUAuthRuntimeException(ACCOUNT_NOT_ENABLE_ERROR.getErrorMessage(), ACCOUNT_NOT_ENABLE_ERROR.getErrorCode());
        }
        if (!selectedUser.getCredentialsNonExpired() || !selectedUser.getAccountNonExpired()) {
            log.error(ACCOUNT_CREDENTIAL_EXPIRED_ERROR.getErrorMessage());
            throw new LBUAuthRuntimeException(ACCOUNT_CREDENTIAL_EXPIRED_ERROR.getErrorMessage(), ACCOUNT_CREDENTIAL_EXPIRED_ERROR.getErrorCode());
        }
        if (!selectedUser.getAccountNonLocked()) {
            log.error(ACCOUNT_LOCKED_ERROR.getErrorMessage());
            throw new LBUAuthRuntimeException(ACCOUNT_LOCKED_ERROR.getErrorMessage(), ACCOUNT_LOCKED_ERROR.getErrorCode());
        }
        if (isPasswordCheck) {
            if (passwordEncoder.matches(user.getPassword(), selectedUser.getPassword())) {
                log.info("generating a jwt token for the user [{}]", user.getUsername());
                return jwtService.generateJwtToken(selectedUser);
            } else {
                log.error(ACCOUNT_PASSWORD_INVALID_ERROR.getErrorMessage());
                throw new LBUAuthRuntimeException(ACCOUNT_PASSWORD_INVALID_ERROR.getErrorMessage(), ACCOUNT_PASSWORD_INVALID_ERROR.getErrorCode());
            }
        } else {
            return jwtService.generateJwtToken(selectedUser);
        }
    }

    @Override
    public User getUserByUserId(String userId, String authToken) {
        jwtService.validateAuthUser(userId, authToken);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(ACCOUNT_NOT_AVAILABLE_USER_ID_ERROR.getErrorMessage());
                    return new LBUAuthRuntimeException(
                            ACCOUNT_NOT_AVAILABLE_USER_ID_ERROR.getErrorMessage(),
                            ACCOUNT_NOT_AVAILABLE_USER_ID_ERROR.getErrorCode());
                });
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void activateAccount(String token) {
        Optional<AccountActivationDetails> activationDetails = activationDetailsRepository.findByToken(token);
        if (activationDetails.isPresent()) {
            AccountActivationDetails accountActivationDetails = activationDetails.get();
            Date currentDate = new Date(System.currentTimeMillis());
            long timeDifference = currentDate.getTime() - accountActivationDetails.getCreatedTimestamp().getTime();
            long hours = timeDifference / (1000 * 60 * 60);
            if (resendLimit > hours) {
                log.info("given token is valid for activation");
                Optional<User> optionalUser = userRepository.findById(accountActivationDetails.getUser().getId());
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    log.info("getting user details [{}]", user.getId());
                    if (!user.getEnabled()) {
                        user.setEnabled(Boolean.TRUE);
                        user.setAccountNonExpired(Boolean.TRUE);
                        user.setCredentialsNonExpired(Boolean.TRUE);
                        user.setAccountNonLocked(Boolean.TRUE);
                        userRepository.save(user);
                    } else {
                        log.error(ACCOUNT_ACTIVATED_ERROR.getErrorMessage());
                        throw new LBUAuthRuntimeException(ACCOUNT_ACTIVATED_ERROR.getErrorMessage(), ACCOUNT_ACTIVATED_ERROR.getErrorCode());
                    }
                } else {
                    log.error(ACCOUNT_NOT_AVAILABLE_USER_ID_ERROR.getErrorMessage());
                    throw new LBUAuthRuntimeException(ACCOUNT_NOT_AVAILABLE_USER_ID_ERROR.getErrorMessage(), ACCOUNT_NOT_AVAILABLE_USER_ID_ERROR.getErrorCode());
                }
                activationDetailsRepository.delete(accountActivationDetails);
                log.info("deleted the account activation token user id: [{}]", accountActivationDetails.getId());
            } else {
                log.error(ACCOUNT_ACTIVATION_TOKEN_EXPIRED.getErrorMessage());
                throw new LBUAuthRuntimeException(ACCOUNT_ACTIVATION_TOKEN_EXPIRED.getErrorMessage(), ACCOUNT_ACTIVATION_TOKEN_EXPIRED.getErrorCode());
            }
        } else {
            log.error(ACCOUNT_ACTIVATION_TOKEN_INVALID.getErrorMessage());
            throw new LBUAuthRuntimeException(ACCOUNT_ACTIVATION_TOKEN_INVALID.getErrorMessage(), ACCOUNT_ACTIVATION_TOKEN_INVALID.getErrorCode());
        }

    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public void reSendActivateToken(String userId) {
        Optional<AccountActivationDetails> activationDetails = activationDetailsRepository.findByUser_Id(userId);
        if (activationDetails.isPresent()) {
            AccountActivationDetails accountActivationDetails = activationDetails.get();
            Date currentDate = new Date(System.currentTimeMillis());
            long timeDifference = currentDate.getTime() - accountActivationDetails.getCreatedTimestamp().getTime();
            long hours = timeDifference / (1000 * 60 * 60);
            if (resendLimit >= hours) {
                Optional<User> userOptional = userRepository.findById(userId);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    emailService.sendOrResendActivationLink(userOptional.get());
                    log.info("successfully send an email to user [{}]", user.getUsername());
                } else {
                    log.error(ACCOUNT_NOT_AVAILABLE_USER_ID_ERROR.getErrorMessage());
                    throw new LBUAuthRuntimeException(ACCOUNT_NOT_AVAILABLE_USER_ID_ERROR.getErrorMessage(), ACCOUNT_NOT_AVAILABLE_USER_ID_ERROR.getErrorCode());
                }
            } else {
                log.error(ACCOUNT_ACTIVATION_OLD_TOKEN_VALID_ERROR.getErrorMessage());
                throw new LBUAuthRuntimeException(ACCOUNT_ACTIVATION_OLD_TOKEN_VALID_ERROR.getErrorMessage(), ACCOUNT_ACTIVATION_OLD_TOKEN_VALID_ERROR.getErrorCode());
            }
        } else {
            log.error(ACCOUNT_ACTIVATED_ERROR.getErrorMessage());
            throw new LBUAuthRuntimeException(ACCOUNT_ACTIVATED_ERROR.getErrorMessage(), ACCOUNT_ACTIVATED_ERROR.getErrorCode());
        }
    }

    @Override
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public User updateUserRole(String userId, String authToken) {
        jwtService.validateAuthUser(userId, authToken);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new LBUAuthRuntimeException(ACCOUNT_NOT_AVAILABLE_USER_ID_ERROR.getErrorMessage(), ACCOUNT_NOT_AVAILABLE_USER_ID_ERROR.getErrorCode());
        }
        User user = optionalUser.get();
        user.setRoleType(RoleType.STUDENT);
        return userRepository.saveAndFlush(user);
    }
}
