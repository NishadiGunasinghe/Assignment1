package com.lbu.lbuauth.controllers.impl;

import com.lbu.lbuauth.commons.exceptions.LBUAuthRuntimeException;
import com.lbu.lbuauth.controllers.UserLoginController;
import com.lbu.lbuauth.dtos.JWTTokenDto;
import com.lbu.lbuauth.dtos.LoginDto;
import com.lbu.lbuauth.dtos.MessageDto;
import com.lbu.lbuauth.models.User;
import com.lbu.lbuauth.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import static com.lbu.lbuauth.commons.constants.ErrorConstants.INVALID_CREDENTIALS;
import static com.lbu.lbuauth.commons.constants.SuccessConstants.*;

@Slf4j
@RestController
public class UserLoginControllerImpl implements UserLoginController {


    private final UserService userService;

    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;

    public UserLoginControllerImpl(UserService userService, ModelMapper modelMapper, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public ResponseEntity<JWTTokenDto> login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUserName(), loginDto.getPassword()));
        if (authentication.isAuthenticated()) {
            log.info("user successfully authenticated {}", loginDto.getUserName());
            return ResponseEntity.ok(userService.generateLoginToken(modelMapper.map(loginDto, User.class), true));
        } else {
            log.error(INVALID_CREDENTIALS.getErrorMessage());
            throw new LBUAuthRuntimeException(INVALID_CREDENTIALS.getErrorMessage(), INVALID_CREDENTIALS.getErrorCode());
        }
    }

    @Override
    public ResponseEntity<MessageDto> reSendActivateUserAccount(String userId) {
        log.info("resend account activation for user [{}]", userId);
        userService.reSendActivateToken(userId);
        MessageDto messageDto = new MessageDto();
        messageDto.setMessage(ACCOUNT_SEND_ACTIVATION.getMessage());
        messageDto.setCode(ACCOUNT_SEND_ACTIVATION.getCode());
        log.info("successfully resend account activation [{}]", userId);
        return ResponseEntity.ok(messageDto);
    }

    @Override
    public ResponseEntity<JWTTokenDto> updateUserRole(String userId, String authToken) {
        log.info("Updating the user into STUDENT {}", userId);
        return ResponseEntity.ok(userService.generateLoginToken(userService.updateUserRole(userId, authToken), false));
    }

    @Override
    public ResponseEntity<MessageDto> activateUserAccount(String token) {
        log.info("sending token for activation [{}]", token);
        userService.activateAccount(token);
        MessageDto messageDto = new MessageDto();
        messageDto.setMessage(ACCOUNT_ACTIVATED.getMessage());
        messageDto.setCode(ACCOUNT_ACTIVATED.getCode());
        log.info("account successfully activated [{}]", token);
        return ResponseEntity.ok(messageDto);
    }

    @Override
    public ResponseEntity<MessageDto> validateToken(JWTTokenDto jwtTokenDto) {
        MessageDto messageDto = new MessageDto();
        userService.validateToken(jwtTokenDto.getJwtToken());
        messageDto.setMessage(JWT_VALID_TOKEN.getMessage());
        messageDto.setCode(JWT_VALID_TOKEN.getCode());
        return ResponseEntity.ok(messageDto);
    }
}
