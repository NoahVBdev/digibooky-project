package org.metrunui.digibooky.domain;

import java.util.UUID;

public class User {

    private final UUID id;
    private String firstname;
    private String lastname;
    private String email;
    private Role role;

    public User(String firstname, String lastname, String email, Role role) {
        this(UUID.randomUUID(), firstname, lastname, email, role);
    }

    public User(UUID id, String firstname, String lastname, String email, Role role) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
    }


    public UUID getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}