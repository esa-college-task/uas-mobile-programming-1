package com.example.uas_mobile_programming_1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uas_mobile_programming_1.data.local.entities.PrivacySettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrivacyDao {
    @Query("SELECT * FROM privacy_settings WHERE userId = :userId")
    fun getSettingsByUser(userId: String): Flow<PrivacySettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: PrivacySettingsEntity)
}
