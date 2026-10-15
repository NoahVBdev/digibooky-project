package org.metrunui.digibooky.controllers.dtos;

import java.util.UUID;

public record BookDetailResponseDTO(UUID bookId, String isbn, String title, String summary, String authorLastName, String authorFirstName) {
}
