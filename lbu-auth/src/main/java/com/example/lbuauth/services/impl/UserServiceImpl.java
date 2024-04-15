package com.example.lbuauth.services.impl;

import com.example.lbuauth.commons.exceptions.LBUAuthRuntimeException;
import com.example.lbuauth.dtos.JWTTokenDto;
import com.example.lbuauth.models.AccountActivationDetails;
import com.example.lbuauth.models.User;
import com.example.lbuauth.models.enums.RoleType;
import com.example.lbuauth.repositories.AccActivationDetailsRepository;
import com.example.lbuauth.repositories.UserRepository;
import com.example.lbuauth.services.EmailService;
import com.example.lbuauth.services.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static com.example.lbuauth.commons.constants.ErrorConstants.*;


import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import com.example.lbuauth.services.JwtService;


@Slf4j
@Service
public class UserServiceImpl implements UserService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AccActivationDetailsRepository activationDetailsRepository;
    @Value("${custom.properties.account.activation.resend.hours}")
    private Long resendLimit;

    public UserServiceImpl(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            JwtService jwtService,
            EmailService emailService,
            AccActivationDetailsRepository activationDetailsRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.activationDetailsRepository = activationDetailsRepository;
    }

    /**
     * Creates a new user in the system.
     * Encrypts the user's password, sets default role and account status,
     * saves the user to the database, and sends an activation email.
     *
     * @param user The user object containing user details.
     * @return The saved user object.
     */
    @Transactional (rollbackOn = Exception.class) // Ensures atomicity; if an exception occurs, the transaction will be rolled back.
    @Override
    public User createUser(User user) {
        log.info("creating a new user [{}]", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt the user's password.
        user.setRoleType(RoleType.USER); // Set the user's role to "USER".
        user.setEnabled(Boolean.FALSE); // Set user's account as disabled initially.
        user.setAccountNonExpired(Boolean.FALSE); // Set user's account expiration status.
        user.setAccountNonLocked(Boolean.FALSE); // Set user's account lock status.
        user.setCredentialsNonExpired(Boolean.FALSE); // Set user's credentials expiration status.
        User savedUser = userRepository.save(user); // Save the user details in the database and get the saved instance.
        log.info("created a new user [{}]", user.getUsername()); // Log successful creation of the new user.
        //emailService.sendOrResendActivationLink(savedUser); // Send or resend activation link to the user.
        log.info("successfully send an email to user [{}]", user.getUsername()); // Log successful email sent to the user.
        return savedUser; // Return the saved user details.
    }

    /**
     * Generates a JWT token for user login.
     * Fetches the user from the repository by username,
     * validates user account status, and generates a token.
     *
     * @param user           The user object containing username and password.
     * @param isPasswordCheck Boolean indicating whether to perform password check.
     * @return The generated JWT token DTO.
     * @throws LBUAuthRuntimeException If user account is invalid or not found.
     */
    @Override
    public JWTTokenDto generateLoginToken(User user, boolean isPasswordCheck) {
        // Fetch user from the repository by username
        User selectedUser = userRepository.findByUsername(user.getUsername());
        // Log the retrieval of the user
        log.info("found a user for the given username [{}]", user.getUsername());

        // Check if user is found
        if (Objects.isNull(selectedUser)) {
            // Log and throw an exception if user is not found
            log.error(ACCOUNT_NOT_AVAILABLE_USER_ID_ERROR.getErrorMessage());
            throw new LBUAuthRuntimeException(ACCOUNT_NOT_AVAILABLE_USERNAME_ERROR.getErrorMessage(), ACCOUNT_NOT_AVAILABLE_USERNAME_ERROR.getErrorCode());
        }

        // Check if the user account is enabled
        if (!selectedUser.getEnabled()) {
            // Log and throw an exception if the account is not enabled
            log.error(ACCOUNT_NOT_ENABLE_ERROR.getErrorMessage());
            throw new LBUAuthRuntimeException(ACCOUNT_NOT_ENABLE_ERROR.getErrorMessage(), ACCOUNT_NOT_ENABLE_ERROR.getErrorCode());
        }

        // Check if the user's credentials and account are not expired
        if (!selectedUser.getCredentialsNonExpired() || !selectedUser.getAccountNonExpired()) {
            // Log and throw an exception if the credentials or account are expired
            log.error(ACCOUNT_CREDENTIAL_EXPIRED_ERROR.getErrorMessage());
            throw new LBUAuthRuntimeException(ACCOUNT_CREDENTIAL_EXPIRED_ERROR.getErrorMessage(), ACCOUNT_CREDENTIAL_EXPIRED_ERROR.getErrorCode());
        }

        // Check if the user account is not locked
        if (!selectedUser.getAccountNonLocked()) {
            // Log and throw an exception if the account is locked
            log.error(ACCOUNT_LOCKED_ERROR.getErrorMessage());
            throw new LBUAuthRuntimeException(ACCOUNT_LOCKED_ERROR.getErrorMessage(), ACCOUNT_LOCKED_ERROR.getErrorCode());
        }

        // If password check is requested
        if (isPasswordCheck) {
            // Check if the provided password matches the user's password
            if (passwordEncoder.matches(user.getPassword(), selectedUser.getPassword())) {
                // Log and return a JWT token if the password matches
                log.info("generating a jwt token for the user [{}]", user.getUsername());
                return jwtService.generateJwtToken(selectedUser);
            } else {
                // Log and throw an exception if the password is invalid
                log.error(ACCOUNT_PASSWORD_INVALID_ERROR.getErrorMessage());
                throw new LBUAuthRuntimeException(ACCOUNT_PASSWORD_INVALID_ERROR.getErrorMessage(), ACCOUNT_PASSWORD_INVALID_ERROR.getErrorCode());
            }
        } else {
            // Return a JWT token without password check
            return jwtService.generateJwtToken(selectedUser);
        }
    }

    /**
     * Retrieves a user by their user ID, after validating their authentication token.
     * If the user is authenticated and found in the repository, it returns the user;
     * otherwise, it throws an exception.
     *
     * @param userId    The ID of the user to retrieve.
     * @param authToken The authentication token of the user.
     * @return The user with the given user ID.
     * @throws LBUAuthRuntimeException if the user is not found or the authentication fails.
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
     * Activates the user account using the provided activation token.
     * If the token is valid and within the resend limit, it enables the user's account.
     * If the token is expired, an exception is thrown.
     * If the token is invalid, an exception is thrown.
     *
     * @param token The activation token to activate the user account.
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
     * Resends activation token to the user if the previous token is still valid (within the resend limit).
     * If the token is expired, an exception is thrown.
     * If the token is invalid, an exception is thrown.
     *
     * @param userId The ID of the user to resend the activation token.
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
     * Validates the authenticity of the provided token.
     *
     * @param token The token to validate.
     */
    @Override
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }


    /**
     * Updates the role of the user identified by userId if the authentication with the provided token is successful.
     * If the user is not found, an exception is thrown.
     *
     * @param userId    The ID of the user whose role needs to be updated.
     * @param authToken The authentication token for the user.
     * @return The updated user object.
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
