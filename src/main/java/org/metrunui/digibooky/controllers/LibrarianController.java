package org.metrunui.digibooky.controllers;

import jakarta.validation.Valid;
import org.metrunui.digibooky.controllers.dto.CreateUserDTO;
import org.metrunui.digibooky.controllers.dto.UserDTO;
import org.metrunui.digibooky.controllers.mapper.UserMapper;
import org.metrunui.digibooky.domain.Feature;
import org.metrunui.digibooky.domain.Role;
import org.metrunui.digibooky.domain.User;
import org.metrunui.digibooky.service.AuthorizationService;
import org.metrunui.digibooky.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/librarians")
public class LibrarianController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthorizationService authorizationService;

    public LibrarianController(UserService userService, UserMapper userMapper, AuthorizationService authorizationService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerLibrarian(@RequestHeader(value = "X-User-Id", required = false) UUID id,
                                     @Valid @RequestBody CreateUserDTO createUserDTO) {


        authorizationService.authorize(id, Feature.REGISTER_LIBRARIAN);
        User librarian = userService.registerLibrarian(userMapper.toUser(createUserDTO, Role.LIBRARIAN));
        return userMapper.toUserDTO(librarian);
    }
}