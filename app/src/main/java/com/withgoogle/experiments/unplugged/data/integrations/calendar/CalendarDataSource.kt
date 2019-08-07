package com.withgoogle.experiments.unplugged.data.integrations.calendar

import com.withgoogle.experiments.unplugged.model.Event

object CalendarDataSource {
    val events = mutableMapOf<Long, List<Event>>()

    val ordered: List<Event>
        get() = events.values.flatten().sortedBy { it.dateStart }
}