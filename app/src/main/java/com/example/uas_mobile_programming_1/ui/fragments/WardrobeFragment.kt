package com.example.uas_mobile_programming_1.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas_mobile_programming_1.data.local.AppDatabase
import com.example.uas_mobile_programming_1.data.local.entities.WardrobeEntity
import com.example.uas_mobile_programming_1.data.repository.AppRepository
import com.example.uas_mobile_programming_1.databinding.FragmentWardrobeBinding
import com.example.uas_mobile_programming_1.databinding.ItemWardrobeBinding
import com.example.uas_mobile_programming_1.ui.camera.CameraActivity
import kotlinx.coroutines.launch

class WardrobeFragment : Fragment() {

    private var _binding: FragmentWardrobeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WardrobeViewModel
    private lateinit var adapter: WardrobeAdapter

    private val startCameraRes = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imagePath = result.data?.getStringExtra("image_path")
            if (imagePath != null) {
                viewModel.addWardrobeItem(imagePath)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWardrobeBinding.inflate(inflater, container, false)
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
                return WardrobeViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[WardrobeViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        binding.fabAddClothes.setOnClickListener {
            startCameraRes.launch(Intent(requireContext(), CameraActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = WardrobeAdapter()
        binding.rvWardrobe.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.wardrobeItems.collect { items ->
                    adapter.submitList(items)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class WardrobeAdapter : ListAdapter<WardrobeEntity, WardrobeAdapter.WardrobeViewHolder>(WardrobeDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WardrobeViewHolder {
            val binding = ItemWardrobeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return WardrobeViewHolder(binding)
        }

        override fun onBindViewHolder(holder: WardrobeViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        class WardrobeViewHolder(private val binding: ItemWardrobeBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: WardrobeEntity) {
                Glide.with(binding.ivClothes.context)
                    .load(item.imageUrl)
                    .centerCrop()
                    .into(binding.ivClothes)
            }
        }
    }

    class WardrobeDiffCallback : DiffUtil.ItemCallback<WardrobeEntity>() {
        override fun areItemsTheSame(oldItem: WardrobeEntity, newItem: WardrobeEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: WardrobeEntity, newItem: WardrobeEntity): Boolean = oldItem == newItem
    }
}
