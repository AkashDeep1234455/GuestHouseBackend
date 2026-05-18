package com.guestHouse.guest_house_management.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GuestHouseResponse {
    private Long id;
    private String name;
    private Integer roomCount;
    private List<RoomResponse> rooms;
    private int totalRooms;
    private int availableRooms;
    private int occupiedRooms;
}