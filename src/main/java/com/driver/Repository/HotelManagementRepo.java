package com.driver.Repository;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
@Repository
public class HotelManagementRepo {
    HashMap <String,Hotel> hotelHashMap=new HashMap<>();
    HashMap <Integer,User> userHashMap=new HashMap<>();
    HashMap <String, Booking> bookingHashMap=new HashMap<>();


    public String addHotel(@RequestBody Hotel hotel){

        //You need to add an hotel to the database
        //incase the hotelName is null or the hotel Object is null return an empty a FAILURE
        //Incase somebody is trying to add the duplicate hotelName return FAILURE
        //in all other cases return SUCCESS after successfully adding the hotel to the hotelDb.

        if(hotel == null)
            return "FAILURE";

        String hotelName= hotel.getHotelName();
        if(hotelName == null || hotelHashMap.containsKey(hotelName)) {
            return "FAILURE";
        }
        hotelHashMap.put(hotelName,hotel);
        return "SUCCESS";
    }

    public Integer addUser(@RequestBody User user){

        //You need to add a User Object to the database
        //Assume that user will always be a valid user and return the aadharCardNo of the user
        userHashMap.put(user.getaadharCardNo(),user);
        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities(){

        //Out of all the hotels we have added so far, we need to find the hotelName with most no of facilities
        //Incase there is a tie return the lexicographically smaller hotelName
        //Incase there is not even a single hotel with atleast 1 facility return "" (empty string)
        String ans="";
        int max=0;
        for(String hname : hotelHashMap.keySet()){
            int facilitySize=0;
            if(hotelHashMap.get(hname).getFacilities()!=null)
                facilitySize= hotelHashMap.get(hname).getFacilities().size();

            if(facilitySize>max){
                max=facilitySize;
                ans=hname;
            }
            if(facilitySize==max && hname.compareTo(ans)<0){
                ans=hname;
            }
        }
        return ans;
    }

    public int bookARoom(@RequestBody Booking booking){
        //The booking object coming from postman will have all the attributes except bookingId and amountToBePaid;
        //Have bookingId as a random UUID generated String
        //save the booking Entity and keep the bookingId as a primary key
        //Calculate the total amount paid by the person based on no. of rooms booked and price of the room per night.
        //If there arent enough rooms available in the hotel that we are trying to book return -1
        //in other case return total amount paid
        int roomsRequired=booking.getNoOfRooms();
        if(!hotelHashMap.containsKey(booking.getHotelName()))
            return -1;

        Hotel hotel=hotelHashMap.get(booking.getHotelName());
        int roomsAvailable=hotel.getAvailableRooms();

        if(roomsRequired>roomsAvailable) {
            return -1;
        }

        hotel.setAvailableRooms(hotel.getAvailableRooms()-roomsRequired);
        int TotalPrice= roomsRequired*hotel.getPricePerNight();
        booking.setAmountToBePaid(TotalPrice);
        String bookingId= UUID.randomUUID().toString();
        booking.setBookingId(bookingId);
        bookingHashMap.put(bookingId,booking);

        return TotalPrice;
    }

    public int getBookings(@PathVariable("aadharCard")Integer aadharCard)
    {
        //In this function return the bookings done by a person
        int ans=0;
        for(String bId : bookingHashMap.keySet()){
            if(bookingHashMap.get(bId).getBookingAadharCard()==aadharCard){
                ans++;
            }
        }
        return ans;
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName){

        //We are having a new facilites that a hotel is planning to bring.
        //If the hotel is already having that facility ignore that facility otherwise add that facility in the hotelDb
        //return the final updated List of facilities and also update that in your hotelDb
        //Note that newFacilities can also have duplicate facilities possible
        Hotel hotel;
        if(!hotelHashMap.containsKey(hotelName)){
            return null;
        }
        else hotel=hotelHashMap.get(hotelName);

        List <Facility> existingFacilities ;
        if(hotel.getFacilities()==null){
            existingFacilities=new ArrayList<>();
        }
        else existingFacilities=hotel.getFacilities();

        for(Facility f: newFacilities){
            if(!existingFacilities.contains(f)){
                existingFacilities.add(f);
            }
        }
        hotel.setFacilities(existingFacilities);
        return hotel;
    }
}
