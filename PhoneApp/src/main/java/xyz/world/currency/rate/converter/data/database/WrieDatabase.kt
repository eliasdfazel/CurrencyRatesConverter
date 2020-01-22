package xyz.world.currency.rate.converter.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.CurrencyDataInterface
import xyz.world.currency.rate.converter.data.RoomDatabaseColumn

class WrieDatabase(var context: Context) {

    fun insertAllData(sourceCurrency: String, lastUpdateTime: Long, allDataHashMap: Map<String, Double>) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        val firestore = FirebaseFirestore.getInstance()

        val roomDatabase = Room.databaseBuilder(context, CurrencyDataInterface::class.java, DatabasePath.CURRENCY_DATABASE_NAME)
            .allowMainThreadQueries()
            .build()
        AddNewTable().addNewTableForNewBaseCurrency(roomDatabase, sourceCurrency)
        val supportSQLiteDatabaseWrite: SupportSQLiteDatabase = roomDatabase.openHelper.writableDatabase

        allDataHashMap.forEach { (key, currencyRate) ->
            val currencyCode = key.replace(sourceCurrency, "")

            firestore.document(DatabasePath.FIRESTORE_REFERENCE_DIRECTORY + currencyCode).get().addOnSuccessListener { documentSnapshot ->

                supportSQLiteDatabaseWrite.insert(sourceCurrency, SQLiteDatabase.CONFLICT_IGNORE, ContentValues().apply {
                    this.put(RoomDatabaseColumn.CurrencyCode, currencyCode)
                    this.put(RoomDatabaseColumn.FullCurrencyName, documentSnapshot.getString("CountryName"))
                    this.put(RoomDatabaseColumn.CurrencyRate, currencyRate)
                    this.put(RoomDatabaseColumn.LastUpdateTime, lastUpdateTime)
                })
            }
        }

        roomDatabase.close()
        supportSQLiteDatabaseWrite.close()
    }

    fun updateAllData(sourceCurrency: String, lastUpdateTime: Long, allDataHashMap: Map<String, Double>) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

        val roomDatabase = Room.databaseBuilder(context, CurrencyDataInterface::class.java, DatabasePath.CURRENCY_DATABASE_NAME)
            .allowMainThreadQueries()
            .build()
        AddNewTable().addNewTableForNewBaseCurrency(roomDatabase, sourceCurrency)
        val supportSQLiteDatabaseWrite: SupportSQLiteDatabase = roomDatabase.openHelper.writableDatabase

        allDataHashMap.forEach { (key, currencyRate) ->
            val currencyCode = key.replace(sourceCurrency, "")

            supportSQLiteDatabaseWrite.update(sourceCurrency, SQLiteDatabase.CONFLICT_IGNORE, ContentValues().apply {
                this.put(RoomDatabaseColumn.CurrencyCode, currencyCode)
                this.put(RoomDatabaseColumn.CurrencyRate, currencyRate)
                this.put(RoomDatabaseColumn.LastUpdateTime, lastUpdateTime)
            }, "CurrencyCode = ? ", arrayOf(RoomDatabaseColumn.CurrencyCode))
        }

        roomDatabase.close()
        supportSQLiteDatabaseWrite.close()
    }
}