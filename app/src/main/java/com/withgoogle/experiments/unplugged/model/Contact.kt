package com.withgoogle.experiments.unplugged.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(@PrimaryKey val id: Long, val fullName: String, val phoneNumber: String)