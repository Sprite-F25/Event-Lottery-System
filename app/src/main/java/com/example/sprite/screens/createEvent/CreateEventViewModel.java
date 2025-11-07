package com.example.sprite.screens.createEvent;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.Event;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Date;

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
    private final DatabaseService db = new DatabaseService();

    private final MutableLiveData<Boolean> shouldResetFields = new MutableLiveData<>(false);
    private void setDummyEventInfo(Event event)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, 10, 10);
        Date startDate = calendar.getTime();
        calendar.set(2025, 10, 12);
        Date endDate = calendar.getTime();
        Date createTime = Calendar.getInstance().getTime();

        event.setTitle("MyEvent");
        event.setDescription("This is the description for the event");
        event.setLocation("123 Street");
        //event.setEventStartDate(); -> Not needed?
        //event.setEventEndDate();
        event.setRegistrationStartDate(startDate);
        event.setRegistrationEndDate(endDate);
        event.setMaxAttendees(5);
        event.setMaxWaitingListSize(5); //Optional!!!
        event.setPrice(10.25);
        event.setPosterImageUrl("POSTER"); //NEED TO CHECK THIS
        event.setStatus(Event.EventStatus.OPEN_FOR_REGISTRATION);
        event.setCreatedAt(createTime);
        //event.setEntrantLimit(); -> Not needed, there is already MaxAttendees?

    }


    public void setDate(Date d)
    {
        date.setValue(d);
    }
    public void setTime(Date d)
    {
        time.setValue(d);
    }
    public void setLocation(String s)
    {
        location.setValue(s);
    }
    public void setDescription(String s)
    {
        description.setValue(s);
    }
   public void setEventTitle(String s)
   {
       title.setValue(s);
   }

   public void setRegistrationStartDate(Date s)
    {
        registrationStartDate.setValue(s);
    }

    public void setRegistrationEndDate(Date s)
    {
        registrationEndDate.setValue(s);
    }

    public void setMaxAttendees(Integer n)
    {
        maxAttendees.setValue(n);
    }

    public void setMaxWaitingList(Integer n)
    {
        maxWaitingList.setValue(n);
    }

    public void setPrice(Double d)
    {
        price.setValue(d);
    }

    public MutableLiveData<Boolean> getShouldResetFields()
    {
       return shouldResetFields;
    }

    public void onResetComplete()
    {
        shouldResetFields.setValue(Boolean.FALSE);
    }

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
    }

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

        db.createEvent(newEvent, task -> {
            if (task.isSuccessful()){
                Log.d("Firestore", "Event Created Successfully");
                shouldResetFields.setValue(Boolean.TRUE);
            } else {
                Log.e("Firestore", "Error Creating Event", task.getException());
            }
        });
    }

    public void resetFields()
    {
        setEventTitle("");
        setDescription("");
        setMaxAttendees(0);
        setMaxWaitingList(0);
        setPrice(0.0);
        setRegistrationStartDate(null);
        setRegistrationEndDate(null);
        setLocation("");
        setDate(null);
        setTime(null);
    }


}