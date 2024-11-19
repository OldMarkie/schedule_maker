package com.mobdeve.s21.mco.schedule_maker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeeklySchedViewModel(application: Application) : AndroidViewModel(application) {

    private val _events = MutableLiveData<List<Events>>()
    val events: LiveData<List<Events>> = _events

    private val databaseHelper = DatabaseHelper(application)

    fun loadEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch weekly events from DatabaseHelper
                val fetchedEvents = databaseHelper.getWeeklyEvents()
                _events.postValue(fetchedEvents)
            } catch (e: Exception) {
                e.printStackTrace()
                _events.postValue(emptyList()) // Fallback to an empty list in case of error
            }
        }
    }
}
