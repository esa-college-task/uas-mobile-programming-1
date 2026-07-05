package com.example.uas_mobile_programming_1.ui.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uas_mobile_programming_1.data.local.entities.OutfitPostEntity
import com.example.uas_mobile_programming_1.data.local.entities.UserEntity
import com.example.uas_mobile_programming_1.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: AppRepository) : ViewModel() {
    private val userId = "user_123"
    
    val user: StateFlow<UserEntity?> = repository.getUserById(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val myPosts: StateFlow<List<OutfitPostEntity>> = repository.getPostsByUser(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateProfile(name: String, bio: String) {
        viewModelScope.launch {
            val current = user.value
            if (current != null) {
                repository.insertUser(current.copy(name = name, bio = bio))
            }
        }
    }
}
