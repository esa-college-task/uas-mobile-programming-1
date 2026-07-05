package com.example.uas_mobile_programming_1.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "post_likes",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = OutfitPostEntity::class, parentColumns = ["id"], childColumns = ["postId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["userId"]), Index(value = ["postId"])]
)
data class PostLikeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val postId: String,
    val createdAt: Long = System.currentTimeMillis()
)
