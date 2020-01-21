package xyz.world.currency.rate.converter.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class CurrencyDataViewModel : ViewModel() {

    val itemsCurrencyData: MutableLiveData<ArrayList<ItemsDataStructure>> by lazy {
        MutableLiveData<ArrayList<ItemsDataStructure>>()
    }

    val baseCurrency: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val itemsCurrencyRate: MutableLiveData<ArrayList<ItemsDataStructure>> by lazy {
        MutableLiveData<ArrayList<ItemsDataStructure>>()
    }

    private val itemsDataStructure: ArrayList<ItemsDataStructure> = ArrayList<ItemsDataStructure>()
    private val itemsRatesStructure: ArrayList<ItemsDataStructure> = ArrayList<ItemsDataStructure>()

    /**
     * Loading Data for the First Time from Public API for Unregistered Users.
     */
    @ExperimentalCoroutinesApi
    fun loadDataFromRetrofitResult(baseCurrency: String, itemsData: Map<String, Double>) =  CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        Log.d("ItemDataStructure All Retrofit", "Adding To ArrayList")
        itemsDataStructure.clear()

        itemsDataStructure.add(ItemsDataStructure(
            baseCurrency,
            1.0
        ))

        itemsData.forEach { (currencyName, currencyRate) ->
            itemsDataStructure.add(ItemsDataStructure(
                currencyName,
                currencyRate
            ))
        }

        itemsCurrencyData
            .postValue(itemsDataStructure)
    }

    /**
     * Updating Data for the First Time from Public API for Unregistered Users.
     */
    @ExperimentalCoroutinesApi
    fun updateDataFromRetrofitResult(baseCurrency: String, itemsData: Map<String, Double>) =  CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        Log.d("ItemDataStructure Update Retrofit", "Adding To ArrayList")
        itemsRatesStructure.clear()

        itemsRatesStructure.add(ItemsDataStructure(
            baseCurrency,
            1.0
        ))

        itemsData.forEach { (currencyName, currencyRate) ->
            itemsRatesStructure.add(ItemsDataStructure(
                currencyName,
                currencyRate
            ))
        }

        itemsCurrencyRate
            .postValue(itemsRatesStructure)
    }
}