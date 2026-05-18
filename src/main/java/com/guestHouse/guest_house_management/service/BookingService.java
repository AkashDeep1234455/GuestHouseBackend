package com.guestHouse.guest_house_management.service;

import com.guestHouse.guest_house_management.dto.request.BookingRequest;
import com.guestHouse.guest_house_management.dto.response.BookingResponse;
import com.guestHouse.guest_house_management.entity.*;
import com.guestHouse.guest_house_management.exception.BadRequestException;
import com.guestHouse.guest_house_management.exception.ResourceNotFoundException;
import com.guestHouse.guest_house_management.repository.BookingRepository;
import com.guestHouse.guest_house_management.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    // Allocate a room
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {

        // Step 1 — find the room
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with id: " + request.getRoomId()));
        // inside createBooking(), after fetching room
        if (Boolean.TRUE.equals(room.getDeleted())) {
            throw new ResourceNotFoundException(
                    "Room not found with id: " + request.getRoomId());
        }

        // Step 2 — check if already occupied
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            throw new BadRequestException(
                    "Room " + room.getRoomNumber() + " is already occupied");
        }

        // Step 3 — calculate check-in and check-out automatically
        LocalDateTime checkIn = LocalDateTime.now();
        LocalDateTime checkOut = checkIn.plusDays(request.getNumberOfDays());

        // Step 4 — create booking
        Booking booking = Booking.builder()
                .guestName(request.getGuestName())
                .guestPhone(request.getGuestPhone())
                .guestEmail(request.getGuestEmail())   // null if not provided
                .checkIn(checkIn)
                .checkOut(checkOut)
                .numberOfDays(request.getNumberOfDays())
                .bookingStatus(BookingStatus.ACTIVE)
                .room(room)
                .build();

        Booking saved = bookingRepository.save(booking);

        // Step 5 — mark room as occupied
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        return mapToResponse(saved);
    }

    // Get all active bookings
    public List<BookingResponse> getActiveBookings() {
        return bookingRepository.findByBookingStatus(BookingStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get last 10 bookings for a specific room
    public List<BookingResponse> getRoomHistory(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found with id: " + roomId);
        }
        return bookingRepository.findTop10ByRoomIdOrderByCheckInDesc(roomId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get global last 10 completed/cancelled bookings
    public List<BookingResponse> getGlobalHistory() {
        return bookingRepository.findLast10CompletedBookings(PageRequest.of(0, 10))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Cancel a booking manually
    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId));

        if (booking.getBookingStatus() != BookingStatus.ACTIVE) {
            throw new BadRequestException("Only active bookings can be cancelled");
        }

        // Mark booking as cancelled
        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Free the room
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        return mapToResponse(booking);
    }

    // Hard delete a booking record
    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId));

        if (booking.getBookingStatus() == BookingStatus.ACTIVE) {
            throw new BadRequestException(
                    "Cannot delete an active booking. Cancel it first.");
        }

        bookingRepository.delete(booking);
    }
    // Get current active booking for a room (who is in the room right now)
    public BookingResponse getCurrentOccupant(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found with id: " + roomId);
        }
        Booking booking = bookingRepository.findByRoomIdAndBookingStatus(
                        roomId, BookingStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active booking found for room id: " + roomId));
        return mapToResponse(booking);
    }

    // Map entity to response DTO
    public BookingResponse mapToResponse(Booking booking) {
        Room room = booking.getRoom();
        return BookingResponse.builder()
                .id(booking.getId())
                .guestName(booking.getGuestName())
                .guestPhone(booking.getGuestPhone())
                .guestEmail(booking.getGuestEmail())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .numberOfDays(booking.getNumberOfDays())
                .bookingStatus(booking.getBookingStatus())
                .roomId(room.getId())
                .roomNumber(room.getRoomNumber())
                .guestHouseId(room.getGuestHouse().getId())
                .guestHouseName(room.getGuestHouse().getName())
                .build();
    }
}