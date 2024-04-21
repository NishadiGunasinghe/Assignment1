package com.lbu.lbuauth.services.impl;

import com.lbu.lbuauth.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs a new UserDetailsServiceImpl with the provided UserRepository.
     *
     * @param userRepository The repository for accessing user data.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a UserDetails object by the given username.
     *
     * @param username The username of the user to load.
     * @return A UserDetails object representing the user with the given username.
     * @throws UsernameNotFoundException if the user with the given username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieves the user details from the UserRepository based on the provided username.
        return userRepository.findByUsername(username);
    }

}
