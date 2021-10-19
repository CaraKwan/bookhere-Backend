package com.projects.bookhere.service;

import com.projects.bookhere.exception.ReservationCollisionException;
import com.projects.bookhere.exception.ReservationNotFoundException;
import com.projects.bookhere.model.Reservation;
import com.projects.bookhere.model.Stay;
import com.projects.bookhere.model.User;
import com.projects.bookhere.repository.ReservationRepository;
import com.projects.bookhere.repository.StayAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/* Handle stay reservation operation. */
@Service
public class ReservationService {
    private ReservationRepository reservationRepository;
    private StayAvailabilityRepository stayAvailabilityRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, StayAvailabilityRepository stayAvailabilityRepository) {
        this.reservationRepository = reservationRepository;
        this.stayAvailabilityRepository = stayAvailabilityRepository;
    }

    //Return a list of reservation of a guest
    public List<Reservation> listByGuest(String username) {
        return reservationRepository.findByGuest(new User.Builder().setUsername(username).build());
    }

    //Return a list of reservation for a stay
    public List<Reservation> listByStay(Long stayId) {
        return reservationRepository.findByStay(new Stay.Builder().setId(stayId).build());
    }

    //Add a reservation and update the availability of the stay
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void add(Reservation reservation) throws ReservationCollisionException {
        //Allow same day check out and check in
        List<LocalDate> dates = stayAvailabilityRepository.countByDateBetweenAndId(reservation.getStay().getId(), reservation.getCheckinDate(), reservation.getCheckoutDate().minusDays(1));
        int duration = (int) Duration.between(reservation.getCheckinDate().atStartOfDay(), reservation.getCheckoutDate().atStartOfDay()).toDays();
        if (duration != dates.size()) {
            throw new ReservationCollisionException("Duplicate reservation");
        }
        stayAvailabilityRepository.reserveByDateBetweenAndId(reservation.getStay().getId(), reservation.getCheckinDate(), reservation.getCheckoutDate().minusDays(1));
        reservationRepository.save(reservation);
    }

    //Delete a reservation and update the availability of the stay
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new ReservationNotFoundException("Reservation is not available"));
        stayAvailabilityRepository.cancelByDateBetweenAndId(reservation.getStay().getId(), reservation.getCheckinDate(), reservation.getCheckoutDate().minusDays(1));
        reservationRepository.deleteById(reservationId);
    }

}