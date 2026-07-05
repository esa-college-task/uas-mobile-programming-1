package com.example.uas_mobile_programming_1.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val bio: String? = null,
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
