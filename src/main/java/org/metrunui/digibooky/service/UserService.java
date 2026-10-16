package org.metrunui.digibooky.service;

import org.metrunui.digibooky.domain.User;
import org.metrunui.digibooky.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerLibrarian(User librarian){
        if (userRepository.existsByEmail(librarian.getEmail())) {
            throw new IllegalArgumentException("A user with this email already exists");
        }

        return userRepository.save(librarian);
    }
}
