package org.metrunui.digibooky.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record LendBookDTO(@NotBlank(message = "ISBN is required") String isbn) {
}