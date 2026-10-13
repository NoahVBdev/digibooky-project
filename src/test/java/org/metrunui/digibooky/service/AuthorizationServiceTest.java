package org.metrunui.digibooky.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.domain.Role;
import org.metrunui.digibooky.domain.User;
import org.metrunui.digibooky.repository.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AuthorizationServiceTest {

    private UserRepository userRepository;
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp(){
        userRepository = new UserRepository();
        authorizationService = new AuthorizationService(userRepository);
    }

    @Test
    void requireRole_whenUserHasRequiredRole_thenReturnsUser(){
        User admin = userRepository.save(new User("Vincent", "Admin", "vincent@digibooky.be", Role.ADMIN);

        User result = authorizationService.requiredRole(admin.getId(), Role.ADMIN);

        assertThat(result).isEqualTo(admin);
    }

    @Test
    void requireRole_whenUserHasOneOfSeveralAllowedRoles_thenReturnsUser(){
        User librarian = userRepository.save(new User("Andreas", "Librarian", "andreas@digibooky.be", Role.LIBRARIAN);

        User result = authorizationService.requiredRole(librarian.getId(), Role.ADMIN, Role.LIBRARIAN);

        assertThat(result).isEqualTo(librarian);
    }

    @Test
    void requireRole_whenUserHasAnotherRole_thenThrowsForbidden(){
        User member = userRepository.save(new User("Lucas", "Member", "lucas@digibooky.be", Role.MEMBER);

        assertThatThrownBy(() -> authorizationService.requireRole(member.getId(), Role.ADMIN))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void requireRole_whenUserIdIsUnknown_thenThrowsUnauthorized(){
        assertThatThrownBy(() -> authorizationService.requiredRole(UUID.randomUUID(), Role.ADMIN))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void requireRole_whenUserIdIsMissing_thenThrowsUnauthorized(){
        assertThatThrownBy(() -> authorizationService.requiredRole(null, Role.ADMIN))
                .isInstanceOf(UnauthorizedException.class);
    }
}
