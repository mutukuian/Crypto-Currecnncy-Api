package com.example.cryptoapi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptoapi.R
import com.example.cryptoapi.adapters.CryptoAdapter
import com.example.cryptoapi.databinding.FragmentHomeBinding
import com.example.cryptoapi.utils.DataStatus
import com.example.cryptoapi.utils.initRecyclerView
import com.example.cryptoapi.utils.isVisible
import com.example.cryptoapi.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.fragment_home) {


    private lateinit var binding: FragmentHomeBinding


    private val viewModel by viewModels<MainViewModel>()

    @Inject
    lateinit var cryptoAdapter: CryptoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCryptoRecView()

        lifecycleScope.launch {
            binding.apply {
                viewModel.getCoinList("eur")
                viewModel.coinList.observe(viewLifecycleOwner){
                    when(it.status){
                        DataStatus.Status.LOADING ->{
                            pBarLoading.isVisible(true,rvCrypto)
                        }
                        DataStatus.Status.SUCCESS ->{
                            pBarLoading.isVisible(false,rvCrypto)
                            cryptoAdapter.differ.submitList(it.data)
                            cryptoAdapter.setOnItemClickListener {
                                val direction = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(it.id)
                                findNavController().navigate(direction)
                            }
                        }
                        DataStatus.Status.ERROR ->{
                            pBarLoading.isVisible(true,rvCrypto)
                            Toast.makeText(requireContext(),"Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupCryptoRecView() {
        binding.rvCrypto.initRecyclerView(LinearLayoutManager(requireContext()),cryptoAdapter)
    }
}