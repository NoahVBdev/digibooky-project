package org.metrunui.digibooky.repository;

import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.domain.Role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private final UserRepository userRepository = new UserRepository();

    @Test
    void newRepository_containsDefaultAdmin() {
        assertThat(userRepository.getById(UserRepository.DEFAULT_ADMIN_ID))
                .hasValueSatisfying(user -> assertThat(user.getRole()).isEqualTo(Role.ADMIN));
    }

    @Test
    void newRepository_containsTheThreeDefaultUsers() {
        assertThat(userRepository.findAll()).hasSize(3);
    }

    @Test
    void existsByEmail_whenEmailIsUsed_thenTrue() {
        assertThat(userRepository.existsByEmail("admin@digibooky.be")).isTrue();
    }

    @Test
    void existsByEmail_ignoresCase() {
        assertThat(userRepository.existsByEmail("ADMIN@digibooky.be")).isTrue();
    }

    @Test
    void existsByEmail_whenUnknown_thenFalse() {
        assertThat(userRepository.existsByEmail("nobody@digibooky.be")).isFalse();
    }

    @Test
    void existsByInss_whenInssIsUsed_thenTrue() {
        assertThat(userRepository.existsByInss("00000000097")).isTrue();
    }

    @Test
    void existsByInss_whenUnknown_thenFalse() {
        assertThat(userRepository.existsByInss("99999999999")).isFalse();
    }

}