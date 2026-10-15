package org.metrunui.digibooky.domain;

import java.util.UUID;

public class Member extends User{
    private final String inss;
    private final Address address;


    public Member(String inss, String firstname, String lastname, String email, Address address) {
        super(firstname, lastname, email, Role.MEMBER);
        this.inss = inss;
        this.address = address;
    }

    public Member(UUID id, String inss, String firstname, String lastname, String email, Address address) {
        super(id, firstname, lastname, email, Role.MEMBER);
        this.inss = inss;
        this.address = address;
    }

    public String getInss() {
        return inss;
    }

    public Address getAddress() {
        return address;
    }
}
