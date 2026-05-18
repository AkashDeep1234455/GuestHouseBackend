package com.guestHouse.guest_house_management.controller;

import com.guestHouse.guest_house_management.dto.request.BookingRequest;
import com.guestHouse.guest_house_management.dto.response.BookingResponse;
import com.guestHouse.guest_house_management.service.BookingService;
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
public class BookingController {

    private final BookingService bookingService;

    // Allocate room
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request));
    }

    // All active bookings
    @GetMapping("/bookings/active")
    public ResponseEntity<List<BookingResponse>> getActiveBookings() {
        return ResponseEntity.ok(bookingService.getActiveBookings());
    }

    // Last 10 bookings for a specific room
    @GetMapping("/rooms/{roomId}/history")
    public ResponseEntity<List<BookingResponse>> getRoomHistory(
            @PathVariable Long roomId) {
        return ResponseEntity.ok(bookingService.getRoomHistory(roomId));
    }

    // Global last 10 past bookings
    @GetMapping("/bookings/history")
    public ResponseEntity<List<BookingResponse>> getGlobalHistory() {
        return ResponseEntity.ok(bookingService.getGlobalHistory());
    }

    // Cancel a booking
    @PatchMapping("/bookings/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    // Hard delete a booking
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
    // Get current occupant of a room
    @GetMapping("/rooms/{roomId}/current")
    public ResponseEntity<BookingResponse> getCurrentOccupant(
            @PathVariable Long roomId) {
        return ResponseEntity.ok(bookingService.getCurrentOccupant(roomId));
    }
}