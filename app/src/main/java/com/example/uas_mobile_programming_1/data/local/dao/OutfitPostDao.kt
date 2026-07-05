package com.example.uas_mobile_programming_1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uas_mobile_programming_1.data.local.entities.OutfitPostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitPostDao {
    @Query("SELECT * FROM outfit_posts WHERE isPublic = 1 ORDER BY createdAt DESC")
    fun getPublicPosts(): Flow<List<OutfitPostEntity>>

    @Query("SELECT * FROM outfit_posts WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPostsByUser(userId: String): Flow<List<OutfitPostEntity>>

    @Query("SELECT * FROM outfit_posts INNER JOIN post_likes ON outfit_posts.id = post_likes.postId WHERE post_likes.userId = :userId ORDER BY post_likes.createdAt DESC")
    fun getLikedPostsByUser(userId: String): Flow<List<OutfitPostEntity>>

    @Query("SELECT * FROM outfit_posts INNER JOIN post_saves ON outfit_posts.id = post_saves.postId WHERE post_saves.userId = :userId ORDER BY post_saves.createdAt DESC")
    fun getSavedPostsByUser(userId: String): Flow<List<OutfitPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: OutfitPostEntity)

    @Query("SELECT COUNT(*) FROM outfit_posts")
    suspend fun getPostCount(): Int
}
