package com.example.uas_mobile_programming_1.ui.generator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.uas_mobile_programming_1.data.local.AppDatabase
import com.example.uas_mobile_programming_1.data.repository.AppRepository
import com.example.uas_mobile_programming_1.databinding.FragmentGeneratorBinding
import kotlinx.coroutines.launch

class GeneratorFragment : Fragment() {

    private var _binding: FragmentGeneratorBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GeneratorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGeneratorBinding.inflate(inflater, container, false)
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
                return GeneratorViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[GeneratorViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        val occasions = arrayOf("Formal", "Casual", "Date Night", "Wedding", "Gym", "Nongkrong")
        
        // Custom adapter to ensure text color is black
        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, occasions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v as? android.widget.TextView)?.setTextColor(android.graphics.Color.BLACK)
                return v
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent)
                (v as? android.widget.TextView)?.setTextColor(android.graphics.Color.BLACK)
                return v
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerOccasion.adapter = adapter

        binding.btnMixMatch.setOnClickListener {
            val occasion = binding.spinnerOccasion.selectedItem.toString()
            viewModel.mixAndMatch(occasion)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.aiCritique.collect { response ->
                        binding.tvAiResponse.text = response
                    }
                }
                launch {
                    viewModel.aiRating.collect { rating ->
                        if (rating > 0) {
                            binding.tvAiRating.text = "Fashion Score: $rating/10"
                            binding.tvAiRating.visibility = View.VISIBLE
                        } else {
                            binding.tvAiRating.visibility = View.GONE
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                        binding.btnMixMatch.isEnabled = !isLoading
                    }
                }
                launch {
                    viewModel.errorMessage.collect { error ->
                        error?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
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
