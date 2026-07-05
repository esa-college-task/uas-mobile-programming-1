package com.example.uas_mobile_programming_1.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "outfit_posts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class OutfitPostEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val occasion: String,
    val aiReview: String,
    val aiRating: Float,
    val wardrobeIds: List<String>,
    val imageUrl: String? = null,
    val isPublic: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
