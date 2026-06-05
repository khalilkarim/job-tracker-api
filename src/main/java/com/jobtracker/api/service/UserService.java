package com.jobtracker.api.service;

import com.jobtracker.api.exception.EmailAlreadyExistsException;
import com.jobtracker.api.exception.ResourceNotFoundException;
import com.jobtracker.api.model.User;
import com.jobtracker.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Boolean existsByEmail(String email) {
       return userRepository.existsByEmail(email);
    }

    public Boolean validateEmailNotTaken(String email) {
        if (existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already taken");
        }
        return false;
    }
}
