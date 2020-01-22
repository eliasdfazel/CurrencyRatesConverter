package net.geekstools.floatshort.PRO.Widget.RoomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CurrencyDataModel::class], version = 1, exportSchema = false)
abstract class CurrencyDataInterface : RoomDatabase() {
    abstract fun initDataAccessObject(): CurrencyDataDAO
}