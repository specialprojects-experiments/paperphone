package com.withgoogle.experiments.unplugged.model

import java.time.Instant

data class TaskList(val id: String, val name: String, val taskItems: List<TaskItem> = emptyList())
data class TaskItem(val title: String, val dueDate: Instant? = null)