package com.example.uas_mobile_programming_1.ui.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uas_mobile_programming_1.data.local.entities.OutfitPostEntity
import com.example.uas_mobile_programming_1.data.local.entities.PostLikeEntity
import com.example.uas_mobile_programming_1.data.local.entities.PostSaveEntity
import com.example.uas_mobile_programming_1.data.local.entities.UserEntity
import com.example.uas_mobile_programming_1.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: AppRepository) : ViewModel() {

    private val userId = "user_123" // Mock current user

    val publicPosts: StateFlow<List<OutfitPostEntity>> = repository.getPublicPosts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        seedDataIfNeeded()
    }

    private fun seedDataIfNeeded() {
        viewModelScope.launch {
            if (repository.getUserCount() < 5) { // Seed if database is empty or near empty
                seedUsersAndPosts()
            }
        }
    }

    private suspend fun seedUsersAndPosts() {
        val fashionImages = listOf(
            "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=500&q=80",
            "https://images.unsplash.com/photo-1483985988355-763728e1935b?w=500&q=80",
            "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=500&q=80",
            "https://images.unsplash.com/photo-1539109136881-3be0616acf4b?w=500&q=80",
            "https://images.unsplash.com/photo-1554412930-c74f66ce5590?w=500&q=80",
            "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?w=500&q=80",
            "https://images.unsplash.com/photo-1525507119028-ed4c629a60a3?w=500&q=80",
            "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=500&q=80",
            "https://images.unsplash.com/photo-1520975661595-6453be3f7070?w=500&q=80",
            "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=500&q=80"
        )

        val usernames = listOf("chic_style", "urban_vibe", "minimal_look", "fashion_icon", "trend_setter")

        for (i in 0 until 10) {
            val newUser = UserEntity(
                name = usernames.getOrElse(i % 5) { "User $i" },
                email = "user$i@example.com",
                photoUrl = "https://i.pravatar.cc/150?u=${i}"
            )
            repository.insertUser(newUser)

            for (j in 0 until 10) {
                val post = OutfitPostEntity(
                    userId = newUser.id,
                    occasion = if (j % 2 == 0) "Formal" else "Casual",
                    aiReview = "Looking sharp! The colors complement each other perfectly. Great choice for a ${if (j % 2 == 0) "meeting" else "brunch"}.",
                    aiRating = 8.0f + (j % 3) * 0.5f,
                    wardrobeIds = emptyList(),
                    imageUrl = fashionImages[(i + j) % fashionImages.size],
                    isPublic = true
                )
                repository.insertPost(post)
            }
        }
    }

    fun toggleLike(postId: String, isCurrentlyLiked: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyLiked) {
                repository.unlikePost(userId, postId)
            } else {
                repository.likePost(PostLikeEntity(userId = userId, postId = postId))
            }
        }
    }

    fun toggleSave(postId: String, isCurrentlySaved: Boolean) {
        viewModelScope.launch {
            if (isCurrentlySaved) {
                repository.unsavePost(userId, postId)
            } else {
                repository.savePost(PostSaveEntity(userId = userId, postId = postId))
            }
        }
    }
    
    fun isLiked(postId: String) = repository.isLiked(userId, postId)
    fun isSaved(postId: String) = repository.isSaved(userId, postId)
}
