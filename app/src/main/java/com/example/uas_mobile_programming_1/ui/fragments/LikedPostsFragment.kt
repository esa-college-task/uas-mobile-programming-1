package com.example.uas_mobile_programming_1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.uas_mobile_programming_1.data.local.AppDatabase
import com.example.uas_mobile_programming_1.data.repository.AppRepository
import com.example.uas_mobile_programming_1.databinding.FragmentLikedPostsBinding
import kotlinx.coroutines.launch

class LikedPostsFragment : Fragment() {
    private var _binding: FragmentLikedPostsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LikedSavedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLikedPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = AppDatabase.getDatabase(requireContext())
        val repository = AppRepository(db.userDao(), db.wardrobeDao(), db.outfitPostDao(), db.interactionDao(), db.privacyDao())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return LikedSavedViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[LikedSavedViewModel::class.java]

        val adapter = WardrobeFragment.WardrobeAdapter()
        binding.rvLikedPosts.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getLikedPosts().collect { posts ->
                    // Reusing WardrobeAdapter logic but mapping OutfitPost to a fake WardrobeItem for simplicity in display
                    val fakeWardrobes = posts.map { 
                        com.example.uas_mobile_programming_1.data.local.entities.WardrobeEntity(
                            id = it.id,
                            userId = it.userId,
                            imageUrl = it.imageUrl ?: "",
                            category = it.occasion
                        )
                    }
                    adapter.submitList(fakeWardrobes)
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView() ; _binding = null }
}
