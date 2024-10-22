package com.mobdeve.s21.mco.schedule_maker

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEvent
import com.alamkanak.weekview.TextResource
import java.util.*

class WeeklyScheduleFragment : Fragment() {

    private lateinit var weekView: WeekView

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.fragment_weekly_schedule, container, false)
            weekView = view.findViewById(R.id.weekView)

            // Load events
            loadWeeklyEvents()

    return view
    }

    private fun loadWeeklyEvents() {
        val weekViewEvents = mutableListOf<WeekViewEvent>()
        val events = DummyData.getEvents()

        // Filter events to include only weekly ones
        for (event in events) {
            if (event.isWeekly) {
                val startTime = Calendar.getInstance().apply { time = event.startTime }
                val endTime = Calendar.getInstance().apply { time = event.endTime }

                val weekViewEvent = WeekViewEvent(
                        event.name.hashCode().toLong(), // Unique ID for the event
                        TextResource(event.name), // TextResource for the event name
                        startTime,
                        endTime
                )

                weekViewEvents.add(weekViewEvent)
            }
        }

        weekView.submit(weekViewEvents)
    }
}
