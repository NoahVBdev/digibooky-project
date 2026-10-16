package org.metrunui.digibooky.controller;

import jakarta.validation.Valid;
import org.metrunui.digibooky.controller.dto.LendBookDTO;
import org.metrunui.digibooky.controller.dto.LendingDTO;
import org.metrunui.digibooky.controller.dto.ReturnLendingDTO;
import org.metrunui.digibooky.controller.mapper.LendingMapper;
import org.metrunui.digibooky.domain.Feature;
import org.metrunui.digibooky.domain.Lending;
import org.metrunui.digibooky.domain.User;
import org.metrunui.digibooky.service.AuthorizationService;
import org.metrunui.digibooky.service.LendingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/lendings")
public class LendingController {
    private final LendingService lendingService;
    private final LendingMapper lendingMapper;
    private final AuthorizationService authorizationService;

    public LendingController(LendingService lendingService, LendingMapper lendingMapper,
                             AuthorizationService authorizationService) {
        this.lendingService = lendingService;
        this.lendingMapper = lendingMapper;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/{lendingId}/return")
    @ResponseStatus(HttpStatus.OK)
    public ReturnLendingDTO returnBook(@RequestHeader(value = "X-User-Id", required = false) UUID userId,
                                       @PathVariable UUID lendingId) {
        User caller = authorizationService.authorize(userId, Feature.RETURN_BOOK);
        Lending lending = lendingService.returnBook(caller.getId(), lendingId);
        return lendingMapper.toReturnLendingDTO(lending);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LendingDTO lendBook(@RequestHeader(value = "X-User-Id", required = false) UUID userId,
                               @Valid @RequestBody LendBookDTO lendBookDTO) {
        User member = authorizationService.authorize(userId, Feature.LEND_BOOK);
        Lending lending = lendingService.lendBook(member.getId(), lendBookDTO.isbn());
        return lendingMapper.toLendingDTO(lending);
    }
}