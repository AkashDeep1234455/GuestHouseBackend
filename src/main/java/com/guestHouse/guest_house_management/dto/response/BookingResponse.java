package com.guestHouse.guest_house_management.dto.response;

import com.guestHouse.guest_house_management.entity.BookingStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private String guestName;
    private String guestPhone;
    private String guestEmail;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer numberOfDays;
    private BookingStatus bookingStatus;
    private Long roomId;
    private String roomNumber;
    private Long guestHouseId;
    private String guestHouseName;
}