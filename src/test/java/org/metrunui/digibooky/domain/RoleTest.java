package org.metrunui.digibooky.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void librarian_canRegisterBooks() {
        assertThat(Role.LIBRARIAN.hasFeature(Feature.REGISTER_BOOK)).isTrue();
    }

    @Test
    void member_cannotRegisterBooks() {
        assertThat(Role.MEMBER.hasFeature(Feature.REGISTER_BOOK)).isFalse();
    }

    @Test
    void admin_canViewAllMembers() {
        assertThat(Role.ADMIN.hasFeature(Feature.VIEW_ALL_MEMBERS)).isTrue();
    }
}