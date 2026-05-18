package com.guestHouse.guest_house_management.service;

import com.guestHouse.guest_house_management.dto.request.GuestHouseRequest;
import com.guestHouse.guest_house_management.dto.response.GuestHouseResponse;
import com.guestHouse.guest_house_management.dto.response.RoomResponse;
import com.guestHouse.guest_house_management.entity.*;
import com.guestHouse.guest_house_management.exception.BadRequestException;
import com.guestHouse.guest_house_management.exception.ResourceNotFoundException;
import com.guestHouse.guest_house_management.repository.GuestHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestHouseService {

    private final GuestHouseRepository guestHouseRepository;

    // Add a new guest house
    public GuestHouseResponse addGuestHouse(GuestHouseRequest request) {
        GuestHouse guestHouse = GuestHouse.builder()
                .name(request.getName())
                .roomCount(request.getRoomCount())
                .deleted(false)
                .build();

        // only auto-create rooms if roomCount was provided
        if (request.getRoomCount() != null && request.getRoomCount() > 0) {
            List<Room> rooms = new ArrayList<>();
            for (int i = 1; i <= request.getRoomCount(); i++) {
                Room room = Room.builder()
                        .roomNumber(String.valueOf(100 + i))
                        .status(RoomStatus.AVAILABLE)
                        .deleted(false)
                        .guestHouse(guestHouse)
                        .build();
                rooms.add(room);
            }
            guestHouse.setRooms(rooms);
        }

        GuestHouse saved = guestHouseRepository.save(guestHouse);
        return mapToResponse(saved);
    }

    // Get all guest houses — only non deleted
    public List<GuestHouseResponse> getAllGuestHouses() {
        return guestHouseRepository.findByDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get single guest house
    public GuestHouseResponse getGuestHouseById(Long id) {
        GuestHouse guestHouse = guestHouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Guest house not found with id: " + id));
        return mapToResponse(guestHouse);
    }

    // Soft delete guest house
    public void deleteGuestHouse(Long id) {
        GuestHouse guestHouse = guestHouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Guest house not found with id: " + id));

        // block deletion if any room is still occupied
        boolean hasActiveBookings = guestHouse.getRooms().stream()
                .anyMatch(room -> room.getStatus() == RoomStatus.OCCUPIED
                        && !Boolean.TRUE.equals(room.getDeleted()));

        if (hasActiveBookings) {
            throw new BadRequestException(
                    "Cannot delete guest house with occupied rooms. Clear all rooms first.");
        }

        guestHouse.setDeleted(true);
        guestHouse.setDeletedAt(LocalDateTime.now());
        guestHouseRepository.save(guestHouse);
    }

    // Map entity to response DTO
    private GuestHouseResponse mapToResponse(GuestHouse guestHouse) {
        List<RoomResponse> roomResponses = guestHouse.getRooms() == null
                ? Collections.emptyList()
                : guestHouse.getRooms().stream()
                .filter(room -> !Boolean.TRUE.equals(room.getDeleted()))
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .roomNumber(room.getRoomNumber())
                        .status(room.getStatus())
                        .guestHouseId(guestHouse.getId())
                        .guestHouseName(guestHouse.getName())
                        .build())
                .collect(Collectors.toList());

        long available = roomResponses.stream()
                .filter(r -> r.getStatus() == RoomStatus.AVAILABLE).count();

        return GuestHouseResponse.builder()
                .id(guestHouse.getId())
                .name(guestHouse.getName())
                .rooms(roomResponses)
                .totalRooms(roomResponses.size())
                .availableRooms((int) available)
                .occupiedRooms((int) (roomResponses.size() - available))
                .build();
    }
}