package com.projects.bookhere.controller;

import com.projects.bookhere.model.Reservation;
import com.projects.bookhere.model.Stay;
import com.projects.bookhere.model.User;
import com.projects.bookhere.service.ReservationService;
import com.projects.bookhere.service.StayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* Handle requests regarding stay */
@RestController
public class StayController {
    private StayService stayService;
    private ReservationService reservationService;

    @Autowired
    public StayController(StayService stayService, ReservationService reservationService) {
        this.stayService = stayService;
        this.reservationService = reservationService;
    }

    //Return a list of all stays of a host
    @GetMapping(value = "/stays")
    public List<Stay> listStays(Principal principal) {
        return stayService.listByUser(principal.getName());
    }

    //Return a stay by id
    @GetMapping(value = "/stays/{stayId}")
    public Stay getStay(@PathVariable Long stayId) {
        return stayService.findByIdAndHost(stayId);
    }

    //Return a list of reservation of a stay
    @GetMapping(value = "/stays/reservations/{stayId}")
    public List<Reservation> listReservations(@PathVariable Long stayId, Principal principal) {
        return reservationService.listByStay(stayId);
    }

    //Add a stay
    @PostMapping("/stays")
    public void addStay(
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("description") String description,
            @RequestParam("guest_number") int guestNumber,
            @RequestParam("images") MultipartFile[] images,
            Principal principal) {

        Stay stay = new Stay.Builder().setName(name)
                .setAddress(address)
                .setDescription(description)
                .setGuestNumber(guestNumber)
                .setHost(new User.Builder()
                        .setUsername(principal.getName()).build())
                .build();
        stayService.add(stay, images);
    }

    //Delete a stay by id
    @DeleteMapping("/stays/{stayId}")
    public void deleteStay(@PathVariable Long stayId) {
        stayService.delete(stayId);
    }
}
