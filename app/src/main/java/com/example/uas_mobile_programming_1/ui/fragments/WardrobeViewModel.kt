package com.example.uas_mobile_programming_1.ui.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uas_mobile_programming_1.data.local.entities.UserEntity
import com.example.uas_mobile_programming_1.data.local.entities.WardrobeEntity
import com.example.uas_mobile_programming_1.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WardrobeViewModel(private val repository: AppRepository) : ViewModel() {

    // Simple dummy user ID
    private val userId = "user_123"

    val wardrobeItems: StateFlow<List<WardrobeEntity>> = repository.getWardrobesByUser(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addWardrobeItem(imageUrl: String) {
        viewModelScope.launch {
            if (repository.getUserById(userId).first() == null) {
                repository.insertUser(UserEntity(id = userId, name = "My Profile", email = "me@example.com"))
            }

            val newItem = WardrobeEntity(
                userId = userId,
                imageUrl = imageUrl,
                category = "Uncategorized",
                description = "New clothing item"
            )
            repository.insertWardrobe(newItem)
        }
    }
}
