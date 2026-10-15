package org.metrunui.digibooky.controllers;

import jakarta.validation.Valid;
import org.metrunui.digibooky.controllers.dto.CreateMemberDTO;
import org.metrunui.digibooky.controllers.dto.MemberDTO;
import org.metrunui.digibooky.controllers.mapper.MemberMapper;
import org.metrunui.digibooky.domain.Member;
import org.metrunui.digibooky.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper memberMapper;

    public MemberController(MemberService memberService, MemberMapper memberMapper) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberDTO registerMember(@Valid @RequestBody CreateMemberDTO createMemberDTO) {
        Member member = memberService.registerMember(memberMapper.toMember(createMemberDTO));
        return memberMapper.toMemberDTO(member);
    }

}
