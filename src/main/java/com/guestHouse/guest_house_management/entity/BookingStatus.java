package com.guestHouse.guest_house_management.entity;


public enum BookingStatus {
    ACTIVE,
    COMPLETED,   // auto-expired by scheduler
    CANCELLED    // manually cleared by admin
}
