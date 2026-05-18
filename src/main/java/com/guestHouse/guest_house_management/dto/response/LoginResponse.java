package com.guestHouse.guest_house_management.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String username;
    private String type = "Bearer";
    private long expiresIn;
}