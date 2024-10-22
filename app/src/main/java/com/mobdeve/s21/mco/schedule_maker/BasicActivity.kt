package com.mobdeve.s21.mco.schedule_maker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alamkanak.weekview.WeekView // Import the WeekView class
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class BasicActivity : AppCompatActivity() {
    private val viewModel by viewModels<BasicViewModel>()
    private lateinit var weekView: WeekView // Change this to WeekView
    private lateinit var adapter: MyCustomPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basic)

        weekView = findViewById(R.id.weekView) // Ensure this ID matches your XML layout

        weekView.minHour = 0;
        weekView.maxHour = 24;

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

        // Set up the back button
        val backBtn: FloatingActionButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            // Navigate back to EventListActivity
            val intent = Intent(this, EventListActivity::class.java)
            startActivity(intent)
            finish() // Optionally finish BasicActivity to remove it from the back stack
        }
    }
}


