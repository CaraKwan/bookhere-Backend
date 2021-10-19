package com.projects.bookhere.controller;

import com.projects.bookhere.exception.InvalidReservationDateException;
import com.projects.bookhere.model.Reservation;
import com.projects.bookhere.model.User;
import com.projects.bookhere.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

/* Handle stay reservation requests */
@RestController
public class ReservationController {
    private ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    //Return a list of reservation of a guest
    @GetMapping(value = "/reservations")
    public List<Reservation> listReservations(Principal principal) {
        return reservationService.listByGuest(principal.getName());
    }

    //Add a reservation
    @PostMapping("/reservations")
    public void addReservation(@RequestBody Reservation reservation, Principal principal) {
        LocalDate checkinDate = reservation.getCheckinDate();
        LocalDate checkoutDate = reservation.getCheckoutDate();
        if (checkinDate.equals(checkoutDate) || checkinDate.isAfter(checkoutDate) || checkinDate.isBefore(LocalDate.now())) {
            throw new InvalidReservationDateException("Invalid date for reservation");
        }
        reservation.setGuest(new User.Builder().setUsername(principal.getName()).build());
        reservationService.add(reservation);
    }

    //Delete a reservation
    @DeleteMapping("/reservations/{reservationId}")
    public void deleteReservation(@PathVariable Long reservationId) {
        reservationService.delete(reservationId);
    }
}
