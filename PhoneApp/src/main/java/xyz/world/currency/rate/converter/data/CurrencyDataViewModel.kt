package xyz.world.currency.rate.converter.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import org.json.JSONObject

class CurrencyDataViewModel : ViewModel() {

    companion object {
        val baseCurrency: MutableLiveData<String> by lazy {
            MutableLiveData<String>()
        }
    }

    val recyclerViewItemsCurrencyData: MutableLiveData<ArrayList<RecyclerViewItemsDataStructure>> by lazy {
        MutableLiveData<ArrayList<RecyclerViewItemsDataStructure>>()
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
    fun loadDataFromResult(baseCurrency: String, itemsData: JSONObject) =  CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        Log.d("ItemDataStructure All Retrofit", "Adding To ArrayList")
        recyclerViewItemsDataStructure.clear()

        itemsData.keys().forEach {
            val currencyCode = (it).toString().replace(baseCurrency, "")

            if (!currencyCode.isNullOrEmpty()) {
                recyclerViewItemsDataStructure.add(RecyclerViewItemsDataStructure(
                    it.replace(baseCurrency, ""),
                    itemsData.getDouble(it)
                ))
            }
        }

        recyclerViewItemsCurrencyData
            .postValue(recyclerViewItemsDataStructure)
    }

    /**
     * Updating Data for the First Time from Public API for Unregistered Users.
     */
    @ExperimentalCoroutinesApi
    fun updateDataFromResult(baseCurrency: String, itemsData: JSONObject) =  CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        Log.d("ItemDataStructure Update Retrofit", "Adding To ArrayList")
        recyclerViewItemsRatesStructure.clear()

        itemsData.keys().forEach {
            val currencyCode = (it).toString().replace(baseCurrency, "")

            if (!currencyCode.isNullOrEmpty()) {
                recyclerViewItemsRatesStructure.add(RecyclerViewItemsDataStructure(
                    it.replace(baseCurrency, ""),
                    itemsData.getDouble(it)
                ))
            }
        }

        recyclerViewItemsCurrencyRate
            .postValue(recyclerViewItemsRatesStructure)
    }
}