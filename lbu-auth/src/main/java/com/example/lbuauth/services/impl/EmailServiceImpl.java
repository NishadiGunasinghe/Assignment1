package com.example.lbuauth.services.impl;

import com.example.lbuauth.commons.exceptions.LBUAuthRuntimeException;
import com.example.lbuauth.models.AccountActivationDetails;
import com.example.lbuauth.models.User;
import com.example.lbuauth.repositories.AccActivationDetailsRepository;
import com.example.lbuauth.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static com.example.lbuauth.commons.constants.ErrorConstants.ACCOUNT_ACTIVATION_OLD_TOKEN_VALID_ERROR;
import static com.example.lbuauth.commons.constants.ErrorConstants.EMAIL_SEND_FAILED;


@Slf4j
@Service
public class EmailServiceImpl implements EmailService{

    @Value("${custom.properties.account.activation.link}")
    private String activationLinkPrefix;

    @Value("${custom.properties.account.activation.resend.hours}")
    private Long resendLimit;

    private final AccActivationDetailsRepository activationDetailsRepository;

    public EmailServiceImpl(AccActivationDetailsRepository activationDetailsRepository) {
        this.activationDetailsRepository = activationDetailsRepository;
    }


    /**
     * Sends or resends the activation link to the user.
     * If an activation link already exists and has not expired, it will not resend the link.
     * If the activation link has expired or doesn't exist, a new activation link is generated and sent.
     *
     * @param user The user for whom the activation link is to be sent.
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
     * Sends an activation email to the user containing the activation link.
     * Generates a random token, creates an activation link, saves it in the database, and sends an email to the user.
     *
     * @param user The user for whom the activation email is to be sent.
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
