package xyz.world.currency.rate.converter.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.DatabaseDataModel
import org.json.JSONObject

class CurrencyDataViewModel : ViewModel() {

    companion object {
        val baseCurrency: MutableLiveData<String> by lazy {
            MutableLiveData<String>()
        }
    }

    val recyclerViewItemsRatesExchange: MutableLiveData<ArrayList<RecyclerViewItemsDataStructure>> by lazy {
        MutableLiveData<ArrayList<RecyclerViewItemsDataStructure>>()
    }

    val supportedCurrencyList: MutableLiveData<List<DatabaseDataModel>> by lazy {
        MutableLiveData<List<DatabaseDataModel>>()
    }

    private val recyclerViewItemsDataStructure: ArrayList<RecyclerViewItemsDataStructure> = ArrayList<RecyclerViewItemsDataStructure>()
    private val recyclerViewItemsRatesStructure: ArrayList<RecyclerViewItemsDataStructure> = ArrayList<RecyclerViewItemsDataStructure>()

    /**
     *  Parsing Json Data for the first time after inserting data to database
     *  I used cached data from memory in this point to read download data instead of database read query.
     */
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

        recyclerViewItemsRatesExchange.postValue(recyclerViewItemsDataStructure)
    }

    /**
     *  Parsing Json Data for the after updating data to database
     *  I used cached data from memory in this point to read download data instead of database read query.
     */
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

        recyclerViewItemsRatesExchange.postValue(recyclerViewItemsRatesStructure)
    }
}