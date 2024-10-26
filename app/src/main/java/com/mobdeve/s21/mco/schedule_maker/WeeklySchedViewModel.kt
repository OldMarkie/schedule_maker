package com.mobdeve.s21.mco.schedule_maker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeeklySchedViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    fun loadEvents() {
        // Simulate loading events (replace this with actual loading logic)
        val fetchedEvents = DummyData.getEvents() // Get all events from DummyData
        _events.postValue(fetchedEvents)
    }
}

