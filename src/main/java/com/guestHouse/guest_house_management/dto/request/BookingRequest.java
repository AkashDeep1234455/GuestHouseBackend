package com.guestHouse.guest_house_management.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotBlank(message = "Guest name is required")
    private String guestName;

    @NotBlank(message = "Guest phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String guestPhone;

    @Email(message = "Invalid email format")
    private String guestEmail;     // optional — no @NotBlank

    @NotNull(message = "Number of days is required")
    @Min(value = 1, message = "Minimum stay is 1 day")
    @Max(value = 365, message = "Maximum stay is 365 days")
    private Integer numberOfDays;
}