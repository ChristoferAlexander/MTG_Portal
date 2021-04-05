package com.example.mtgportal.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.mtgportal.App
import com.example.mtgportal.databinding.FragmentHomeBinding
import com.example.mtgportal.utils.ViewModelFactory

class HomeFragment : BaseFragment() {

    //region declaration
    private  val _viewModel by lazy { ViewModelProvider(viewModelStore, ViewModelFactory(App.instance.apiService)).get(HomeViewModel::class.java) }
    private val _binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    //endregion

    //region lifecycle
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }
    //endregion

    override fun getInflatedView(): View {
        return _binding.root
    }
}