package com.example.lbuauth.controllers.impl;

import com.example.lbuauth.controllers.UserController;
import com.example.lbuauth.dtos.MessageDto;
import com.example.lbuauth.dtos.UserDto;
import com.example.lbuauth.models.User;
import com.example.lbuauth.services.UserService;
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
     * Retrieves user data for the given user ID and authorization token.
     *
     * @param userId    The ID of the user to retrieve.
     * @param authToken The authorization token for accessing the user data.
     * @return ResponseEntity containing the user data.
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
     * Deletes the user associated with the provided user ID, using the given authorization token.
     *
     * @param userId    The ID of the user to be deleted.
     * @param authToken The authorization token for accessing the user data.
     * @return ResponseEntity indicating the result of the deletion operation.
     */
    @Override
    public ResponseEntity<MessageDto> deleteUser(String userId, String authToken) {
        return null; // Placeholder for the implementation of the delete operation.
    }

    /**
     * Updates the user data with the provided user information, using the given authorization token.
     *
     * @param userDto   The updated user data.
     * @param authToken The authorization token for accessing the user data.
     * @return ResponseEntity containing the updated user data.
     */
    @Override
    public ResponseEntity<UserDto> updateUser(UserDto userDto, String authToken) {
        return null; // Placeholder f

    }
}