package xyz.world.currency.rate.converter.data.database.currencies

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.DatabaseInterface
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel
import xyz.world.currency.rate.converter.data.database.DatabasePath

class ReadCurrenciesDatabase(var context: Context) {

    fun readAllData(currencyDataViewModel: CurrencyDataViewModel) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        val roomDatabaseRead = Room.databaseBuilder(context, DatabaseInterface::class.java,
            DatabasePath.CURRENCY_DATABASE_NAME).build()

        currencyDataViewModel.supportedCurrencyList
            .postValue(roomDatabaseRead.initDataAccessObject().getAllCurrencyBaseTable())
    }
}