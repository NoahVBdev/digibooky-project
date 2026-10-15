package org.metrunui.digibooky.repository;

import org.metrunui.digibooky.domain.Role;
import org.metrunui.digibooky.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {

    Map<UUID, User> users = new ConcurrentHashMap<>();
    public static final UUID DEFAULT_ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID DEFAULT_LIBRARIAN_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    public static final UUID DEFAULT_MEMBER_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    public UserRepository() {
        save(new User(DEFAULT_ADMIN_ID, "Default", "Admin", "admin@digibooky.be", Role.ADMIN));
        save(new User(DEFAULT_LIBRARIAN_ID, "Default", "Librarian", "librarian@digibooky.be", Role.LIBRARIAN));
        save(new User(DEFAULT_MEMBER_ID, "Default", "Member", "member@digibooky.be", Role.MEMBER));
    }

//    public UserRepository() {
//        this.users = new ConcurrentHashMap<>();
//    }

    public Optional<User> getById(UUID id){
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll(){
        return List.copyOf(users.values());
    }

    public User save(User user){
        users.put(user.getId(), user);
        return user;
    }
}