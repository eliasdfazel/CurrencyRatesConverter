package xyz.world.currency.rate.converter.utils.checkpoints

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import net.geekstools.floatshort.PRO.Widget.RoomDatabase.CurrencyDataInterface
import xyz.world.currency.rate.converter.data.database.DatabasePath

class DatabaseCheckpoint {

    fun doesTableExist(context: Context, roomDatabase: RoomDatabase, tableName: String) : Boolean{

        val supportSQLiteDatabase: SupportSQLiteDatabase = roomDatabase.openHelper.writableDatabase
        var cursor: Cursor? = null
        return if (context.getDatabasePath(DatabasePath.CURRENCY_DATABASE_NAME).exists()) {
            try {
                cursor = supportSQLiteDatabase.query("SELECT name FROM sqlite_master WHERE name='${tableName}'")

                (cursor.count > 0)
            } catch (e: SQLiteException) {
                e.printStackTrace()

                false
            } finally {
                cursor?.close()
                roomDatabase.close()
            }
        } else {
            false
        }
    }

    fun doesTableExist(context: Context, tableName: String) : Boolean {
        val roomDatabase = Room.databaseBuilder(context, CurrencyDataInterface::class.java, DatabasePath.CURRENCY_DATABASE_NAME)
            .build()

        val supportSQLiteDatabase: SupportSQLiteDatabase = roomDatabase.openHelper.writableDatabase
        var cursor: Cursor? = null
        return if (context.getDatabasePath(DatabasePath.CURRENCY_DATABASE_NAME).exists()) {
            try {
                cursor = supportSQLiteDatabase.query("SELECT name FROM sqlite_master WHERE name='${tableName}'")

                (cursor.count > 0)
            } catch (e: SQLiteException) {
                e.printStackTrace()

                false
            } finally {
                cursor?.close()
                roomDatabase.close()
            }
        } else {

            false
        }
    }
}