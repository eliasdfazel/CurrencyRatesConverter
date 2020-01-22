package xyz.world.currency.rate.converter.data.database

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

data class WriteDatabaseEssentials(var roomDatabase: RoomDatabase, var baseCurrency: String, var updateTimestamp: Long, var jsonObjectRates: JSONObject, var currencyDataViewModel: CurrencyDataViewModel)

class WriteDatabase(var context: Context, var writeDatabaseEssentials: WriteDatabaseEssentials) {

    fun handleDatabase() {
        GlobalScope.launch {
            if (!DatabaseCheckpoint().doesTableExist(context, writeDatabaseEssentials.roomDatabase, writeDatabaseEssentials.baseCurrency)) {
                insertAllData()
            } else {
                updateAllData()
            }
        }
    }

    fun insertAllData()
            = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

        AddNewTable().addNewTableForNewBaseCurrency(writeDatabaseEssentials.roomDatabase, writeDatabaseEssentials.baseCurrency)
        val supportSQLiteDatabaseWrite: SupportSQLiteDatabase = writeDatabaseEssentials.roomDatabase.openHelper.writableDatabase

        writeDatabaseEssentials.jsonObjectRates.keys().forEach {
            val currencyCode = (it).toString().replace(writeDatabaseEssentials.baseCurrency, "")

            if (!currencyCode.isNullOrEmpty()) {
                supportSQLiteDatabaseWrite.insert(
                    writeDatabaseEssentials.baseCurrency,
                    SQLiteDatabase.CONFLICT_IGNORE,
                    ContentValues().apply {
                        this.put(RoomDatabaseColumn.CurrencyCode, currencyCode)
                        this.put(
                            RoomDatabaseColumn.CurrencyRate,
                            writeDatabaseEssentials.jsonObjectRates.getDouble(it)
                        )
                        this.put(RoomDatabaseColumn.LastUpdateTime, writeDatabaseEssentials.updateTimestamp)
                    })
            }
        }

        writeDatabaseEssentials.currencyDataViewModel.loadDataFromResult(writeDatabaseEssentials.baseCurrency, writeDatabaseEssentials.jsonObjectRates)

        writeDatabaseEssentials.roomDatabase.close()
    }

    fun updateAllData()
            = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

        AddNewTable().addNewTableForNewBaseCurrency(writeDatabaseEssentials.roomDatabase, writeDatabaseEssentials.baseCurrency)
        val supportSQLiteDatabaseWrite: SupportSQLiteDatabase = writeDatabaseEssentials.roomDatabase.openHelper.writableDatabase


        writeDatabaseEssentials.jsonObjectRates.keys().forEach {
            val currencyCode = (it).toString().replace(writeDatabaseEssentials.baseCurrency, "")

            if (!currencyCode.isNullOrEmpty()) {

                supportSQLiteDatabaseWrite.update(writeDatabaseEssentials.baseCurrency, SQLiteDatabase.CONFLICT_IGNORE, ContentValues().apply {
                    this.put(RoomDatabaseColumn.CurrencyCode, currencyCode)
                    this.put(RoomDatabaseColumn.CurrencyRate, writeDatabaseEssentials.jsonObjectRates.getDouble(it))
                    this.put(RoomDatabaseColumn.LastUpdateTime, writeDatabaseEssentials.updateTimestamp)
                }, "CurrencyCode = ? ", arrayOf(RoomDatabaseColumn.CurrencyCode))
            }
        }

        writeDatabaseEssentials.currencyDataViewModel.updateDataFromResult(writeDatabaseEssentials.baseCurrency, writeDatabaseEssentials.jsonObjectRates)

        writeDatabaseEssentials.roomDatabase.close()
    }
}