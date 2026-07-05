package com.example.uas_mobile_programming_1.ui.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uas_mobile_programming_1.data.local.entities.OutfitPostEntity
import com.example.uas_mobile_programming_1.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class LikedSavedViewModel(private val repository: AppRepository) : ViewModel() {
    private val userId = "user_123"

    fun getLikedPosts(): StateFlow<List<OutfitPostEntity>> = repository.getLikedPostsByUser(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getSavedPosts(): StateFlow<List<OutfitPostEntity>> = repository.getSavedPostsByUser(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
