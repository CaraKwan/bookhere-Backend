package com.projects.bookhere.service;

import com.projects.bookhere.model.Stay;
import com.projects.bookhere.repository.LocationRepository;
import com.projects.bookhere.repository.StayAvailabilityRepository;
import com.projects.bookhere.repository.StayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/* Handle stay search operation. */
@Service
public class SearchService {
    private StayRepository stayRepository;
    private StayAvailabilityRepository stayAvailabilityRepository;
    private LocationRepository locationRepository;

    @Autowired
    public SearchService(StayRepository stayRepository, StayAvailabilityRepository stayAvailabilityRepository, LocationRepository locationRepository) {
        this.stayRepository = stayRepository;
        this.stayAvailabilityRepository = stayAvailabilityRepository;
        this.locationRepository = locationRepository;
    }

    //Return a list of stay that satisfy all the inputs
    public List<Stay> search(int guestNumber, LocalDate checkinDate, LocalDate checkoutDate, double lat, double lon, String distance) {
        List<Long> stayIds = locationRepository.searchByDistance(lat, lon, distance);
        long duration = Duration.between(checkinDate.atStartOfDay(), checkoutDate.atStartOfDay()).toDays();

        List<Long> filteredStayIds = stayAvailabilityRepository.findByDateBetweenAndStateIsAvailable(stayIds, checkinDate, checkoutDate.minusDays(1), duration);
        return stayRepository.findByIdInAndGuestNumberGreaterThanEqual(filteredStayIds, guestNumber);
    }
}
