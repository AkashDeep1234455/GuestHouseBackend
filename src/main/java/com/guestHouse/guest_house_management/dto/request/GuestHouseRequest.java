package com.guestHouse.guest_house_management.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class GuestHouseRequest {

    @NotBlank(message = "Guest house name is required")
    private String name;

    // optional — no @NotNull
    @Min(value = 0, message = "Room count must be at least 1 if provided")
    @Max(value=200, message="Room count should be less than 200")
    private Integer roomCount;
}