package com.auth.dto;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.auth.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern.Flag;
import lombok.Data;

@Data
public class DTOLoginRequest {
    @NotEmpty(message = "Email is required")
    @Email(message = "Email is invalid", flags = { Flag.CASE_INSENSITIVE })
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    /**
     * Validate the provided password against the stored password hash
     * 
     * @param user User entity object
     * @return true if the password is correct, false otherwise
     */
    public boolean validatePassword(User user) {
        BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
        return pwdEncoder.matches(this.password, user.getPassword());
    }
}
