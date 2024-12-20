package com.mobdeve.s21.mco.schedule_maker

import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEntity
import java.util.Calendar

// Data class for MyEvent
data class MyEvent(
    val id: Long,
    val title: String,
    val startTime: Calendar,
    val endTime: Calendar
)

// LoadMoreHandler interface to handle loading more events
interface LoadMoreHandler {
    fun loadMore(startDate: Calendar, endDate: Calendar)
}

// MyCustomPagingAdapter implementation
class MyCustomPagingAdapter(
    private val loadMoreHandler: LoadMoreHandler
) : WeekView.PagingAdapter<MyEvent>() {

    // Method to create a WeekViewEntity from a MyEvent
    override fun onCreateEntity(item: MyEvent): WeekViewEntity {
        return WeekViewEntity.Event.Builder(item)
            .setId(item.id)
            .setTitle(item.title)
            .setStartTime(item.startTime)
            .setEndTime(item.endTime)
            .build()
    }

    // Method to handle loading more events when the user scrolls
    override fun onLoadMore(startDate: Calendar, endDate: Calendar) {
        loadMoreHandler.loadMore(startDate, endDate)
    }

    // Convert Events to MyEvent
    fun convertEventToMyEvent(events: Events): MyEvent {
        return MyEvent(
            id = events.hashCode().toLong(), // Example ID, consider using a better unique ID if available
            title = events.getName(),
            startTime = Calendar.getInstance().apply { time = events.getStartTime() },
            endTime = Calendar.getInstance().apply { time = events.getEndTime() }
        )
    }

    // Convert a list of Events to MyEvent
    fun convertEventsToMyEvents(events: List<Events>): List<MyEvent> {
        return events.map { convertEventToMyEvent(it) }
    }
}
