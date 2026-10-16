package org.metrunui.digibooky.service;

import org.metrunui.digibooky.domain.Address;
import org.metrunui.digibooky.domain.Member;
import org.metrunui.digibooky.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final UserRepository userRepository;

    public MemberService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Member registerMember(Member member) {
        if (userRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("A user with this email already exists");
        }
        if (userRepository.existsByInss(member.getInss())) {
            throw new IllegalArgumentException("A member with this INSS already exists");
        }
        userRepository.save(member);
        return member;
    }

    public List<Member> getAllMembers() {
        return userRepository.findAllMembers();
    }
}
