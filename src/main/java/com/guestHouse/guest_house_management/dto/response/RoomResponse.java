package com.guestHouse.guest_house_management.dto.response;

import com.guestHouse.guest_house_management.entity.RoomStatus;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private RoomStatus status;
    private Long guestHouseId;
    private String guestHouseName;
}