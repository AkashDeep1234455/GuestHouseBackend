package com.guestHouse.guest_house_management.controller;

import com.guestHouse.guest_house_management.dto.request.RoomRequest;
import com.guestHouse.guest_house_management.dto.response.RoomResponse;
import com.guestHouse.guest_house_management.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/guest-houses/{guestHouseId}/rooms")
    public ResponseEntity<RoomResponse> addRoom(
            @PathVariable Long guestHouseId,
            @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.addRoom(guestHouseId, request));
    }

    @GetMapping("/guest-houses/{guestHouseId}/rooms")
    public ResponseEntity<List<RoomResponse>> getRoomsByGuestHouse(
            @PathVariable Long guestHouseId) {
        return ResponseEntity.ok(roomService.getRoomsByGuestHouse(guestHouseId));
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PatchMapping("/rooms/{id}/clear")
    public ResponseEntity<RoomResponse> clearRoom(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.clearRoom(id));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}