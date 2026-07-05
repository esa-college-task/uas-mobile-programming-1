package com.example.uas_mobile_programming_1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uas_mobile_programming_1.data.local.entities.PostLikeEntity
import com.example.uas_mobile_programming_1.data.local.entities.PostSaveEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InteractionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: PostLikeEntity)

    @Query("DELETE FROM post_likes WHERE userId = :userId AND postId = :postId")
    suspend fun removeLike(userId: String, postId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSave(save: PostSaveEntity)

    @Query("DELETE FROM post_saves WHERE userId = :userId AND postId = :postId")
    suspend fun removeSave(userId: String, postId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM post_likes WHERE userId = :userId AND postId = :postId)")
    fun isLiked(userId: String, postId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM post_saves WHERE userId = :userId AND postId = :postId)")
    fun isSaved(userId: String, postId: String): Flow<Boolean>
}
