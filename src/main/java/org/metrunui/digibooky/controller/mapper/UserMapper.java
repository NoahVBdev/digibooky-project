package org.metrunui.digibooky.controllers.mapper;

import org.metrunui.digibooky.controllers.dto.CreateUserDTO;
import org.metrunui.digibooky.controllers.dto.UserDTO;
import org.metrunui.digibooky.domain.Role;
import org.metrunui.digibooky.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(CreateUserDTO dto, Role role){
        return new User(dto.firstname(), dto.lastname(), dto.email(), role);

    }

    public UserDTO toUserDTO(User user){
        return new UserDTO(user.getId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getRole());

    }
}
