package com.example.cryptoapi.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoapi.repository.ApiRepository
import com.example.cryptoapi.retrofit.detailsresponse.ResponseDetails
import com.example.cryptoapi.retrofit.getresponse.ResponseListItem


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

    private val _coinDetails = MutableLiveData<DataStatus<ResponseDetails>>()
        val coinDetails : LiveData<DataStatus<ResponseDetails>>
            get() = _coinDetails

    fun getCoinList(vs_currency : String)  = viewModelScope.launch {
        repository.getCoinList(vs_currency).collect{
            _coinList.value = it
        }
    }
    fun getCoinDetails(id:String) = viewModelScope.launch {
        repository.getCoinDetails(id).collect{
            _coinDetails.value = it
        }
    }

}