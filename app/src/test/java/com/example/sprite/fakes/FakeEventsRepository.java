package com.example.sprite.fakes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.*;
import com.example.sprite.Models.Event;
import com.example.sprite.Models.User;

public class FakeEventsRepository {
    private final MutableLiveData<List<Event>> all = new MutableLiveData<>(new ArrayList<>());
    private final Map<String, List<User>> entrants = new HashMap<>();

    public FakeEventsRepository withEvents(List<Event> es){ all.postValue(es); return this; }
    public FakeEventsRepository withEntrants(String eventId, List<User> us){ entrants.put(eventId, us); return this; }

    public LiveData<List<Event>> getAllEvents(){ return all; }

    public LiveData<List<Event>> getEventsForOrganizer(String organizerId){
        List<Event> src = all.getValue()==null? Collections.emptyList():all.getValue();
        List<Event> filtered = new ArrayList<>();
        for (Event e: src) if (organizerId != null && organizerId.equals(e.getOrganizerId())) filtered.add(e);
        return new MutableLiveData<>(filtered);
    }

    public LiveData<List<User>> getEntrantsForEvent(String eventId){
        return new MutableLiveData<>(entrants.getOrDefault(eventId, Collections.emptyList()));
    }
}
