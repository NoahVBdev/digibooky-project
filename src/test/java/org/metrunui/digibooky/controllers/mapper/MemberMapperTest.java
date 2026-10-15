package org.metrunui.digibooky.controllers.mapper;

import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.controllers.dto.CreateMemberDTO;
import org.metrunui.digibooky.controllers.dto.MemberDTO;
import org.metrunui.digibooky.domain.Address;
import org.metrunui.digibooky.domain.Member;
import org.metrunui.digibooky.domain.Role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberMapperTest {

    private final MemberMapper memberMapper = new MemberMapper();

    @Test
    void toDomain_mapsAllFieldsAndRoleIsMember() {
        CreateMemberDTO dto = new CreateMemberDTO("85073003328", "Jane", "Doe", "jane@mail.be",
                "Rue de la Loi", "16", "1000", "Brussels");

        Member member = memberMapper.toMember(dto);

        assertThat(member.getInss()).isEqualTo("85073003328");
        assertThat(member.getLastname()).isEqualTo("Doe");
        assertThat(member.getEmail()).isEqualTo("jane@mail.be");
        assertThat(member.getAddress()).isEqualTo(new Address("Rue de la Loi", "16", "1000", "Brussels"));
        assertThat(member.getRole()).isEqualTo(Role.MEMBER);
    }

    @Test
    void toDto_copiesIdNameEmailAndAddress() {
        Member member = new Member("85073003328", "Jane", "Doe", "jane@mail.be",
                new Address("Rue de la Loi", "16", "1000", "Brussels"));

        MemberDTO dto = memberMapper.toMemberDTO(member);

        assertThat(dto).isEqualTo(new MemberDTO(member.getId(), "Jane", "Doe", "jane@mail.be",
                "Rue de la Loi", "16", "1000", "Brussels"));
    }
}