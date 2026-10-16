package org.metrunui.digibooky.controller.mapper;

import org.metrunui.digibooky.controller.dto.LendingDTO;
import org.metrunui.digibooky.controller.dto.ReturnLendingDTO;
import org.metrunui.digibooky.domain.Lending;
import org.springframework.stereotype.Component;

@Component
public class LendingMapper {

    public ReturnLendingDTO toReturnLendingDTO(Lending lending) {
        boolean late = lending.isReturnedLate();
        String message = late
                ? "Book returned late. It was due on " + lending.getDueDate() + "."
                : "Book returned on time.";

        return new ReturnLendingDTO(
                lending.getId(),
                lending.getBookId(),
                lending.getDueDate(),
                lending.getReturnDate(),
                late,
                message
        );
    }

    public LendingDTO toLendingDTO(Lending lending) {
        return new LendingDTO(lending.getId(), lending.getMemberId(), lending.getBookId(),
                lending.getLendingDate(), lending.getDueDate());
    }
}