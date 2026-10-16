package org.metrunui.digibooky.controller;

import jakarta.validation.Valid;
import org.metrunui.digibooky.controller.dto.CreateMemberDTO;
import org.metrunui.digibooky.controller.dto.MemberDTO;
import org.metrunui.digibooky.controller.mapper.MemberMapper;
import org.metrunui.digibooky.domain.Feature;
import org.metrunui.digibooky.domain.Member;
import org.metrunui.digibooky.domain.User;
import org.metrunui.digibooky.service.AuthorizationService;
import org.metrunui.digibooky.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final AuthorizationService authorizationService;

    public MemberController(MemberService memberService, MemberMapper memberMapper, AuthorizationService authorizationService) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberDTO registerMember(@Valid @RequestBody CreateMemberDTO createMemberDTO) {
        Member member = memberService.registerMember(memberMapper.toMember(createMemberDTO));
        return memberMapper.toMemberDTO(member);
    }

    @GetMapping
    public List<MemberDTO> getAllMembers(@RequestHeader(value = "X-User-id", required = false) UUID userId){
        User caller = authorizationService.getCallingUser(userId);
        authorizationService.requiredFeature(caller, Feature.VIEW_ALL_MEMBERS);

        return memberService.getAllMembers().stream().map(member -> memberMapper.toMemberDTO(member)).toList();
    }

}
