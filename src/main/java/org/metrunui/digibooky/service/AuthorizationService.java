package org.metrunui.digibooky.service;


import org.metrunui.digibooky.exception.ForbiddenException;
import org.metrunui.digibooky.exception.UnauthorizedException;
import org.metrunui.digibooky.domain.Feature;
import org.metrunui.digibooky.domain.User;
import org.metrunui.digibooky.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorizationService {

    private final UserRepository userRepository;
    private static final Logger logs = LoggerFactory.getLogger(AuthorizationService.class);

    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void requiredFeature(User user, Feature feature) throws ForbiddenException {

        if (!user.getRole().hasFeature(feature)){
            throw new ForbiddenException("Not allowed");
        }
    }

    public User getCallingUser(UUID id) throws UnauthorizedException {
        if (id == null) {
            logs.warn("Request received without a user id");
            throw new UnauthorizedException("Must provide a user id");
        }
        return userRepository.getById(id).orElseThrow(() -> {
            logs.warn("Request received with unknown user id {}", id);
            return new UnauthorizedException("Unknown user id");
        });
    }

    public User authorize(UUID userId, Feature feature) {
        User caller = getCallingUser(userId);
        requiredFeature(caller, feature);
        return caller;
    }


}