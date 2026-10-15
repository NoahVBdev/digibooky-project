package org.metrunui.digibooky.controllers.dtos;

import java.util.UUID;

public record BookResponseDTO(UUID bookId, String isbn, String title, String authorLastName, String authorFirstName) {
}
