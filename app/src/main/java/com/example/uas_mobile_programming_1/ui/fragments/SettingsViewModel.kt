package com.example.uas_mobile_programming_1.ui.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uas_mobile_programming_1.data.local.entities.PrivacySettingsEntity
import com.example.uas_mobile_programming_1.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: AppRepository) : ViewModel() {
    private val userId = "user_123"

    val settings: StateFlow<PrivacySettingsEntity?> = repository.getSettingsByUser(userId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun updateSettings(autoShare: Boolean, allowSave: Boolean) {
        viewModelScope.launch {
            val current = settings.value
            val newSettings = current?.copy(
                autoShareToFyp = autoShare,
                allowOthersToSave = allowSave,
                updatedAt = System.currentTimeMillis()
            ) ?: PrivacySettingsEntity(
                userId = userId,
                autoShareToFyp = autoShare,
                allowOthersToSave = allowSave
            )
            repository.updateSettings(newSettings)
        }
    }
}
