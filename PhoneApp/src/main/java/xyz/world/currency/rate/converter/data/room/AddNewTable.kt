package xyz.world.currency.rate.converter.data.room

import android.database.sqlite.SQLiteException
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase


class AddNewTable {

    fun addNewTableForNewBaseCurrency(roomDatabase: RoomDatabase, databaseName: String): Boolean {
        val supportSQLiteDatabase: SupportSQLiteDatabase = roomDatabase.openHelper.writableDatabase
        return try {
            supportSQLiteDatabase.execSQL(
                DatabasePath.CREATE_DATABASE_TABLE.replace(
                    DatabasePath.CURRENCY_DATABASE,
                    databaseName
                )
            )

            true
        } catch (e: SQLiteException) {
            e.printStackTrace()

            false
        }
    }
}