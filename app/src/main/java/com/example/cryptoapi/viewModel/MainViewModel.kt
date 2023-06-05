package com.example.cryptoapi.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoapi.repository.ApiRepository
import com.example.cryptoapi.retrofit.ResponseList
import com.example.cryptoapi.retrofit.ResponseListItem


import com.example.cryptoapi.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private  val repository: ApiRepository
):ViewModel() {
    private val _coinList = MutableLiveData<DataStatus<List<ResponseListItem>>>()
        val coinList :LiveData<DataStatus<List<ResponseListItem>>>
                get() = _coinList


    fun getCoinList(vs_currency : String)  = viewModelScope.launch {
        repository.getCoinList(vs_currency).collect{
            _coinList.value = it
        }
    }

}