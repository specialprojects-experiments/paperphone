package com.withgoogle.experiments.unplugged.ui.tasks

import com.withgoogle.experiments.unplugged.model.TaskList

data class TaskListModel(val taskList: TaskList, var checked: Boolean = false)