package org.metrunui.digibooky.controller.dto;

import java.util.UUID;

public record BookResponseDTO(UUID bookId, String isbn, String title, String authorLastName, String authorFirstName) {
}
