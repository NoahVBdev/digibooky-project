package org.metrunui.digibooky.controller;

import org.metrunui.digibooky.controller.mapper.LendingMapper;
import org.metrunui.digibooky.service.AuthorizationService;
import org.metrunui.digibooky.service.LendingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}