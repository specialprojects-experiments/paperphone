package com.withgoogle.experiments.unplugged.data.integrations.calendar

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import com.withgoogle.experiments.unplugged.model.Account
import com.withgoogle.experiments.unplugged.model.Calendar
import com.withgoogle.experiments.unplugged.model.Event
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneOffset

class CalendarImporter(val context: Context) {
    private val CALENDAR_PROJECTION = arrayOf(
        CalendarContract.Calendars._ID,                     // 0
        CalendarContract.Calendars.ACCOUNT_NAME,            // 1
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 2
        CalendarContract.Calendars.ACCOUNT_TYPE,            // 3
        CalendarContract.Calendars.OWNER_ACCOUNT            // 4
    )

    private val EVENTS_PROJECTION = arrayOf(
        CalendarContract.Events.CALENDAR_ID,
        CalendarContract.Events.TITLE,
        CalendarContract.Events.DTSTART,
        CalendarContract.Events.DTEND,
        CalendarContract.Events.EVENT_LOCATION
    )

    fun calendars(account: Account): List<Calendar> {
        val uri: Uri = CalendarContract.Calendars.CONTENT_URI

        val selection = "(${CalendarContract.Calendars.ACCOUNT_NAME} = ? AND ${CalendarContract.Calendars.ACCOUNT_TYPE} = ?)"

        val cursor = context.contentResolver.query(uri, CALENDAR_PROJECTION, selection,
            arrayOf(account.accountName, account.accountType), null)

        cursor?.use {
            return if (cursor.count > 0) {
                generateSequence { if (cursor.moveToNext()) cursor else null }
                    .map { cursorToCalendar(cursor) }
                    .toList()
            } else {
                emptyList()
            }
        }

        return emptyList()
    }

    private fun cursorToCalendar(cursor: Cursor): Calendar {
        val uri: Uri = CalendarContract.Events.CONTENT_URI

        val calendarId = cursor.getLong(0)

        val beginDay = LocalDate.now().atStartOfDay().toInstant(
            ZoneOffset.UTC).toEpochMilli()

        val endDay = LocalDate.now().atTime(23, 59, 59).toInstant(
            ZoneOffset.UTC).toEpochMilli()

        val selection = "${CalendarContract.Events.CALENDAR_ID} = $calendarId AND (${CalendarContract.Events.DTSTART} >= $beginDay AND ${CalendarContract.Events.DTEND} <= $endDay)"

        Timber.d(selection)

        val result = context.contentResolver.query(uri, arrayOf(CalendarContract.Events._COUNT), selection, null, null)

        val eventsCount = result?.use {
            it.moveToFirst()
            it.getInt(0)
        } ?: 0


        return Calendar(
            id = cursor.getLong(0),
            accountName = cursor.getString(1),
            displayName = cursor.getString(2),
            accountType = cursor.getString(3),
            accountOwner = cursor.getString(4),
            eventsCount = eventsCount
        )
    }

    fun events(calendarId: Long): List<Event> {
        val uri: Uri = CalendarContract.Events.CONTENT_URI

        val beginDay = LocalDate.now().atStartOfDay().toInstant(
            ZoneOffset.UTC).toEpochMilli()

        val endDay = LocalDate.now().atTime(23, 59, 59).toInstant(
            ZoneOffset.UTC).toEpochMilli()

        val selection = "${CalendarContract.Events.CALENDAR_ID} = $calendarId AND (${CalendarContract.Events.DTSTART} >= $beginDay AND ${CalendarContract.Events.DTEND} <= $endDay)"

        Timber.d(selection)

        val cursor = context.contentResolver.query(uri, EVENTS_PROJECTION, selection, null, null)

        cursor?.use {
            return if (cursor.count > 0) {
                generateSequence { if (cursor.moveToNext()) cursor else null }
                    .map { cursorToEvent(cursor) }
                    .toList()
            } else {
                emptyList()
            }
        }

        return emptyList()
    }

    private fun cursorToEvent(cursor: Cursor): Event {
        return Event(
            id = cursor.getLong(0),
            title = cursor.getString(1),
            dateStart = cursor.getLong(2),
            dateEnd = cursor.getLong(3)
        )
    }
}

