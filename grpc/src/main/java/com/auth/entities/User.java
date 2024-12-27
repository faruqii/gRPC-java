package com.auth.entities;

import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class User {
    private UUID id;
    private String name;
    private String email;
    private String password;
}
