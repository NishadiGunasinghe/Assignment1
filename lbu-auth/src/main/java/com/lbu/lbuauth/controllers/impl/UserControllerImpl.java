package com.lbu.lbuauth.controllers.impl;

import com.lbu.lbuauth.controllers.UserController;
import com.lbu.lbuauth.dtos.MessageDto;
import com.lbu.lbuauth.dtos.UserDto;
import com.lbu.lbuauth.models.User;
import com.lbu.lbuauth.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserControllerImpl(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<UserDto> createUser(UserDto userDto) {
        User savedUser = userService.createUser(modelMapper.map(userDto, User.class));
        UserDto createdUserDto = modelMapper.map(savedUser, UserDto.class);
        createdUserDto.setPassword(null);
        return ResponseEntity.ok(createdUserDto);
    }

    @Override
    public ResponseEntity<UserDto> getUser(String userId, String authToken) {
        User user = userService.getUserByUserId(userId, authToken);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setUserName(user.getUsername());
        userDto.setPassword(null);
        return ResponseEntity.ok(userDto);
    }

    @Override
    public ResponseEntity<MessageDto> deleteUser(String userId, String authToken) {
        return null;
    }

    @Override
    public ResponseEntity<UserDto> updateUser(UserDto userDto, String authToken) {
        return null;
    }
}
