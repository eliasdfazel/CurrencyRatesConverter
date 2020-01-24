package xyz.world.currency.rate.converter.data.download.currencies

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.room.Room
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.DatabaseInterface
import org.json.JSONException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import xyz.learn.world.heritage.SavedData.PreferencesHandler
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel
import xyz.world.currency.rate.converter.data.EndpointInterface
import xyz.world.currency.rate.converter.data.database.DatabasePath
import xyz.world.currency.rate.converter.data.database.currencies.ReadCurrenciesDatabase
import xyz.world.currency.rate.converter.data.database.currencies.WriteCurrenciesDatabase
import xyz.world.currency.rate.converter.data.database.currencies.WriteCurrenciesDatabaseEssentials
import xyz.world.currency.rate.converter.utils.checkpoints.SystemCheckpoints


/**
 * Call for Updating Data on Server Side Or Updating Local Data from Cloud
 */
class UpdateSupportedListData (var systemCheckpoints: SystemCheckpoints) {

    /**
     *  Download JSON Data from a Public API call on device.
     *  & Parse data on device.
     *  Process will trigger each second
     */
    @SuppressLint("CheckResult")
    fun triggerSupportedCurrenciesUpdate(context: Context, currencyDataViewModel: CurrencyDataViewModel) = CoroutineScope(Dispatchers.IO).launch {

        val roomDatabase = Room.databaseBuilder(context, DatabaseInterface::class.java, DatabasePath.CURRENCY_DATABASE_NAME)
            .build()
        if (roomDatabase.initDataAccessObject().getRowCount() > 0) {

            ReadCurrenciesDatabase(context)
                .readAllData(currencyDataViewModel)
        } else {

            val flowableItemsDataStructure = endpointInterfaceRetrofit().getListOfSupportCurrencies()
                .doOnSubscribe {}
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter {

                    systemCheckpoints.networkConnection()
                }
            flowableItemsDataStructure.subscribe({
                Log.d("Json Result: Currency List", it.success.toString())

                if (it.success) {
                    try {
                        val currencyList = it.currencies
                        val updateTimestamp: Long = System.currentTimeMillis()
                        PreferencesHandler(context).CurrencyPreferences().saveLastCurrencyListUpdate(updateTimestamp)

                        WriteCurrenciesDatabaseEssentials(
                            roomDatabase,
                            updateTimestamp,
                            currencyList,
                            currencyDataViewModel
                        ).also { databaseEssentials ->
                            val writeCurrenciesDatabase =
                                WriteCurrenciesDatabase(
                                    context,
                                    databaseEssentials
                                )
                            writeCurrenciesDatabase.handleCurrenciesDatabase()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }, {
                Log.d("Retrofit Error", "${it.message}")
            })
        }
    }

    /**
     *  Initializing Retrofit & Json Parser
     */
    private fun endpointInterfaceRetrofit(): EndpointInterface {

        return Retrofit.Builder()
            .baseUrl(EndpointInterface.BASE_Link)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(EndpointInterface::class.java)
    }
}