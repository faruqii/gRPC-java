package com.auth.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class DTOLoginResponse {
    UUID id;
    String name;
    String email;
}
