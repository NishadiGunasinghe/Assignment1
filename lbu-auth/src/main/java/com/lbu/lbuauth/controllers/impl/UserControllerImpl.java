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
        // Initializes UserControllerImpl with UserService and ModelMapper instances.
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    /**
     * Creates a new user using the provided user data.
     *
     * @param userDto The user data to create the user.
     * @return ResponseEntity containing the created user data.
     */
    @Override
    public ResponseEntity<UserDto> createUser(UserDto userDto) {
        User savedUser = userService.createUser(modelMapper.map(userDto, User.class));
        UserDto createdUserDto = modelMapper.map(savedUser, UserDto.class);
        createdUserDto.setPassword(null);
        return ResponseEntity.ok(createdUserDto);
    }

    /**
     * Retrieves user data based on the provided user ID and authentication token.
     *
     * @param userId     The ID of the user to retrieve.
     * @param authToken  The authentication token for authorization.
     * @return ResponseEntity containing the user data corresponding to the provided ID.
     */
    @Override
    public ResponseEntity<UserDto> getUser(String userId, String authToken) {
        User user = userService.getUserByUserId(userId, authToken);
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setUserName(user.getUsername());
        userDto.setPassword(null);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Deletes the user corresponding to the provided user ID and authentication token.
     *
     * @param userId     The ID of the user to delete.
     * @param authToken  The authentication token for authorization.
     * @return ResponseEntity indicating the success or failure of the operation.
     */
    @Override
    public ResponseEntity<MessageDto> deleteUser(String userId, String authToken) {
        return null;
    }

    /**
     * Updates user data with the provided user data and authentication token.
     *
     * @param userDto    The updated user data.
     * @param authToken  The authentication token for authorization.
     * @return ResponseEntity containing the updated user data.
     */
    @Override
    public ResponseEntity<UserDto> updateUser(UserDto userDto, String authToken) {
        return null;
    }

}
