package com.withgoogle.experiments.unplugged.data.integrations.tasks

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.tasks.Tasks
import com.withgoogle.experiments.unplugged.model.TaskItem
import com.withgoogle.experiments.unplugged.model.TaskList
import timber.log.Timber
import java.time.Instant

class TasksImporter(val token: String) {
    val service by lazy {
        Timber.d("Tasks token: $token")

        val credential = GoogleCredential().setAccessToken(token)
        Tasks.Builder(NetHttpTransport(), JacksonFactory(), credential)
            .setApplicationName("Paper phone")
            .build()
    }

    fun taskItems(taskListId: String): List<TaskItem> {
        val tasksModel = service.tasks().list(taskListId).execute()

        val tasks = tasksModel.items?.map { task ->
            TaskItem(task.title, task.due?.let { Instant.ofEpochMilli(it.value) })
        } ?: emptyList()

        Timber.d(tasks.toString())

        return tasks
    }

    fun taskList(): List<TaskList> {
        val taskListModel = service.tasklists().list().execute()

        val taskLists = taskListModel.items.map { taskList ->
            TaskList(taskList.id, taskList.title, taskItems(taskList.id))
        }

        Timber.d(taskLists.toString())

        return taskLists
    }
}

