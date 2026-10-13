package org.metrunui.digibooky.repository;

import org.metrunui.digibooky.domain.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    Map<String, User> users;

    public UserRepository() {
        this.users = new ConcurrentHashMap<>();
    }
}
