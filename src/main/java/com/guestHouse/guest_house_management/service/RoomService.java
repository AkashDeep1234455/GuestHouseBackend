package com.guestHouse.guest_house_management.service;

import com.guestHouse.guest_house_management.dto.request.RoomRequest;
import com.guestHouse.guest_house_management.dto.response.RoomResponse;
import com.guestHouse.guest_house_management.entity.*;
import com.guestHouse.guest_house_management.exception.BadRequestException;
import com.guestHouse.guest_house_management.exception.ResourceNotFoundException;
import com.guestHouse.guest_house_management.repository.BookingRepository;
import com.guestHouse.guest_house_management.repository.GuestHouseRepository;
import com.guestHouse.guest_house_management.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final GuestHouseRepository guestHouseRepository;
    private final BookingRepository bookingRepository;

    // Add room to a guest house
    public RoomResponse addRoom(Long guestHouseId, RoomRequest request) {
        GuestHouse guestHouse = guestHouseRepository.findById(guestHouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Guest house not found with id: " + guestHouseId));

        if (Boolean.TRUE.equals(guestHouse.getDeleted())) {
            throw new ResourceNotFoundException(
                    "Guest house not found with id: " + guestHouseId);
        }

        boolean exists = roomRepository.findByGuestHouseIdAndDeletedFalse(guestHouseId)
                .stream()
                .anyMatch(r -> r.getRoomNumber().equals(request.getRoomNumber()));

        if (exists) {
            throw new BadRequestException("Room number " + request.getRoomNumber()
                    + " already exists in this guest house");
        }

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .status(RoomStatus.AVAILABLE)
                .deleted(false)
                .guestHouse(guestHouse)
                .build();

        Room saved = roomRepository.save(room);
        return mapToResponse(saved);
    }

    // Get all rooms in a guest house — only non deleted
    public List<RoomResponse> getRoomsByGuestHouse(Long guestHouseId) {
        if (!guestHouseRepository.existsById(guestHouseId)) {
            throw new ResourceNotFoundException(
                    "Guest house not found with id: " + guestHouseId);
        }
        return roomRepository.findByGuestHouseIdAndDeletedFalse(guestHouseId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get single room
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with id: " + id));
        return mapToResponse(room);
    }

    // Manually clear a room
    public RoomResponse clearRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with id: " + id));

        if (room.getStatus() == RoomStatus.AVAILABLE) {
            throw new BadRequestException("Room is already available");
        }

        bookingRepository.findByRoomIdAndBookingStatus(id, BookingStatus.ACTIVE)
                .ifPresent(booking -> {
                    booking.setBookingStatus(BookingStatus.COMPLETED);
                    bookingRepository.save(booking);
                });

        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
        return mapToResponse(room);
    }

    // Soft delete a room
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with id: " + id));

        if (room.getStatus() == RoomStatus.OCCUPIED) {
            throw new BadRequestException(
                    "Cannot delete an occupied room. Clear it first.");
        }

        // double check no active booking exists
        bookingRepository.findByRoomIdAndBookingStatus(id, BookingStatus.ACTIVE)
                .ifPresent(b -> { throw new BadRequestException(
                        "Room has an active booking. Cancel it first."); });

        room.setDeleted(true);
        room.setDeletedAt(LocalDateTime.now());
        roomRepository.save(room);
    }

    public RoomResponse mapToResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .status(room.getStatus())
                .guestHouseId(room.getGuestHouse().getId())
                .guestHouseName(room.getGuestHouse().getName())
                .build();
    }
}