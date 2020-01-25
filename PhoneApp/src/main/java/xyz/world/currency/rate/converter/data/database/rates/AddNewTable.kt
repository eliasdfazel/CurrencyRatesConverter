package xyz.world.currency.rate.converter.data.database.rates

import android.database.sqlite.SQLiteException
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import xyz.world.currency.rate.converter.data.database.DatabasePath


class AddNewTable {

    /**
     *  Creating Database Table of Given Name when users select a currency as source currency.
     *
     *  ⚠ Changing source currency is not available with Free AccessKey - So I simulate approx exchange rate based on default source: USD  ⚠
     */
    fun addNewTableForNewBaseCurrency(roomDatabase: RoomDatabase, databaseName: String): Boolean {
        val supportSQLiteDatabase: SupportSQLiteDatabase = roomDatabase.openHelper.writableDatabase
        return try {
            supportSQLiteDatabase.execSQL(
                DatabasePath.CREATE_DATABASE_TABLE_COMMAND.replace(
                    DatabasePath.CURRENCY_DATABASE_NAME,
                    databaseName
                )
            )

            true
        } catch (e: SQLiteException) {
            e.printStackTrace()

            false
        } finally {
            roomDatabase.close()
        }
    }
}