package com.guestHouse.guest_house_management.controller;

import com.guestHouse.guest_house_management.dto.request.GuestHouseRequest;
import com.guestHouse.guest_house_management.dto.response.GuestHouseResponse;
import com.guestHouse.guest_house_management.service.GuestHouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guest-houses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GuestHouseController {

    private final GuestHouseService guestHouseService;

    @PostMapping
    public ResponseEntity<GuestHouseResponse> addGuestHouse(
            @Valid @RequestBody GuestHouseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(guestHouseService.addGuestHouse(request));
    }

    @GetMapping
    public ResponseEntity<List<GuestHouseResponse>> getAllGuestHouses() {
        return ResponseEntity.ok(guestHouseService.getAllGuestHouses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestHouseResponse> getGuestHouseById(@PathVariable Long id) {
        return ResponseEntity.ok(guestHouseService.getGuestHouseById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuestHouse(@PathVariable Long id) {
        guestHouseService.deleteGuestHouse(id);
        return ResponseEntity.noContent().build();
    }
}