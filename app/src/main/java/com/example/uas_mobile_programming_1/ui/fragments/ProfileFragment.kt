package com.example.uas_mobile_programming_1.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.uas_mobile_programming_1.R
import com.example.uas_mobile_programming_1.data.local.AppDatabase
import com.example.uas_mobile_programming_1.data.repository.AppRepository
import com.example.uas_mobile_programming_1.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        val repository = AppRepository(
            db.userDao(), db.wardrobeDao(), db.outfitPostDao(),
            db.interactionDao(), db.privacyDao()
        )
        val factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnHamburger.setOnClickListener { showHamburgerMenu(it) }
        
        // Set stock photo for profile picture
        Glide.with(this)
            .load("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&q=80")
            .circleCrop()
            .into(binding.ivProfilePic)

        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }
    }

    private fun showEditProfileDialog() {
        val dialogContext = android.view.ContextThemeWrapper(requireContext(), com.google.android.material.R.style.Theme_Material3_Light_Dialog)
        val dialogBinding = com.example.uas_mobile_programming_1.databinding.DialogEditProfileBinding.inflate(layoutInflater)
        val user = viewModel.user.value
        dialogBinding.etName.setText(user?.name ?: "Alex Rivera")
        dialogBinding.etBio.setText(binding.tvBio.text.toString())

        com.google.android.material.dialog.MaterialAlertDialogBuilder(dialogContext)
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val newName = dialogBinding.etName.text.toString()
                val newBio = dialogBinding.etBio.text.toString()
                viewModel.updateProfile(newName, newBio)
                binding.tvBio.text = newBio
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showHamburgerMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        
        val menuItems = listOf(
            "My Wardrobe",
            "Liked Posts",
            "Saved Posts",
            "Settings & Privacy"
        )

        menuItems.forEachIndexed { index, title ->
            val spannable = SpannableString(title)
            spannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, spannable.length, 0)
            popup.menu.add(0, index + 1, index, spannable)
        }

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> findNavController().navigate(R.id.wardrobeFragment)
                2 -> findNavController().navigate(R.id.likedPostsFragment)
                3 -> findNavController().navigate(R.id.savedPostsFragment)
                4 -> findNavController().navigate(R.id.settingsPrivacyFragment)
            }
            true
        }
        popup.show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.user.collect { user ->
                        user?.let {
                            binding.tvDisplayName.text = it.name
                            binding.tvBio.text = it.bio ?: "Visual Storyteller & Curator"
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
