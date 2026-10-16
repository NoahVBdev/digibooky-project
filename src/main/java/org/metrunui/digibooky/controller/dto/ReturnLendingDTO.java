package org.metrunui.digibooky.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ReturnLendingDTO(
        UUID lendingId,
        UUID bookId,
        LocalDate dueDate,
        LocalDate returnDate,
        boolean late,
        String message
) {
}