package org.metrunui.digibooky.controllers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(
        @NotBlank String firstname,
        @NotBlank String lastname,
        @NotBlank @Email String email) {
}