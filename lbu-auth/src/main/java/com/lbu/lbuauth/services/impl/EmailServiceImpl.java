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

    public EmailServiceImpl(AccountActivationDetailsRepository activationDetailsRepository) {
        this.activationDetailsRepository = activationDetailsRepository;
    }

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
