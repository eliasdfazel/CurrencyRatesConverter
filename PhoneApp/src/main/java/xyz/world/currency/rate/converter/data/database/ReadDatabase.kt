package xyz.world.currency.rate.converter.data.database

import android.content.Context
import android.database.Cursor
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.CurrencyDataInterface
import xyz.world.currency.rate.converter.data.CurrencyDataViewModel
import xyz.world.currency.rate.converter.data.RecyclerViewItemsDataStructure
import xyz.world.currency.rate.converter.data.RoomDatabaseColumn

class ReadDatabase(var context: Context) {

    fun readAllData(tableName: String, currencyDataViewModel: CurrencyDataViewModel) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        val recyclerViewItemsDataStructure: ArrayList<RecyclerViewItemsDataStructure> = ArrayList<RecyclerViewItemsDataStructure>()

        val roomDatabaseRead = Room.databaseBuilder(context, CurrencyDataInterface::class.java, DatabasePath.CURRENCY_DATABASE_NAME)
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

        currencyDataViewModel.recyclerViewItemsCurrencyData.postValue(recyclerViewItemsDataStructure)
    }
}