package com.lbu.lbuauth.services.impl;

import com.lbu.lbuauth.commons.exceptions.LBUAuthRuntimeException;
import com.lbu.lbuauth.models.AccountActivationDetails;
import com.lbu.lbuauth.models.User;
import com.lbu.lbuauth.repositories.AccountActivationDetailsRepository;
import com.lbu.lbuauth.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static com.lbu.lbuauth.commons.constants.ErrorConstants.ACCOUNT_ACTIVATION_OLD_TOKEN_VALID_ERROR;
import static com.lbu.lbuauth.commons.constants.ErrorConstants.EMAIL_SEND_FAILED;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    @Value("${custom.properties.account.activation.link}")
    private String activationLinkPrefix;

    @Value("${custom.properties.account.activation.resend.hours}")
    private Long resendLimit;

    private final AccountActivationDetailsRepository activationDetailsRepository;

    /**
     * Initializes an EmailServiceImpl with the provided AccountActivationDetailsRepository.
     *
     * @param activationDetailsRepository The repository used to access account activation details.
     */
    public EmailServiceImpl(AccountActivationDetailsRepository activationDetailsRepository) {
        this.activationDetailsRepository = activationDetailsRepository;
    }

    /**
     * Sends or resends an activation link to the specified user. If an activation link exists and is still valid,
     * it checks whether the resend limit has been exceeded. If so, the existing activation details are deleted, and a new
     * activation link is sent. Otherwise, an exception is thrown indicating that the existing activation token is still valid.
     * If no activation details exist, a new activation link is sent.
     *
     * @param user The user to send the activation link to.
     */
    @Override
    public void sendOrResendActivationLink(User user) {
        Optional<AccountActivationDetails> activationDetails = activationDetailsRepository.findByUser_Username(user.getUsername());
        if (activationDetails.isPresent()) {
            AccountActivationDetails accountActivationDetails = activationDetails.get();
            Date currentDate = new Date(System.currentTimeMillis());
            long timeDifference = currentDate.getTime() - accountActivationDetails.getCreatedTimestamp().getTime();
            long hours = timeDifference / (1000 * 60 * 60);
            if (resendLimit <= hours) {
                activationDetailsRepository.delete(accountActivationDetails);
                sendActivation(user);
            } else {
                throw new LBUAuthRuntimeException(ACCOUNT_ACTIVATION_OLD_TOKEN_VALID_ERROR.getErrorMessage(), ACCOUNT_ACTIVATION_OLD_TOKEN_VALID_ERROR.getErrorCode());
            }
        } else {
            sendActivation(user);
        }
    }

    /**
     * Sends an activation email to the specified user. It generates a unique token, logs the email content, saves the
     * activation details, and sends the email. If any exception occurs during this process, it is caught and rethrown
     * as an LBUAuthRuntimeException.
     *
     * @param user The user to send the activation email to.
     */
    private void sendActivation(User user) {
        try {
            String token = UUID.randomUUID().toString();
            log.info("*************** Sending an Email ***************");
            log.info("email: {} content: \n {}", user.getEmail(), String.format(ACTIVATION_EMAIL_TMPL, user.getFirstName() + " " + user.getLastName(), activationLinkPrefix + token));
            log.info("*************** End an Email ***************");
            AccountActivationDetails activationDetails = new AccountActivationDetails();
            activationDetails.setUser(user);
            activationDetails.setToken(token);
            activationDetailsRepository.save(activationDetails);
        } catch (Exception e) {
            throw new LBUAuthRuntimeException(EMAIL_SEND_FAILED.getErrorMessage(), e, EMAIL_SEND_FAILED.getErrorCode());
        }
    }

}
