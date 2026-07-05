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
import com.example.uas_mobile_programming_1.databinding.FragmentSettingsPrivacyBinding
import kotlinx.coroutines.launch

class SettingsPrivacyFragment : Fragment() {
    private var _binding: FragmentSettingsPrivacyBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsPrivacyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = AppDatabase.getDatabase(requireContext())
        val repository = AppRepository(db.userDao(), db.wardrobeDao(), db.outfitPostDao(), db.interactionDao(), db.privacyDao())
        val factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        observeViewModel()
        setupListeners()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settings.collect { settings ->
                    settings?.let {
                        binding.switchShareFyp.isChecked = it.autoShareToFyp
                        binding.switchAllowSave.isChecked = it.allowOthersToSave
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        val listener = { _: View ->
            viewModel.updateSettings(
                binding.switchShareFyp.isChecked,
                binding.switchAllowSave.isChecked
            )
        }
        binding.switchShareFyp.setOnClickListener(listener)
        binding.switchAllowSave.setOnClickListener(listener)
    }

    override fun onDestroyView() { super.onDestroyView() ; _binding = null }
}
