package org.metrunui.digibooky.repository;

import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.domain.Address;
import org.metrunui.digibooky.domain.Member;
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

    @Test
    void findAllMembers_containsDefaultMember(){
        assertThat(userRepository.findAllMembers())
                .extracting(member -> member.getId())
                .contains(UserRepository.DEFAULT_MEMBER_ID);
    }

    @Test
    void findAllMembers_doesNotContainAdminOrLibrarian(){
        assertThat(userRepository.findAllMembers())
                .extracting(member -> member.getId())
                .doesNotContain(UserRepository.DEFAULT_ADMIN_ID, UserRepository.DEFAULT_LIBRARIAN_ID);
    }

    @Test
    void findAllMembers_containsNewSavedMember(){
        Member jane = new Member("85073003328", "Jane", "Doe", "jane@mail.be",
                new Address("Rue de la Loi", "16", "1000", "Brussels"));
        userRepository.save(jane);
        assertThat(userRepository.findAllMembers()).contains(jane);

    }

}