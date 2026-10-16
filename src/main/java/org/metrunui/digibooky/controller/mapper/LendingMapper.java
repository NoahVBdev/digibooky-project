package org.metrunui.digibooky.controller.mapper;

import org.metrunui.digibooky.controller.dto.LendingDTO;
import org.metrunui.digibooky.domain.Lending;
import org.springframework.stereotype.Component;

@Component
public class LendingMapper {

    public LendingDTO toLendingDTO(Lending lending) {
        return new LendingDTO(lending.getId(), lending.getMemberId(), lending.getBookId(),
                lending.getLendingDate(), lending.getDueDate());
    }
}