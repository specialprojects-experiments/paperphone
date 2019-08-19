package com.withgoogle.experiments.unplugged.ui.tasks

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.withgoogle.experiments.unplugged.PaperPhoneApp
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.data.integrations.tasks.TasksDataSource
import com.withgoogle.experiments.unplugged.data.integrations.tasks.TasksImporter
import com.withgoogle.experiments.unplugged.ui.widget.BetterViewAnimator
import com.withgoogle.experiments.unplugged.ui.widget.ModuleView
import com.withgoogle.experiments.unplugged.util.bindView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskListActivity: AppCompatActivity() {
    private val recyclerView by bindView<RecyclerView>(R.id.recycler_view)
    private val moduleView by bindView<ModuleView>(R.id.module)
    private val betterViewAnimator by bindView<BetterViewAnimator>(R.id.view_animator)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        setupRecyclerView()

        moduleView.setText("T", "Tasks")
        moduleView.isChecked = true

        loadTaskLists()

        findViewById<View>(R.id.finish).setOnClickListener {
            finish()
        }
    }

    private val tasksAdapter = TaskListAdapter {
        taskList, checked ->
        with(TasksDataSource.tasks) {
            if (checked) {
                put(taskList.id, taskList.taskItems)
            } else {
                remove(taskList.id)
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = tasksAdapter
    }

    private fun loadTaskLists() {
        val token = PaperPhoneApp.obtain(this).taskToken

        token?.let {
            CoroutineScope(Dispatchers.Main).launch {
                val taskLists = withContext(Dispatchers.IO) { TasksImporter(token).taskList() }
                tasksAdapter.changeData(taskLists.map { taskList ->
                    TaskListModel(taskList, TasksDataSource.tasks.containsKey(taskList.id))
                })
                betterViewAnimator.displayedChildId = R.id.recycler_view
            }
        }
    }
}