package org.metrunui.digibooky.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

public record LendingDTO(UUID lendingId, UUID memberId, UUID bookId, LocalDate lendingDate, LocalDate dueDate) {
}