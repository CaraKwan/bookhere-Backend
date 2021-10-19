package com.projects.bookhere.service;

import com.projects.bookhere.exception.StayDeleteException;
import com.projects.bookhere.model.*;
import com.projects.bookhere.repository.LocationRepository;
import com.projects.bookhere.repository.ReservationRepository;
import com.projects.bookhere.repository.StayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/* Handle operations on stay */
@Service
public class StayService {
    private StayRepository stayRepository;
    private ImageStorageService imageStorageService;
    private LocationRepository locationRepository;
    private GeoEncodingService geoEncodingService;
    private ReservationRepository reservationRepository;

    @Autowired
    public StayService(StayRepository stayRepository, LocationRepository locationRepository,
                       ReservationRepository reservationRepository,
                       ImageStorageService imageStorageService,
                       GeoEncodingService geoEncodingService) {
        this.stayRepository = stayRepository;
        this.locationRepository = locationRepository;
        this.reservationRepository = reservationRepository;
        this.imageStorageService = imageStorageService;
        this.geoEncodingService = geoEncodingService;
    }

    //Return a list of all stays for a host
    public List<Stay> listByUser(String username) {
        //findByHost conforms with JpaRepository naming convention
        return stayRepository.findByHost(new User.Builder().setUsername(username).build());
    }

    //Return a stay by id
    public Stay findByIdAndHost(Long stayId) {
        return stayRepository.findById(stayId).orElse(null);
    }


    //Add a stay and save to database, have to set up availabilities and images
    //Save images to GCS, save location to elasticsearch
    public void add(Stay stay, MultipartFile[] images) {
        //Set Stay availability field
        LocalDate date = LocalDate.now().plusDays(1);
        List<StayAvailability> availabilities = new ArrayList<>();
        for (int i = 0; i < 30; ++i) {
            availabilities.add(new StayAvailability.Builder().setId(new StayAvailabilityKey(stay.getId(), date)).setStay(stay).setState(StayAvailabilityState.AVAILABLE).build());
            date = date.plusDays(1);
        }
        stay.setAvailabilities(availabilities);

        //Upload images and set stay image field with return url
        //Parallel processing for fast uploading
        List<String> mediaLinks = Arrays.stream(images).parallel().map(image -> imageStorageService.save(image)).collect(Collectors.toList());

        List<StayImage> stayImages = new ArrayList<>();
        for (String mediaLink : mediaLinks) {
            stayImages.add(new StayImage(mediaLink, stay));
        }
        stay.setImages(stayImages);

        stayRepository.save(stay);

        //Convert address to location(lat, long) and save to elasticsearch
        Location location = geoEncodingService.getLatLng(stay.getId(), stay.getAddress());
        locationRepository.save(location);
    }

    //Delete a stay from database
    public void delete(Long stayId) throws StayDeleteException {
        //Cannot delete this stay if there are still active reservations
        List<Reservation> reservations = reservationRepository.findByStayAndCheckoutDateAfter(new Stay.Builder().setId(stayId).build(), LocalDate.now());
        if (reservations != null && reservations.size() > 0) {
            throw new StayDeleteException("Cannot delete stay with active reservation");
        }
        stayRepository.deleteById(stayId);
    }
}