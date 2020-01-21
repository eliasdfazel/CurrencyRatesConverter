package xyz.world.currency.rate.converter.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class CurrencyDataViewModel : ViewModel() {

    val recyclerViewItemsCurrencyData: MutableLiveData<ArrayList<RecyclerViewItemsDataStructure>> by lazy {
        MutableLiveData<ArrayList<RecyclerViewItemsDataStructure>>()
    }

    val baseCurrency: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val recyclerViewItemsCurrencyRate: MutableLiveData<ArrayList<RecyclerViewItemsDataStructure>> by lazy {
        MutableLiveData<ArrayList<RecyclerViewItemsDataStructure>>()
    }

    private val recyclerViewItemsDataStructure: ArrayList<RecyclerViewItemsDataStructure> = ArrayList<RecyclerViewItemsDataStructure>()
    private val recyclerViewItemsRatesStructure: ArrayList<RecyclerViewItemsDataStructure> = ArrayList<RecyclerViewItemsDataStructure>()

    /**
     * Loading Data for the First Time from Public API for Unregistered Users.
     */
    @ExperimentalCoroutinesApi
    fun loadDataFromRetrofitResult(baseCurrency: String, itemsData: Map<String, Double>) =  CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        Log.d("ItemDataStructure All Retrofit", "Adding To ArrayList")
        recyclerViewItemsDataStructure.clear()

        recyclerViewItemsDataStructure.add(RecyclerViewItemsDataStructure(
            baseCurrency,
            1.0
        ))

        itemsData.forEach { (currencyName, currencyRate) ->
            recyclerViewItemsDataStructure.add(RecyclerViewItemsDataStructure(
                currencyName,
                currencyRate
            ))
        }

        recyclerViewItemsCurrencyData
            .postValue(recyclerViewItemsDataStructure)
    }

    /**
     * Updating Data for the First Time from Public API for Unregistered Users.
     */
    @ExperimentalCoroutinesApi
    fun updateDataFromRetrofitResult(baseCurrency: String, itemsData: Map<String, Double>) =  CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        Log.d("ItemDataStructure Update Retrofit", "Adding To ArrayList")
        recyclerViewItemsRatesStructure.clear()

        recyclerViewItemsRatesStructure.add(RecyclerViewItemsDataStructure(
            baseCurrency,
            1.0
        ))

        itemsData.forEach { (currencyName, currencyRate) ->
            recyclerViewItemsRatesStructure.add(RecyclerViewItemsDataStructure(
                currencyName,
                currencyRate
            ))
        }

        recyclerViewItemsCurrencyRate
            .postValue(recyclerViewItemsRatesStructure)
    }
}