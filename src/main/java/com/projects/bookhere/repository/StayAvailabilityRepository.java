package com.projects.bookhere.repository;

import com.projects.bookhere.model.StayAvailability;
import com.projects.bookhere.model.StayAvailabilityKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/* Database operation of stay availability */
public interface StayAvailabilityRepository extends JpaRepository<StayAvailability, StayAvailabilityKey> {
    //Spring can convert SQL into java implementation

    //Return a list id of stays in the given stay list that are available between start and end dates
    @Query(value = "SELECT sa.id.stay_id FROM StayAvailability sa WHERE sa.id.stay_id IN ?1 AND sa.state = 0 AND sa.id.date BETWEEN ?2 AND ?3 GROUP BY sa.id.stay_id HAVING COUNT(sa.id.date) = ?4")
    List<Long> findByDateBetweenAndStateIsAvailable(List<Long> stayIds, LocalDate startDate, LocalDate endDate, long duration);

    //Return a list of dates for a specific stay that is available between start and end dates
    @Query(value = "SELECT sa.id.date FROM StayAvailability sa WHERE sa.id.stay_id = ?1 AND sa.state = 0 AND sa.id.date BETWEEN ?2 AND ?3")
    List<LocalDate> countByDateBetweenAndId(Long stayId, LocalDate startDate, LocalDate endDate);

    //Set the availability for a stay to reserved between start and end dates
    @Modifying   //Update database
    @Query(value = "UPDATE StayAvailability sa SET sa.state = 1 WHERE sa.id.stay_id = ?1 AND sa.id.date BETWEEN ?2 AND ?3")
    void reserveByDateBetweenAndId(Long stayId, LocalDate startDate, LocalDate endDate);

    //Set the availability for a stay to available between start and end dates
    @Modifying   //Update database
    @Query(value = "UPDATE StayAvailability sa SET sa.state = 0 WHERE sa.id.stay_id = ?1 AND sa.id.date BETWEEN ?2 AND ?3")
    void cancelByDateBetweenAndId(Long stayId, LocalDate startDate, LocalDate endDate);
}
