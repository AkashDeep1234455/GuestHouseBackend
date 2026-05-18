package com.guestHouse.guest_house_management.scheduler;

import com.guestHouse.guest_house_management.entity.*;
import com.guestHouse.guest_house_management.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingExpiryScheduler {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final GuestHouseRepository guestHouseRepository;

    // ── Job 1: Expire bookings at midnight ──
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expireBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expired = bookingRepository
                .findByBookingStatusAndCheckOutBefore(BookingStatus.ACTIVE, now);

        if (expired.isEmpty()) {
            log.info("Nightly expiry check — no expired bookings");
            return;
        }

        for (Booking booking : expired) {
            booking.setBookingStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);
            booking.getRoom().setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(booking.getRoom());
            log.info("Booking {} expired — Room {} is now AVAILABLE",
                    booking.getId(), booking.getRoom().getRoomNumber());
        }
    }

    // ── Job 2: Cleanup booking history at 1 AM ──
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void cleanupBookingHistory() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        List<Room> activeRooms = roomRepository.findByDeletedFalse();

        for (Room room : activeRooms) {
            List<Booking> history = bookingRepository
                    .findByRoomIdAndBookingStatusNotOrderByCheckInDesc(
                            room.getId(), BookingStatus.ACTIVE);

            for (int i = 0; i < history.size(); i++) {
                Booking b = history.get(i);
                boolean isBeyond10 = i >= 10;
                boolean isOlderThan30Days = b.getCheckOut() != null
                        && b.getCheckOut().isBefore(cutoff);

                if (isBeyond10 || isOlderThan30Days) {
                    bookingRepository.delete(b);
                    log.info("History cleanup — deleted booking {} for room {} " +
                                    "(beyond10={}, old={})",
                            b.getId(), room.getRoomNumber(), isBeyond10, isOlderThan30Days);
                }
            }
        }
        log.info("Booking history cleanup complete");
    }

    // ── Job 3: Permanently delete soft deleted records at 2 AM ──
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void permanentlyDeleteSoftDeleted() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        // rooms deleted by admin 30+ days ago
        List<Room> oldRooms = roomRepository
                .findByDeletedTrueAndDeletedAtBefore(cutoff);

        for (Room room : oldRooms) {
            List<Booking> roomBookings = bookingRepository
                    .findByRoomIdAndBookingStatusNot(room.getId(), BookingStatus.ACTIVE);
            bookingRepository.deleteAll(roomBookings);
            roomRepository.delete(room);
            log.info("Permanent delete — Room {} removed after 30 days",
                    room.getRoomNumber());
        }

        // guest houses deleted by admin 30+ days ago
        List<GuestHouse> oldHouses = guestHouseRepository
                .findByDeletedTrueAndDeletedAtBefore(cutoff);

        for (GuestHouse gh : oldHouses) {
            List<Room> remainingRooms = roomRepository.findByGuestHouseId(gh.getId());
            for (Room room : remainingRooms) {
                List<Booking> roomBookings = bookingRepository
                        .findByRoomIdAndBookingStatusNot(room.getId(), BookingStatus.ACTIVE);
                bookingRepository.deleteAll(roomBookings);
                roomRepository.delete(room);
            }
            guestHouseRepository.delete(gh);
            log.info("Permanent delete — Guest house {} removed after 30 days",
                    gh.getName());
        }
        log.info("Permanent deletion job complete");
    }
}