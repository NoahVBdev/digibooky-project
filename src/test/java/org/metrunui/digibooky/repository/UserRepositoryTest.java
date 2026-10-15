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

}