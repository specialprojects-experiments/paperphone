package com.withgoogle.experiments.unplugged.ui.calendar

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.data.integrations.calendar.CalendarDataSource
import com.withgoogle.experiments.unplugged.data.integrations.calendar.CalendarImporter
import com.withgoogle.experiments.unplugged.ui.AppState
import com.withgoogle.experiments.unplugged.ui.widget.ModuleView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalendarSelectorActivity: AppCompatActivity() {
    private val recyclerView by lazy {
        findViewById<RecyclerView>(R.id.recycler_view)
    }

    private val moduleView by lazy {
        findViewById<ModuleView>(R.id.module)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_selector)

        setupRecyclerView()
        moduleView.setText("C", "Calendar")
        moduleView.isChecked = true

        loadCalendars()

        findViewById<View>(R.id.finish).setOnClickListener {
            finish()
        }
    }

    private val calendarAdapter = CalendarAdapter { calendar, checked ->
        with(CalendarDataSource.events) {
            if (checked) {
                CoroutineScope(Dispatchers.Main).launch {
                    val events = withContext(Dispatchers.IO) {
                        CalendarImporter(this@CalendarSelectorActivity).events(calendar.id)
                    }
                    put(calendar.id, events)
                }
            } else {
                remove(calendar.id)
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = calendarAdapter
    }

    private fun loadCalendars() {
        AppState.account.observe(this, Observer { account ->
            account?.let {
                val calendars = CalendarImporter(this).calendars(account)

                calendarAdapter.changeData(calendars
                    .filter{ it.eventsCount > 0 }
                    .map { CalendarModel(it, CalendarDataSource.events.containsKey(it.id)) })
            }
        })
    }
}