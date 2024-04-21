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

    /**
     * Constructor for UserServiceImpl class.
     * Initializes the UserServiceImpl with necessary dependencies.
     *
     * @param passwordEncoder               Password encoder for encoding passwords.
     * @param userRepository                Repository for User entities.
     * @param jwtService                    Service for JWT token management.
     * @param emailService                  Service for sending emails.
     * @param activationDetailsRepository   Repository for AccountActivationDetails entities.
     */

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

    /**
     * Creates a new user.
     * Validates the content before saving.
     * Sets default values for user roles and enables.
     * Saves the user to the database.
     * Sends an activation link to the user's email.
     *
     * @param user The user to be created.
     * @return The saved user.
     */

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

    /**
     * Generates a JWT token for user login.
     * Checks if the user exists and is enabled.
     * Validates the password if required.
     * Generates a JWT token for the user.
     *
     * @param user           The user attempting to login.
     * @param isPasswordCheck Whether to validate the user's password.
     * @return A DTO containing the generated JWT token.
     */

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

    /**
     * Retrieves a user by user ID.
     * Validates the authenticity of the user through JWT token.
     *
     * @param userId    The ID of the user to retrieve.
     * @param authToken JWT token for user authentication.
     * @return The user corresponding to the given user ID.
     * @throws LBUAuthRuntimeException If the user is not found.
     */

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

    /**
     * Activates a user account using the activation token.
     * Deletes the activation token after successful activation.
     *
     * @param token The activation token.
     * @throws LBUAuthRuntimeException If the activation token is invalid or expired.
     */

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

    /**
     * Resends the activation token to a user for account activation.
     * Checks if the previous token is still valid for resending.
     *
     * @param userId The ID of the user to resend the activation token.
     * @throws LBUAuthRuntimeException If the previous token is expired or invalid.
     */

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

    /**
     * Validates the authenticity of a JWT token.
     *
     * @param token The JWT token to validate.
     */

    @Override
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    /**
     * Updates the role of a user to STUDENT.
     * Validates the authenticity of the user through JWT token.
     *
     * @param userId    The ID of the user to update the role.
     * @param authToken JWT token for user authentication.
     * @return The updated user with the new role.
     * @throws LBUAuthRuntimeException If the user is not found.
     */

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
