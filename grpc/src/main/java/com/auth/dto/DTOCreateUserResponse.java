package com.auth.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class DTOCreateUserResponse {
    UUID id;
    String name;
    String email;
}
