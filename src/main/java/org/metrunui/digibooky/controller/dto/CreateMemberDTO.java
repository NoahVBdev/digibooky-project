package org.metrunui.digibooky.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateMemberDTO(
        @NotBlank(message = "INSS is required") String inss,
        String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @NotBlank(message = "Email is required")
        @Pattern(regexp = "^[A-Za-z0-9.]+@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)+$",
                message = "Email must be of the form x@x.x") String email,
        String streetName,
        String streetNumber,
        String postalCode,
        @NotBlank(message = "City is required") String city) {
}