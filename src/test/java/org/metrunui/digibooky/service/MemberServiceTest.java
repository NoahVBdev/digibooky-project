package org.metrunui.digibooky.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.metrunui.digibooky.domain.Address;
import org.metrunui.digibooky.domain.Member;
import org.metrunui.digibooky.domain.Role;
import org.metrunui.digibooky.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberServiceTest {

    private static final Address ADDRESS = new Address("Rue de la Loi", "16", "1000", "Brussels");

    private MemberService memberService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        memberService = new MemberService(userRepository);
    }

    @Test
    void whenRegisterMember_thenMemberIsCreated() {
        Member andreas = new Member("85073003328", "Andreas", "Lemoine", "andreas@digibooky.be", ADDRESS);

        Member result = memberService.registerMember(andreas);

        assertThat(userRepository.getById(result.getId())).contains(andreas);
        assertThat(result.getRole()).isEqualTo(Role.MEMBER);
    }

    @Test
    void registerMember_whenEmailAlreadyUsedByAnyUser_thenThrows() {
        Member sameEmailAsAdmin = new Member("85073003328", "Andreas", "Lemoine", "admin@digibooky.be", ADDRESS);

        assertThatThrownBy(() -> memberService.registerMember(sameEmailAsAdmin))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email already exists");
    }

    @Test
    void registerMember_whenInssAlreadyUsed_thenThrows() {
        Member sameInssAsDefaultMember = new Member("00000000097", "Andreas", "Lemoine", "andreas@digibooky.be", ADDRESS);

        assertThatThrownBy(() -> memberService.registerMember(sameInssAsDefaultMember))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("INSS already exists");
    }

    @Test
    void getAllMembers_returnsOnlyMembers() {
        Member andreas = memberService.registerMember(
                new Member("85073003328", "Andreas", "Lemoine", "andreas@digibooky.be", ADDRESS));

        assertThat(memberService.getAllMembers())
                .contains(andreas)
                .allSatisfy(member -> assertThat(member.getRole()).isEqualTo(Role.MEMBER));
    }




}