package xyz.world.currency.rate.converter.data.database.rates

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.*
import org.json.JSONObject
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel
import xyz.world.currency.rate.converter.data.RoomDatabaseColumn
import xyz.world.currency.rate.converter.utils.checkpoints.DatabaseCheckpoint

data class WriteRatesDatabaseEssentials(var roomDatabase: RoomDatabase, var baseCurrency: String, var updateTimestamp: Long, var jsonObjectRates: JSONObject, var currencyDataViewModel: CurrencyDataViewModel)

class WriteRatesDatabase(var context: Context, var writeRatesDatabaseEssentials: WriteRatesDatabaseEssentials) {

    fun handleRatesDatabase() {
        GlobalScope.launch {
            if (!DatabaseCheckpoint(context).doesTableExist(writeRatesDatabaseEssentials.roomDatabase, writeRatesDatabaseEssentials.baseCurrency)) {
                insertAllData()
            } else {
                updateAllData()
            }
        }
    }

    private fun insertAllData()
            = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

        AddNewTable().addNewTableForNewBaseCurrency(writeRatesDatabaseEssentials.roomDatabase, writeRatesDatabaseEssentials.baseCurrency)
        val supportSQLiteDatabaseWrite: SupportSQLiteDatabase = writeRatesDatabaseEssentials.roomDatabase.openHelper.writableDatabase

        writeRatesDatabaseEssentials.jsonObjectRates.keys().forEach {
            val currencyCode = (it).toString().replace(writeRatesDatabaseEssentials.baseCurrency, "")

            if (!currencyCode.isNullOrEmpty()) {
                supportSQLiteDatabaseWrite.insert(
                    writeRatesDatabaseEssentials.baseCurrency,
                    SQLiteDatabase.CONFLICT_IGNORE,
                    ContentValues().apply {
                        this.put(RoomDatabaseColumn.CurrencyCode, currencyCode)
                        this.put(
                            RoomDatabaseColumn.CurrencyRate,
                            writeRatesDatabaseEssentials.jsonObjectRates.getDouble(it)
                        )
                        this.put(RoomDatabaseColumn.LastUpdateTime, writeRatesDatabaseEssentials.updateTimestamp)
                    })
            }
        }

        writeRatesDatabaseEssentials.currencyDataViewModel.loadDataFromResult(writeRatesDatabaseEssentials.baseCurrency, writeRatesDatabaseEssentials.jsonObjectRates)

        writeRatesDatabaseEssentials.roomDatabase.close()
    }

    private fun updateAllData()
            = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

        AddNewTable().addNewTableForNewBaseCurrency(writeRatesDatabaseEssentials.roomDatabase, writeRatesDatabaseEssentials.baseCurrency)
        val supportSQLiteDatabaseWrite: SupportSQLiteDatabase = writeRatesDatabaseEssentials.roomDatabase.openHelper.writableDatabase


        writeRatesDatabaseEssentials.jsonObjectRates.keys().forEach {
            val currencyCode = (it).toString().replace(writeRatesDatabaseEssentials.baseCurrency, "")

            if (!currencyCode.isNullOrEmpty()) {

                supportSQLiteDatabaseWrite.update(writeRatesDatabaseEssentials.baseCurrency, SQLiteDatabase.CONFLICT_IGNORE, ContentValues().apply {
                    this.put(RoomDatabaseColumn.CurrencyCode, currencyCode)
                    this.put(RoomDatabaseColumn.CurrencyRate, writeRatesDatabaseEssentials.jsonObjectRates.getDouble(it))
                    this.put(RoomDatabaseColumn.LastUpdateTime, writeRatesDatabaseEssentials.updateTimestamp)
                }, "CurrencyCode = ? ", arrayOf(RoomDatabaseColumn.CurrencyCode))
            }
        }

        writeRatesDatabaseEssentials.currencyDataViewModel.updateDataFromResult(writeRatesDatabaseEssentials.baseCurrency, writeRatesDatabaseEssentials.jsonObjectRates)

        writeRatesDatabaseEssentials.roomDatabase.close()
    }
}