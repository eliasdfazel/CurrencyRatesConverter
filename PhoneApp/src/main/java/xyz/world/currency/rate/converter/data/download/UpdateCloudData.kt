package xyz.world.currency.rate.converter.data.download

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.CurrencyDataInterface
import org.json.JSONException
import org.json.JSONObject
import xyz.learn.world.heritage.SavedData.PreferencesHandler
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel
import xyz.world.currency.rate.converter.data.JsonDataStructure
import xyz.world.currency.rate.converter.data.database.DatabasePath
import xyz.world.currency.rate.converter.data.database.ReadDatabase
import xyz.world.currency.rate.converter.data.database.WriteDatabase
import xyz.world.currency.rate.converter.data.database.WriteDatabaseEssentials
import xyz.world.currency.rate.converter.utils.checkpoints.DatabaseCheckpoint
import xyz.world.currency.rate.converter.utils.checkpoints.SystemCheckpoints
import java.util.concurrent.TimeUnit


/**
 * Call for Updating Data on Server Side Or Updating Local Data from Cloud
 */
class UpdateCloudData (var systemCheckpoints: SystemCheckpoints) {

    companion object {
        var CONTINUE_UPDATE_SUBSCRIPTION: Boolean = true
    }

    /**
     * To check current currency & control flow of FlowableItemsDataStructure after base currency changed by item clicks.
     */
    var currentBaseCurrency: String? = null

    /**
     * Invoking Cloud Function to update the currency rates & date in database.
     * It will only pass FirebaseUser.UID as userId to update users specific database and selected base currency to update order of database.
     * All Process of downloading new JsonObject Data & extracting data from JsonObjects will be done on server side.
     * It will only update changed data on database, so on device listener only receive Document Snapshot when data updated.
     */
    fun triggerCloudDataUpdateSchedule(context: Context, currencyDataViewModel: CurrencyDataViewModel) {

        Observable
            .interval(1, (30*60), TimeUnit.SECONDS)
            .repeatUntil {
                currentBaseCurrency != CurrencyDataViewModel.baseCurrency.value
            }
            .filter {
                Log.d("Base Filter", "$CONTINUE_UPDATE_SUBSCRIPTION")

                systemCheckpoints.networkConnection() && CONTINUE_UPDATE_SUBSCRIPTION
            }
            .subscribeOn(Schedulers.io())
            .doOnNext {
                Log.d("Observable", "${it}")


                val currencyPreferences = PreferencesHandler(context).CurrencyPreferences()
                val baseCurrency = currencyPreferences.readSaveCurrency()
                if (!DatabaseCheckpoint().doesTableExist(context, baseCurrency)) {
                    Log.d("UpdateCloudData", "Updating Database")

                    jsonObjectRequest(context,
                        currencyDataViewModel)

                } else {
                    val lastTimeUpdate = currencyPreferences.readLastUpdate()
                    val timeDiffer = (System.currentTimeMillis() - lastTimeUpdate)
                    val thirtyMinutesMillis = (30*60*1000)

                    if (timeDiffer > thirtyMinutesMillis) {
                        Log.d("UpdateCloudData", "Updating Database After 30 Minutes")

                        jsonObjectRequest(context,
                            currencyDataViewModel)

                    } else {
                        Log.d("Getting Information", "Reading Database")

                        ReadDatabase(context)
                            .readAllData(baseCurrency, currencyDataViewModel)
                    }
                }
            }
            .observeOn(Schedulers.io())
            .subscribe()
    }

    fun triggerCloudDataUpdateForce(context: Context, currencyDataViewModel: CurrencyDataViewModel)
            = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

        jsonObjectRequest(context, currencyDataViewModel)
    }

    private fun jsonObjectRequest(context: Context, currencyDataViewModel: CurrencyDataViewModel) {
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            Endpoint().BASE_Link + PreferencesHandler(context).CurrencyPreferences().readSaveCurrency(),
            null,
            Response.Listener<JSONObject?> { response ->
                Log.d("Json Result: Currency List", response.toString())

                if (response != null) {
                    try {
                        val baseCurrency: String = response.getString(JsonDataStructure.SOURCE)
                        val updateTimestamp: Long = response.getLong(JsonDataStructure.TIMESTAMP)
                        val currencyRates = response.getJSONObject(JsonDataStructure.QUOTES)
                        PreferencesHandler(context).CurrencyPreferences().saveLastUpdate(updateTimestamp)

                        GlobalScope.launch {
                            val roomDatabase = Room.databaseBuilder(context, CurrencyDataInterface::class.java, DatabasePath.CURRENCY_DATABASE_NAME)
                                .build()

                            WriteDatabaseEssentials(roomDatabase, baseCurrency, updateTimestamp, currencyRates, currencyDataViewModel).also {
                                val writeDatabase = WriteDatabase(context, it)
                                writeDatabase.handleDatabase()
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

            }, Response.ErrorListener {
                Log.d("JsonObjectRequest Error", it.toString())
            })

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(jsonObjectRequest)
    }
}