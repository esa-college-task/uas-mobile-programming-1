package com.example.uas_mobile_programming_1.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uas_mobile_programming_1.data.local.entities.WardrobeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WardrobeDao {
    @Query("SELECT * FROM wardrobes WHERE userId = :userId")
    fun getWardrobesByUser(userId: String): Flow<List<WardrobeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWardrobe(wardrobe: WardrobeEntity)

    @Delete
    suspend fun deleteWardrobe(wardrobe: WardrobeEntity)
}
