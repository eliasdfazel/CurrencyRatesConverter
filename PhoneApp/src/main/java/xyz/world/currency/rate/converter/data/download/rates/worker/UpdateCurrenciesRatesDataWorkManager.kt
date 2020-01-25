package xyz.world.currency.rate.converter.data.download.rates.worker

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.android.volley.BuildConfig
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.DatabaseInterface
import org.json.JSONException
import org.json.JSONObject
import xyz.learn.world.heritage.SavedData.PreferencesHandler
import xyz.world.currency.rate.converter.data.RatesJsonDataStructure
import xyz.world.currency.rate.converter.data.database.DatabasePath
import xyz.world.currency.rate.converter.data.database.rates.WriteRatesDatabase
import xyz.world.currency.rate.converter.data.database.rates.WriteRatesDatabaseEssentials
import xyz.world.currency.rate.converter.data.download.rates.Endpoint
import xyz.world.currency.rate.converter.utils.checkpoints.SystemCheckpoints
import java.util.concurrent.TimeUnit


/**
 * Call for Updating Data on Server Side Or Updating Local Data from Cloud
 */
class UpdateCurrenciesRatesDataWorkManager {


    fun triggerCloudDataUpdateScheduleWorkManager(context: Context) {
        val workBuilder = PeriodicWorkRequest.Builder(
            RatesUpdatingWorker::class.java,
            30,
            TimeUnit.MINUTES
        )

        val periodicWorkRequest = workBuilder.build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniquePeriodicWork("RatesUpdate", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest)
    }

    fun jsonObjectRequestWorkManager(context: Context, systemCheckpoints: SystemCheckpoints) {
        if (systemCheckpoints.networkConnection()) {
            val requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(
                JsonObjectRequest(
                    Request.Method.GET,
                    Endpoint().BASE_Link + if (BuildConfig.DEBUG) {
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
                                    null
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
            )
        }
    }
}