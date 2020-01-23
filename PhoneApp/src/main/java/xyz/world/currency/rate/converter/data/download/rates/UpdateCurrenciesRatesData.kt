package xyz.world.currency.rate.converter.data.download.rates

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
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.DatabaseInterface
import org.json.JSONException
import org.json.JSONObject
import xyz.learn.world.heritage.SavedData.PreferencesHandler
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel
import xyz.world.currency.rate.converter.data.RatesJsonDataStructure
import xyz.world.currency.rate.converter.data.database.DatabasePath
import xyz.world.currency.rate.converter.data.database.rates.ReadRatesDatabase
import xyz.world.currency.rate.converter.data.database.rates.WriteRatesDatabase
import xyz.world.currency.rate.converter.data.database.rates.WriteRatesDatabaseEssentials
import xyz.world.currency.rate.converter.utils.checkpoints.DatabaseCheckpoint
import xyz.world.currency.rate.converter.utils.checkpoints.SystemCheckpoints
import java.util.concurrent.TimeUnit


/**
 * Call for Updating Data on Server Side Or Updating Local Data from Cloud
 */
class UpdateCurrenciesRatesData (var systemCheckpoints: SystemCheckpoints) {

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
                if (!DatabaseCheckpoint(context).doesTableExist(baseCurrency)) {
                    Log.d("UpdateCloudData", "Updating Database")

                    jsonObjectRequest(context,
                        currencyDataViewModel)

                } else {
                    val lastTimeUpdate = currencyPreferences.readLastRatesUpdate()
                    val timeDiffer = (System.currentTimeMillis() - lastTimeUpdate)
                    val thirtyMinutesMillis = (30*60*1000)
                    if (timeDiffer > thirtyMinutesMillis) {
                        Log.d("UpdateCloudData", "Updating Database After 30 Minutes")

                        jsonObjectRequest(context,
                            currencyDataViewModel)

                    } else {
                        Log.d("Getting Information", "Reading Database")

                        ReadRatesDatabase(
                            context
                        )
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
                Log.d("Json Result: Currency Rates", response.toString())

                if (response != null && response.getBoolean(RatesJsonDataStructure.SUCCESS)) {
                    try {
                        val baseCurrency: String = response.getString(RatesJsonDataStructure.SOURCE)
                        val currencyRates = response.getJSONObject(RatesJsonDataStructure.QUOTES)

                        val updateTimestamp: Long = System.currentTimeMillis()
                        PreferencesHandler(context).CurrencyPreferences().saveLastRatesUpdate(updateTimestamp)

                        GlobalScope.launch {
                            val roomDatabase = Room.databaseBuilder(context, DatabaseInterface::class.java, DatabasePath.CURRENCY_DATABASE_NAME)
                                .build()

                            WriteRatesDatabaseEssentials(
                                roomDatabase,
                                baseCurrency,
                                updateTimestamp,
                                currencyRates,
                                currencyDataViewModel
                            ).also {
                                val writeRatesDatabase =
                                    WriteRatesDatabase(
                                        context,
                                        it
                                    )
                                writeRatesDatabase.handleRatesDatabase()
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