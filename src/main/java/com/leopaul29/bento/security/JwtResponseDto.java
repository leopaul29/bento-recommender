package com.leopaul29.bento.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String role;
    private long expiresIn;
}
