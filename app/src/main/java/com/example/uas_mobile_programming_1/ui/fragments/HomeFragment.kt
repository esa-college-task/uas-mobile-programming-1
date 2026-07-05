package com.example.uas_mobile_programming_1.ui.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_mobile_programming_1.R
import com.example.uas_mobile_programming_1.data.local.AppDatabase
import com.example.uas_mobile_programming_1.data.local.entities.OutfitPostEntity
import com.example.uas_mobile_programming_1.data.repository.AppRepository
import com.example.uas_mobile_programming_1.databinding.FragmentHomeBinding
import com.example.uas_mobile_programming_1.databinding.ItemPostBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Manual DI
        val db = AppDatabase.getDatabase(requireContext())
        val repository = AppRepository(
            db.userDao(), db.wardrobeDao(), db.outfitPostDao(),
            db.interactionDao(), db.privacyDao()
        )
        val factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        setupViewPager()
        observeViewModel()
    }

    private fun setupViewPager() {
        adapter = PostAdapter(
            onLikeClick = { post, isLiked -> viewModel.toggleLike(post.id, isLiked) },
            onSaveClick = { post, isSaved -> viewModel.toggleSave(post.id, isSaved) },
            getLikeStatus = { postId -> viewModel.isLiked(postId) },
            getSaveStatus = { postId -> viewModel.isSaved(postId) },
            lifecycleOwner = viewLifecycleOwner
        )
        binding.viewPagerFyp.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.publicPosts.collectLatest { posts ->
                    adapter.submitList(posts)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class PostAdapter(
        private val onLikeClick: (OutfitPostEntity, Boolean) -> Unit,
        private val onSaveClick: (OutfitPostEntity, Boolean) -> Unit,
        private val getLikeStatus: (String) -> kotlinx.coroutines.flow.Flow<Boolean>,
        private val getSaveStatus: (String) -> kotlinx.coroutines.flow.Flow<Boolean>,
        private val lifecycleOwner: androidx.lifecycle.LifecycleOwner
    ) : ListAdapter<OutfitPostEntity, PostAdapter.PostViewHolder>(PostDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PostViewHolder(binding, onLikeClick, onSaveClick, getLikeStatus, getSaveStatus, lifecycleOwner)
        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        class PostViewHolder(
            private val binding: ItemPostBinding,
            private val onLikeClick: (OutfitPostEntity, Boolean) -> Unit,
            private val onSaveClick: (OutfitPostEntity, Boolean) -> Unit,
            private val getLikeStatus: (String) -> kotlinx.coroutines.flow.Flow<Boolean>,
            private val getSaveStatus: (String) -> kotlinx.coroutines.flow.Flow<Boolean>,
            private val lifecycleOwner: androidx.lifecycle.LifecycleOwner
        ) : RecyclerView.ViewHolder(binding.root) {

            private var isLiked = false
            private var isSaved = false
            private var statusJob: Job? = null

            fun bind(post: OutfitPostEntity) {
                // Cancel previous job if it exists
                statusJob?.cancel()

                binding.tvUsername.text = "@fashion_curator"
                
                Glide.with(binding.ivUserAvatar.context)
                    .load("https://i.pravatar.cc/150?u=${post.userId.hashCode()}")
                    .circleCrop()
                    .into(binding.ivUserAvatar)

                val imageSource = if (!post.imageUrl.isNullOrEmpty()) {
                    post.imageUrl
                } else if (post.wardrobeIds.isNotEmpty()) {
                    android.R.drawable.ic_menu_gallery
                } else {
                    android.R.drawable.ic_menu_gallery
                }

                Glide.with(binding.ivPostImage.context)
                    .load(imageSource)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(binding.ivPostImage)

                // Manage collection statusJob
                statusJob = lifecycleOwner.lifecycleScope.launch {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        launch {
                            getLikeStatus(post.id).collectLatest { liked ->
                                if (isLiked != liked) {
                                    isLiked = liked
                                    animateButton(binding.btnLike, liked, Color.RED)
                                } else {
                                    isLiked = liked
                                    updateButtonColor(binding.btnLike, liked, Color.RED)
                                }
                            }
                        }
                        launch {
                            getSaveStatus(post.id).collectLatest { saved ->
                                if (isSaved != saved) {
                                    isSaved = saved
                                    animateButton(binding.btnSave, saved, Color.parseColor("#FFD700")) // Gold
                                } else {
                                    isSaved = saved
                                    updateButtonColor(binding.btnSave, saved, Color.parseColor("#FFD700"))
                                }
                            }
                        }
                    }
                }

                binding.btnLike.setOnClickListener { 
                    onLikeClick(post, isLiked)
                }
                binding.btnSave.setOnClickListener { 
                    onSaveClick(post, isSaved)
                }
                binding.btnFollow.setOnClickListener {
                    it.animate().alpha(0f).setDuration(200).withEndAction {
                        binding.btnFollow.text = "Following"
                        it.animate().alpha(1f).setDuration(200).start()
                    }.start()
                }
            }

            private fun updateButtonColor(view: android.widget.ImageView, active: Boolean, activeColor: Int) {
                view.imageTintList = if (active) {
                    ColorStateList.valueOf(activeColor)
                } else {
                    ColorStateList.valueOf(Color.WHITE)
                }
            }

            private fun animateButton(view: android.widget.ImageView, active: Boolean, activeColor: Int) {
                view.animate()
                    .scaleX(1.4f)
                    .scaleY(1.4f)
                    .setDuration(150)
                    .withEndAction {
                        updateButtonColor(view, active, activeColor)
                        view.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(150)
                            .start()
                    }.start()
            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<OutfitPostEntity>() {
        override fun areItemsTheSame(oldItem: OutfitPostEntity, newItem: OutfitPostEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: OutfitPostEntity, newItem: OutfitPostEntity): Boolean = oldItem == newItem
    }
}
