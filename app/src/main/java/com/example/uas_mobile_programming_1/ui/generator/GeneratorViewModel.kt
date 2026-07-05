package com.example.uas_mobile_programming_1.ui.generator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uas_mobile_programming_1.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GeneratorViewModel(private val repository: AppRepository) : ViewModel() {

    private val userId = "user_123"

    private val _aiCritique = MutableStateFlow<String>("")
    val aiCritique: StateFlow<String> = _aiCritique

    private val _aiRating = MutableStateFlow<Float>(0f)
    val aiRating: StateFlow<Float> = _aiRating

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun mixAndMatch(occasion: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // 1. Fetch all wardrobe items
                val clothes = repository.getWardrobesList(userId)
                
                if (clothes.isEmpty()) {
                    _errorMessage.value = "Lemari baju kamu kosong! Foto baju dulu ya di menu Wardrobe."
                    _isLoading.value = false
                    return@launch
                }

                // 2. Build context text
                val clothesContext = clothes.joinToString(", ") { 
                    "${it.category} (${it.description ?: "tanpa deskripsi"})" 
                }

                // 3. Construct structured prompt
                val prompt = """
                    Halo Gemini, bertindaklah sebagai stylist fashion profesional. 
                    Saya memiliki koleksi pakaian berikut di lemari saya: $clothesContext.
                    
                    Tolong pilihkan kombinasi setelan (mix & match) terbaik untuk acara: $occasion.
                    
                    Berikan jawaban dalam format persis seperti ini:
                    Skor: [angka 0-10]/10
                    Alasan: [penjelasan singkat kenapa kombinasi ini cocok]
                """.trimIndent()

                // 4. Call Gemini
                val response = repository.generateOutfitCritique(prompt)
                
                // 5. Parse response
                parseResponse(response)

            } catch (e: Exception) {
                _errorMessage.value = "Gagal menghubungi AI: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseResponse(response: String) {
        _aiCritique.value = response
        
        // Simple regex to find "Skor: X/10"
        val scoreRegex = "Skor:?\\s*([0-9.]+)/10".toRegex(RegexOption.IGNORE_CASE)
        val match = scoreRegex.find(response)
        if (match != null) {
            val scoreString = match.groupValues[1]
            _aiRating.value = scoreString.toFloatOrNull() ?: 0f
        } else {
            _aiRating.value = 0f
        }
    }
}
