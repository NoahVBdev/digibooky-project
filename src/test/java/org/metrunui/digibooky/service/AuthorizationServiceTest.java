package org.metrunui.digibooky.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.exception.ForbiddenException;
import org.metrunui.digibooky.exception.UnauthorizedException;
import org.metrunui.digibooky.domain.Feature;
import org.metrunui.digibooky.domain.Role;
import org.metrunui.digibooky.domain.User;
import org.metrunui.digibooky.repository.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorizationServiceTest {

    private UserRepository userRepository;
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        authorizationService = new AuthorizationService(userRepository);
    }

    // ---------- getCallingUser: who are you? ----------

    @Test
    void getCallingUser_whenIdExists_thenReturnsUser() throws UnauthorizedException {
        User admin = userRepository.save(new User("Vincent", "Admin", "vincent@digibooky.be", Role.ADMIN));

        User result = authorizationService.getCallingUser(admin.getId());

        assertThat(result).isEqualTo(admin);
    }

    @Test
    void getCallingUser_whenIdIsUnknown_thenThrowsUnauthorized() {
        assertThatThrownBy(() -> authorizationService.getCallingUser(UUID.randomUUID()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void getCallingUser_whenIdIsNull_thenThrowsUnauthorized() {
        assertThatThrownBy(() -> authorizationService.getCallingUser(null))
                .isInstanceOf(UnauthorizedException.class);
    }

    // ---------- requiredFeature: are you allowed? ----------

    @Test
    void requiredFeature_whenRoleHasFeature_thenDoesNotThrow() {
        User librarian = new User("Andreas", "Librarian", "andreas@digibooky.be", Role.LIBRARIAN);

        assertThatCode(() -> authorizationService.requiredFeature(librarian, Feature.REGISTER_BOOK))
                .doesNotThrowAnyException();
    }

    @Test
    void requiredFeature_whenRoleLacksFeature_thenThrowsForbidden() {
        User member = new User("Lucas", "Member", "lucas@digibooky.be", Role.MEMBER);

        assertThatThrownBy(() -> authorizationService.requiredFeature(member, Feature.REGISTER_BOOK))
                .isInstanceOf(ForbiddenException.class);
    }
}