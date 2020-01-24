package xyz.world.currency.rate.converter.data.database.rates

import android.content.Context
import android.database.Cursor
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.*
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.DatabaseInterface
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel
import xyz.world.currency.rate.converter.data.RecyclerViewItemsDataStructure
import xyz.world.currency.rate.converter.data.RoomDatabaseColumn
import xyz.world.currency.rate.converter.data.database.DatabasePath

class ReadRatesDatabase(var context: Context) {

    fun readAllData(tableName: String, currencyDataViewModel: CurrencyDataViewModel) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        val recyclerViewItemsDataStructure: ArrayList<RecyclerViewItemsDataStructure> = ArrayList<RecyclerViewItemsDataStructure>()

        val roomDatabaseRead = Room.databaseBuilder(context, DatabaseInterface::class.java,
            DatabasePath.CURRENCY_DATABASE_NAME
        )
            .build()

        val supportSQLiteDatabase: SupportSQLiteDatabase = roomDatabaseRead.openHelper.readableDatabase
        val cursor: Cursor = supportSQLiteDatabase.query("SELECT * FROM $tableName")
        cursor.moveToFirst()

        while (!cursor.isAfterLast) {

            recyclerViewItemsDataStructure.add(
                RecyclerViewItemsDataStructure(
                    cursor.getString(cursor.getColumnIndex(RoomDatabaseColumn.CurrencyCode)),
                    cursor.getDouble(cursor.getColumnIndex(RoomDatabaseColumn.CurrencyRate))
                )
            )

            cursor.moveToNext()
        }
        roomDatabaseRead.close()

        currencyDataViewModel.recyclerViewItemsRatesExchange.postValue(recyclerViewItemsDataStructure)
    }

    /**
     * CurrencyAPI Free AccessKey Does NOT Support Source Currency Change. The Default Source is USD.
     * So, I calculate approx Rate Offset based on selected currency exchange rate with USD.
     */
    fun readSpecificRateFromTableUSD(currencyCode: String) : Deferred<Double> = CoroutineScope(Dispatchers.IO).async {
        val roomDatabaseRead = Room.databaseBuilder(context, DatabaseInterface::class.java,
            DatabasePath.CURRENCY_DATABASE_NAME
        )
            .build()

        val supportSQLiteDatabase: SupportSQLiteDatabase = roomDatabaseRead.openHelper.readableDatabase
        val cursor: Cursor = supportSQLiteDatabase.query("SELECT * FROM USD WHERE CurrencyCode='${currencyCode}'")
        cursor.moveToFirst()

        cursor.getDouble(cursor.getColumnIndex(RoomDatabaseColumn.CurrencyRate))
    }
}