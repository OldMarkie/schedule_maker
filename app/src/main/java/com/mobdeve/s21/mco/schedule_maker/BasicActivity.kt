package com.mobdeve.s21.mco.schedule_maker

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alamkanak.weekview.WeekView // Import the WeekView class
import java.util.Calendar

class BasicActivity : AppCompatActivity() {
    private val viewModel by viewModels<BasicViewModel>()
    private lateinit var weekView: WeekView // Change this to WeekView
    private lateinit var adapter: MyCustomPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        weekView = findViewById(R.id.weekView) // Ensure this ID matches your XML layout
        adapter = MyCustomPagingAdapter(object : LoadMoreHandler {
            override fun loadMore(startDate: Calendar, endDate: Calendar) {
                // Implement your logic to load more events here
            }
        })
        weekView.adapter = adapter

        viewModel.events.observe(this) { events ->
            val myEvents = adapter.convertEventsToMyEvents(events) // Convert Event to MyEvent
            adapter.submitList(myEvents) // Updates the adapter with the new event list
        }

        // Load events (for example purposes)
        viewModel.loadEvents() // Call your method to load events
    }
}


