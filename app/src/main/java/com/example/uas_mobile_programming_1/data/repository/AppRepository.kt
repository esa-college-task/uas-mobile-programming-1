package com.example.uas_mobile_programming_1.data.repository

import com.example.uas_mobile_programming_1.data.local.dao.*
import com.example.uas_mobile_programming_1.data.local.entities.*
import com.example.uas_mobile_programming_1.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.first

class AppRepository(
    private val userDao: UserDao,
    private val wardrobeDao: WardrobeDao,
    private val outfitPostDao: OutfitPostDao,
    private val interactionDao: InteractionDao,
    private val privacyDao: PrivacyDao
) {
    // Gemini API Key placeholder
    private val GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3.5-flash",
        apiKey = GEMINI_API_KEY
    )

    // User
    fun getUserById(userId: String) = userDao.getUserById(userId)
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)

    // Wardrobe
    fun getWardrobesByUser(userId: String) = wardrobeDao.getWardrobesByUser(userId)
    suspend fun getWardrobesList(userId: String) = wardrobeDao.getWardrobesByUser(userId).first()
    suspend fun insertWardrobe(wardrobe: WardrobeEntity) = wardrobeDao.insertWardrobe(wardrobe)
    suspend fun deleteWardrobe(wardrobe: WardrobeEntity) = wardrobeDao.deleteWardrobe(wardrobe)

    // Posts
    fun getPublicPosts() = outfitPostDao.getPublicPosts()
    fun getPostsByUser(userId: String) = outfitPostDao.getPostsByUser(userId)
    fun getLikedPostsByUser(userId: String) = outfitPostDao.getLikedPostsByUser(userId)
    fun getSavedPostsByUser(userId: String) = outfitPostDao.getSavedPostsByUser(userId)
    suspend fun insertPost(post: OutfitPostEntity) = outfitPostDao.insertPost(post)

    // Interactions
    suspend fun likePost(like: PostLikeEntity) = interactionDao.insertLike(like)
    suspend fun unlikePost(userId: String, postId: String) = interactionDao.removeLike(userId, postId)
    suspend fun savePost(save: PostSaveEntity) = interactionDao.insertSave(save)
    suspend fun unsavePost(userId: String, postId: String) = interactionDao.removeSave(userId, postId)
    fun isLiked(userId: String, postId: String) = interactionDao.isLiked(userId, postId)
    fun isSaved(userId: String, postId: String) = interactionDao.isSaved(userId, postId)

    // Privacy
    fun getSettingsByUser(userId: String) = privacyDao.getSettingsByUser(userId)
    suspend fun updateSettings(settings: PrivacySettingsEntity) = privacyDao.insertSettings(settings)

    // Seeding helpers
    suspend fun getUserCount() = userDao.getUserCount()
    suspend fun getPostCount() = outfitPostDao.getPostCount()

    // Google Gemini AI Implementation
    suspend fun generateOutfitCritique(prompt: String): String {
        return try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: "No response from AI."
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
