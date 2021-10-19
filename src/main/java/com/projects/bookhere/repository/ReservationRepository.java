package com.projects.bookhere.repository;

import com.projects.bookhere.model.Reservation;
import com.projects.bookhere.model.Stay;
import com.projects.bookhere.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/* Database operation of stay reservation */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByGuest(User guest);

    List<Reservation> findByStay(Stay stay);

    //Return a list of reservation with check out dates later than the input date
    List<Reservation> findByStayAndCheckoutDateAfter(Stay stay, LocalDate date);
}