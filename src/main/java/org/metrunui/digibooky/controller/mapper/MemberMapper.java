package org.metrunui.digibooky.controller.mapper;

import org.metrunui.digibooky.controller.dto.CreateMemberDTO;
import org.metrunui.digibooky.controller.dto.MemberDTO;
import org.metrunui.digibooky.domain.Address;
import org.metrunui.digibooky.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public Member toMember(CreateMemberDTO dto) {
        return new Member(dto.inss(), dto.firstName(), dto.lastName(), dto.email(),
                new Address(dto.streetName(), dto.streetNumber(), dto.postalCode(), dto.city()));
    }

    public MemberDTO toMemberDTO(Member member) {
        Address address = member.getAddress();
        return new MemberDTO(member.getId(), member.getFirstname(), member.getLastname(), member.getEmail(),
                address.streetName(), address.streetNumber(), address.postalCode(), address.city());
    }
}