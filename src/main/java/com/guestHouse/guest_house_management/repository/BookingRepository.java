package com.guestHouse.guest_house_management.repository;

import com.guestHouse.guest_house_management.entity.Booking;
import com.guestHouse.guest_house_management.entity.BookingStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // current active booking for a room
    Optional<Booking> findByRoomIdAndBookingStatus(Long roomId, BookingStatus status);

    // for expiry scheduler
    List<Booking> findByBookingStatusAndCheckOutBefore(BookingStatus status, LocalDateTime now);

    // last 10 bookings for a room
    List<Booking> findTop10ByRoomIdOrderByCheckInDesc(Long roomId);

    // all active bookings
    List<Booking> findByBookingStatus(BookingStatus status);

    // global last 10 past bookings
    @Query("SELECT b FROM Booking b WHERE b.bookingStatus <> 'ACTIVE' ORDER BY b.checkOut DESC")
    List<Booking> findLast10CompletedBookings(Pageable pageable);

    // for history cleanup — all past bookings for a room newest first
    List<Booking> findByRoomIdAndBookingStatusNotOrderByCheckInDesc(
            Long roomId, BookingStatus status);

    // for permanent deletion — all non active bookings for a room
    List<Booking> findByRoomIdAndBookingStatusNot(Long roomId, BookingStatus status);
}