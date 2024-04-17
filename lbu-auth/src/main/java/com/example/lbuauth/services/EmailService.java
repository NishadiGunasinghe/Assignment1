package com.example.lbuauth.services;

import com.example.lbuauth.models.User;

public interface EmailService {

    String ACTIVATION_EMAIL_TMPL = """
            Subject: Activate Your Account
                            
            Dear %s,
                            
            Welcome to our platform! We're excited to have you join our community.
                            
            To activate your account and start enjoying our services, please click on the link below:
                            
            %s
                            
            Thank you for choosing us. We look forward to serving you!
                            
            Best regards,
            LBU Authentication
            """;

    void sendOrResendActivationLink(User user);
}
