package com.guestHouse.guest_house_management.repository;

import com.guestHouse.guest_house_management.entity.Room;
import com.guestHouse.guest_house_management.entity.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // only non deleted rooms for a guest house
    List<Room> findByGuestHouseIdAndDeletedFalse(Long guestHouseId);

    // all rooms for a guest house (used in permanent deletion)
    List<Room> findByGuestHouseId(Long guestHouseId);

    // all non deleted rooms (used in history cleanup)
    List<Room> findByDeletedFalse();

    // soft deleted rooms older than 30 days
    List<Room> findByDeletedTrueAndDeletedAtBefore(LocalDateTime cutoff);

    List<Room> findByGuestHouseIdAndStatus(Long guestHouseId, RoomStatus status);
}