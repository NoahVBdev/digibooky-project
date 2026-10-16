package org.metrunui.digibooky.controller.dto;

import org.metrunui.digibooky.domain.Role;
import java.util.UUID;

public record UserDTO(UUID id, String firstname, String lastname, String email, Role role) {
}