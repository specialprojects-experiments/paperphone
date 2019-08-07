package com.withgoogle.experiments.unplugged.data.integrations.tasks

import com.withgoogle.experiments.unplugged.model.TaskItem

object TasksDataSource {
    val tasks = mutableMapOf<String, List<TaskItem>>()

    val ordered: List<TaskItem>
        get() = tasks.values.flatten()
}