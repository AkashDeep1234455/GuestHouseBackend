package com.guestHouse.guest_house_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GuestHouseManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(GuestHouseManagementApplication.class, args);
	}

}
