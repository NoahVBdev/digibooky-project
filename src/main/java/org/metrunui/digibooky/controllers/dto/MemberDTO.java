package org.metrunui.digibooky.controllers.dto;

import java.util.UUID;

public record MemberDTO(UUID id, String firstName, String lastName, String email,
                        String streetName, String streetNumber, String postalCode, String city) {
}