package org.metrunui.digibooky.controllers.dtos;

import jakarta.validation.constraints.NotBlank;

public record CreateBookDTO(
        @NotBlank(message = "isbn is required") String isbn,
        @NotBlank(message = "title is required") String title,
        String summary,
        @NotBlank(message = "author last name is required") String authorLastName,
        @NotBlank(message = "author first name is required") String authorFirstName) {
}
