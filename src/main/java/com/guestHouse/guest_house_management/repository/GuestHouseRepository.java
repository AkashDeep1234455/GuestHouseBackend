package com.guestHouse.guest_house_management.repository;

import com.guestHouse.guest_house_management.entity.GuestHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GuestHouseRepository extends JpaRepository<GuestHouse, Long> {

    // dashboard — only show non deleted
    List<GuestHouse> findByDeletedFalse();

    // scheduler — soft deleted older than 30 days
    List<GuestHouse> findByDeletedTrueAndDeletedAtBefore(LocalDateTime cutoff);
}