package com.example.uas_mobile_programming_1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.uas_mobile_programming_1.data.local.converters.Converters
import com.example.uas_mobile_programming_1.data.local.dao.*
import com.example.uas_mobile_programming_1.data.local.entities.*

@Database(
    entities = [
        UserEntity::class,
        PrivacySettingsEntity::class,
        WardrobeEntity::class,
        OutfitPostEntity::class,
        PostLikeEntity::class,
        PostSaveEntity::class,
        FollowerEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun wardrobeDao(): WardrobeDao
    abstract fun outfitPostDao(): OutfitPostDao
    abstract fun interactionDao(): InteractionDao
    abstract fun privacyDao(): PrivacyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ga_ada_baju_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
