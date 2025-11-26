package com.example.sprite.screens.createEvent;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Controllers.ImageService;
import com.example.sprite.Models.Event;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * ViewModel for managing event creation data and operations.
 * 
 * <p>This ViewModel holds the state of event creation form fields and handles
 * the creation of new events in the database. It validates required fields
 * such as max attendees and waiting list size before creating the event.</p>
 * 
 * <p>The ViewModel uses LiveData to observe changes in form fields and
 * provides a mechanism to reset the form after successful event creation.</p>
 */
public class CreateEventViewModel extends ViewModel {
    private FirebaseUser firebaseUser = new Authentication_Service().getCurrentUser();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();
    private ImageService imageService = new ImageService();
    private MutableLiveData<String> title = new MutableLiveData<>();
    private MutableLiveData<Date> registrationStartDate = new MutableLiveData<>();
    private MutableLiveData<Date> registrationEndDate = new MutableLiveData<>();
    private MutableLiveData<String> description = new MutableLiveData<>();
    private MutableLiveData<String> location = new MutableLiveData<>();
    private MutableLiveData<Integer> maxAttendees = new MutableLiveData<>();
    private MutableLiveData<Integer> maxWaitingList = new MutableLiveData<>();
    private MutableLiveData<Double> price = new MutableLiveData<>();
    private MutableLiveData<Date> date = new MutableLiveData<>();
    private MutableLiveData<Date> time = new MutableLiveData<>();
    private MutableLiveData<Date> startDate = new MutableLiveData<>();
    private MutableLiveData<Date> endDate = new MutableLiveData<>();
    private MutableLiveData<Uri> localPosterUri = new MutableLiveData<>();
    private final DatabaseService db = new DatabaseService();

    private final MutableLiveData<Boolean> shouldResetFields = new MutableLiveData<>(false);


    /**
     * Sets the event date.
     * 
     * @param d The event date
     */
    public void setDate(Date d)
    {
        date.setValue(d);
    }
    
    /**
     * Sets the event time.
     * 
     * @param d The event time
     */
    public void setTime(Date d)
    {
        time.setValue(d);
    }
    
    /**
     * Sets the event location.
     * 
     * @param s The location string
     */
    public void setLocation(String s)
    {
        location.setValue(s);
    }
    
    /**
     * Sets the event description.
     * 
     * @param s The description string
     */
    public void setDescription(String s)
    {
        description.setValue(s);
    }
    
    /**
     * Sets the event title.
     * 
     * @param s The title string
     */
   public void setEventTitle(String s)
   {
       title.setValue(s);
   }

   /**
    * Sets the registration start date.
    * 
    * @param s The registration start date
    */
   public void setRegistrationStartDate(Date s)
    {
        registrationStartDate.setValue(s);
    }

    /**
     * Sets the registration end date.
     * 
     * @param s The registration end date
     */
    public void setRegistrationEndDate(Date s)
    {
        registrationEndDate.setValue(s);
    }

    /**
     * Sets the event start date.
     * 
     * @param date The event start date
     */
    public void setEventStartDate(Date date)
    {
        startDate.setValue(date);
    }

    /**
     * Sets the maximum number of attendees.
     * 
     * @param n The maximum attendees count
     */
    public void setMaxAttendees(Integer n)
    {
        maxAttendees.setValue(n);
    }

    /**
     * Sets the maximum waiting list size.
     * 
     * @param n The maximum waiting list size
     */
    public void setMaxWaitingList(Integer n)
    {
        maxWaitingList.setValue(n);
    }

    /**
     * Sets the event price.
     * 
     * @param d The price value
     */
    public void setPrice(Double d)
    {
        price.setValue(d);
    }

    /**
     * Sets the local poster uri
     * @param uri poster uri
     */
    public void setLocalPosterUri(Uri uri) {localPosterUri.setValue(uri);}

    /**
     * Gets the local poster uri
     * @return Uri LocalPosterUri
     */
    public Uri getLocalPosterUri(){return localPosterUri.getValue();}
    /**
     * Gets the LiveData indicating whether fields should be reset.
     * 
     * @return LiveData containing the reset flag
     */
    public MutableLiveData<Boolean> getShouldResetFields()
    {
       return shouldResetFields;
    }

    /**
     * Marks the reset operation as complete.
     * 
     * <p>This method is called after fields have been reset to clear the reset flag.</p>
     */
    public void onResetComplete()
    {
        shouldResetFields.setValue(Boolean.FALSE);
    }

