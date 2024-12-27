package com.auth.repositories;

import java.util.UUID;

import com.auth.entities.User;

public interface UserRepository {
    public void createUser(User user);
    public void getUser(UUID id);
    User fincByEmail(String email);
}
        
