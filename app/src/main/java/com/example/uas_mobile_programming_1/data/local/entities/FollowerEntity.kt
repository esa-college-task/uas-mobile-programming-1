package com.example.uas_mobile_programming_1.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "followers",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["followerId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["followingId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["followerId"]), Index(value = ["followingId"])]
)
data class FollowerEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val followerId: String,
    val followingId: String,
    val createdAt: Long = System.currentTimeMillis()
)
