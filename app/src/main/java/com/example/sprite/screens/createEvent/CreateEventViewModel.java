package com.example.sprite.screens.createEvent;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sprite.Controllers.Authentication_Service;
import com.example.sprite.Controllers.DatabaseService;
import com.example.sprite.Models.Event;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Date;

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
    private void setEventInfo(Event event)
    {
        event.setTitle(title.getValue());
        event.setRegistrationStartDate(registrationStartDate.getValue());
        event.setRegistrationEndDate(registrationEndDate.getValue());
        event.setCreatedAt(Calendar.getInstance().getTime());
        event.setStatus(Event.EventStatus.OPEN_FOR_REGISTRATION);
        event.setOrganizerId(firebaseUser.getUid());

        event.setMaxAttendees(maxAttendees.getValue());
        event.setMaxWaitingListSize(maxWaitingList.getValue());
        event.setPrice(price.getValue());
        event.setDescription(description.getValue());
        event.setLocation(location.getValue());
        event.setDate(date.getValue());
        event.setTime(time.getValue());
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