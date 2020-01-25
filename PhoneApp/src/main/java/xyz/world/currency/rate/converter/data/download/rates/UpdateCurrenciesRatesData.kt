package xyz.world.currency.rate.converter.data.download.rates

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.DatabaseInterface
import org.json.JSONException
import org.json.JSONObject
import xyz.learn.world.heritage.SavedData.PreferencesHandler
import xyz.world.currency.rate.converter.BuildConfig
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
 * Call for Updating Local Data from Cloud Or Just Trigger Local Database Read.
 */
class UpdateCurrenciesRatesData (var systemCheckpoints: SystemCheckpoints) {

    /**
     * To check current currency & control flow of FlowableItemsDataStructure after base currency changed by item clicks.
     */
    var currentBaseCurrency: String? = null

    lateinit var disposeObservable: Disposable
    /**
     * Invoking Cloud Function to update the currency rates & date in database.
     */
    fun triggerCloudDataUpdateSchedule(context: Context, currencyDataViewModel: CurrencyDataViewModel) {


        /*
         * UpdateCurrenciesRatesDataWorkManager().triggerCloudDataUpdateScheduleWorkManager(context!!)
         *
         * To have Even Better Friendly App with CPU & Battery, I didn't use fixed scheduler to download & update database from servers.
         * So, I added an Observer to repeat every 30 minutes until the app is running.
         * & to avoid recalling for fetching data from server every time user open the app, I am saving time when Observable Subscription occur...
         * then compare saved time with next time to check if it is been 30 minutes since last API Call.
         */

        disposeObservable = Observable
            .interval(1, (30*60), TimeUnit.SECONDS)
            .doOnSubscribe {
                PreferencesHandler(context).CurrencyPreferences().saveLastRatesUpdate(System.currentTimeMillis())
            }
            .repeatUntil {
                currentBaseCurrency != CurrencyDataViewModel.baseCurrency.value
            }
            .filter {

                systemCheckpoints.networkConnection()
            }
            .subscribeOn(Schedulers.io())
            .doOnNext {
                Log.d("Observable", "${it}")

                val currencyPreferences = PreferencesHandler(context).CurrencyPreferences()
                val baseCurrency = if (BuildConfig.DEBUG) {
                    /*
                     * CurrencyAPI Free AccessKey Does NOT Support Source Currency Change. The Default Source is USD.
                     * So, I calculate approx Rate Offset based on selected currency exchange rate with USD.
                     */
                    "USD"
                } else {
                    currencyPreferences.readSaveCurrency()
                }
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
                        Log.d("GettingInformation", "Reading Database")

                        ReadRatesDatabase(context)
                            .readAllData(baseCurrency, currencyDataViewModel)
                    }
                }
            }
            .observeOn(Schedulers.io())
            .subscribe()
    }

    /**
     *  API Call to Download New Data:
     *  1. When user select new source currency.
     *  2. When user click on refresh button.
     */
    fun triggerCloudDataUpdateForce(context: Context, currencyDataViewModel: CurrencyDataViewModel)
            = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

        jsonObjectRequest(context, currencyDataViewModel)
    }

    private fun jsonObjectRequest(context: Context, currencyDataViewModel: CurrencyDataViewModel) {

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            Endpoint.BASE_Link + if (BuildConfig.DEBUG) {
                /*
                 * CurrencyAPI Free AccessKey Does NOT Support Source Currency Change. The Default Source is USD.
                 * So, I calculate approx Rate Offset based on selected currency exchange rate with USD.
                 */
                "USD"
            } else {
                PreferencesHandler(context).CurrencyPreferences().readSaveCurrency()
            },
            null,
            Response.Listener<JSONObject?> { response ->
                Log.d("JsonResult: Currency Rates", response.toString())

                if (response != null && response.getBoolean(RatesJsonDataStructure.SUCCESS)) {
                    try {
                        val baseCurrency: String = response.getString(RatesJsonDataStructure.SOURCE)
                        val currencyRates = response.getJSONObject(RatesJsonDataStructure.QUOTES)
                        val updateTimestamp: Long = System.currentTimeMillis()

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

    fun clearObservable() {
        if (disposeObservable != null && !disposeObservable.isDisposed) {
            disposeObservable.dispose()
        }
    }
}