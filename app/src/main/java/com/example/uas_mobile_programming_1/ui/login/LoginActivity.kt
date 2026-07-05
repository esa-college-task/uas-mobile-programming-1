package com.example.uas_mobile_programming_1.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.uas_mobile_programming_1.MainActivity
import com.example.uas_mobile_programming_1.databinding.ActivityLoginBinding

import com.example.uas_mobile_programming_1.data.local.AppDatabase
import com.example.uas_mobile_programming_1.data.local.entities.UserEntity
import com.example.uas_mobile_programming_1.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        val repository = AppRepository(
            db.userDao(), db.wardrobeDao(), db.outfitPostDao(),
            db.interactionDao(), db.privacyDao()
        )

        binding.btnLogin.setOnClickListener {
            // Mock Google Sign-In Success
            val userId = "user_123"
            CoroutineScope(Dispatchers.IO).launch {
                repository.insertUser(
                    UserEntity(
                        id = userId,
                        name = "Alex Rivera",
                        email = "alex.rivera@example.com",
                        photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&q=80"
                    )
                )
                runOnUiThread {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}
