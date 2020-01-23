package net.geekstools.floatshort.PRO.Widget.RoomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DatabaseDataModel::class], version = 1, exportSchema = false)
abstract class DatabaseInterface : RoomDatabase() {
    abstract fun initDataAccessObject(): DataDAO
}