package com.mobdeve.s21.mco.schedule_maker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BasicViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>() // Use Event instead of MyEvent
    val events: LiveData<List<Event>> = _events

    // Example method to load events (replace with your own logic)
    fun loadEvents() {
        // Load or fetch events and post value to _events
        // _events.postValue(fetchedEvents)
    }
}