    /**
     * Sets event information from ViewModel LiveData values.
     * 
     * <p>This method populates an Event object with values from the ViewModel's
     * LiveData fields. Only non-null values are set.</p>
     * 
     * @param event The event to populate
     */
    private void setEventInfo(Event event) {
        if (event == null) return;

        if (title != null && title.getValue() != null)
            event.setTitle(title.getValue());

        if (startDate != null && startDate.getValue() != null)
            event.setEventStartDate(startDate.getValue());

        if (endDate != null && endDate.getValue() != null)
            event.setEventEndDate(endDate.getValue());

        if (registrationStartDate != null && registrationStartDate.getValue() != null)
            event.setRegistrationStartDate(registrationStartDate.getValue());

        if (registrationEndDate != null && registrationEndDate.getValue() != null)
            event.setRegistrationEndDate(registrationEndDate.getValue());

        event.setCreatedAt(Calendar.getInstance().getTime());
        event.setStatus(Event.EventStatus.OPEN_FOR_REGISTRATION);

        if (firebaseUser != null)
            event.setOrganizerId(firebaseUser.getUid());

        if (maxAttendees != null && maxAttendees.getValue() != null)
            event.setMaxAttendees(maxAttendees.getValue());

        if (maxWaitingList != null && maxWaitingList.getValue() != null)
            event.setMaxWaitingListSize(maxWaitingList.getValue());

        if (price != null && price.getValue() != null)
            event.setPrice(price.getValue());

        if (description != null && description.getValue() != null)
            event.setDescription(description.getValue());

        if (location != null && location.getValue() != null)
            event.setLocation(location.getValue());

        if (date != null && date.getValue() != null)
            event.setDate(date.getValue());

        if (time != null && time.getValue() != null)
            event.setTime(time.getValue());
    }

    /**
     * Creates and returns an Event object from ViewModel LiveData values.
     * 
     * <p>This method creates a new Event and populates it with values from
     * the ViewModel's LiveData fields. Only non-null values are set.</p>
     * 
     * @return A new Event object with populated fields, or null if creation fails
     */
    @Nullable
    private Event getEventInfo() {
        Event event = new Event();

        if (title != null && title.getValue() != null)
            event.setTitle(title.getValue());

        if (startDate != null && startDate.getValue() != null)
            event.setEventStartDate(startDate.getValue());

        if (endDate != null && endDate.getValue() != null)
            event.setEventEndDate(endDate.getValue());

        if (registrationStartDate != null && registrationStartDate.getValue() != null)
            event.setRegistrationStartDate(registrationStartDate.getValue());

        if (registrationEndDate != null && registrationEndDate.getValue() != null)
            event.setRegistrationEndDate(registrationEndDate.getValue());

        event.setCreatedAt(Calendar.getInstance().getTime());
        event.setStatus(Event.EventStatus.OPEN_FOR_REGISTRATION);

        if (firebaseUser != null)
            event.setOrganizerId(firebaseUser.getUid());

        if (maxAttendees != null && maxAttendees.getValue() != null)
            event.setMaxAttendees(maxAttendees.getValue());

        if (maxWaitingList != null && maxWaitingList.getValue() != null)
            event.setMaxWaitingListSize(maxWaitingList.getValue());

        if (price != null && price.getValue() != null)
            event.setPrice(price.getValue());

        if (description != null && description.getValue() != null)
            event.setDescription(description.getValue());

        if (location != null && location.getValue() != null)
            event.setLocation(location.getValue());

        if (date != null && date.getValue() != null)
            event.setDate(date.getValue());

        return event;
    }



    /**
     * Creates a new event in the database.
     * 
     * <p>This method validates required fields (max attendees and waiting list size)
     * before creating the event. If validation fails, the operation is aborted
     * and an error is logged.</p>
     */
    public void createEvent()
    {
        // Validate required fields
        Integer maxAttendeesValue = maxAttendees.getValue();
        Integer maxWaitingListValue = maxWaitingList.getValue();

        if (maxAttendeesValue == null || maxAttendeesValue <= 0) {
            Log.e("CreateEventViewModel", "Max Attendees is required and must be greater than 0");
            return;
        }

        if (maxWaitingListValue == null || maxWaitingListValue <= 0) {
            Log.e("CreateEventViewModel", "Max Waiting List Size is required and must be greater than 0");
            return;
        }

        Event newEvent = new Event();
        setEventInfo(newEvent);
        imageService.setEventImageUri(newEvent, localPosterUri.getValue(), () -> {
            db.createEvent(newEvent, task -> {
                if (task.isSuccessful()){
                    Log.d("Firestore", "Event Created Successfully");
                    shouldResetFields.setValue(Boolean.TRUE);
                } else {
                    Log.e("Firestore", "Error Creating Event", task.getException());
                }
            });
        });

    }

    /**
     * Resets all form fields to their default/empty values.
     * 
     * <p>This method clears all event creation form fields and sets them
     * to empty or null values.</p>
     */
    public void resetFields()
    {
        setEventTitle("");
        setDescription("");
        setMaxAttendees(0);
        setMaxWaitingList(0);
        setPrice(0.0);
        setRegistrationStartDate(null);
        setRegistrationEndDate(null);
        setEventStartDate(null);
        setLocation("");
        setDate(null);
        setTime(null);
        setLocalPosterUri(null);
    }


}