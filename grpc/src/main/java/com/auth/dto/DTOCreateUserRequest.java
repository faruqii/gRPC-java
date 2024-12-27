package com.auth.dto;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.auth.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Pattern.Flag;
import lombok.Data;

@Data
public class DTOCreateUserRequest {
    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email is invalid", flags = { Flag.CASE_INSENSITIVE })
    private String email;

    @NotEmpty(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*_=+-]).{8,}$", message = "Password must contain at least 8 characters, one digit, one lowercase, one uppercase, and one special character")
    private String password;

    public User toUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(name);
        user.setEmail(email);
        user.setPassword(hashPassword(password));

        return user;
    }

    private static String hashPassword(String password) {
        // Hash password
        BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
        return pwdEncoder.encode(password);
    }
}
